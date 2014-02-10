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
 *  Mario Macias Lloret, 2014
 * ----------------------------------------------------------------------------
 */

package easyrpc.client;

import easyrpc.client.service.HttpClient;
import easyrpc.marshall.PropertiesMarshaller;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;

public class ClientFactory {

    HttpClient client;
    PropertiesMarshaller marshaller;

    public ClientFactory(HttpClient client, PropertiesMarshaller marshaller) {
        this.client = client;
        this.marshaller = marshaller;
    }


    public Object instantiate(Class ifaceClass) {
        try {
            ProxyFactory factory = new ProxyFactory();
            factory.setInterfaces(new Class[] { ifaceClass });
            Class cl = factory.createClass();
            Object instance = cl.newInstance(); // an object implementing the interface
            ((Proxy)instance).setHandler(new MethodHandlerImpl(ifaceClass.getCanonicalName()));
            return instance;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    class MethodHandlerImpl implements MethodHandler {
        String interfaceName;

        MethodHandlerImpl(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        @Override
        public Object invoke(Object theProxy, Method thisMethod, Method superClassMethod, Object[] args) throws Throwable {
            if(superClassMethod == null) {
                byte[] msg = marshaller.marshall(theProxy,thisMethod,superClassMethod,args);

                System.out.println("Enviando " + new String(msg));
                byte[] ret = client.sendMessage(interfaceName,msg);
                System.out.println("new String(ret) = " + (ret == null ? null : new String(ret)));
                return marshaller.unmarshallResponse(ret);
                /*if(thisMethod.getReturnType().isPrimitive())
                    return 0;
                return null;*/
            } else {
                return superClassMethod.invoke(theProxy, args);
            }
        }
    }
}
