## TOML format

TOML is a configuration file format that is intended to be easy to read and write due to obvious semantics which aim to be "minimal",
and is designed to map unambiguously to a dictionary.
Its specification is open-source, and receives community contributions.
TOML is used in a number of software projects, and is implemented in a large number of programming languages.

## Dependencies

DataTree API supports 2 TOML implementations.
For example, to use Toml4j's version, just add Toml4j JARs to the classpath.
If DataTree detects Toml4j API on classpath, DataTree will use Toml4j API to read/write TOML documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- TOML4J API -->
<dependency>
    <groupId>com.moandjiezana.toml</groupId>
    <artifactId>toml4j</artifactId>
    <version>0.7.2</version>
</dependency>
```

## Reading and writing TOML documents

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Convert the Tree to TOML text (Toml4j, which provides the writer):
String toml = document.toString("toml");
System.out.println(toml);
```

The printed output:

```toml
name = "Alice"
age = 30
languages = ["Java", "Go"]
```

Parse a TOML string back into a `Tree` with the `"toml"` format name:

```java
Tree parsed = new Tree(toml, "toml");
String name = parsed.get("name", "");   // "Alice"
```

If there is more than one TOML implementation on classpath, the preferred
implementation is adjustable with the following System Properties:

```
// Using JToml API:
-Ddatatree.toml.reader=io.datatree.dom.adapters.TomlJtoml2
-Ddatatree.toml.writer=io.datatree.dom.adapters.TomlJtoml2

// Using Toml4j API:
-Ddatatree.toml.reader=io.datatree.dom.adapters.TomlToml4j
-Ddatatree.toml.writer=io.datatree.dom.adapters.TomlToml4j
```

## Required dependencies of TOML adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| JToml | TomlJtoml2 | [group: 'io.ous', name: 'jtoml', version: '2.0.0'](https://mvnrepository.com/artifact/io.ous/jtoml) |
| Toml4j | TomlToml4j | [group: 'com.moandjiezana.toml', name: 'toml4j', version: '0.7.2'](https://mvnrepository.com/artifact/com.moandjiezana.toml/toml4j) | 