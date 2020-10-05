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
    <version>1.0.14</version>
</dependency>

<!-- SMILE API -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-smile</artifactId>
    <version>2.11.3</version>
</dependency>
```

## Reading and writing SMILE documents

```java
// Parsing SMILE document
byte[] smile = " ... bytes of the SMILE document ... ";
Tree document = new Tree(smile, "smile");

// Getting / setting values
Date value = document.get("timestamp").asDate();
document.put("timestamp", new Date());

// Generating SMILE byte array from Tree
byte[] smile = document.toBinary("smile");
``` 