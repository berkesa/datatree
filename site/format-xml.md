## XML format

Extensible Markup Language (XML) is a markup language that defines a set of rules for encoding documents
in a format that is both human-readable and machine-readable.
The World Wide Web Consortium's XML 1.0 Specification of 1998 and several other related specifications
— all of them free open standards—define XML.
The design goals of XML emphasize simplicity, generality, and usability across the Internet.
It is a textual data format with strong support via Unicode for different human languages.
Although the design of XML focuses on documents,
the language is widely used for the representation of arbitrary data structures such as those used in web services. 

## Dependencies

DataTree API supports 3 XML reader/writer implementations.
The default (built-in) XML adapter has no dependencies.

## Reading and writing XML documents

```java
// Parsing XML document
String xml = "< ... XML document ...>";
Tree document = new Tree(xml, "xml");

// Getting / setting values
String value = document.get("node.subnode.subnode", "defaultValue");
document.put("node.subnode.subnode", "newValue");

// Generating XML string from Tree
String xml = document.toString("xml");
```

If you would like to use the Jackson or XMLStream reader/writer add the proper dependency
(see the table below) to the application's classpath.
If there is more than one XML implementation on classpath, the preferred
implementation is adjustable with the following System Properties:

```
// Using XMLStream API:
-Ddatatree.xml.reader=io.datatree.dom.adapters.XmlXStream
-Ddatatree.xml.writer=io.datatree.dom.adapters.XmlXStream

// Using Jackson XML API:
-Ddatatree.xml.reader=io.datatree.dom.adapters.XmlJackson
-Ddatatree.xml.writer=io.datatree.dom.adapters.XmlJackson

// Using Builting XML API:
-Ddatatree.xml.reader=io.datatree.dom.adapters.XmlBuiltin
-Ddatatree.xml.writer=io.datatree.dom.adapters.XmlBuiltin
```

## Required dependencies of XML adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson XML | XmlJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-xml', version: '2.12.3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml) |
| XMLStream | XmlXStream | [group: 'xstream', name: 'xstream', version: '1.2.2'](https://mvnrepository.com/artifact/xstream/xstream) |
| Built-in XML | XmlBuiltin | - | 