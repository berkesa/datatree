## TOML format

TOML is a configuration file format that is intended to be easy to read and write due to obvious semantics which aim to be "minimal",
and is designed to map unambiguously to a dictionary.
Its specification is open-source, and receives community contributions.
TOML is used in a number of software projects, and is implemented in a large number of programming languages.

## Dependencies

DataTree API supports 3 TOML implementations.
For example, to use Toml4j's version, just add Toml4j JARs to the classpath.
If DataTree detects Toml4j API on classpath, DataTree will use Toml4j API to read/write TOML documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.13</version>
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
// Parsing TOML document
String toml = " ... TOML document ... ";
Tree document = new Tree(toml, "toml");

// Getting / setting values
document.get("subObject").forEach((child) -> {
  ...
});
document.putList("newList").add(1).add(2).add(3);

// Generating TOML string from Tree
String toml = document.toString("toml");
```

## Required dependencies of TOML adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| JToml | TomlJtoml | [group: 'me.grison', name: 'jtoml', version: '1.0.0'](https://mvnrepository.com/artifact/me.grison/jtoml) |
| JToml | TomlJtoml2 | [group: 'io.ous', name: 'jtoml', version: '2.0.0'](https://mvnrepository.com/artifact/io.ous/jtoml) |
| Toml4j | TomlToml4j | [group: 'com.moandjiezana.toml', name: 'toml4j', version: '0.7.2'](https://mvnrepository.com/artifact/com.moandjiezana.toml/toml4j) | 