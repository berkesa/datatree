## JSON format

DataTree API supports 11 popular JSON implementations,
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
    <version>2.0.0</version>
</dependency>

<!-- JACKSON JSON API -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.19.0</version>
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
If you add Gson dependency to classpath instead of Jackson,
DataTree will use Gson, and so on.

### Using BSON (MongoDB) API

This is a Java API for MongoDB (JSON and BSON).
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonBson
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonBson
```

Set as default JSON API (using static methods):

```java
static {
    JsonBson jsonBson = new JsonBson();
    TreeReaderRegistry.setReader("json", jsonBson);
    TreeWriterRegistry.setWriter("json", jsonBson);
}
```

::: warning BSON dependencies
To use BSON JSON API, add the following dependency to the build script:  
[group: 'org.mongodb', name: 'bson', version: '5.8.0'](https://mvnrepository.com/artifact/org.mongodb/bson)
:::

### Using DSLJson API

This is a DSL Platform compatible JSON library (https://dsl-platform.com).
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonDSL
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonDSL
```

Set as default JSON API (using static methods):

```java
static {
    JsonDSL jsonDSL = new JsonDSL();
    TreeReaderRegistry.setReader("json", jsonDSL);
    TreeWriterRegistry.setWriter("json", jsonDSL);
}
```

::: warning DSLJson dependencies
To use DSLJson JSON API, add the following dependency to the build script:  
[group: 'com.dslplatform', name: 'dsl-json', version: '2.0.2'](https://mvnrepository.com/artifact/com.dslplatform/dsl-json)
:::

### Using Genson JSON API

Genson API is designed to be easy to use, it handles for you
all the databinding, streaming and much more.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonGenson
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonGenson
```

Set as default JSON API (using static methods):

```java
static {
    JsonGenson jsonGenson = new JsonGenson();
    TreeReaderRegistry.setReader("json", jsonGenson);
    TreeWriterRegistry.setWriter("json", jsonGenson);
}
```

::: warning Genson dependencies
To use Genson JSON API, add the following dependency to the build script:  
[group: 'com.owlike', name: 'genson', version: '1.6'](https://mvnrepository.com/artifact/com.owlike/genson)
:::

### Using Google Gson JSON API

Gson is a Java library that can be used to convert Java
Objects into their JSON representation. It can also be used to convert a
JSON string to an equivalent Java object. Gson can work with arbitrary
Java objects including pre-existing objects that you do not have
source-code of.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonGson
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonGson
```

Set as default JSON API (using static methods):

```java
static {
    JsonGson jsonGson = new JsonGson();
    TreeReaderRegistry.setReader("json", jsonGson);
    TreeWriterRegistry.setWriter("json", jsonGson);
}
```

::: warning Gson dependencies
To use Gson JSON API, add the following dependency to the build script:  
[group: 'com.google.code.gson', name: 'gson', version: '2.14.0'](https://mvnrepository.com/artifact/com.google.code.gson/gson)
:::

### Using Jackson JSON API

Standard JSON library for Java (or JVM platform in general), or,
as the "best JSON parser for Java".
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonJackson
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJackson
```

Set as default JSON API (using static methods):

```java
static {
    JsonJackson jsonJackson = new JsonJackson();
    TreeReaderRegistry.setReader("json", jsonJackson);
    TreeWriterRegistry.setWriter("json", jsonJackson);
}
```

::: warning Jackson dependencies
To use Jackson JSON API, add the following dependency to the build script:  
[group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.19.0'](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind)
:::

### Using Jodd JSON API

Jodd Json is lightweight library for (de)serializing Java
objects into and from JSON.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonJodd
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJodd
```

Set as default JSON API (using static methods):

```java
static {
    JsonJodd jsonJodd = new JsonJodd();
    TreeReaderRegistry.setReader("json", jsonJodd);
    TreeWriterRegistry.setWriter("json", jsonJodd);
}
```

::: warning Jodd dependencies
To use Jodd JSON API, add the following dependency to the build script:  
[group: 'org.jodd', name: 'jodd-json', version: '6.0.3'](https://mvnrepository.com/artifact/org.jodd/jodd-json)
:::

### Using Apache Johnzon API

Apache Johnzon is a project providing an implementation of
JsonProcessing (aka jsr-353) and a set of useful extension for this
specification like an Object mapper, some JAX-RS providers and a
WebSocket module provides a basic integration with Java WebSocket API.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonJohnzon
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJohnzon
```

Set as default JSON API (using static methods):

```java
static {
    JsonJohnzon jsonJohnzon = new JsonJohnzon();
    TreeReaderRegistry.setReader("json", jsonJohnzon);
    TreeWriterRegistry.setWriter("json", jsonJohnzon);
}
```

::: warning Johnzon dependencies
To use Johnzon JSON API, add the following dependencies to the build script:  
[group: 'org.apache.johnzon', name: 'johnzon-mapper', version: '2.1.0'](https://mvnrepository.com/artifact/org.apache.johnzon/johnzon-mapper)  
[group: 'jakarta.json', name: 'jakarta.json-api', version: '2.1.3'](https://mvnrepository.com/artifact/jakarta.json/jakarta.json-api)
:::

### Using JsonIO API

JsonIO - Convert Java to JSON. Convert JSON to Java.
PrettyFormatter print JSON. Java JSON serializer.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonJsonIO
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJsonIO
```

Set as default JSON API (using static methods):

```java
static {
    JsonJsonIO jsonJsonIO = new JsonJsonIO();
    TreeReaderRegistry.setReader("json", jsonJsonIO);
    TreeWriterRegistry.setWriter("json", jsonJsonIO);
}
```

::: warning JsonIO dependencies
To use JsonIO JSON API, add the following dependency to the build script:  
[group: 'com.cedarsoftware', name: 'json-io', version: '4.56.0'](https://mvnrepository.com/artifact/com.cedarsoftware/json-io)
:::

### Using NanoJson API

NanoJson is a tiny, compliant JSON parser and writer for Java.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonNano
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonNano
```

Set as default JSON API (using static methods):

```java
static {
    JsonNano jsonNano = new JsonNano();
    TreeReaderRegistry.setReader("json", jsonNano);
    TreeWriterRegistry.setWriter("json", jsonNano);
}
```

::: warning NanoJson dependencies
To use NanoJson JSON API, add the following dependency to the build script:  
[group: 'com.grack', name: 'nanojson', version: '1.10'](https://mvnrepository.com/artifact/com.grack/nanojson)
:::

### Using Json-smart API

Json-smart is a performance focused, JSON processor lib.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonSmart
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonSmart
```

Set as default JSON API (using static methods):

```java
static {
    JsonSmart jsonSmart = new JsonSmart();
    TreeReaderRegistry.setReader("json", jsonSmart);
    TreeWriterRegistry.setWriter("json", jsonSmart);
}
```

::: warning Json-smart dependencies
To use Json-smart JSON API, add the following dependency to the build script:  
[group: 'net.minidev', name: 'json-smart', version: '2.5.2'](https://mvnrepository.com/artifact/net.minidev/json-smart)
:::

### Using Amazon Ion API

Amazon Ion is a richly-typed, self-describing, hierarchical data
serialization format offering interchangeable binary and text
representations. The text format (a superset of JSON) is easy to read and
author, supporting rapid prototyping. The rich type system provides
unambiguous semantics for long-term preservation of business data which can
survive multiple generations of software evolution. Ion was built to solve
the rapid development, decoupling, and efficiency challenges faced every day
while engineering large-scale, service-oriented architectures.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonIon
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonIon
```

Set as default JSON API (using static methods):

```java
static {
    JsonIon jsonIon = new JsonIon();
    TreeReaderRegistry.setReader("json", jsonIon);
    TreeWriterRegistry.setWriter("json", jsonIon);
}
```

::: warning Ion dependencies
To use Amazon Ion JSON API, add the following dependency to the build script:  
[group: 'com.amazon.ion', name: 'ion-java', version: '1.11.10'](https://mvnrepository.com/artifact/com.amazon.ion/ion-java)
:::

### Using Built-in parser

The `JsonBuiltin` is the default JSON reader / writer.
Set as default JSON API (using Java System Properties):

```
-Ddatatree.json.reader=io.datatree.dom.builtin.JsonBuiltin
-Ddatatree.json.writer=io.datatree.dom.builtin.JsonBuiltin
```

Set as default JSON API (using static methods):

```java
static {
    JsonBuiltin json = new JsonBuiltin();
    TreeReaderRegistry.setReader("json", json);
    TreeWriterRegistry.setWriter("json", json);
}
```

## Using different JSON implementations for reading and writing

If DataTree detects more JSON implementations on classpath, DataTree will use the fastest implementation.
To force DataTree to use the proper APIs, use the `datatree.json.reader` and `datatree.json.writer`
System Properties to specify the appropriate Adapter Class (see above in the table) for reading and writing:

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonGson
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJackson
```

Add Gson and Jackson to your pom.xml:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- JACKSON JSON API -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.19.0</version>
</dependency>

<!-- GSON JSON API -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.14.0</version>
</dependency>
```

After that, DataTree will use Gson API for parsing, and Jackson for generating JSON strings.

```java
// Parsing JSON document using Gson API
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
| SmartJson | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| DSLJson | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Amazon Ion | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** | special base64 | **Yes** | **Yes** |
| NanoJson | **Yes** | **Yes** | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Jackson | **Yes** | **Yes** | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Jodd | **Yes** | **Yes** | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Genson | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Gson | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| JsonIO | **Yes** | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |
| Bson | **Yes** | No | No | **Yes** | **Yes** | special base64 | **Yes** | special |
| Johnzon | No | No | No | **Yes** | **Yes** | base64 | **Yes** | **Yes** |

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
