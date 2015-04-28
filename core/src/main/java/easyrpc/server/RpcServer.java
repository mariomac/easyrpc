package easyrpc.server;
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

import easyrpc.server.serialization.RPCallee;
import easyrpc.server.protocol.RpcService;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mmacias on 08/02/14.
 */
public class RpcServer {
    // Key : name of the implementing interfaces
    private Map<String,Object> endpoints = new TreeMap<String, Object>();

    protected RpcService protocolLayer;
    protected RPCallee serializer;

    public RpcServer(RpcService protocolLayer, RPCallee serializer) {
        this.protocolLayer = protocolLayer;
        this.serializer = serializer;

        protocolLayer.setRpcServer(this);
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
        protocolLayer.start();
    }

    public void stop() { protocolLayer.stop(); }

    public byte[] forwardCall(String endpoint, byte[] data) {
        //System.out.println("endpoint = " + endpoint);
        //System.out.println("endpoints = " + endpoints.keySet());
        Object o = endpoints.get(endpoint);
        if(o == null) throw new RuntimeException("Endpoint " + endpoint + " does not exist");

        return serializer.matchMethod(o, data);

    }
}
