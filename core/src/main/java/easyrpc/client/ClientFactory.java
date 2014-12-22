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

import easyrpc.client.serialization.RPCaller;
import easyrpc.server.protocol.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientFactory {

    RpcClient client;
    RPCaller caller;

    public ClientFactory(RpcClient client, RPCaller caller) {
        this.client = client;
        this.caller = caller;
    }


    public Object instantiate(Class ifaceClass) {
        try {
            Object instance = Proxy.newProxyInstance(ifaceClass.getClassLoader(),new Class[]{ifaceClass},
                            new MethodHandlerImpl(ifaceClass.getCanonicalName()));
            return instance;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    class MethodHandlerImpl implements InvocationHandler {
        String interfaceName;

        MethodHandlerImpl(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        @Override
        public Object invoke(Object theProxy, Method thisMethod, Object[] args) throws Throwable {
            byte[] msg = caller.serializeCall(theProxy, thisMethod, args);

            //System.out.println("Enviando " + new String(msg));
            byte[] ret = client.sendMessage(interfaceName,msg);
            //System.out.println("new String(ret) = " + (ret == null ? null : new String(ret)));
            return caller.deserializeResponse(thisMethod.getReturnType(), ret);

        }
    }
}
