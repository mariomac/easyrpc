package easyrpc.amqpjson;

import easyrpc.client.ClientFactory;
import easyrpc.client.protocol.amqp.AmqpClient;
import easyrpc.client.serialization.jsonrpc.JSONCaller;
import easyrpc.server.RpcServer;
import easyrpc.server.protocol.amqp.AmqpService;
import easyrpc.server.serialization.jsonrpc.JSONCallee;
import easyrpc.httpjson.FakeClass;
import easyrpc.httpjson.IFace;
import easyrpc.httpjson.Implementation;
import easyrpc.httpjson.OtherFake;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by mmacias on 23/12/14.
 */

public class TestAmqpJson extends TestCase {
	RpcServer server;
	IFace iface;

	private static final String QUEUE_NAME = "TestQueue";
	@Override
	public void setUp() throws Exception {
		super.setUp();
		URI brokerUri = new URI("amqp://localhost:5672"); //new URI("amqp://guest:guest@localhost:5672?clientid=test-client&remote-host=default");
		server = new RpcServer(
				new AmqpService(brokerUri, QUEUE_NAME),
				new JSONCallee());

		server.addEndpoint(new Implementation());

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				server.start();
			}
		});
		th.start();
		Thread.sleep(2000);
		iface = new ClientFactory(new AmqpClient(brokerUri,QUEUE_NAME), new JSONCaller()).instantiate(IFace.class);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		server.stop();
	}

	@org.junit.Test
	public void testBasicCalls() throws Exception {
		assertEquals(iface.add(2, 3), 5);
	}

	@org.junit.Test
	public void testPOJOCalls() throws Exception {
		OtherFake of = new OtherFake();
		of.int1 = 1;
		of.int2 = 2;
		FakeClass ret = iface.getFake(1L,"aeiou",'z',of);
		assertEquals((long)ret.property1, 1L);
		assertEquals(ret.charProperty, 'z');
		assertEquals(ret.stringProperty, "aeiou");
		assertEquals((int)of.int1, 1);
		assertEquals((int)of.int2, 2);
//        System.out.println("Mola");

	}

	@org.junit.Test
	public void testArrays() {
		int[] in = { 1, 2, 3, 4, 5, 6, -2 };
		int[] out = iface.doubleArray(in);
		for(int i = 0 ; i < in.length ; i++) {
			assertEquals(in[i], out[i] / 2);
		}
	}

	@org.junit.Test
	public void testList() {
		int[] in = new int[] { 1,2,3,4,5,6,10,222 };
		List<String> out = iface.asString(in);
		for(int i = 0 ; i < out.size() ; i++) {
			assertEquals((int)Integer.valueOf(out.get(i)), in[i]);
		}
	}

	@org.junit.Test
	public void testMaps() {
		Map<String,Integer> wc = iface.wordHistogram("el perro de san roque no tiene rabo perro perro rabo");
		assertNull(wc.get("tralariero"));
		assertEquals((int) wc.get("el"), 1);
		assertEquals((int)wc.get("perro"),3);
		assertEquals((int)wc.get("de"),1);
		assertEquals((int)wc.get("san"),1);
		assertEquals((int)wc.get("roque"),1);
		assertEquals((int)wc.get("no"),1);
		assertEquals((int)wc.get("tiene"),1);
		assertEquals((int)wc.get("rabo"),2);
	}

	public void fakeMethod(int a, String b, FakeClass o) {}

	@org.junit.Test
	public void testAmqpJson() throws Throwable {

		FakeClass fc = new FakeClass();
		fc.property1 = 1L; fc.stringProperty = "hola"; fc.charProperty='c';
		fc.other = new OtherFake();
		fc.other.int1 = 125;
		fc.other.int2 = null;

		JSONCaller caller = new JSONCaller();
		byte[] json = caller.serializeCall((Object)null,this.getClass().getMethod("fakeMethod", int.class, String.class, FakeClass.class),
				new Object[] { 666, "777", fc });

		assertTrue(new String(json).startsWith("{\"jsonrpc\":\"2.0\",\"method\":\"fakeMethod\",\"params\":[666,\"777\",{\"property1\":1,\"stringProperty\":\"hola\",\"charProperty\":\"c\",\"other\":{\"int1\":125,\"int2\":null}}],\"id\":"));
	}

	@org.junit.Test
	public void testWorksAfterNonExistentInterfaceCall() throws Exception {
//		boolean exception = false;
//		try {
//			URI brokerUri = new URI("amqp://localhost:5672"); //new URI("amqp://guest:guest@localhost:5672?clientid=test-client&remote-host=default");
//			List l = new ClientFactory(new AmqpClient(brokerUri,QUEUE_NAME), new JSONCaller()).instantiate(List.class);
//			l.clear();
//		} catch(Exception e) {
//			exception = true;
//			e.printStackTrace();
//		}
//
//		assertTrue(exception);

		assertEquals(iface.add(66,134),200);
	}


}
