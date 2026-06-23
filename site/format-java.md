## Java Object Serializator/Deserializator support

Java provides a mechanism, called object serialization where an object can be represented as a sequence of bytes that includes the object's data as well as information about the object's type and the types of data stored in the object.
After a serialized object has been written into a file, it can be read from the file and deserialized that is, the type information and bytes that represent the object and its data can be used to recreate the object in memory.
Most impressive is that the entire process is JVM independent, meaning an object can be serialized on one platform and deserialized on an entirely different platform.

## Dependencies

Add DataTree Adapters to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Reading and writing serialized data structures

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree with standard Java object serialization (binary):
byte[] bytes = document.toBinary("java");   // 302 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(bytes, "java");
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