## JSON format

DataTree API supports 18 popular JSON implementations,
you can use your favorite one for reading/writing JSON structures.
The following sample demonstrates, how to replace the built-in JSON API to Jackson's JSON API.
The only thing you have to do is add Jackson JARs to the classpath.
If DataTree detects Jackson API on classpath,
DataTree will use Jackson's Object Mapper to read/write JSON documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.13</version>
</dependency>

<!-- JACKSON JSON API -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.10.1</version>
</dependency>
``` 

```java
// Parsing JSON document using Jackson API
String json = "{ ... json document ...}";
Tree document = new Tree(json);

// Generating JSON string from Tree using Jackson API
String json = document.toString();
```

That is all. The table below shows the dependencies of the supported JSON implementations.
If you add FastJson dependency to classpath instead of Jackson,
DataTree will use FastJson, and so on.

## Required dependencies of JSON adapters

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Boon JSON API | JsonBoon | [group: 'io.fastjson', name: 'boon', version: '0.34'](https://mvnrepository.com/artifact/io.fastjson/boon) |
| BSON (MongoDB) | JsonBson | [group: 'org.mongodb', name: 'bson', version: '3.12.1'](https://mvnrepository.com/artifact/org.mongodb/bson) |
| DSLJson | JsonDSL | [group: 'com.dslplatform', name: 'dsl-json', version: '1.9.5'](https://mvnrepository.com/artifact/com.dslplatform/dsl-json) |
| FastJson | JsonFast | [group: 'com.alibaba', name: 'fastjson', version: '1.2.62'](https://mvnrepository.com/artifact/com.alibaba/fastjson) |
| Flexjson | JsonFlex | [group: 'net.sf.flexjson', name: 'flexjson', version: '3.3'](https://mvnrepository.com/artifact/net.sf.flexjson/flexjson) |
| Genson | JsonGenson | [group: 'com.owlike', name: 'genson', version: '1.6'](https://mvnrepository.com/artifact/com.owlike/genson) |
| Google Gson | JsonGson | [group: 'com.google.code.gson', name: 'gson', version: '2.8.6'](https://mvnrepository.com/artifact/com.google.code.gson/gson) |
| Jackson JSON | JsonJackson | [group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.10.1'](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) |
| Jodd Json | JsonJodd  | [group: 'org.jodd', name: 'jodd-json', version: '5.0.13'](https://mvnrepository.com/artifact/org.jodd/jodd-json) |
| Apache Johnzon | JsonJohnzon | [group: 'org.apache.johnzon', name: 'johnzon-mapper', version: '1.2.2'](https://mvnrepository.com/artifact/org.apache.johnzon/johnzon-mapper) |
| JsonIO | JsonJsonIO | [group: 'com.cedarsoftware', name: 'json-io', version: '4.12.0'](https://mvnrepository.com/artifact/com.cedarsoftware/json-io) |
| NanoJson | JsonNano | [group: 'com.grack', name: 'nanojson', version: '1.4'](https://mvnrepository.com/artifact/com.grack/nanojson) |
| JSON.simple | JsonSimple | [group: 'com.googlecode.json-simple', name: 'json-simple', version: '1.1.1'](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple) |
| Json-smart | JsonSmart | [group: 'net.minidev', name: 'json-smart', version: '2.3'](https://mvnrepository.com/artifact/net.minidev/json-smart) |
| SOJO | JsonSojo | [group: 'net.sf.sojo', name: 'sojo', version: '1.0.13'](https://mvnrepository.com/artifact/net.sf.sojo/sojo) |
| JsonUtil | JsonUtil | [group: 'org.kopitubruk.util', name: 'JSONUtil', version: '1.10.4'](https://mvnrepository.com/artifact/org.kopitubruk.util/JSONUtil) |
| Amazon Ion | JsonIon  | [group: 'software.amazon.ion', name: 'ion-java', version: '1.5.1'](https://mvnrepository.com/artifact/software.amazon.ion/ion-java) |
| Json Iterator | JsonJsoniter  | [group: 'com.jsoniter', name: 'jsoniter', version: '0.9.23'](https://mvnrepository.com/artifact/com.jsoniter/jsoniter) |
| Built-in parser | JsonBuiltin | - |

## Using different JSON implementations for reading and writing

If DataTree detects more JSON implementations on classpath, DataTree will use the fastest implementation. To force DataTree to use the proper APIs, use the `datatree.json.reader` and `datatree.json.writer` System Properties to specify the appropriate Adapter Class (see above in the table) for reading and writing:

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonBoon
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJackson
```

Add Boon and Jackson to your pom.xml:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.13</version>
</dependency>

<!-- JACKSON JSON API -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- BOON JSON API -->
<dependency>
    <groupId>io.fastjson</groupId>
    <artifactId>boon</artifactId>
    <version>0.34</version>
</dependency>
```

After that, DataTree will use Boon API for parsing, and Jackson for generating JSON strings.

```java
// Parsing JSON document using Boon API
String json = "{ ... json document ...}";
Tree document = new Tree(json);

// Getting / setting values
int number = document.get("node.subnode.subnode").asInteger();
document.put("node.subnode.subnode", 5);

// Generating JSON string from Tree using Jackson API
String json = document.toString();
``` 

## Performance of JSON APIs

### JSON readers/parsers

*The higher values are better:* 

<div align="center">
    <img src="adapters/json-readers.png" alt="JSON readers" />
</div>

Created at January 22, 2020
[Sample JSON](https://github.com/berkesa/datatree-adapters/blob/master/src/test/resources/sample-small.json)

### JSON writers/serializers

*The higher values are better:*
 
<div align="center">
    <img src="adapters/json-writers.png" alt="JSON writers" />
</div>

Created at January 22, 2020
[Sample JSON](https://github.com/berkesa/datatree-adapters/blob/master/src/test/resources/sample-small.json) 

## Feature comparison of JSON APIs

| JSON API | Long | BigInteger | BigDecimal | MongoDB | Cassandra | Binary | Pretty | Date |
| -------- | ---- | ---------- | ---------- | ------- | --------- | ------ | ------ | ---- |
| Built-in | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| FastJson | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| SmartJson | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| DSLJson | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Amazon Ion | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | special base64 | **Yes** | **Yes** |
| JsonUtil | **Yes** | **Yes** | **Yes** | No | **Yes** | array | **Yes** | **Yes** |
| NanoJson | **Yes** | **Yes** | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Jackson | **Yes** | **Yes** | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Jodd | **Yes** | **Yes** | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Genson | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Gson | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| JsonIO | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| FlexJson | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Boon | **Yes** | No | No | **Yes** | **Yes** | array | **Yes** | **Yes** |
| Jsoniter | **Yes** | No | No | **Yes** | **Yes** | array | **Yes** | **Yes** |
| Bson | **Yes** | No | No | **Yes** | **Yes** | special base64 | **Yes** | special |
| Sojo | **Yes** | No | No | **Yes** | **Yes** | array | **Yes** | fixed |
| Johnzon | No | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| JSON.Simple | **Yes** | No | No | No | No | No | **Yes** | No |

The meanings of the columns are as follows:

* JSON API: Name of the underlying JSON API
* Long: Deserializes 64-bit integers as Long, without overflowing
* BigInteger: Automatically deserializes very long (>64-bit) integers as BigInteger
* BigDecimal: Automatically deserializes very long floating-point numbers as BigDecimal
* MongoDB: Able to serialize MongoDB types (ObjectID, BsonDateTime, BsonRegularExpression, etc.)
* Cassandra: Serializes all datatypes of Appache Cassandra (UUID, Date, Set, InetAddress, etc.)
* Binary: Output format of byte arrays in the generated JSON file
* Pretty: Supports pretty-printing (formatted JSON output)
* Date: Able to serialize Date objects in custom timestamp format 