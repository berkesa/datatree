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
    <version>2.1.0</version>
</dependency>

<!-- KRYO API -->
<dependency>
    <groupId>com.esotericsoftware</groupId>
    <artifactId>kryo</artifactId>
    <version>5.6.2</version>
</dependency>
``` 

## Reading and writing using Kryo adapter

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree to Kryo (binary):
byte[] bytes = document.toBinary("kryo");   // 84 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(bytes, "kryo");
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