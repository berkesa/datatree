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
    <version>1.0.15</version>
</dependency>

<!-- BSON API -->
<dependency>
    <groupId>de.undercouch</groupId>
    <artifactId>bson4jackson</artifactId>
    <version>2.12.0</version>
</dependency>
```

## Reading and writing BSON documents

```java
// Parsing BSON document
byte[] bson = " ... bytes of the BSON document ... ";
Tree document = new Tree(bson, "bson");

// Getting / setting values
int value = document.get("intValue", 0);
document.put("intValue", 1);

// Generating BSON byte array from Tree
byte[] bson = document.toBinary("bson");
```
