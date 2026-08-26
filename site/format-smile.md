## SMILE format

Smile is a computer data interchange format based on JSON. It can also be considered
as a binary serialization of generic JSON data model, which means that tools that operate
on JSON may be used with Smile as well, as long as proper encoder/decoder exists for tool to use. 

## Dependencies

Add DataTree Adapters and SMILE JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- SMILE API -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-smile</artifactId>
    <version>2.22.2</version>
</dependency>
```

## Reading and writing SMILE documents

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Serialize the Tree to SMILE (binary):
byte[] smile = document.toBinary("smile");   // 43 bytes

// A byte array is not human-readable. To picture the structure, parse it
// back and print it as JSON (the default text format):
Tree reloaded = new Tree(smile, "smile");
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