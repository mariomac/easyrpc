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

package easyrpc.marshall;

import easyrpc.serialization.RPCallee;
import easyrpc.unmarshall.PropertiesUnmarshaller;
import easyrpc.util.TypeManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Simple marshall for testing.
 */
public class PropertiesMarshaller implements RPCallee {

    @Override
    public byte[] matchMethod(Object object, byte[] callInfo) {
        Properties p = new Properties();
        Object returnedObject = null;

        try {
            p.load(new ByteArrayInputStream(callInfo));

            // todo: meter todo esto en PropertiesUnmarshaller
            Class iface = object.getClass(); // Class.forname(endpoint)
            //System.out.println("Clase a analizar: " + iface.getCanonicalName());
            for(Method m : iface.getMethods()) {
                if(m.getName().equals(p.getProperty(PropertiesUnmarshaller.METHOD_NAME))) {
                    //System.out.println("Llamando a " + m.getName());
                    int numParams = Integer.valueOf(p.getProperty(PropertiesUnmarshaller.NUM_PARAMS));
                    if(numParams == 0) {
                        m.invoke(object);
                    } else {
                        Object[] params = new Object[numParams];
                        for(int i = 0 ; i < numParams ; i++) {
                            params[i] = TypeManager.instantiateValue(p.getProperty(PropertiesUnmarshaller.PARAM_TYPE_+i),p.getProperty(PropertiesUnmarshaller.PARAM_VALUE_+i));
                        }
                        returnedObject = m.invoke(object, params);
                    }
                    break;
                }
            }
        } catch(IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        //o.getClass()
        try {
            // mirar cuando la propiedad es un tipo primitivo: int, double, void...
            Properties rp = new Properties();
            String returnType = p.getProperty(PropertiesUnmarshaller.RETURN_TYPE);
            rp.setProperty(PropertiesUnmarshaller.RETURN_TYPE,returnType);
            if(returnedObject != null) {
                rp.setProperty(PropertiesUnmarshaller.RETURN_VALUE,returnedObject.toString());
            }
            StringWriter sw = new StringWriter();
            rp.store(sw,null);
            return sw.getBuffer().toString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
