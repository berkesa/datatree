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
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree to MessagePack (binary):
byte[] msgpack = document.toBinary("msgpack");   // 36 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(msgpack, "msgpack");
System.out.println(reloaded.toString(true));
```

The reparsed document as JSON:

```json
{
  "name":"Alice",
  "age":30,
  "languages":[
    "Java",
    "Go"
  ]
}
```

## Required dependencies of MessagePack adapter

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson MsgPack | MsgPackJackson | [group: 'org.msgpack', name: 'jackson-dataformat-msgpack', version: '0.9.9'](https://mvnrepository.com/artifact/org.msgpack/jackson-dataformat-msgpack) | 