package easyrpc.server.protocol;

/**
 * Created by mmacias on 22/12/14.
 */
public abstract class RpcClient {
	public abstract byte[] sendMessage(String endpoint, byte[] info);
}
