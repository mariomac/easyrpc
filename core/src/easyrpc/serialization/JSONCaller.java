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

package easyrpc.serialization;

import easyrpc.error.RemoteMethodException;
import easyrpc.error.SerializationException;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by mmacias on 10/03/14.
 */
public class JSONCaller implements RPCaller {
    private int id = 0;

    @Override
    public Object deserializeResponse(Class returnType, byte[] response) {
        JSONObject resp = new JSONObject(new String(response));
        String jsonversion = resp.optString("jsonrpc");
        if(!"2.0".equals(jsonversion)) {
            throw new SerializationException("'jsonrpc' value must be '2.0' and actually is '"+jsonversion+"'");
        }

        // todo: differentiate exceptions as defined in the interfaces
        if(resp.has("error")) {
            JSONObject error = resp.optJSONObject("error");
            throw new RemoteMethodException(error.toString());
        }
        // todo: convert JAXB XMLs and JSON POJOs
        Object result = resp.opt("result");
        return result;
    }

    @Override
    public byte[] serializeCall(Object theProxy, Method thisMethod, Object[] args) throws Throwable {
        JSONObject sc = new JSONObject();
        sc.accumulate("jsonrpc","2.0");
        sc.accumulate("method",thisMethod.getName());
        if(args != null && args.length > 0) {
            for(Object arg : args) {
                sc.append("params", convertType(arg));
            }
        }
        sc.accumulate("id",id++);

        String json = sc.toString();
        System.out.println("json = " + json);
        return json.getBytes();
    }

    private static Object convertType(Object arg) {
        return arg;
    }
}
