# Reading values

This page covers the **read side** of the `Tree` API: getting values back out of a document, with
automatic type conversion, null-safe defaults, iteration and type inspection. For *building* a
document see [Data Manipulation](manipulation.html); for working with whole collections and nested
shapes see [Maps, lists, sets &amp; nested structures](collections.html).

The getters described here **never mutate** the node — they read the value and (when needed) convert
a copy of it.

## Raw vs. converted values

`asObject()` returns the underlying Java value, untouched:

```java
Tree node = new Tree().put("a", 42);
Object raw = node.get("a").asObject();
// raw == Integer 42
```

The typed getters return the value **converted** to the requested type. Conversion is automatic, so
a node that holds the String `"2"` still reads back as the number `2`:

```java
Tree node = new Tree().put("count", "2");
int n = node.get("count").asInteger();
// n + 3 == 5
```

There is a getter for every scalar type — `asByte asShort asInteger asLong asFloat asDouble
asBoolean asString asBytes asDate asUUID asBigDecimal asBigInteger asInetAddress`. Each returns the
boxed value (or `null` if the node's value is `null` or cannot be converted):

```java
Tree node = new Tree();
node.put("name", "John");
node.put("age", 30);
node.put("balance", "1234.56");

String     name    = node.get("name").asString();        // "John"
Integer    age     = node.get("age").asInteger();         // 30
BigDecimal balance = node.get("balance").asBigDecimal();  // 1234.56
```

## Reading by path

`get(String path)` returns the child `Tree` at a path, or `null` if the path does not exist:

```java
Tree node = new Tree().put("a", 1);
node.get("a");         // a Tree wrapping 1
node.get("missing");   // null
```

`isExists(String path)` tells you whether a path is present:

```java
node.isExists("a");        // true
node.isExists("missing");  // false
```

### Null-safe defaults

The `get(path, defaultValue)` family reads **and converts** in one call, returning the default when
the path is missing or the value cannot be converted. This is the null-safe way to read — no
intermediate `null` check, no `NullPointerException`. There is an overload for each scalar type
(`int double byte float long boolean byte[] short String UUID Date BigDecimal BigInteger
InetAddress`):

```java
Tree node = new Tree().put("age", 30);

int    age     = node.get("age", 0);          // 30
int    weight  = node.get("weight", -1);      // -1  (path missing)
String nick    = node.get("nickname", "n/a"); // "n/a"
```

`getObject(path, defaultValue)` is the generic version. It converts to the default value's type and
falls back to the default when the path is missing:

```java
Tree node = new Tree().put("price", "19.99");
BigDecimal price = node.getObject("price", BigDecimal.ZERO);
// 19.99
```

## Reading collections

`asList(Class<T>)` copies all children of a node into a `List`, converting each element to `T`:

```java
Tree node = new Tree();
node.putList("ids").add("1").add("2").add("3");

List<Integer> ids = node.get("ids").asList(Integer.class);
// [1, 2, 3]
```

A `Tree` is `Iterable<Tree>`, so a `for` loop walks the **elements** of a list/set or the
**values** of a map. For a list, iterate the children directly:

```java
Tree node = new Tree();
node.putList("ids").add(101).add(102).add(103);

for (Tree child : node.get("ids")) {
    System.out.print(child.asInteger() + " ");
}
// 101 102 103
```

For a map, each iterated child carries its key — read it with `getName()`:

```java
Tree node = new Tree().put("firstName", "John").put("lastName", "Doe");

for (Tree child : node) {
    System.out.println(child.getName() + "=" + child.asString());
}
// firstName=John
// lastName=Doe
```

`stream()` returns a `Stream<Tree>` for filter/map/collect pipelines:

```java
Tree node = new Tree();
node.putList("nums").add(1).add(2).add(3).add(4).add(5).add(6);

int evenSum = node.get("nums").stream()
        .filter(c -> c.asInteger() % 2 == 0)
        .mapToInt(Tree::asInteger)
        .sum();
// 12
```

## Navigating children

Beyond path access, you can move around by position:

```java
Tree node = new Tree();
node.putList("items").add("a").add("b").add("c");
Tree items = node.get("items");

items.size();                 // 3
items.isEmpty();              // false
items.getFirstChild();        // "a"
items.getLastChild();         // "c"
items.get(1);                 // "b"  (by index)
```

`getNextSibling()` / `getPreviousSibling()` move between the children of a map:

```java
Tree node = new Tree().put("a", 1).put("b", 2).put("c", 3);
Tree b = node.get("b");

b.getPreviousSibling().getName();   // "a"
b.getNextSibling().getName();       // "c"
```

## Inspecting the type

`getType()` returns the value's class; the `is…` predicates answer specific questions without
casting:

```java
Tree node = new Tree();
node.put("name", "John");
node.putList("tags").add("x");
node.putMap("address").put("city", "Phoenix");

node.get("name").isPrimitive();   // true
node.get("tags").isList();        // true
node.get("address").isMap();      // true
```

The full set is `isNull`, `isPrimitive`, `isStructure`, `isMap`, `isList`, `isSet`, `isArray`,
`isEnumeration` (List/Set/array) and `isEmpty`.

Next: [Maps, lists, sets &amp; nested structures](collections.html) puts the write and read sides
together on realistic message shapes.
