## BSON format

BSON is a computer data interchange format used mainly as a data storage and
network transfer format in the MongoDB database. It is a binary form for
representing simple data structures, associative arrays
(called objects or documents in MongoDB), and various data types of specific interest to MongoDB. 

## Dependencies

Add DataTree Adapters and BSON JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- BSON API -->
<dependency>
    <groupId>de.undercouch</groupId>
    <artifactId>bson4jackson</artifactId>
    <version>2.18.0</version>
</dependency>
```

## Reading and writing BSON documents

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree to BSON (binary):
byte[] bson = document.toBinary("bson");   // 68 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(bson, "bson");
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
