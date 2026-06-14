# Data Manipulation

This page covers the **write side** of the `Tree` API: how to create a document and how to
build and modify its content. Reading values back out is covered on
[Reading values](reading-values.html); maps, lists, sets and nested structures get a dedicated,
example-rich page in [Maps, lists, sets &amp; nested structures](collections.html); and turning a
`Tree` into JSON (or another format) and back is on [Serialization &amp; I/O](serialization.html).

Everything in DataTree is a single node type, `io.datatree.Tree`. A `Tree` wraps one Java value
(a `Map`, a `List`/`Set`, or a scalar such as `Integer` or `String`) together with pointers to its
parent and root. There is no separate typed-node hierarchy, so you never cast.

```java
import io.datatree.Tree;
```

## Creating a Tree

Create an empty document. The root starts as an empty `Map` (~= an empty JSON object):

```java
Tree node = new Tree();
// {}
```

Parse a JSON `String`:

```java
Tree node = new Tree("{\"a\":2,\"b\":\"text\"}");
// { "a":2, "b":"text" }
```

Parse a `String` in another format (the second argument is the format name). The non-JSON formats
require the `datatree-adapters` artifact on the classpath — see
[Serialization &amp; I/O](serialization.html#format-availability-dependencies):

```java
Tree node = new Tree(yamlString, "yaml");
```

Parse a JSON byte array, or a byte array in another format:

```java
Tree node = new Tree(jsonBytes);             // JSON
Tree node = new Tree(cborBytes, "cbor");     // any registered format
```

Wrap an existing Java `Map` or `Collection` (the `Tree` operates on the live object):

```java
Map<String, Object> map = new LinkedHashMap<>();
map.put("city", "Phoenix");
Tree node = new Tree(map);
// { "city":"Phoenix" }
```

You can also load directly from a `File`, `URL`, `InputStream` or `ReadableByteChannel`; the
`File`/`URL` forms guess the format from the file extension. Those constructors are described on the
[Serialization &amp; I/O](serialization.html#parsing-loading) page.

## Handling the name of a node

`getName()` returns the key under which this node is stored in its parent (the array index for list
elements):

```java
Tree node = new Tree("{\"a\":3}");
System.out.println(node.get("a").getName());
// a
```

`setName(String)` renames the node:

```java
Tree node = new Tree("{\"a\":3}");
node.get("a").setName("b");
System.out.println(node.toString(true));
// { "b":3 }
```

## Working with JSON paths

`getPath()` returns the absolute path of a node, using the same JavaScript-like syntax accepted by
`get`/`put`:

```java
Tree node = new Tree("{\"a\":{\"b\":[1,2,3,4]}}");
System.out.println(node.get("a").get("b").get(1).getPath());
// a.b[1]
```

`getPath(int startIndex)` lets you choose the first array index (pass `1` for one-based indexing):

```java
System.out.println(node.get("a").get("b").get(1).getPath(1));
// a.b[2]
```

## Type of a node

`getType()` returns the `Class` of the wrapped value (or `null` for a `null` value):

```java
Tree node = new Tree().put("a", 5);
System.out.println(node.get("a").getType());
// class java.lang.Integer
```

`setType(Class)` converts the value to another type **in place**, using the built-in type
converters:

```java
Tree node = new Tree().put("a", "123");
node.get("a").setType(Integer.class);
System.out.println(node.toString(true));
// { "a":123 }
```

`setType` also converts between container types (e.g. `Map` &rarr; `List`); see
[Converting between container types](collections.html#converting-between-container-types).

## Parent and root

```java
Tree parent = node.getParent();   // null if node is the root
Tree root   = node.getRoot();     // the top-level document node
boolean top = node.isRoot();
```

## The metadata container

A document may carry an optional **metadata** node at its root — a separate map for processing
instructions and variables (status codes, headers, session id, …) that lives alongside the document
body. `getMeta()` returns it, creating it on first use:

```java
Tree meta = node.getMeta();
meta.put("$statusCode", 200);
```

How the metadata block is serialized (and how to keep it out of the output) is covered in
[A record + its metadata block](collections.html#a-record-its-metadata-block-meta) and on the
[Serialization &amp; I/O](serialization.html#the-meta-block) page.

## Setting the value of the current node

Once you have a node, `set(...)` replaces its value. There is an overload for every scalar type
(`byte short int long float double boolean String Date UUID BigDecimal BigInteger InetAddress
byte[]`):

```java
Tree node = new Tree();
node.put("a.b.c", true);

// Replace the boolean with a number:
node.get("a.b.c").set(123);
// { "a":{ "b":{ "c":123 } } }
```

For a byte array you can choose how it is rendered as text — Base64 (the default) is controlled with
the `asBase64String` flag:

```java
Tree node = new Tree();
node.put("raw", new byte[] { 1, 2, 3, 4 }, false);   // numeric array form
node.put("text", new byte[] { 1, 2, 3, 4 }, true);   // Base64 string
```

`setMap()`, `setList()` and `setSet()` change the current node into an (empty) container of that
type and return it:

```java
Tree node = new Tree();
node.put("a.b.c", true);

// Turn the boolean into a List, then fill it:
node.get("a.b.c").setList().add(1).add(2);
// { "a":{ "b":{ "c":[1,2] } } }
```

`setObject(Object)` stores an arbitrary Java object (scalar, `Map`, `Collection`, array, or even
another `Tree`'s value).

## Building arrays

On a `List` or `Set` node, `add(...)` appends a value and **returns the same list/set node**, so
calls chain:

```java
Tree node = new Tree();
node.putList("path.to.array").add(1).add(2).add(3);
// { "path":{ "to":{ "array":[1,2,3] } } }
```

> **Note:** there is no single-argument `put(String)`. To create an array at a path, use
> `putList(path)` (or `putMap`/`putSet`) and then `add(...)`.

`addMap()`, `addList()` and `addSet()` append a new **container** to a list and return the new
child, so you can build arrays of objects:

```java
Tree node  = new Tree();
Tree array = node.putList("rows");
array.addMap().put("a", 1).put("b", 2);
array.addMap().put("c", 3).put("d", 4);
// { "rows":[ { "a":1,"b":2 }, { "c":3,"d":4 } ] }
```

`insert(int index, ...)` (and `insertMap/insertList/insertSet(int)`, `insertObject(int, …)`) place
a value at a specific position:

```java
Tree node  = new Tree();
Tree array = node.putList("list").add("a").add("b").add("c");
array.insert(1, "X");
// { "list":["a","X","b","c"] }
```

## Path-based writes

`put(String path, value)` associates a value with a path, **auto-creating** any missing
intermediate nodes. There is a `put` overload for every scalar type:

```java
Tree node = new Tree();
node.put("a.b.c", "value");
// { "a":{ "b":{ "c":"value" } } }
```

Array indices work inside paths; gaps are filled with `null`:

```java
Tree node = new Tree();
node.put("b[0]", 6);
node.put("b[2]", 8);
// { "b":[6,null,8] }
```

`putMap(path)`, `putList(path)` and `putSet(path)` create an empty container at the path and
**return that new container node** (not the document root):

```java
Tree node = new Tree();
Tree list = node.putList("a.b.c");
list.add(1).add(2).add(3);
// { "a":{ "b":{ "c":[1,2,3] } } }
```

> **Keep a reference to the root.** `putList`/`putMap`/`putSet` return the *child* they created, and
> `put("x.y", value)` returns the *container* that received the value — **not** the root. When you
> need the whole document afterwards, keep the `new Tree()` reference (or call `getRoot()`):
> ```java
> Tree root = new Tree();
> Tree ids  = root.putList("ids");   // 'ids' is the LIST node, not the root
> ids.add(101).add(102);
> System.out.println(root.toString(true));
> // { "ids":[101,102] }
> ```
> Chaining several `put` calls on the root works only as long as the keys are **flat** (no dots):
> `new Tree().put("a", 1).put("b", 2)` returns the root each time, so it builds `{ "a":1, "b":2 }`.

Each container method has a `putIfAbsent` variant: when `true` and the path already holds a value,
the existing container is returned (and kept) instead of being replaced:

```java
Tree node = new Tree();
node.putList("ids", true).add(1).add(2);
node.putList("ids", true).add(3);   // reuses the existing list
// { "ids":[1,2,3] }
```

`putObject(path, Object[, putIfAbsent])` drops an arbitrary Java object (or another `Tree`'s value)
into a path — see
[From existing Java collections](collections.html#from-existing-java-collections).

## Removing and clearing

`remove(String path)` deletes the node at a path and returns the removed node (or `null`):

```java
Tree node = new Tree().put("a.b", 1).put("a.c", 2);
node.remove("a.b");
// { "a":{ "c":2 } }
```

Other removal methods:

```java
node.remove(index);            // remove a list element by position
node.remove(childTree);        // remove a specific child
node.remove();                 // remove THIS node from its parent
node.removeFirst();            // remove & return the first child
node.removeLast();             // remove & return the last child
```

`remove(Predicate<Tree>)` removes the first matching child; pass `true` as the second argument to
remove **all** matches:

```java
Tree node = new Tree();
node.putList("nums").add(1).add(2).add(3).add(4);
node.get("nums").remove(child -> child.asInteger() % 2 == 0, true);
// { "nums":[1,3] }
```

`clear()` empties a node (keeps the node, drops its children); `clear(String path)` empties the
sub-node at a path (creating an empty map if it does not exist):

```java
Tree node = new Tree().put("a", 1).put("b", 2);
node.clear();
node.put("c", 3);
// { "c":3 }
```

## Merging and copying

`copyFrom(source)` appends every child of `source` into this node. For a map, same-named children
are **overwritten** by default:

```java
Tree target = new Tree().put("a", 1).put("b", 2);
Tree source = new Tree().put("b", 20).put("c", 30);
target.copyFrom(source);
// { "a":1, "b":20, "c":30 }
```

Pass `false` as `overwriteExisting` to keep the existing values:

```java
Tree target = new Tree().put("a", 1).put("b", 2);
Tree source = new Tree().put("b", 20).put("c", 30);
target.copyFrom(source, false);
// { "a":1, "b":2, "c":30 }
```

Restrict the copy to named fields:

```java
Tree target = new Tree();
Tree source = new Tree().put("a", 1).put("b", 2).put("c", 3);
target.copyFrom(source, "a", "c");
// { "a":1, "c":3 }
```

…or to fields selected by a `Predicate`:

```java
Tree target = new Tree();
Tree source = new Tree().put("blue", 1).put("black", 2).put("red", 3);
target.copyFrom(source, child -> child.getName().startsWith("bl"));
// { "blue":1, "black":2 }
```

`assign(Tree source)` replaces **this** node's value with a deep copy of the source node's value
(rather than merging children into it):

```java
Tree root   = new Tree();
Tree first  = root.putList("first").add(1).add(2).add(3);
Tree second = root.putList("second");
second.assign(first);
// { "first":[1,2,3], "second":[1,2,3] }
```

## Deep clone

`clone()` makes a recursive **deep copy**; mutating the copy never affects the original:

```java
Tree original = new Tree();
original.put("user.name", "Alice");

Tree copy = original.clone();
copy.put("user.name", "Bob");

System.out.println(original.get("user.name").asString());   // Alice
System.out.println(copy.get("user.name").asString());       // Bob
```

## Quick reference — building &amp; modifying

| Method | What it does |
|---|---|
| `new Tree()` | Empty document (root is an empty Map). |
| `new Tree(json)` / `new Tree(s, format)` | Parse a String. |
| `new Tree(map)` / `new Tree(collection)` | Wrap an existing Java map / collection. |
| `set(value)` | Replace this node's value (one overload per scalar type). |
| `setMap()` / `setList()` / `setSet()` | Turn this node into an empty container. |
| `setObject(obj)` | Store an arbitrary Java object. |
| `add(value)` | Append to a List/Set (chainable). |
| `addMap()` / `addList()` / `addSet()` | Append a new container and return it. |
| `insert(index, value)` / `insertMap/List/Set(index)` | Insert at a position. |
| `put(path, value)` | Set a value by path, auto-creating intermediates. |
| `putMap(path)` / `putList(path)` / `putSet(path)` | Create a container at a path, return it. |
| `putObject(path, obj)` | Put an arbitrary object at a path. |
| `remove(path/index/child)` / `remove()` | Remove a node. |
| `remove(predicate[, all])` | Remove matching child/children. |
| `removeFirst()` / `removeLast()` | Remove and return an end element. |
| `clear()` / `clear(path)` | Empty a node. |
| `copyFrom(source[, …])` | Merge children from another node. |
| `assign(source)` | Replace this node's value with a copy of another node's. |
| `clone()` | Recursive deep copy. |

Continue with [Reading values](reading-values.html) to get data back out.
