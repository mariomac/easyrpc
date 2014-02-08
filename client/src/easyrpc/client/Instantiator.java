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

import javassist.*;

public class Instantiator {

    public Object instantiate(Class ifaceClass) {
        try {
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
                    if(!m.getReturnType().equals(CtClass.voidType)) {
                        if(m.getReturnType().isPrimitive())
                            sb.append("return 0;");
                        else {
                            sb.append("return null;");
                        }
                    }
                    sb.append("}");
                    nm.setBody(sb.toString());
                    cl.addMethod(nm);
                }
            }

            return cl.toClass().newInstance();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
