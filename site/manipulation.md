## Creating Tree structures

(TODO)

Creates an empty root structure:

```java
Tree node = new Tree();
```

Creates a hierarchial structure by a JSON String:

```java
Tree node = new Tree("{\"a\":2,\"b\":\"text\"}");
```

Creates a hierarchial structure by a String with the specified format:

```java
Tree node = new Tree(yamlString, "yaml");
```

Creates a hierarchial structure by a JSON byte array:

```java
byte[] bytes = // JSON bytes
Tree node = new Tree(bytes);
```

Creates a hierarchial structure by a JSON, XML, YAML, CBOR, SMILE, CSV,
TOML or other binary source array:

```java
byte[] bytes = // loaded from file
Tree node = new Tree(bytes, "msgpack");
```

## Handling name of a node

Returns the name of this node:

```java
Tree node = new Tree("{\"a\":3}");
System.out.println(node.get("a").getName());

// This code above prints "a"
```

Changes this node's name:

```java
Tree node = new Tree("{\"a\":3}");
node.get("a").setName("b");
System.out.println(node.toJSON());

// This code above prints "{"b": 3}"
```

## Working with JSON Paths

Returns the absolute path of this node:

```java
Tree node = new Tree("{\"a\":{\"b\":[1,2,3,4]}}");
String path = node.get("a").get("b").get(1).getPath();
System.out.println(path);

// This code above prints "a.b[1]"
```

Returns the absolute path of this node:

```java
Tree node = new Tree("{\"a\":{\"b\":[1,2,3,4]}}");
String path = node.get("a").get("b").get(1).getPath(1);
System.out.println(path);

// This code above prints "a.b[2]"
```

## Type of a node

Returns the value's class of this node:

```java
Class c = node.getType();
```

Sets this node's type and converts the value into the specified type:

```java
node.setType(String.class)
```

## Accessing parent and root node

Returns this node's parent. If this node is a root node, returns null:

```java
Tree parent = node.getParent();
```

Returns the top-level (document) node of the structure:

```java
Tree root = node.getRoot();
```

## Using the meta-data container

Returns the metadata node of this document structure. Creates new
metadata node, if it doesn't exist. Meta node contains optional
processing instructions and variables (language code, session ID, request
and response variables, etc). One document (hierarchial node structure)
has only one meta node at root level.

```java
Tree meta = node.getMeta();
```

## Set the value of the current node

Sets the current node's value to the specified value:

```java
Tree node = new Tree();
node.put("a.b.c", true);

// For change the value:
node.get("a.b.c").set(123);
```

Sets the current node's type to Map (~= JSON object):

```java
Tree node = new Tree();
node.put("a.b.c", true);

// For change the boolean value to Map:
Tree map = node.get("a.b.c").setMap();
```

Sets the current node's type to List (~= JSON array):

```java
Tree node = new Tree();
node.put("a.b.c", true);

// For change the boolean value to List:
Tree list = node.get("a.b.c").setList();
```

Sets the current node's type to Set:

```java
Tree node = new Tree();
node.put("a.b.c", true);

// For change the boolean value to Set:
Tree set = node.get("a.b.c").setSet();
```

## Adding items to a List or Set

Appends the specified value to the end of this List (or adds the
value to a Set - it depends on the type of this node):

```java
Tree node = new Tree();
node.put("path.to.array").add(1).add(2);
```

Appends the specified Map to the end of this List (or adds the value to
a Set - it depends on the type of this node):

```java
Tree node = new Tree();
Tree array = node.putList("path.to.array");
Tree map1 = array.addMap().put("a", 1).put("b", 2);
Tree map2 = array.addMap().put("c", 3).put("d", 4);
```

Appends the specified List to the end of this List (or adds the value to
a Set - it depends on the type of this node):

```java
Tree node = new Tree();
Tree array = node.putList("path.to.array");
Tree map1 = array.addList().add(1).add(2);
Tree map2 = array.addList().add("a").add("b");
```

Appends the specified Set to the end of this List (or adds the value to
a Set - it depends on the type of this node):

```java
Tree node = new Tree();
Tree array = node.putList("path.to.array");
Tree map1 = array.addSet().add(1).add(2);
Tree map2 = array.addSet().add("a").add("b");
```

## Inserting into a List

Inserts the specified value at the specified position in this List:

```java
Tree node = new Tree();
Tree array = node.putList("path.to.array");

Tree childValue = array.insert(12, "value");
Tree childMap   = array.insertMap(7);
Tree childList  = array.insertList(5);
Tree childSet   = array.insertSet(3);
```

## Path-based value setters

Associates the specified value with the specified path. If the
structure previously contained a mapping for the path, the old value is
replaced.

```java
Tree node = new Tree();
node.put("a.b.c", "new value");
```

Associates the specified Map (~= JSON object) container with the
specified path. If the structure previously contained a mapping for the
path, the old value is replaced.

```java
Tree node = new Tree();
Tree map = node.putMap("a.b.c");
map.put("d.e.f", 123);
```

Associates the specified List (~= JSON array) container with the
specified path. If the structure previously contained a mapping for the
path, the old value is replaced.

```java
Tree node = new Tree();
Tree list = node.putList("a.b.c");
list.add(1).add(2).add(3);
```

Associates the specified Set container with the specified path. If the
structure previously contained a mapping for the path, the old value is
replaced. Set similar to List, but contains no duplicate elements.

```java
Tree node = new Tree();
Tree set = node.putSet("a.b.c");
set.add(1).add(2).add(3);
```

## Basic value getters

Return raw value of a node.

```java
Object value = node.asObject();
```

(TODO)