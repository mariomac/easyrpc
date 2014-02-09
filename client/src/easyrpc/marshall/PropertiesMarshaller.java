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

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Simple marshall for testing.
 */
public class PropertiesMarshaller {

    public static final String CLASS_NAME = "className";
    public static final String METHOD_NAME = "methodName";
    public static final String RETURN_TYPE = "returnType";
    public static final String NUM_PARAMS = "numParams";
    public static final String PARAM_TYPE_ = "paramType.";
    public static final String PARAM_VALUE_ = "paramValue.";

    public byte[] marshall(Object theProxy, Method thisMethod, Method superClassMethod, Object[] args) throws Throwable {
        Properties p = new Properties();
        p.setProperty(PropertiesMarshaller.CLASS_NAME, thisMethod.getDeclaringClass().getCanonicalName());
        p.setProperty(PropertiesMarshaller.METHOD_NAME, thisMethod.getName());
        p.setProperty(PropertiesMarshaller.RETURN_TYPE, thisMethod.getReturnType().getCanonicalName());
        Class[] params = thisMethod.getParameterTypes();
        if(params != null || params.length == 0) {
            p.setProperty(PropertiesMarshaller.NUM_PARAMS, ""+params.length);
            for(int i = 0 ; i < params.length ; i++) {
                p.setProperty(PARAM_TYPE_+i, params[i].getCanonicalName());
                p.setProperty(PARAM_VALUE_+i, args[i].toString());
            }
        }
        return p.toString().getBytes();
    }
}
