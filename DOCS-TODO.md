# DOCS-TODO — Complete the **Tree API** documentation (datatree site)

> **You are the per-project Claude Code instance for `datatree`.** This file is self-contained.
> **Goal:** write high-quality, accurate reference + tutorial documentation for the `io.datatree.Tree`
> API on the project's own documentation website. The current guide (`site/manipulation.md`) is
> **explicitly unfinished** (`(TODO)` markers) and the existing examples contain **API bugs**. Finish
> it, fix the bugs, and add the missing pages so a reader can learn the whole `Tree` API — building,
> reading, working with maps/lists/sets and nested structures, and serializing to/from JSON and other
> formats.
>
> This is a **documentation-only** task. **Do not change Java source** under `src/main/java/` — it is
> the source of truth, not something to edit. (The separate `TODO.md` in this repo is the already-done
> 2.0.0 *code* modernization; ignore it except as background.)

---

## 0. The single most important rule: examples must be CORRECT

Every Java snippet you write **must compile and behave as shown against the real 2.0.0 API**. The API
is one class: **`src/main/java/io/datatree/Tree.java`** (~4650 lines, fully Javadoc'd). Treat it as the
authoritative reference — read the exact method signature and its Javadoc before you document it. The
existing `site/manipulation.md` was written against an older/looser API and contains at least these
**known bugs you must fix wherever they appear**:

| Bug in current docs | Why it's wrong | Correct form |
|---|---|---|
| `node.put("path.to.array").add(1)` | There is **no single-argument `put(String)`** overload. | `node.putList("path.to.array").add(1)` |
| `node.toJSON()` | `toJSON()` does not exist (only appears inside a Javadoc comment). | `node.toString()` or `node.toString("json")` |

**Strongly recommended:** before publishing, paste your examples into a scratch JUnit test under
`src/test/java/` (e.g. `DocExamplesTest`), run `mvn -q -Dtest=DocExamplesTest test` to confirm they
compile and the printed output matches, then **delete the scratch test** (don't commit it). The local
`~/.m2` already has `datatree-core:2.0.0-SNAPSHOT` installed, and tests run on JUnit 5. This is the
single best way to keep the docs honest. If you can't compile-check, at minimum re-read each method's
signature in `Tree.java` and confirm the parameter/return types.

---

## 1. How this documentation site works (VuePress 1.x)

Same `src`/`docs` split as every site in this workspace:

- **`site/*.md`** = the **authoring source** you edit. One file per page. `site/README.md` is the
  VuePress homepage (`home: true` frontmatter — leave it). `site/.vuepress/config.js` holds the
  title, `base: '/datatree/'`, `dest: '../docs'`, nav and **sidebar**. `site/.vuepress/public/` holds
  images; `site/.vuepress/styles/palette.styl` the theme (accent `#37996B`).
- **`docs/`** = the **generated HTML output**, committed and served by GitHub Pages at
  `https://berkesa.github.io/datatree/`. **Never hand-edit `docs/`** — it is overwritten on build.

**Build (must run from inside `site/`, because `dest:'../docs'` is relative to CWD):**
```bash
cd site
npx vuepress dev .      # live preview while writing
npx vuepress build .    # build → ../docs  (the committed output)
```
There is no `package.json` here, so VuePress resolves via `npx` (Node required).

### Audience, language & quality bar
- **Language: English.** The whole site is English; write all prose and comments in clear, concise
  English. The end result must read as a **complete, example-rich developer reference for engineers who
  use the `Tree` API** — not a changelog and not internal notes.
- **Comprehensive & example-first.** Every public capability in §3 is covered; every concept is shown
  with a **self-contained, runnable snippet** whose **output is shown inline**. Prefer realistic,
  message-shaped data over `foo`/`bar` (see Page 3). When in doubt, add another example.

### Authoring conventions (match the existing pages)
- **Adding a page needs two edits:** create `site/<name>.md` **and** add `['<name>', 'Sidebar Label']`
  to the right `sidebar` group in `site/.vuepress/config.js`. A page not in the sidebar is invisible.
- **Internal links use `.html`**, not `.md` (e.g. `format-json.html`, `reading-values.html#getters`),
  because they point at the built output.
- **House style** (see current `manipulation.md`): short `##`/`###` headings; one or two sentences of
  prose; then a fenced ```java block. Show the **result inline** as a comment
  (`// This prints "a.b[1]"` or a `// {... }` JSON block). Keep snippets minimal and focused on one idea.
- Images (if any) live in `site/.vuepress/public/` and are referenced by bare filename in raw HTML
  with `class="zoom"` for click-to-zoom.

---

## 2. Current state of the docs (what exists, what's missing)

Sidebar group **"DataTree Core API"** currently has two pages:
- `introduction.md` — high-level intro (leave largely as-is; only fix broken examples/versions if you spot them).
- `manipulation.md` — **"Data Manipulation", the Tree API guide — UNFINISHED.**

`manipulation.md` today covers (with examples): creating a `Tree`, node name (`getName`/`setName`),
JSON paths (`getPath`), type (`getType`/`setType`), parent/root, `getMeta`, value setters
(`set`/`setMap`/`setList`/`setSet`), adding to a list/set (`add`/`addMap`/`addList`/`addSet`),
inserting (`insert`/`insertMap`/...), path-based setters (`put`/`putMap`/`putList`/`putSet`), then it
**opens "Basic value getters", shows only `asObject()`, and stops at a `(TODO)`.** There is also a
`(TODO)` at the very top ("Creating Tree structures").

**Missing entirely** (this is the work): the whole read side, working with collections & nested
structures end-to-end, querying/iteration, removing, merging, cloning, and **serialization / file I/O
(generating JSON & other formats, parsing them back)**.

---

## 2b. Site scope — remove the Template-engine and Promise sections

**Decision (apply this as part of the task):** narrow this site to the **data-handling API only**. Keep
exactly **three** sidebar groups — **DataTree Core API**, **Text-based formats**, **Binary formats** —
and **remove** the **DataTree template engine** and **Asynchronous data processing** groups entirely.

**Why:** the Core API and the format adapters are *one and the same subject* — the same `Tree` document,
just serialized to different representations (JSON/XML/YAML/… and CBOR/BSON/MessagePack/…); they belong
together. The **template engine** (`datatree-templates`) and the **Promise / async** layer
(`datatree-promise`) are *separate products and separate topics* that don't fit a data-manipulation
reference. They ship as their own artifacts/repos, so removing them here only narrows this site's
scope — no content is lost.

**Steps:**
1. In `site/.vuepress/config.js`, **delete two whole sidebar groups**: `DataTree template engine`
   (children `template-introduction`, `template-usage`, `template-syntax`) and `Asynchronous data
   processing` (children `promise-introduction`, `promise-usage`). The final sidebar keeps only the
   three groups (see §5). Leave the `nav` array as-is (it doesn't reference these pages).
2. **Delete the orphaned source pages:** `site/template-introduction.md`, `site/template-usage.md`,
   `site/template-syntax.md`, `site/promise-introduction.md`, `site/promise-usage.md`.
3. **Fix dangling links.** Grep the *remaining* `site/*.md` — **including the homepage `site/README.md`**
   (`home: true`; its feature list may advertise the template engine / promise) — for
   `template-introduction`, `template-usage`, `template-syntax`, `promise-introduction`, `promise-usage`
   (and their `.html` forms). Remove or rephrase each reference so nothing points at a deleted page.
4. **Clean the build output.** After rebuilding (§6), make sure no stale generated files survive — delete
   `docs/template-introduction.html`, `docs/template-usage.html`, `docs/template-syntax.html`,
   `docs/promise-introduction.html`, `docs/promise-usage.html` if the VuePress 1.x build didn't prune
   them from `dest`. (The hashed `docs/assets/js/*.js` chunks are regenerated/managed by the build —
   leave those alone.)

**Cross-project inbound links (checked workspace-wide).** Removing these five pages breaks any link
that pointed at them. Status:
- ✅ **Removed by the orchestrator:** the `## Documentation` badges in `datatree-templates/README.md`
  (→ `template-introduction.html`) and `datatree-promise/README.md` (→ `promise-introduction.html`) —
  so they don't become dead links once the pages are gone.
- ✅ **Also removed by the orchestrator:** the moleculer `site` previously linked to these pages in 6
  spots (`site/src/actions.md`, `concepts.md` ×2, `moleculer-web.md` ×2, `performance-tips.md` — to
  `promise-introduction.html` / `template-introduction.html` / `template-syntax.html`). Those links were
  stripped (link removed, surrounding wording kept) so nothing points at the deleted pages. A
  workspace-wide grep now finds **zero** remaining links to the five removed pages.

---

## 3. The `Tree` API, grouped (your coverage checklist)

This is the map of what to document. **Confirm each signature in `Tree.java` before writing.** Methods
marked ⚠ have non-obvious behavior — read the Javadoc/body and document it precisely (don't guess).

**Construct / parse (input):**
`new Tree()` (empty; the root starts as a Map) · `new Tree(Map)` / `new Tree(Collection)` (wrap an
existing Java map/list/set) · `new Tree(String json)` / `new Tree(String, format)` · `new Tree(byte[])`
/ `new Tree(byte[], format)` · `new Tree(File)` / `(File, format)` · `new Tree(URL)` / `(URL, format)`
· `new Tree(InputStream …)` · `new Tree(ReadableByteChannel …)`. ⚠ The `File`/`URL` forms **guess the
format from the extension** (`.json`, `.yaml`, `.bson`, …) and **fall back to JSON** if unknown.

**Node identity / navigation:**
`getName`/`setName` · `getPath`/`getPath(startIndex)` · `getType`/`setType(Class)` ⚠(converts the
value) · `getParent`/`getRoot`/`isRoot` · `getFirstChild`/`getLastChild`/`getNextSibling`/
`getPreviousSibling`/`get(int index)` · `getMeta`/`getMeta(boolean)`/`hasMeta`/`isMeta`.

**Write / modify the current node:**
`set(…)` for every scalar (`byte short int long float double boolean byte[] String Date UUID
BigDecimal BigInteger InetAddress`), plus `set(byte[], boolean asBase64String)` and `setObject(Object)`
· `setMap()`/`setList()`/`setSet()` ⚠(change this node's container type) · append: `add(…)` (same
scalar set), `add(byte[], boolean asBase64String)`, `addMap()`/`addList()`/`addSet()`, `addObject`
⚠(appends to a List/Set; converts a Map node to a List) · positional: `insert(int, …)`,
`insertMap/insertList/insertSet(int)`, `insertObject(int, …)`.

**Write / modify by path:**
`put(String path, …)` for every scalar + `put(path, byte[], boolean asBase64String)` + `putObject(path,
Object[, boolean putIfAbsent])` · `putMap(path[, putIfAbsent])` / `putList(...)` / `putSet(...)`
⚠ Paths auto-create intermediate nodes (`put("a.b.c", 1)` makes `a`→`b`→`c`). Array indices in paths
work too (`put("b[0]", 6)`). **There is no `put(String)` with no value.**

**Read / extract values (output side — the big gap):**
`asObject()` (raw value) and the converting getters that **do not mutate** the node:
`asByte asShort asInteger asLong asFloat asDouble asBoolean asBytes asString asDate asUUID asBigDecimal
asBigInteger asInetAddress` (each returns the boxed type or `null`; conversion is automatic via the
type-converter layer — e.g. a node holding the String `"2"` returns `2` from `asInteger()`) · path
getters **with a default value**: `get(String path, int default)` and the same for
`double byte float long boolean byte[] short String UUID Date BigDecimal BigInteger InetAddress`
(returns the converted value, or the default if the path is missing/unconvertible) · `getObject(String
path, TO default)` (generic) · `get(String path)` → child `Tree` or `null` · `isExists(path)`.

**Read collections:** `asList(Class<T>)` ⚠(copies all children, each converted to `T`) · iteration:
`Tree` is `Iterable<Tree>`, so `for (Tree child : node)` walks a Map's values, a List's/Set's elements
· `stream()` → `Stream<Tree>`.

**Query / inspect / transform:**
`size()` / `isEmpty()` · type checks `isMap isList isSet isArray isNull isPrimitive isStructure
isEnumeration` · `find(Predicate<Tree>)` ⚠(confirm exact semantics from source — what it returns and
how deep it searches — before documenting) · `sort()` / `sort(Comparator<Tree>)` ⚠(confirm) ·
`stream()` for filter/map/collect pipelines.

**Remove / clear:**
`remove(String path)` · `remove(int index)` · `remove(Tree child)` · `remove()` (removes this node
from its parent) · `remove(Predicate<Tree>)` / `remove(Predicate, boolean allOccurrences)` ⚠ ·
`removeFirst()` / `removeLast()` · `clear()` / `clear(String path)`.

**Merge / copy / clone:**
`copyFrom(Tree source)` · `copyFrom(source, boolean overwriteExisting)` · `copyFrom(source, String…
fields)` · `copyFrom(source, Predicate<Tree>)` · `assign(Tree source)` ⚠ · `clone()` ⚠(deep clone —
verify and state that mutating the clone doesn't affect the original).

**Serialize / output (the other big gap):**
`toString()` ⚠(pretty-printed JSON, **includes** the meta block) · `toString(boolean pretty)` (JSON,
**no** meta) · `toString(String format)` · `toString(format, boolean pretty)` · `toString(format,
pretty, boolean insertMeta)` · `toBinary()` / `toBinary(format)` / `toBinary(format, insertMeta)` ·
`writeTo(String path)` / `writeTo(File[, format[, insertMeta]])` / `writeTo(OutputStream …)` /
`writeTo(WritableByteChannel …)`.
⚠ **Format availability:** `datatree-core` ships only the **built-in** `json`, `java`, and `debug`
writers/readers. Other formats (`yaml`, `xml`, `csv`, `tsv`, `toml`, `properties`, `cbor`, `bson`,
`smile`, `ion`, `msgpack`, `kryo`) require the **`datatree-adapters`** artifact on the classpath; if a
format's adapter is missing, the API falls back to JSON and prints a hint about which Maven dependency
to add. **Say this explicitly** on the serialization page and link each format to its existing
`format-*.html` page.

---

## 4. The page plan

Keep everything in the **"DataTree Core API"** sidebar group. Deliverable = **complete 1 page + add 3
new pages**, in this reading order. (This split is a recommendation; you may merge/rename if a better
structure emerges — but cover every group from §3 and keep pages focused and non-duplicative. State a
one-line scope at the top of each page and cross-link instead of repeating.)

### Page 1 — `manipulation.md` ✏️ COMPLETE (scope: *building & modifying* a tree)
Fill both `(TODO)`s, fix the §0 bugs, and round the page out so it tells the whole **write** story:
1. **Creating a Tree** — empty; from a JSON string; from another format string; from `byte[]`; from a
   `Map`/`Collection`; (mention File/URL/stream loading but defer the detail to *Serialization & I/O*).
2. Name / path / type / parent / root / meta — keep & tidy the existing sections.
3. **Setting the current node's value** — `set(…)` scalars, base64 bytes, `setMap/setList/setSet`.
4. **Building arrays** — `add`/`addMap`/`addList`/`addSet` and `insert…`; show method **chaining**
   (`putList("a.b.c").add(1).add(2).add(3)`).
5. **Path-based writes** — `put`, `putMap`, `putList`, `putSet`, `putObject`; auto-created intermediate
   nodes; the `putIfAbsent` variants; array indices in paths (`put("b[0]", 6)`).
6. **Removing & clearing** — `remove(path/index/child/Predicate)`, `removeFirst/Last`, `clear`.
7. **Merging & copying** — `copyFrom` (all 4 variants, with a before/after example for
   `overwriteExisting` and for the `fields…`/`Predicate` filters), `assign`.
8. **Deep clone** — `clone()` with a demonstration that the copy is independent.
> Move all *reading* and *serialization* examples out of this page into Pages 2 and 4.

### Page 2 — `reading-values.md` 🆕 (sidebar: **"Reading values"**) (scope: *getting data back out*)
1. **Raw vs. converted** — `asObject()` vs the typed getters; emphasize automatic type conversion
   (String `"2"` → `asInteger()` → `2`) and that getters don't mutate the node. Cover the full scalar
   set.
2. **Reading by path** — `get(path)` → `Tree`/`null`; `isExists(path)`; the `get(path, default)` family
   (missing path → default); `getObject(path, default)`. Show why defaults make code null-safe.
3. **Reading collections** — `asList(Class)`; iterating with `for (Tree child : node)` over a list and
   over a map (show `child.getName()` for map keys); `stream()` with a filter/map example.
4. **Navigating children** — `get(index)`, `getFirstChild/getLastChild`, `getNextSibling/
   getPreviousSibling`, `size()`, `isEmpty()`.
5. **Inspecting type** — `getType`, `isMap/isList/isSet/isArray/isNull/isPrimitive/isStructure`.

### Page 3 — `collections.md` 🆕 (sidebar: **"Maps, lists, sets & nested structures"**) — **the centerpiece**

> **Scope & framing.** The most example-heavy page and the main reason for this effort. Teach maps /
> lists / sets and **nested** structures by building the payloads developers actually move with `Tree`
> — request parameters, list responses, records with a hidden metadata block, descriptor objects. Keep
> them **generic enough that a reader needn't know Moleculer** ("a request payload", "a list of user
> records", "a response with a metadata block"), but **model them on real message shapes**. For every
> example: **build it → show the JSON (`toString(true)`) → read it back.**
>
> **Teach this gotcha early:** `putList(path)` / `putMap(path)` / `putSet(path)` return the **new child
> container**, not the document root — keep a root reference (or use `getRoot()`) to serialize the whole
> structure:
> ```java
> Tree root = new Tree();
> Tree ids  = root.putList("ids");   // 'ids' is the LIST node, not the root
> ids.add(101).add(102);
> System.out.println(root.toString(true));   // { "ids":[101,102] }
> ```

Sub-sections (each: build → JSON → read back):

1. **Flat object (Map) — a request payload** (model on a `broker.call` params object):
   ```java
   Tree params = new Tree()
       .put("firstName", "John")
       .put("lastName", "Doe")
       .put("age", 30)
       .put("active", true);
   // { "firstName":"John", "lastName":"Doe", "age":30, "active":true }
   ```
   Read back with typed getters + defaults: `params.get("firstName").asString()`,
   `int age = params.get("age", 0)`, `params.get("nickname", "n/a")`; iterate entries, read keys via
   `child.getName()`.

2. **Array of scalars (List) — a list of ids/tags:**
   ```java
   Tree root = new Tree();
   Tree ids  = root.putList("ids");
   ids.add(101).add(102).add(103);            // { "ids":[101,102,103] }
   ```
   Sum/collect via `stream()`; bulk-convert via `ids.asList(Integer.class)`.

3. **Set — deduplicating values (e.g. roles):** same API as List, duplicates collapse:
   ```java
   Tree roles = new Tree().putSet("roles");
   roles.add("admin").add("user").add("admin");   // "admin" stored once
   ```

4. **Array of objects (object-in-array) — the headline case, a list of records:**
   ```java
   Tree root  = new Tree();
   Tree users = root.putList("users");
   users.addMap().put("id", 1).put("name", "Alice");
   users.addMap().put("id", 2).put("name", "Bob");
   // { "users":[ {"id":1,"name":"Alice"}, {"id":2,"name":"Bob"} ] }
   ```
   Read back three ways — iterate, by index, by path:
   ```java
   for (Tree user : root.get("users")) {
       int    id   = user.get("id", 0);
       String name = user.get("name", "");
   }
   String second = root.get("users[1].name").asString();   // "Bob"
   ```

5. **Object with nested array + nested object — a descriptor** (model on a real `$node.*` descriptor):
   show **path access into nesting** and the framework's own **nested-loop** idiom:
   ```java
   // $node.services shape: array of service descriptors, each with a nested "nodes" array.
   for (Tree serviceInfo : rsp) {
       String name = serviceInfo.get("name", "");
       for (Tree node : serviceInfo.get("nodes")) {
           String nodeID = node.asString();
       }
   }
   ```
   Also build such a shape (`addMap().putList("nodes").add(...)`, a nested `ipList`) and read a deep
   value by path: `rsp.get("[0].ipList[0]").asString()`.

6. **A record + its metadata block (`_meta`)** — the `getMeta()` pattern Moleculer uses to carry
   response status/headers next to a clean body:
   ```java
   Tree rsp = new Tree();
   rsp.put("result", "ok");

   Tree meta = rsp.getMeta();
   meta.put("$statusCode", 200);
   meta.put("$responseType", "application/json");
   Tree headers = meta.putMap("$responseHeaders");
   headers.put("Content-Type", "application/json; charset=utf-8");
   ```
   Show `rsp.toString()` (includes `_meta`) vs `rsp.toString(true)` (omits it); cross-link the
   *Serialization & I/O* page.

7. **From existing Java collections:** `new Tree(map)` / `new Tree(list)`; drop a prepared
   `Map`/`List`/array into a path with `putObject` / `addObject` / `setObject` (incl. nested). ⚠ Verify
   in `Tree.java` how `*Object` handles nested `Tree`s / object arrays before asserting behavior.

8. **Converting between container types:** `setType(...)`, Map↔List conversion; Set never stores
   duplicates.

> Be generous: a developer should leave this page able to construct and read **any** request/response/
> event shape. Always show the resulting JSON.

### Page 4 — `serialization.md` 🆕 (sidebar: **"Serialization & I/O"**) (scope: *generate & parse*)
1. **To a String** — `toString()` (pretty JSON **with** meta) vs `toString(true)` (no meta); custom
   formats `toString("json"/"yaml"/"xml"/"csv"/"toml"/…)`; `pretty` and `insertMeta` flags. Show actual
   output for at least JSON + one other text format.
2. **To binary** — `toBinary("cbor"/"bson"/"smile"/"msgpack"/…)`, round-trip `byte[]` → `new Tree(bytes,
   format)`.
3. **To a file / stream / channel** — `writeTo("/path/file.json")` (extension picks the format),
   `writeTo(File, format)`, `writeTo(OutputStream/Channel …)`.
4. **Parsing / loading (input)** — from String, `byte[]`, `File`, `URL`, `InputStream`,
   `ReadableByteChannel`; extension-based auto-detection and the JSON fallback.
5. **The `_meta` block** — what it is, `getMeta()`, and how `insertMeta` controls whether it appears in
   output. (Cross-reference the format pages and the moleculer docs which use meta for `$status`/headers.)
6. **Format availability & dependencies** — the built-in (`json`/`java`/`debug`) vs `datatree-adapters`
   distinction from §3, with a small table linking each format to its `format-*.html` page and the Maven
   artifact to add. Adapters/formats removed in 2.0.0 (boon, fastjson v1, sojo, flexjson, jsoniter,
   JSONUtil, json-simple, grison-jtoml — see `datatree-adapters`) must **not** be presented as available.

### Optional but recommended — a **Quick reference** table
A compact "method → what it does" cheat-sheet (grouped as in §3) at the bottom of `manipulation.md` or
`reading-values.md`. Great for scanning; cheap to add once the prose pages exist.

---

## 5. Sidebar wiring (`site/.vuepress/config.js`)

Update the first sidebar group so the new pages appear in reading order:
```js
{
    title: 'DataTree Core API',
    sidebarDepth: 2,
    children: [
        ['introduction',   'Introduction'],
        ['manipulation',   'Data Manipulation'],
        ['reading-values', 'Reading values'],
        ['collections',    'Maps, lists, sets & nested structures'],
        ['serialization',  'Serialization & I/O']
    ]
},
```
The **Template engine** and **Asynchronous data processing** groups are **removed** (see §2b). After
your edits the sidebar has exactly **three** top-level groups, in this order:

1. **DataTree Core API** — the five pages above.
2. **Text-based formats** — `format-json`, `-xml`, `-yaml`, `-toml`, `-properties`, `-csv`, `-tsv`
   (leave the group untouched).
3. **Binary formats** — `format-cbor`, `-bson`, `-smile`, `-ion`, `-msgpack`, `-java`, `-kryo`,
   `performance-binary` (leave the group untouched).

---

## 6. Build, verify, finish

1. Write/extend the four `site/*.md` pages, **remove the template/promise pages + sidebar groups (§2b)**,
   and update `config.js`.
2. (Recommended) compile-check examples via a scratch `src/test/java/DocExamplesTest`, then delete it.
3. `cd site && npx vuepress build .` → regenerates `../docs`. Fix any build errors.
4. **Verify:** sidebar shows all 5 Core pages; every internal link resolves (`.html`, not `.md`); no
   `(TODO)` remains; the two known bugs (`put(String)` single-arg, `toJSON()`) are gone everywhere;
   output blocks match what the code actually prints. **Also confirm the removal (§2b):** the sidebar
   has exactly the three groups from §5, the five template/promise `site/*.md` pages are deleted, no
   `docs/template-*.html` / `docs/promise-*.html` orphans remain, and nothing links to a deleted page.
5. Commit the edited `site/*.md`, `config.js`, **and** the regenerated `docs/` together (if this is a
   git repo).

## Definition of done
- `manipulation.md` is complete (no `(TODO)`), bug-free, and scoped to *building & modifying*.
- New `reading-values.md`, `collections.md`, `serialization.md` exist, are in the sidebar, and cover
  every API group in §3 with **correct, output-bearing** examples — including the object-in-array and
  other nested-structure cases, and generating/parsing JSON + at least one other format.
- All examples verified against `Tree.java` (ideally compiled); `docs/` rebuilt from `site/`.
- **The Template-engine and Promise sections are removed (§2b):** the site is exactly **DataTree Core
  API + Text-based formats + Binary formats**, with no orphaned pages, build output, or dangling links.

---

## 7. Related but SEPARATE (don't let it block this task)

This datatree site, like the moleculer `site`, still needs the standard **2.0.0 / Java-21 refresh**:
hard-coded versions → `2.0.0`, "Java 8" → "Java 21", Maven (not Gradle) install snippets, and removing
mention of the adapters/formats dropped in 2.0.0. That is a **content-refresh** concern distinct from
the **missing-Tree-API-docs** concern this file is about. If convenient, fold the version/Java touch-ups
into the pages you edit; otherwise leave a note for whoever does the site-wide refresh. Either way, do
**not** present removed libraries as available in your new examples.
