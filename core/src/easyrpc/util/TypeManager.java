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

package easyrpc.util;

/**
 * Created by mmacias on 14/02/14.
 */
public class TypeManager {
    public static Object getNullValue(String typeName) {
        switch(typeName) {
            case "int":
            case "char":
            case "long":
            case "short":
            case "byte":
            case "float":
            case "double":
                return 0;
            default:
                return null;
        }
    }

    public static Object instantiateValue(String typeName, String value) throws TypeNotPresentException {
        if(value == null
                || value.trim().equals("")
                || value.trim().equals("null")) {
            return TypeManager.getNullValue(typeName);
        }
        switch (typeName) {
            case "java.lang.Integer":
            case "int":
                return Integer.valueOf(value);
            case "java.lang.Long":
            case "long":
                return Long.valueOf(value);
            case "java.lang.Char":
            case "char":
                return Character.valueOf(value.charAt(0));
            case "java.lang.Void":
            case "void":
                return 0;
            case "java.lang.Float":
            case "float":
                return Float.valueOf(value);
            case "java.lang.Double":
            case "double":
                return Double.valueOf(value);
            case "java.lang.String":
                return String.valueOf(value);
            default:
                throw new TypeNotPresentException("The return type of the method is not supported: " + typeName,null);
        }

    }
}
