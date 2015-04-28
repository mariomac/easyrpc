package easyrpc.client.protocol.amqp;

import easyrpc.server.protocol.RpcClient;
import easyrpc.server.protocol.amqp.AmqpService;

import javax.jms.*;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.StringReader;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mmacias on 23/12/14.
 */
public class AmqpClient extends RpcClient {

	private URI broker;
	private String serverQueue;
	private String clientQueue;

	Context context;
	Connection conn;
	Session producerSession;
	Queue recvQueue;
	Queue sendQueue;

	public AmqpClient(URI broker, String serverQueue) {
		this.broker = broker;
		this.serverQueue = serverQueue;

		try {
			clientQueue = UUID.randomUUID().toString();
			// example taken from http://svn.apache.org/repos/asf/qpid/branches/0.30/qpid/java/amqp-1-0-client-jms/example/src/main/java/org/apache/qpid/amqp_1_0/jms/example/hello.properties
			Properties p = new Properties();
//			p.put("java.naming.factory.initial","org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
			p.put("java.naming.factory.initial","org.apache.qpid.jms.jndi.JmsInitialContextFactory");

			p.put("connectionfactory.broker",broker.toString()); // e.g. amqp://guest:guest@localhost:5672?clientid=test-client&remote-host=default
			p.put("queue.queue", clientQueue);
			p.put("queue.serverQueue", serverQueue);

			context = new InitialContext(p);

			ConnectionFactory cf = (ConnectionFactory) context.lookup("broker");
			conn = cf.createConnection();

			producerSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			recvQueue = (Queue) context.lookup("queue");
			sendQueue = (Queue) context.lookup("serverQueue");

			Session consumerSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer mc = consumerSession.createConsumer(recvQueue);

			mc.setMessageListener(new RpcClientListener());

			conn.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	CountDownLatch operatingCall = null;
	CountDownLatch waitingForResponse = null;
	byte[] response = null;

	@Override
	public byte[] sendMessage(String endpoint, byte[] info) {
		byte[] ret = null;
		try {
			// if there is another message waiting for a response, we await for it
			// TODO: enable this for multithreading
			if(operatingCall != null) {
				operatingCall.await();
			}
			operatingCall = new CountDownLatch(1);
			waitingForResponse = new CountDownLatch(1);

			MessageProducer prod = producerSession.createProducer(sendQueue);
			BytesMessage bm = producerSession.createBytesMessage();
			bm.setJMSReplyTo(recvQueue);
			bm.setStringProperty(AmqpService.PROP_ENDPOINT, endpoint);

			Enumeration en = bm.getPropertyNames();
			while(en.hasMoreElements()) {
				String p = en.nextElement().toString();
			}

			bm.writeBytes(info);

			prod.send(bm);
			waitingForResponse.await();

			ret = response;

			operatingCall.countDown();

			return ret;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	class RpcClientListener implements MessageListener {

		@Override
		public void onMessage(Message message) {

			BytesMessage bm = (BytesMessage) message;

			try {
				response = new byte[(int)bm.getBodyLength()];
				bm.readBytes(response);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			} finally {
				waitingForResponse.countDown();
			}
		}
	}
}
