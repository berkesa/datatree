## CBOR format

CBOR is based on the wildly successful JSON data model: numbers,
strings, arrays, maps (called objects in JSON), and a few values such as
false, true, and null. One of the major practical wins of JSON is that
successful data interchange is possible without casting a schema in concrete.
This works much better in a world where both ends of a communication
relationship may be evolving at high speed. 

## Dependencies

Add DataTree Adapters and CBOR JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- CBOR API -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-cbor</artifactId>
    <version>2.22.2</version>
</dependency>
``` 

## Reading and writing CBOR documents

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree to CBOR (binary):
byte[] cbor = document.toBinary("cbor");   // 38 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(cbor, "cbor");
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