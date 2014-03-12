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

package easyrpc.server.serialization;

import easyrpc.error.RemoteMethodException;
import easyrpc.error.SerializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mmacias on 10/03/14.
 */
public class JSONCallee implements RPCallee {
    @Override
    public byte[] matchMethod(Object object, byte[] callInfo) {
        Object returnedObject = null;
        JSONObject call = new JSONObject(new String(callInfo));
        String jsonrpc = call.optString("jsonrpc");
        if(jsonrpc == null || !"2.0".equals(jsonrpc)) {
            throw new SerializationException("'jsonrpc' value must be '2.0' and actually is '"+jsonrpc+"'");
        }
        String methodName = call.optString("method");
        if(methodName == null) throw new SerializationException("The 'method' field must not be null: " + call.toString());

        Class iface = object.getClass();
        for(Method m : iface.getMethods()) {
            if(methodName.equals(m.getName())) {
                JSONArray jsParams = call.optJSONArray("params");
                if(jsParams == null || jsParams.length() == 0) {
                    try {
                        returnedObject = m.invoke(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return returnJsonRpcError(call.opt("id"),e);
                    }
                } else {
                    Object[] params = new Object[jsParams.length()];
                    for(int i = 0 ; i < params.length ; i++) {
                        params[i] = jsParams.get(i);
                    }
                    try {
                        returnedObject = m.invoke(object,params);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return returnJsonRpcError(call.opt("id"),e);
                    }
                }
                break;
            }
        }
        JSONObject jsret = new JSONObject();
        jsret.accumulate("jsonrpc", "2.0");
        jsret.accumulate("id",call.opt("id"));
        if(returnedObject != null) {
            jsret.accumulate("result",returnedObject);
        }


        return jsret.toString().getBytes();
    }

    byte[] returnJsonRpcError(Object id, Exception e) {
        JSONObject object = new JSONObject();
        object.accumulate("jsonrpc","2.0");
        object.accumulate("id",id);
        JSONObject error = new JSONObject();
        error.accumulate("code",-1);
        error.accumulate("message", e.getClass().getCanonicalName() +" : " + e.getMessage());
        object.accumulate("error", error);

        return object.toString().getBytes();
    }
}
