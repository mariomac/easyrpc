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
import easyrpc.formatter.PropertiesFormatter;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public class ClientFactory {

    HttpClient client;
    PropertiesFormatter formatter;

    public ClientFactory(HttpClient client) {
        this.client = client;
        formatter = new PropertiesFormatter(client);
    }

    public Object instantiate(Class ifaceClass) {
        try {
            ProxyFactory factory = new ProxyFactory();
            factory.setInterfaces(new Class[] { ifaceClass });
            Class cl = factory.createClass();
            Object instance = cl.newInstance(); // an object implementing the interface
            ((Proxy)instance).setHandler(formatter);
            return instance;


            /*factory.setHandler(new PropertiesFormatter());

            Class c = factory.createClass();

            Object object = c.newInstance();
            ClassPool cp = ClassPool.getDefault();
            CtClass iface = cp.get(ifaceClass.getName());
            CtClass cl = cp.makeClass(iface.getName()+"Impl");
            cl.addInterface(iface);
            cl.addConstructor(CtNewConstructor.defaultConstructor(cl));
            for(CtMethod m : cl.getMethods()) {
                if(m.getDeclaringClass() == iface) {
                    System.out.println(m.getLongName());
                    CtMethod nm = CtNewMethod.copy(m, cl, null);
                    StringBuilder sb = new StringBuilder("{ System.out.println(\"Se ha llamado a "+m.getName()+"\");");
                    sb.append("}");
                    nm.setBody(sb.toString());
                    cl.addMethod(nm);
                }
            }

            return cl.toClass().newInstance();*/
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
