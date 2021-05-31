## XML-RPC format

XML-RPC is a remote procedure call (RPC) protocol which uses XML to encode its calls and HTTP as a transport mechanism.
"XML-RPC" also refers generically to the use of XML for remote procedure call,
independently of the specific protocol. 

## Dependencies

To use XML-RPC format add DataTree Adapters and XML-RPC JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.15</version>
</dependency>

<!-- SOJO XML-RPC API -->
<dependency>
    <groupId>net.sf.sojo</groupId>
    <artifactId>sojo</artifactId>
    <version>1.0.14</version>
</dependency>
```

## Reading and writing XML-RPC documents

```java
// Parsing XML-RPC request / response
String xmlRpc = " ... XML-RPC XML document ... ";
Tree document = new Tree(xmlRpc, "xmlrpc");

// Getting / setting values
for (Tree row: document) {
  for (Tree cell: row) {
    ...
  }
}

// Generating XML-RPC method response from Tree
String xmlRpc = document.toString("xmlrpc");

// Generating XML-RPC method call from Tree
document.getMeta().put("method", "methodName");
String xmlRpc = document.toString("xmlrpc");
``` 