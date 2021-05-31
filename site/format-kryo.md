## KRYO format

Kryo is a fast and efficient binary object graph serialization framework for Java.
The goals of the project are high speed, low size, and an easy to use API.
The project is useful any time objects need to be persisted,
whether to a file, database, or over the network.
Kryo can also perform automatic deep and shallow copying/cloning.
This is direct copying from object to object, not object to bytes to object.

## Dependencies

To use the Kryo API, add Kryo Adapters and Kryo JARs to the classpath: 

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.15</version>
</dependency>

<!-- KRYO API -->
<dependency>
    <groupId>com.esotericsoftware</groupId>
    <artifactId>kryo</artifactId>
    <version>5.1.1</version>
</dependency>
``` 

## Reading and writing using Kryo adapter

```java
// Parsing Kryo document
byte[] bytes = " ... bytes of the Kryo document ... ";
Tree document = new Tree(bytes, "kryo");

// Getting / setting values
InetAddress value = document.get("host").asInetAddress();
document.put("host", InetAddress.getLocalHost());

// Generating Kryo byte array from Tree
byte[] bytes = document.toBinary("kryo");
```