/*
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

import easyrpc.RpcServer;
import easyrpc.client.ClientFactory;
import easyrpc.client.service.HttpClient;
import easyrpc.marshall.PropertiesMarshaller;
import easyrpc.test.Implementation;
import easyrpc.unmarshall.PropertiesUnmarshaller;
import easyrpc.server.service.HttpService;
import easyrpc.test.IFace;

/**
 * Created by mmacias on 08/02/14.
 */
public class Test {
    public static final void main(String[] args) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                RpcServer server = new RpcServer(new HttpService(8080,"/rpc"),new PropertiesUnmarshaller());
                server.addEndpoint(new Implementation());
                server.start();
            }
        }).start();

        Thread.sleep(5000);

        IFace obj = (IFace) new ClientFactory(new HttpClient("localhost", 8080, "/rpc"), new PropertiesMarshaller()).instantiate(IFace.class);

        System.out.println("LLamando a concat: " + obj.concat("left", "right"));
        System.out.println("Llamando a add: " + obj.add(2, 3));
        System.out.println("Sacando algo por pantalla: ");
        obj.doSomeStupidStuff("Hola Mundo!");

    }
}
