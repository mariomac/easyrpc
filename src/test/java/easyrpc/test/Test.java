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
import easyrpc.client.protocol.http.HttpClient;
import easyrpc.client.serialization.jsonrpc.JSONCaller;
import easyrpc.server.RpcServer;
import easyrpc.server.serialization.jsonrpc.JSONCallee;
import easyrpc.server.protocol.http.HttpService;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

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
//        System.out.println("Mola");

    }

    @org.junit.Test
    public void testArrays() {
        int[] in = { 1, 2, 3, 4, 5, 6, -2 };
        int[] out = obj.doubleArray(in);
        for(int i = 0 ; i < in.length ; i++) {
            assertEquals(in[i], out[i] / 2);
        }
    }

    @org.junit.Test
    public void testList() {
        int[] in = new int[] { 1,2,3,4,5,6,10,222 };
        List<String> out = obj.asString(in);
        for(int i = 0 ; i < out.size() ; i++) {
            assertEquals((int)Integer.valueOf(out.get(i)), in[i]);
        }
    }

    @org.junit.Test
    public void testMaps() {
        Map<String,Integer> wc = obj.wordHistogram("el perro de san roque no tiene rabo perro perro rabo");
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

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        server.stop();

    }
}

