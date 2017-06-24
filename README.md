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

The [datatree-adapters](https://github.com/berkesa/datatree-adapters) artifact contains lot of text (JSON, XML, YAML, TOML, CSV, TSV, Properties) and binary (BSON, ION, CBOR, SMILE) adapters. If you'd like to use these formats, add the following dependency to pom.xml:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>
```
