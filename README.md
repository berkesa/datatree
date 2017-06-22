# DataTree Core API
Extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats.

 ![architecture](https://github.com/berkesa/datatree/blob/master/architecture.png)

The [datatree-adapters](https://github.com/berkesa/datatree-adapters) project contains lot of text (JSON, XML, YAML, TOML, CSV, TSV, Java Properties) and binary (BSON, ION, CBOR, SMILE, Java Object Serialization) adapters.

## Usage

```javascript
Tree document = new Tree();
document.put("address.city", "Phoenix");
String json = document.toString();

{
  "address": {
    "city": "Phoenix"
  }
}
```
