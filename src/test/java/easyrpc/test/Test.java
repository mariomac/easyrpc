package easyrpc.test;/*
 * ----------------------------------------------------------------------------
 * This code is distributed under a Beer-Ware license
 * ----------------------------------------------------------------------------
 * Mario Macias wrote this file. Considering this, you can do what the fuck you
 * want: modify it, distribute it, sell it, etc. But you MUST always credit me
 * as the original author of this code. In addition, if we met some day and you
 * think this code was useful to you, you MUST pay me a beer (a good one, if
 * possible) as reward for my contribution.
 *
 * Mario Macias Lloret, 2014
 * ----------------------------------------------------------------------------
 */

import easyrpc.client.ClientFactory;
import easyrpc.client.serialization.JSONCaller;
import easyrpc.client.transport.HttpClient;
import easyrpc.server.RpcServer;
import easyrpc.server.serialization.JSONCallee;
import easyrpc.server.transport.HttpService;
import junit.framework.TestCase;
import org.junit.Ignore;

/**
 * Created by mmacias on 08/02/14.
 */
public class Test extends TestCase {
    RpcServer server;
    IFace obj;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        server = new RpcServer(new HttpService(8080,"/rpc"),new JSONCallee());
        server.addEndpoint(new Implementation());

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        });
        th.start();
        Thread.sleep(2000);
        obj = (IFace) new ClientFactory(new HttpClient("localhost", 8080, "/rpc"), new JSONCaller()).instantiate(IFace.class);

    }

    @org.junit.Test
    public void testBasicHttpCalls() throws Exception {
        //System.out.println("LLamando a concat: " + obj.concat("left", "right"));
        assertEquals(obj.add(2, 3), 5);
        obj.doSomeStupidStuff("Hola Mundo!");
        obj.doSomething();
    }

    @org.junit.Test
    public void testPOJOCalls() throws Exception {
        OtherFake of = new OtherFake();
        of.int1 = 1;
        of.int2 = 2;
        FakeClass ret = obj.getFake(1L,"aeiou",'z',of);
        assertEquals((long)ret.property1, 1L);
        assertEquals(ret.charProperty, 'z');
        assertEquals(ret.stringProperty, "aeiou");
        assertEquals((int)of.int1, 1);
        assertEquals((int)of.int2, 2);
        System.out.println("Mola");

    }





    public void fakeMethod(int a, String b, FakeClass o) {}

    @Ignore
    public void amqpJson() throws Throwable {

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

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        server.stop();

    }
}

