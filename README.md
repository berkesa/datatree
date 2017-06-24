# DataTree Core API
Extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats.
DataTree is NOT an another JSON parser. It's a top-level API layer that uses existing JSON implementations.
Even though the JSON format is the default, DataTree supports other formats, such as XML, YAML, TOML, etc.
DataTree enables you to replace the underlaying implementation (to a smaller, smarter, faster version)
during the software development without any code modifications.
In addition, the DataTree API provides you with a logical set of tools
to manipulate (put, get, remove, insert, sort, find, stream, etc.) the content of the hierarchical documents.

![architecture](https://github.com/berkesa/datatree/blob/master/docs/images/architecture.png)

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

The DataTree Core API contains the complete Tree toolkit, and one built-in JSON reader/writer. If you use maven, add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

The [datatree-adapters](https://github.com/berkesa/datatree-adapters) artifact contains lot of text (JSON, XML, YAML, TOML, CSV, TSV, Properties) and binary (BSON, ION, CBOR, SMILE) adapters. If you'd like to use these formats, add the following dependency instead of the "datatree-core":

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Features:

* JSON path functions (`Tree location = tree.get("cities[2].location")`)
* Single universal type (no type casting, everything is a `Tree`)
* Easy iteration over Java Collections, Maps, arrays or scalar values (`for (Tree child: parent)`)
* Recursive deep cloning (`Tree copy = tree.clone()`)
* Support for all Java types of Appache Cassandra (BigInteger, BigDecimal, UUID, InetAddress, etc.)
* Support for all Java types of MongoDB (BsonNumber, BsonNull, BsonString, BsonBoolean, etc.)
* Parent pointers, getPreviousSibling/getNextSibling methods to traverse the data structure
* Methods for type-check (`Class valueClass = tree.getType()`)
* Methods for modify the type of the underlying Java value (`tree.setType(String.class)`)
* Method chaining (`tree.put("name1", "value1").put("name2", "value2")`);
* All Tree "nodes" are serializable, iterable and cloneable
