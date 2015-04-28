package easyrpc.server.protocol.amqp;

import easyrpc.error.RemoteMethodException;
import easyrpc.error.SerializationException;
import easyrpc.server.protocol.RpcService;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.StringReader;
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by mmacias on 23/12/14.
 */
public class AmqpService extends RpcService {
	private URI broker;
	private String queueName;

	Context context;
	Connection conn;
	Session producerSession;
	Queue recvQueue;

	public AmqpService(URI broker, String queueName) {
		this.broker = broker;
		this.queueName = queueName;
	}

	@Override
	public void start() {
		try {

			// example taken from http://svn.apache.org/repos/asf/qpid/branches/0.30/qpid/java/amqp-1-0-client-jms/example/src/main/java/org/apache/qpid/amqp_1_0/jms/example/hello.properties
			Properties p = new Properties();
			p.put("java.naming.factory.initial","org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
			p.put("connectionfactory.broker",broker.toString()); // e.g. amqp://guest:guest@localhost:5672?clientid=test-client&remote-host=default
			p.put("queue.queue", queueName);
			context = new InitialContext(p);

			ConnectionFactory cf = (ConnectionFactory) context.lookup("broker");
			conn = cf.createConnection();

			producerSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			recvQueue = (Queue) context.lookup("queue");

			Session consumerSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer mc = consumerSession.createConsumer(recvQueue);

			mc.setMessageListener(new RpcServiceListener());

			conn.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		try {
			conn.close();
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	class RpcServiceListener implements MessageListener {
		@Override
		public void onMessage(Message message) {
			try {
				BytesMessage bytesMessage = (BytesMessage) message;
				byte[] bytes = new byte[(int)(bytesMessage.getBodyLength())];
				bytesMessage.readBytes(bytes);

/*				System.out.println("new String(bytes) = " + new String(bytes));
				System.out.println("Properties");
				Enumeration en = bytesMessage.getPropertyNames();
				while(en.hasMoreElements()) {
					String p = en.nextElement().toString();
					System.out.println(p + " -> " + bytesMessage.getObjectProperty(p));
				}

*/

//				if(tm.getJMSReplyTo() != null) {
//					//System.out.println("tm.getJMSReplyTo().toString() = " + tm.getJMSReplyTo().toString());
//					//if("producer".equals("name")) sendMessage((Queue)tm.getJMSReplyTo(),tm.getText());
//				}

				//System.out.println("Pt: " + req.getPathInfo());
				//System.out.println("Received: " + contents.toString());
				String endpoint = message.getStringProperty(PROP_ENDPOINT);

				byte[] ret = rpcServer.forwardCall(endpoint, bytes);
				if(ret!=null) {
					sendMessage(bytesMessage.getJMSReplyTo(), ret);
				}
			}catch(JMSException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void sendMessage(Destination to, byte[] msg) {
		try {
			MessageProducer prod = producerSession.createProducer(to);
			BytesMessage tm = producerSession.createBytesMessage();
			tm.writeBytes(msg);
			//tm.setJMSReplyTo(to);

			prod.send(tm);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static final String PROP_ENDPOINT = "endpoint";
}
