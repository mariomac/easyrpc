easyrpc
=======

Automatic RPC stubs generation for (at the moment) Java.

Features:
* Easy to adopt. No need for configuration files. Just import libraries and use them.
* Client-side and server-side stubs generation at runtime
* Communication through HTTP and AMQP 1.0. Extensible to more communication protocols.
* JSON-RPC 2.0 serialization. Extensible to other serialization (SOAP, ProtoBuf...)
* Serializes POJOs and Collections thanks to [Jackson Databind](https://github.com/FasterXML/jackson-databind/)

How it works:

Server side
-----------

**Step 1**: create a service interface
```java
public interface IFace {
    int add(int a, int b);
    String concat(String s1, String s2);
    int[] doubleArray(int[] arr);
}
```  
**Step 2**: implement the interface
```java
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
    public int[] doubleArray(int[] arr) {
        for(int i = 0 ; i < arr.length ; i++) {
            arr[i] = arr[i] * 2;
        }
        return arr;
    }
}
```
    
**Step 3**: instantiate the server.

E.g. JSON-RPC 2.0 over HTTP:

```java
RpcServer server = new RpcServer(
                        new HttpService(8080,"/rpc"),
                        new JSONCallee());
```    

E.g. JSON-RPC 2.0 over AMQP:

```java
RpcServer = new RpcServer(
	            	new AmqpService(new URI("amqp://localhost:5672"), "rpcQueue"),
				    new JSONCallee());
```

**Step 4**: register the service through the implementing class and start the service
```java
server.addEndpoint(new Implementation());
server.start();
```    
Client side
-----------

**Step 1**: get the interface IFace. This must be provided by the service provider by sharing, for example, the source code or a compiled jar binary.

**Step 2**: instantiate the runtime stub generator for the client (in the example, it enables the communication through HTTP and marshalls the RPC info in JSON-RPC 2.0):
```java
ClientFactory stubGenerator = new ClientFactory(
                                new HttpClient("server.address.com", 8080, "/rpc"),
                                new JSONCaller());
```                                    
**Step 3**: generate a client class:
```java
IFace obj = stubGenerator.instantiate(IFace.class);
```
**Step 4**: use the service:
```java
int result = obj.add(2,3);
```

Known Limitations
-----------------

* The method names for every interface must be unique.

* If the service modifies the value of an object passed as parameter, the client won't see such changes.

* AMQP: not ready for sending messages at the same time from multiple thread. At the moment they are internally synchronized to be sent serially.


Si te gustan mis aportaciones a github, quizás te gustará mi libro [Del bit a la Nube](http://www.xaas.guru/del-bit-a-la-nube/)
