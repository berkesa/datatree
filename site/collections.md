# Maps, lists, sets &amp; nested structures

This is the page that ties everything together. It builds the payloads developers actually move
around with `Tree` — request parameters, list responses, records with a hidden metadata block,
descriptor objects — and for every shape it shows the same three steps: **build it &rarr; show the
JSON &rarr; read it back.** The examples are generic (a request payload, a list of user records, a
response with a metadata block) but modelled on real message shapes.

If you need the underlying method reference, see [Data Manipulation](manipulation.html) (write side)
and [Reading values](reading-values.html) (read side).

```java
import io.datatree.Tree;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
```

> **Read this gotcha first.** `putList(path)` / `putMap(path)` / `putSet(path)` return the **new
> child container**, not the document root. Keep a reference to the root (or call `getRoot()`) when
> you want to serialize the whole structure:
>
> ```java
> Tree root = new Tree();
> Tree ids  = root.putList("ids");   // 'ids' is the LIST node, not the root
> ids.add(101).add(102);
> System.out.println(root.toString(true));
> // { "ids":[101,102] }
> ```

## Flat object (Map) — a request payload

The simplest shape is a flat map of named values — for example the parameters of a service call.
Because the keys are flat (no dots), the `put` calls chain on the root:

```java
Tree params = new Tree()
        .put("firstName", "John")
        .put("lastName", "Doe")
        .put("age", 30)
        .put("active", true);
```

```json
{
  "firstName":"John",
  "lastName":"Doe",
  "age":30,
  "active":true
}
```

Read it back with typed getters and null-safe defaults:

```java
String firstName = params.get("firstName").asString();   // "John"
int    age       = params.get("age", 0);                  // 30
String nickname  = params.get("nickname", "n/a");         // "n/a" (absent)

for (Tree field : params) {
    System.out.println(field.getName() + " = " + field.asString());
}
```

## Array of scalars (List) — a list of ids or tags

```java
Tree root = new Tree();
Tree ids  = root.putList("ids");
ids.add(101).add(102).add(103);
```

```json
{
  "ids":[
    101,
    102,
    103
  ]
}
```

Process the elements with a stream, or bulk-convert them to a typed `List`:

```java
int sum = root.get("ids").stream().mapToInt(Tree::asInteger).sum();   // 306
List<Integer> asList = root.get("ids").asList(Integer.class);         // [101, 102, 103]
```

## Set — deduplicating values (e.g. roles)

A `Set` node behaves exactly like a `List` for the caller — same `add` API — but it stores no
duplicates:

```java
Tree root  = new Tree();
Tree roles = root.putSet("roles");
roles.add("admin").add("user").add("admin");   // "admin" added twice, stored once
```

```json
{
  "roles":[
    "admin",
    "user"
  ]
}
```

## Array of objects — the headline case

A list whose elements are maps is the shape you reach for constantly: a list of records. Use
`addMap()` to append a fresh object and fill it:

```java
Tree root  = new Tree();
Tree users = root.putList("users");
users.addMap().put("id", 1).put("name", "Alice");
users.addMap().put("id", 2).put("name", "Bob");
```

```json
{
  "users":[
    {
      "id":1,
      "name":"Alice"
    },
    {
      "id":2,
      "name":"Bob"
    }
  ]
}
```

Read it back three ways — iterate, by index, and by path:

```java
// 1) Iterate the records:
for (Tree user : root.get("users")) {
    int    id   = user.get("id", 0);
    String name = user.get("name", "");
    System.out.println(id + " -> " + name);
}
// 1 -> Alice
// 2 -> Bob

// 2) By path, with an array index:
String second = root.get("users[1].name").asString();   // "Bob"
```

## Object with a nested array + nested object — a descriptor

Real descriptors nest: an array of objects, each of which contains its own arrays. Build one with
`addMap().putList(...)`, then read deep values both by nested loops and by path:

```java
Tree root     = new Tree();
Tree services = root.putList("services");
Tree svc      = services.addMap();
svc.put("name", "math");
svc.putList("nodes").add("node-1").add("node-2");
svc.putList("ipList").add("10.0.0.1").add("10.0.0.2");
```

```json
{
  "services":[
    {
      "name":"math",
      "nodes":[
        "node-1",
        "node-2"
      ],
      "ipList":[
        "10.0.0.1",
        "10.0.0.2"
      ]
    }
  ]
}
```

```java
// Nested-loop idiom: walk services, then the nodes inside each:
for (Tree serviceInfo : root.get("services")) {
    String name = serviceInfo.get("name", "");
    for (Tree node : serviceInfo.get("nodes")) {
        String nodeID = node.asString();
    }
}

// Or read a deep value directly by path:
String firstIp = root.get("services[0].ipList[0]").asString();   // "10.0.0.1"
```

## A record + its metadata block (`_meta`)

A document can carry a **metadata** node next to its body. This is how a clean response body keeps
its status code and headers out of the way: the body holds the data, `getMeta()` holds the
out-of-band fields.

```java
Tree rsp = new Tree();
rsp.put("result", "ok");

Tree meta = rsp.getMeta();
meta.put("$statusCode", 200);
meta.put("$responseType", "application/json");
Tree headers = meta.putMap("$responseHeaders");
headers.put("Content-Type", "application/json; charset=utf-8");
```

`toString()` (no arguments) serializes the body **with** the metadata block, under the `_meta` key:

```json
{
  "result":"ok",
  "_meta":{
    "$statusCode":200,
    "$responseType":"application/json",
    "$responseHeaders":{
      "Content-Type":"application/json; charset=utf-8"
    }
  }
}
```

`toString(true)` serializes the body **without** the metadata:

```json
{
  "result":"ok"
}
```

Whether the metadata appears in the output is controlled by the `insertMeta` flag — see
[The `_meta` block](serialization.html#the-meta-block).

## From existing Java collections

You can wrap a prepared Java `Map`/`List` directly, or drop one into a path. `new Tree(map)` makes
the map the document root:

```java
Map<String, Object> address = new LinkedHashMap<>();
address.put("city", "Phoenix");
address.put("zip", "85001");

Tree root = new Tree(address);
```

```json
{
  "city":"Phoenix",
  "zip":"85001"
}
```

`putObject(path, object)` (or `addObject` / `setObject`) places a prepared object under a key:

```java
Tree doc = new Tree();
doc.putObject("address", address);
```

```json
{
  "address":{
    "city":"Phoenix",
    "zip":"85001"
  }
}
```

The `*Object` methods unwrap `Tree` values for you: if the object you pass is a `Tree`, or a
`Collection`/array that contains `Tree` elements, the underlying values are stored (not the `Tree`
wrappers), so the result serializes cleanly.

## Converting between container types

`setType(Class)` converts a node's container in place. Converting a `Map` whose keys are `"0"`,
`"1"`, … to a `List` keeps the values in order and drops the keys:

```java
Tree root = new Tree();
Tree data = root.putMap("data");
data.put("0", "a").put("1", "b");

root.get("data").setType(List.class);   // Map -> List
```

```json
{
  "data":[
    "a",
    "b"
  ]
}
```

A `Set` never stores duplicates, so converting a `List` with repeats to a `Set` collapses them. With
these building blocks — flat maps, scalar lists, deduplicating sets, arrays of objects, deep
nesting, and an optional metadata block — you can construct and read **any** request, response or
event shape.

Next: [Serialization &amp; I/O](serialization.html) turns these structures into JSON (or other
formats) and parses them back.
