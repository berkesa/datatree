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
import io.datatree.Tree;

// Build a small document (an object with a nested array):
Tree document = new Tree();
document.put("name", "Alice");
document.put("age", 30);
document.putList("languages").add("Java").add("Go");

// Convert the Tree to XML (the dependency-free built-in adapter is used
// when no other XML library is on the classpath):
String xml = document.toString("xml");
System.out.println(xml);
```

The printed output:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xml><name>Alice</name><age>30</age><languages><item>Java</item><item>Go</item></languages></xml>
```

Parse an XML string back into a `Tree` with the `"xml"` format name:

```java
Tree parsed = new Tree(xml, "xml");
int age = parsed.get("age", 0);   // 30
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
| Jackson XML | XmlJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-xml', version: '2.19.0'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml) |
| XMLStream | XmlXStream | [group: 'com.thoughtworks.xstream', name: 'xstream', version: '1.4.21'](https://mvnrepository.com/artifact/com.thoughtworks.xstream/xstream) |
| Built-in XML | XmlBuiltin | - | 