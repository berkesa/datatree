# Serialization &amp; I/O

This page covers turning a `Tree` into text or binary — and parsing it back — to and from Strings,
byte arrays, files, streams and channels. JSON is the default format; other formats are selected by
name and may require an extra dependency (see
[Format availability](#format-availability-dependencies) at the bottom).

For building and reading the in-memory structure itself, see [Data Manipulation](manipulation.html)
and [Reading values](reading-values.html).

```java
import io.datatree.Tree;
import java.io.File;
import java.net.URL;
```

## To a String

`toString()` (no arguments) produces **pretty-printed JSON, including the metadata block**:

```java
Tree node = new Tree();
node.put("address.city", "Phoenix");
System.out.println(node.toString());
```

```json
{
  "address":{
    "city":"Phoenix"
  }
}
```

The overloads give you control over pretty-printing, the format, and whether the metadata block is
written:

```java
node.toString();                          // pretty JSON, WITH meta
node.toString(true);                      // pretty JSON, WITHOUT meta
node.toString(false);                     // compact JSON, WITHOUT meta
node.toString("json");                    // named format, compact, WITHOUT meta
node.toString("json", true);              // named format, pretty, WITHOUT meta
node.toString("json", true, true);        // named format, pretty, WITH meta
```

Compact JSON is a single line:

```java
System.out.println(node.toString(false));
// {"address":{"city":"Phoenix"}}
```

Any registered format name works. The built-in `debug` format renders a readable type-annotated
dump — handy for logging:

```java
Tree node = new Tree().put("a", 1).put("b", "c");
System.out.println(node.toString("debug"));
```

```
Map:
a -> Number: 1
b -> class java.lang.String: c
```

Other text formats — `yaml`, `xml`, `csv`, `tsv`, `toml`, `properties` — use the same call,
`node.toString("yaml")`, once the `datatree-adapters` artifact is on the classpath.

## To binary

`toBinary()` returns a JSON byte array (without meta); `toBinary(format[, insertMeta])` selects a
binary format. The built-in `java` format (standard Java serialization) is always available and
round-trips losslessly:

```java
Tree node = new Tree().put("a", 1).put("b", "hello");

byte[] bytes = node.toBinary("java");
Tree   copy  = new Tree(bytes, "java");
System.out.println(copy.toString(true));
// { "a":1, "b":"hello" }
```

The compact binary formats — `cbor`, `bson`, `smile`, `msgpack`, `ion`, `kryo` — are used the same
way (`node.toBinary("cbor")`) once `datatree-adapters` (and that format's backing library) is
present. See [Performance of binary APIs](performance-binary.html) for a comparison.

## To a file, stream or channel

`writeTo(String path)` (or `writeTo(File)`) writes the document, **guessing the format from the file
extension** and defaulting to JSON when the extension is unknown:

```java
Tree node = new Tree().put("key", "value");
node.writeTo("/path/to/file.json");       // JSON, from the .json extension
node.writeTo("/path/to/file.cbor");       // CBOR (needs datatree-adapters)
```

Pass the format (and `insertMeta`) explicitly to override the guess, or write to an `OutputStream` /
`WritableByteChannel`:

```java
node.writeTo(new File("data.bin"), "java");
node.writeTo(outputStream, "json");
node.writeTo(byteChannel, "json");
```

## Parsing / loading

Every output path has a matching input path. Construct a `Tree` from a String, a byte array, a
`File`, a `URL`, an `InputStream` or a `ReadableByteChannel`:

```java
Tree a = new Tree(jsonString);                       // JSON String
Tree b = new Tree(yamlString, "yaml");               // String + format
Tree c = new Tree(jsonBytes);                        // JSON byte[]
Tree d = new Tree(cborBytes, "cbor");                // byte[] + format
Tree e = new Tree(new File("data.yaml"));            // format from extension
Tree f = new Tree(new URL("http://host/data.json")); // format from extension
Tree g = new Tree(inputStream, "json");
Tree h = new Tree(byteChannel, "json");
```

A round-trip through a file:

```java
Tree node = new Tree().put("key", "value");
node.writeTo("/tmp/data.json");
Tree loaded = new Tree(new File("/tmp/data.json"));
System.out.println(loaded.toString(true));
// { "key":"value" }
```

For `File` and `URL` sources the format is taken from the extension (`.json`, `.yaml`/`.yml`,
`.bson`, `.cbor`, …). If the extension is missing, unrecognized, or its adapter is not on the
classpath, the source is parsed as **JSON**. To force a specific format, use the two-argument
constructor.

## The `_meta` block

A document may carry an optional metadata node alongside its body — see
[A record + its metadata block](collections.html#a-record-its-metadata-block-meta) for how to build
one with `getMeta()`. The `insertMeta` flag controls whether that block is serialized:

- `toString()` and `toString(format, pretty, true)` **include** the metadata (under the `_meta`
  key).
- `toString(boolean)`, `toString(format)`, `toString(format, pretty)`, `toBinary()` and
  `toBinary(format)` **omit** it.

```java
Tree rsp = new Tree().put("result", "ok");
rsp.getMeta().put("$statusCode", 200);

rsp.toString();        // { "result":"ok", "_meta":{ "$statusCode":200 } }
rsp.toString(true);    // { "result":"ok" }
```

The key used for the metadata block defaults to `_meta` and can be changed with the
`-Ddatatree.meta.name=…` system property.

## Format availability &amp; dependencies

The **`datatree-core`** artifact ships only three built-in formats, available with no extra
dependency:

| Format | Kind | Notes |
|---|---|---|
| `json` | text | The default. Reader + writer. |
| `java` | binary | Standard Java serialization. |
| `debug` | text | Human-readable, type-annotated dump (output only). |

All other formats are provided by the **`datatree-adapters`** artifact (plus the backing library for
that format). Add it to use them:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>
```

| Format | Page | Kind |
|---|---|---|
| JSON | [format-json.html](format-json.html) | text (built-in) |
| XML | [format-xml.html](format-xml.html) | text |
| YAML | [format-yaml.html](format-yaml.html) | text |
| TOML | [format-toml.html](format-toml.html) | text |
| Java Properties | [format-properties.html](format-properties.html) | text |
| CSV | [format-csv.html](format-csv.html) | text |
| TSV | [format-tsv.html](format-tsv.html) | text |
| CBOR | [format-cbor.html](format-cbor.html) | binary |
| BSON | [format-bson.html](format-bson.html) | binary |
| SMILE | [format-smile.html](format-smile.html) | binary |
| Amazon ION | [format-ion.html](format-ion.html) | binary |
| MessagePack | [format-msgpack.html](format-msgpack.html) | binary |
| Java Serialization | [format-java.html](format-java.html) | binary (built-in) |
| Kryo | [format-kryo.html](format-kryo.html) | binary |

> **If a format's adapter is missing, the call fails fast.** Asking for a format by name —
> `toString("yaml")`, `toBinary("cbor")`, `new Tree(s, "yaml")` — when no adapter is registered
> prints a hint about the Maven dependency to add and then throws an `IllegalArgumentException`. It
> does **not** silently produce JSON. To branch on availability instead of catching the exception,
> check first:
>
> ```java
> if (TreeWriterRegistry.isAvailable("yaml")) {
>     String yaml = node.toString("yaml");
> }
> ```
>
> (`io.datatree.dom.TreeReaderRegistry.isAvailable(format)` does the same for parsing.) The
> extension-based auto-detection used by the `File`/`URL` constructors and `writeTo(String)` is the
> one exception: an unknown or unavailable extension falls back to JSON rather than throwing.
