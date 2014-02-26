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

package easyrpc.unmarshall;

import easyrpc.marshall.PropertiesMarshaller;
import easyrpc.serialization.RPCaller;
import easyrpc.util.TypeManager;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by mmacias on 08/02/14.
 */
public class PropertiesUnmarshaller implements RPCaller {
    public static final String CLASS_NAME = "className";
    public static final String METHOD_NAME = "methodName";
    public static final String RETURN_TYPE = "returnType";
    public static final String RETURN_VALUE = "returnValue";
    public static final String NUM_PARAMS = "numParams";
    public static final String PARAM_TYPE_ = "paramType.";
    public static final String PARAM_VALUE_ = "paramValue.";

    @Override
    public Object deserializeResponse(byte[] response) {
        try {
            Properties p = new Properties();
            p.load(new ByteArrayInputStream(response));
            String returnType = p.getProperty(RETURN_TYPE);
            String returnValue = p.getProperty(RETURN_VALUE);

            return TypeManager.instantiateValue(returnType, returnValue);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serializeCall(Object theProxy, Method thisMethod, Method superClassMethod, Object[] args) throws Throwable {
        Properties p = new Properties();
        p.setProperty(CLASS_NAME, thisMethod.getDeclaringClass().getCanonicalName());
        p.setProperty(METHOD_NAME, thisMethod.getName());
        p.setProperty(RETURN_TYPE, thisMethod.getReturnType().getCanonicalName());
        Class[] params = thisMethod.getParameterTypes();
        if(params != null || params.length == 0) {
            p.setProperty(NUM_PARAMS, ""+params.length);
            for(int i = 0 ; i < params.length ; i++) {
                p.setProperty(PARAM_TYPE_+i, params[i].getCanonicalName());
                p.setProperty(PARAM_VALUE_+i, args[i].toString());
            }
        }
        StringWriter sr = new StringWriter();
        p.store(sr,null);

        return sr.getBuffer().toString().getBytes();
    }
}
