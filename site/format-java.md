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
    <version>1.0.14</version>
</dependency>
```

## Reading and writing serialized data structures

```java
// Reading serialized data structure
byte[] bytes = " ... bytes of the document ... ";
Tree document = new Tree(bytes, "java");

// Getting / setting values
document.stream().limit(10).forEach(System.out::println);
document.remove("path.to.subnode");

// Serialize Java Objects into byte array
byte[] bytes = document.toBinary("java");
``` 