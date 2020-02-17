## Java Properties format

The Java Properties class, `java.util.Properties`,
is like a Java Map of Java String key and value pairs.
The Java Properties class can write the key, value pairs to a properties file on disk,
and read the properties back in again.
This is an often used mechanism for storing simple configuration properties for Java applications. 

## Dependencies

DataTree API supports 2 Java Property reader/writer implementations.
The default (built-in) Property adapter has no dependencies.

```java
// Parsing Java Properties file
String properties = "< ... properties ...>";
Tree document = new Tree(properties, "properties");

// Getting / setting values
boolean value = document.get("array[2].subItem.value", false);
document.put("path.to.item", true);

// Generating Java Properties string from Tree
String properties = document.toString("properties");
``` 

If you would like to use the Jackson's Java Property reader/writer add the proper dependency (see the table below) to the application's classpath.

## Required dependencies of Java Property adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson Properties | PropertiesJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-properties', version: '2.10.1'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-properties) |
| Built-in Properties | PropertiesBuiltin | - | 