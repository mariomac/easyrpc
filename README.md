easyrpc
=======

Automatic RPC stubs generation for (initially) Java.

Features:
* Easy to adopt
* Client-side and server-side stubs generation at runtime
* Will support multiple communication protocols (AMQP, Http...)
* Will support multiple data formatting: JSON, Protocol Buffers, Thrift, XML...
* Efficient communication

Here is an previous idea about how it will work:

Server side
-----------

**Step 1**: create a service interface

    public interface IFace {
        int add(int a, int b);
        String concat(String s1, String s2);
        void doSomeStupidStuff(String str);
    }
  
**Step 2**: implement the interface

    public class Implementation implements IFace {
        @Override
        public int add(int a, int b) {
            return a+b;
        }
        @Override
        public String concat(String s1, String s2) {
            return s1+s2;
        }
        @Override
        public void doSomeStupidStuff(String str) {
            System.out.println("str = " + str);
        }
    }
    
**Step 3**: instantiate the server (in the example, it enables the communication through HTTP and marshalls the RPC info within java properties):

    RpcServer server = new RpcServer(
                        new HttpService(8080,"/rpc"),
                        new PropertiesUnmarshaller());
    
**Step 4**: register the service through the implementing class and start the service

    server.addEndpoint(new Implementation());
    server.start();
    
Client side
-----------

**Step 1**: get the interface IFace. This must be provided by the service provider by sharing, for example, the source code or a compiled jar binary.

**Step 2**: instantiate the runtime stub generator for the client (in the example, it enables the communication through HTTP and marshalls the RPC info within java properties):

    ClientFactory stubGenerator = new ClientFactory(
                                    new HttpClient("server.address.com", 8080, "/rpc"),
                                    new PropertiesMarshaller());
                                    
**Step 3**: generate a client class:

    IFace obj = (IFace) stubGenerator.instantiate(IFace.class);
    
**Step 4**: use the service:

    int result = obj.add(2,3);
