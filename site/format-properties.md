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
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Convert the Tree to a Java Properties string (the dependency-free
// built-in adapter is the default):
String properties = document.toString("properties");
System.out.println(properties);
```

The printed output. The flat key/value model has no arrays, so list elements become numbered
(1-based) keys:

```properties
name=Alice
age=30
languages.1=Java
languages.2=Go
```

Parse a Java Properties string back into a `Tree` with the `"properties"` format name:

```java
Tree parsed = new Tree(properties, "properties");
int age = parsed.get("age", 0);   // 30
```

If there is more than one Promerties implementation on classpath, the preferred
implementation is adjustable with the following System Properties:

```
// Using Jackson API:
-Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesJackson
-Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesJackson

// Using built-in API:
-Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesBuiltin
-Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesBuiltin
```

## Required dependencies of Java Property adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson Properties | PropertiesJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-properties', version: '2.22.2'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-properties) |
| Built-in Properties | PropertiesBuiltin | - | 