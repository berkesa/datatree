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
    <version>1.0.13</version>
</dependency>

<!-- CBOR API -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-cbor</artifactId>
    <version>2.10.1</version>
</dependency>
``` 

## Reading and writing CBOR documents

```java
// Parsing CBOR document
byte[] cbor = " ... bytes of the CBOR document ... ";
Tree document = new Tree(cbor, "cbor");

// Getting / setting values
int value = document.get("intValue").asInteger();
document.put("intValue", 1);

// Generating CBOR byte array from Tree
byte[] cbor = document.toBinary("cbor");
``` 