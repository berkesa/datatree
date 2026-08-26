# DataTree API

DataTree is an extensible Java library for reading, manipulating, and writing hierarchical
data structures in many formats. It is **not** yet another JSON parser — it is a top-level API
layer that drives existing parser implementations through a single, uniform document type
called `Tree`. JSON is the default format, but XML, YAML, TOML, CBOR, and ~30 other formats are
available through pluggable adapters, and you can swap the underlying implementation (for a
smaller, smarter, or faster one) **without changing a line of your code**. On top of that, the
`Tree` API gives you a complete toolset to manipulate documents — put, get, remove, insert,
sort, find, stream, and more.

![architecture](https://raw.githubusercontent.com/berkesa/datatree/master/docs/architecture.png)

## Documentation

[![Documentation](https://raw.githubusercontent.com/berkesa/datatree/master/docs/docs-button.png)](https://berkesa.github.io/datatree/introduction.html#datatree-api)

## Download

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-core</artifactId>
    <version>2.1.0</version>
</dependency>
```

The core artifact ships the built-in JSON, Java-serialization, and debug adapters. To read and
write the other ~30 formats (Jackson, Gson, SnakeYAML, CBOR, BSON, …), add the
[`datatree-adapters`](https://github.com/berkesa/datatree-adapters) pack.

## Quick start

```java
// Build a document in memory
Tree node = new Tree();
node.put("address.city", "Phoenix");          // intermediate nodes are auto-created
node.putList("numbers").add(1).add(2).add(3);

// Read values with JavaScript-like path access
String city = node.get("address.city", "");   // "Phoenix"
int first   = node.get("numbers[0]", 0);       // 1

// Serialize (JSON is the built-in default) and parse back
String json  = node.toString();
Tree parsed  = new Tree(json);
```

With the adapter pack on the classpath the same document serializes to any other format —
`node.toString("yaml")`, `new Tree(xml, "xml")`, and so on.

## Requirements

DataTree requires **Java 11** or newer.

## License

DataTree is licensed under the Apache License, Version 2.0 — you can use it in your commercial
products for free.
