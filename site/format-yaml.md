## YAML format

YAML (a recursive acronym for "YAML Ain't Markup Language") is a human-readable data-serialization language.
It is commonly used for configuration files and in applications where data is being stored or transmitted.
YAML targets many of the same communications applications as Extensible Markup Language (XML)
but has a minimal syntax which intentionally differs from SGML.
It uses both Python-style indentation to indicate nesting,
and a more compact format that uses [] for lists and {} for maps making YAML 1.2 a superset of JSON. 

## Dependencies

DataTree API supports 2 YAML reader/writer implementations.
For example, to use SnakeYAML's version, just add SnakeYAML JARs to the classpath.
If DataTree detects SnakeYAML API on classpath,
DataTree will use SnakeYAML API to read/write YAML documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- SNAKEYAML API -->
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.4</version>
</dependency>
```

## Reading and writing YAML documents

```java
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Convert the Tree to YAML text (SnakeYAML, when it is on the classpath):
String yaml = document.toString("yaml");
System.out.println(yaml);
```

The printed output:

```yaml
name: Alice
age: 30
languages:
- Java
- Go
```

Parse a YAML string back into a `Tree` with the `"yaml"` format name:

```java
Tree parsed = new Tree(yaml, "yaml");
String first = parsed.get("languages[0]", "");   // "Java"
```

If there is more than one YAML implementation on classpath, the preferred
implementation is adjustable with the following System Properties:

```
// Using SnakeYAML API:
-Ddatatree.yaml.reader=io.datatree.dom.adapters.YamlSnakeYaml
-Ddatatree.yaml.writer=io.datatree.dom.adapters.YamlSnakeYaml

// Using Jackson YAML API:
-Ddatatree.yaml.reader=io.datatree.dom.adapters.YamlJackson
-Ddatatree.yaml.writer=io.datatree.dom.adapters.YamlJackson
```

## Required dependencies of YAML adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson YAML | YamlJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-yaml', version: '2.19.0'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml) |
| SnakeYAML | YamlSnakeYaml | [compile group: 'org.yaml', name: 'snakeyaml', version: '2.4'](https://mvnrepository.com/artifact/org.yaml/snakeyaml) |
 