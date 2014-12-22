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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import easyrpc.error.SerializationException;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by mmacias on 10/03/14.
 */
public class JSONCallee implements RPCallee {
    private static ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public byte[] matchMethod(Object object, byte[] callInfo) {
        try {
            Object returnedObject = null;
            ObjectNode call = (ObjectNode) MAPPER.readTree(callInfo);

            String jsonrpc = call.get("jsonrpc").textValue();
            if (jsonrpc == null || !"2.0".equals(jsonrpc)) {
                throw new SerializationException("'jsonrpc' value must be '2.0' and actually is: '" + jsonrpc + "'");
            }

            String methodName = call.get("method").textValue();
            if (methodName == null)
                throw new SerializationException("The 'method' field must not be null: " + call.toString());

            Class iface = object.getClass();
            for (Method m : iface.getMethods()) {
                if (methodName.equals(m.getName())) {
                    ArrayNode jsParams = (ArrayNode)call.get("params");
                    if (jsParams == null || jsParams.size() == 0) {
                        try {
                            returnedObject = m.invoke(object);
//                            System.out.println("returnedObject = " + returnedObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return returnJsonRpcError(call.get("id"), e);
                        }
                    } else {
//                        System.out.println("methodName = " + methodName);
                        Object[] params = new Object[jsParams.size()];
                        for (int i = 0; i < params.length; i++) {
                            params[i] = MAPPER.convertValue(jsParams.get(i), m.getParameters()[i].getType());
//                            System.out.println("params[i] = " + params[i] + "("+ params[i].getClass().getName() +")");
                        }
                        try {
                            returnedObject = m.invoke(object, params);
//                            System.out.println("returnedObject = " + returnedObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return returnJsonRpcError(call.get("id"), e);
                        }
                    }
                    break;
                }
            }
            ObjectNode jsret = JsonNodeFactory.instance.objectNode();
            jsret.put("jsonrpc", "2.0");
            jsret.put("id", call.get("id").toString());
            if (returnedObject != null) {
                addResult(jsret,returnedObject);
            }

//            System.out.println("jsret.toString() = " + jsret.toString());
            return jsret.toString().getBytes();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final void addResult(ObjectNode json, Object value) {
            switch (value.getClass().getName()) {
                case "java.lang.Integer":
                case "int":
                    json.put("result", (Integer) value);
                    break;
                case "java.lang.Long":
                case "long":
                    json.put("result",(Long) value);
                    break;
                case "java.lang.Character":
                case "char":
                    json.put("result", (java.lang.Character) value);
                    break;
                case "java.lang.Void":
                case "void":
                    throw new IllegalArgumentException("A parameter cannot be of void type");
                case "java.lang.Float":
                case "float":
                    json.put("result", (Float) value);
                    break;
                case "java.lang.Double":
                case "double":
                    json.put("result",(Double) value);
                    break;
                case "java.lang.String":
                    json.put("result", (String) value);
                    break;
                default:
                    // map an object
                    ObjectNode retPojo = json.putPOJO("result", MAPPER.valueToTree(value));
//                    System.out.println("retPojo.toString() = " + retPojo.toString());
            }
        }

    byte[] returnJsonRpcError(Object id, Exception e) {
        ObjectNode object = JsonNodeFactory.instance.objectNode();
        object.put("jsonrpc", "2.0");
        object.put("id", id.toString());
        ObjectNode error = object.putObject("error");
        error.put("code", -1);
        error.put("message", e.getClass().getCanonicalName() + " : " + e.getMessage());
        object.put("error", error);

        return object.toString().getBytes();
    }
}
