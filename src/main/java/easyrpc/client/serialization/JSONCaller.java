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

package easyrpc.client.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import easyrpc.error.RemoteMethodException;
import easyrpc.error.SerializationException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by mmacias on 10/03/14.
 */
public class JSONCaller implements RPCaller {
    private static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Object deserializeResponse(Class returnType, byte[] response) {
        try {
            ObjectNode resp = (ObjectNode) MAPPER.readTree(response);

            String jsonversion = resp.get("jsonrpc").textValue();
            if (!"2.0".equals(jsonversion)) {
                throw new SerializationException("'jsonrpc' value must be '2.0' and actually is '" + jsonversion + "'");
            }

            // todo: differentiate exceptions as defined in the interfaces
            if (resp.has("error")) {
                JsonNode error = resp.get("error");
                throw new RemoteMethodException(error.toString());
            }
            System.out.println("resp.get(\"result\").toString() = " + resp.toString());
            if(!returnType.equals(Void.class) && !returnType.equals(void.class)) {
                Object result = MAPPER.treeToValue(resp.get("result"), returnType);
                return result;
            } else {
                return null;
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serializeCall(Object theProxy, Method thisMethod, Object[] args) throws Throwable {
        ObjectNode sc = JsonNodeFactory.instance.objectNode();
        sc.put("jsonrpc","2.0");
        sc.put("method",thisMethod.getName());

        if(args != null && args.length > 0) {
            ArrayNode params = JsonNodeFactory.instance.arrayNode();
            for(Object arg : args) {
                addType(arg,params);
            }
            sc.set("params",params);
        }
        sc.put("id", UUID.randomUUID().toString());

        String json = sc.toString();
        return json.getBytes();
    }

    private static void addType(Object value, ArrayNode arr) {
        switch (value.getClass().getName()) {
            case "java.lang.Integer":
            case "int":
                arr.add((Integer) value);
                break;
            case "java.lang.Long":
            case "long":
                arr.add((Long) value);
                break;
            case "java.lang.Character":
            case "char":
                arr.add((java.lang.Character) value);
                break;
            case "java.lang.Void":
            case "void":
                throw new IllegalArgumentException("A parameter cannot be of void type");
            case "java.lang.Float":
            case "float":
                arr.add((Float) value);
                break;
            case "java.lang.Double":
            case "double":
                arr.add((Double) value);
                break;
            case "java.lang.String":
                arr.add((String) value);
                break;
            default:
                // map an object
                arr.add(MAPPER.valueToTree(value));
        }
    }
}
