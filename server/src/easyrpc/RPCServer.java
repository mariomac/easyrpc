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

import easyrpc.reader.PropertiesReader;
import easyrpc.server.service.HttpService;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mmacias on 08/02/14.
 */
public class RPCServer {
    // Key : name of the implementing interfaces
    private Map<String,Object> endpoints = new TreeMap<String, Object>();

    protected HttpService serviceLayer;
    protected PropertiesReader dataReader;

    public RPCServer(HttpService serviceLayer, PropertiesReader dataReader) {
        this.serviceLayer = serviceLayer;
        this.dataReader = dataReader;
    }

    public Object getEndpoint(Class iface) {
        return endpoints.get(iface.getCanonicalName());
    }

    public void addEndpoint(Object o) {
        Class c = o.getClass();
        Class[] interfaces = c.getInterfaces();
        for(Class iface : interfaces) {
            if(endpoints.get(iface.getCanonicalName()) != null) {
                Logger.getLogger(RPCServer.class.getCanonicalName()).log(Level.WARNING,
                        "Registering class " + c.getCanonicalName() + ". Interface "
                                + iface.getCanonicalName() + " was already registered. Overwriting");
            }
            endpoints.put(iface.getCanonicalName(), o);
        }
    }

    public void start() {
        serviceLayer.start();
    }

}
