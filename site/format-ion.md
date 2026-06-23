## Amazon ION format

Amazon Ion is a richly-typed, self-describing, hierarchical data serialization format
offering interchangeable binary and text representations. The text format (a superset of JSON)
is easy to read and author, supporting rapid prototyping. The binary representation is efficient
to store, transmit, and skip-scan parse. The rich type system provides unambiguous semantics for
long-term preservation of business data which can survive multiple generations of software evolution. 

## Dependencies

Add DataTree Adapters and ION JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- ION API -->
<dependency>
    <groupId>com.amazon.ion</groupId>
    <artifactId>ion-java</artifactId>
    <version>1.11.10</version>
</dependency>
```

## Reading and writing ION documents

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree to ION (binary):
byte[] ion = document.toBinary("ion");   // 49 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(ion, "ion");
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