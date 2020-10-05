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
    <version>1.0.14</version>
</dependency>

<!-- ION API -->
<dependency>
    <groupId>software.amazon.ion</groupId>
    <artifactId>ion-java</artifactId>
    <version>1.5.1</version>
</dependency>
```

## Reading and writing ION documents

```java
// Parsing ION document
byte[] ion = " ... bytes of the ION document ... ";
Tree document = new Tree(ion, "ion");

// Getting / setting values
UUID value = document.get("id").asUUID();
document.put("id", UUID.randomUUID());

// Generating ION byte array from Tree
byte[] ion = document.toBinary("ion");
``` 