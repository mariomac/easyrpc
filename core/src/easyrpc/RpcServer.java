package easyrpc;
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

import easyrpc.marshall.PropertiesMarshaller;
import easyrpc.server.service.RpcService;
import easyrpc.unmarshall.PropertiesUnmarshaller;
import easyrpc.util.TypeManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mmacias on 08/02/14.
 */
public class RpcServer {
    // Key : name of the implementing interfaces
    private Map<String,Object> endpoints = new TreeMap<String, Object>();

    protected RpcService serviceLayer;
    protected PropertiesUnmarshaller unmarshaller;

    public RpcServer(RpcService serviceLayer, PropertiesUnmarshaller unmarshaller) {
        this.serviceLayer = serviceLayer;
        this.unmarshaller = unmarshaller;

        serviceLayer.setRpcServer(this);
    }

    public Object getEndpoint(Class iface) {
        return endpoints.get(iface.getCanonicalName());
    }

    public void addEndpoint(Object o) {
        Class c = o.getClass();
        Class[] interfaces = c.getInterfaces();
        for(Class iface : interfaces) {
            if(endpoints.get(iface.getCanonicalName()) != null) {
                Logger.getLogger(RpcServer.class.getCanonicalName()).log(Level.WARNING,
                        "Registering class " + c.getCanonicalName() + ". Interface "
                                + iface.getCanonicalName() + " was already registered. Overwriting");
            }
            endpoints.put(iface.getCanonicalName(), o);
        }
    }

    public void start() {
        serviceLayer.start();
    }

    public PropertiesUnmarshaller getUnmarshaller() {
        return unmarshaller;
    }


    public byte[] forwardCall(String endpoint, byte[] data) throws ClassNotFoundException, IOException, InvocationTargetException, IllegalAccessException {
        Object o = endpoints.get(endpoint);
        if(o == null) throw new RuntimeException("Endpoint " + endpoint + " does not exist");
        Properties p = new Properties();
        p.load(new ByteArrayInputStream(data));

        // todo: meter todo esto en PropertiesUnmarshaller
        Class iface = Class.forName(endpoint);
        Object returnedObject = null;
        //System.out.println("Clase a analizar: " + iface.getCanonicalName());
        for(Method m : iface.getMethods()) {
            if(m.getName().equals(p.getProperty(PropertiesMarshaller.METHOD_NAME))) {
                //System.out.println("Llamando a " + m.getName());
                int numParams = Integer.valueOf(p.getProperty(PropertiesMarshaller.NUM_PARAMS));
                if(numParams == 0) {
                    m.invoke(o);
                } else {
                    Object[] params = new Object[numParams];
                    for(int i = 0 ; i < numParams ; i++) {
                        params[i] = TypeManager.instantiateValue(p.getProperty(PropertiesMarshaller.PARAM_TYPE_+i),p.getProperty(PropertiesMarshaller.PARAM_VALUE_+i));
                    }
                    returnedObject = m.invoke(o, params);
                }
                break;
            }
        }

        //o.getClass()
        try {
            // mirar cuando la propiedad es un tipo primitivo: int, double, void...
            Properties rp = new Properties();
            String returnType = p.getProperty(PropertiesMarshaller.RETURN_TYPE);
            rp.setProperty(PropertiesMarshaller.RETURN_TYPE,returnType);
            if(returnedObject != null) {
                rp.setProperty(PropertiesMarshaller.RETURN_VALUE,returnedObject.toString());
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
