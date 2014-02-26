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

import java.lang.reflect.Method;

/**
 * Created by mmacias on 26/02/14.
 */
public interface RPCaller {
    public Object deserializeResponse(byte[] response);
    byte[] serializeCall(Object theProxy, Method thisMethod, Method superClassMethod, Object[] args) throws Throwable;

}
