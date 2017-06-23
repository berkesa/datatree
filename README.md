# DataTree Core API
Extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats.

 ![architecture](https://github.com/berkesa/datatree/blob/master/docs/images/architecture.png)

## Download

Add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>
```

The [datatree-adapters](https://github.com/berkesa/datatree-adapters) project contains lot of text (JSON, XML, YAML, TOML, CSV, TSV, Java Properties) and binary (BSON, ION, CBOR, SMILE, Java Object Serialization) adapters.

## Usage

```javascript
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