## MessagePack format

MessagePack is an efficient binary serialization format.
It lets you exchange data among multiple languages like JSON. But it's faster and smaller.
Small integers are encoded into a single byte,
and typical short strings require only one extra byte in addition to the strings themselves.

## Dependencies

To use the MessagePack format, just add DataTree Adapters and the
Jackson MessagePack JARs to the classpath: 

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- MESSAGEPACK API -->
<dependency>
    <groupId>org.msgpack</groupId>
    <artifactId>jackson-dataformat-msgpack</artifactId>
    <version>0.9.9</version>
</dependency>
``` 

## Reading and writing using MessagePack adapter

```java
// Parsing MessagePack document
byte[] msgpack = " ... bytes of the MessagePack document ... ";
Tree document = new Tree(msgpack, "msgpack");

// Getting / setting values
InetAddress value = document.get("host").asInetAddress();
document.put("host", InetAddress.getLocalHost());

// Generating MessagePack byte array from Tree
byte[] msgpack = document.toBinary("msgpack");
```

## Required dependencies of MessagePack adapter

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson MsgPack | MsgPackJackson | [group: 'org.msgpack', name: 'jackson-dataformat-msgpack', version: '0.9.9'](https://mvnrepository.com/artifact/org.msgpack/jackson-dataformat-msgpack) | 