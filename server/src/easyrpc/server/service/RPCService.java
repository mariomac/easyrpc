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

package easyrpc.server.service;

import easyrpc.Rpc_Server;

/**
 * Created by mmacias on 09/02/14.
 */
public abstract class RpcService {
    protected Rpc_Server rpcServer;

    public Rpc_Server getRpcServer() {
        return rpcServer;
    }

    public void setRpcServer(Rpc_Server rpcServer) {
        this.rpcServer = rpcServer;
    }

    public abstract void start();

}
