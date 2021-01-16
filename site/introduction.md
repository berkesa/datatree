## DataTree API

Extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats.
DataTree is NOT an another JSON parser. It's a top-level API layer that uses existing JSON implementations.
Even though the JSON format is the default, DataTree supports other formats, such as XML, YAML, TOML, etc.
DataTree enables you to replace the underlaying implementation (to a smaller, smarter, faster version)
during the software development without any code modifications.
In addition, the DataTree API provides you with a logical set of tools
to manipulate (put, get, remove, insert, sort, find, stream, etc.) the content of the hierarchical documents. 

<div align="center">
    <img src="architecture.png" alt="DataTree architecture" />
</div>

## Usage

```java
import io.datatree.Tree;

Tree document = new Tree();
document.put("address.city", "Phoenix");
String json = document.toString();

Result:

{
  "address": {
    "city": "Phoenix"
  }
}
```

## Download

The DataTree Core API contains the complete Tree toolkit, and one built-in JSON reader/writer. If you use Maven, add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-core</artifactId>
    <version>1.1.0</version>
</dependency>
```

[...or download the JARs directly from the Maven Central](https://search.maven.org/artifact/com.github.berkesa/datatree-core)

## Features

- DataTree API supports 18 popular JSON implementations (Jackson, Gson, Boon, Jodd, FastJson, etc.)
- DataTree API supports 10 other (non-JSON) formats (YAML, ION, BSON, MessagePack, etc.)
- Single universal type (no type casting, everything is a `Tree`)
- JSON path functions (`tree.get("cities[2].location")`)
- Easy iteration over Java Collections and Maps (`for (Tree child: parent)`)
- Recursive deep cloning (`Tree copy = tree.clone()`)
- Support for all Java types of Appache Cassandra (`BigDecimal`, `UUID`, `InetAddress`, etc.)
- Support for all Java types of MongoDB (`BsonNumber`, `BsonNull`, `BsonString`, `BsonBoolean`, etc.)
- Root and parent pointers, methods to traverse the data structure (`tree.getParent()` or `tree.getRoot()`)
- Methods for type-check (`Class valueClass = tree.getType()`)
- Methods for modify the type of the underlying Java value (`tree.setType(String.class)`)
- Method chaining (`tree.put("name1", "value1").put("name2", "value2")`)
- Merging, filtering structures (`tree.copyFrom(source)`, `tree.find(condition)`, etc.)
- Documents have an optional metadata container (`tree.getMeta().put("name", "value")`)
- Pretty printing (`String json = tree.toString(true / false)`)

## Supported formats and APIs

The "datatree-adapters" artifact contains lot of text (JSON, XML, YAML, TOML, CSV, TSV, Properties)
and binary (BSON, ION, CBOR, SMILE, MessagePack) adapters. If you'd like to use these formats,
add the following dependency instead of the "datatree-core":

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.14</version>
</dependency>
```

Supported JSON APIs:

- Apache Jackson
- Boon JSON API
- FastJson
- JsonIO
- Google Gson
- BSON ("extended JSON")
- DSLJson
- Flexjson
- Genson
- Jodd Json
- Apache Johnzon
- NanoJson
- JSON.simple
- Json-smart
- SOJO
- JsonUtil
- Amazon Ion
- Json-iterator

Supported non-JSON text formats:

- XML
- YAML
- TOML
- Java Properties
- CSV
- TSV
- XML-RPC

Supported binary formats:

- MessagePack
- BSON (binary JSON)
- Kryo
- CBOR
- SMILE
- Amazon Ion
- Java Object Serializaton

## Requirements

The DataTree API requires Java 8.

## License

DataTree is licensed under the Apache License V2, you can use it in your commercial products for free.
