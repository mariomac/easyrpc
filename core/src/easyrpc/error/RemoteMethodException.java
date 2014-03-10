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

package easyrpc.error;

/**
 * Created by mmacias on 10/03/14.
 */
public class RemoteMethodException extends RuntimeException {
    public RemoteMethodException() {
    }

    public RemoteMethodException(String message) {
        super(message);
    }

    public RemoteMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteMethodException(Throwable cause) {
        super(cause);
    }

    public RemoteMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
