# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

DataTree Core API (`com.github.berkesa:datatree-core`) — an extensible Java 21 library for reading, manipulating, and writing hierarchical data. It is **not** a JSON parser; it is a top-level API layer that delegates serialization to pluggable third-party implementations, while providing a single universal node type (`Tree`) for in-memory manipulation. JSON is the default format; other formats (XML, YAML, TOML, BSON, CBOR, etc.) are supported through adapters.

This repo is the **core** artifact. It ships only the built-in adapters (JSON, Java serialization, debug). The ~30 third-party-backed adapters (Jackson, Gson, SnakeYAML, etc.) live in a *separate* `datatree-adapters` artifact under the package `io.datatree.dom.adapters` — that package is referenced here by name but its classes are not present in this source tree.

## Build & Test

Maven build (`pom.xml`), compiled with **`javac`** via `maven-compiler-plugin` targeting **Java 21** (`<maven.compiler.release>21</maven.compiler.release>`). Version is `2.0.0` (`2.0.0-SNAPSHOT` while developing).

```bash
mvn clean verify             # compile + test + jar (the full check)
mvn clean install            # also install datatree-core to the local ~/.m2 repo
mvn test                     # run all tests
mvn test -Dtest=TreeTest             # run one test class
mvn test -Dtest=TreeTest#testMethod  # run one test method
```

Tests use **JUnit 5 (Jupiter)**. The only runtime dependency is `org.mongodb:bson` 5.x (used by the BSON type converters). Maven Central publishing (sources + javadoc + GPG signing via the Central Portal `central-publishing-maven-plugin`) lives under the `release` profile: `mvn -Prelease clean deploy`.

## Architecture

Two layers:

### 1. The `Tree` manipulation layer (`io.datatree.Tree`)
A single ~4600-line class is the entire public manipulation API. A `Tree` is one node in a document; it wraps a plain Java `Object` (typically a `LinkedHashMap`, `LinkedList`, or a scalar) plus parent/key pointers. Everything is a `Tree` — there is no separate typed node hierarchy, so callers never cast. Key behaviors:
- **Path access**: `get`/`put`/`remove` accept JavaScript-like paths — `"address.city"`, `"cities[2].location"`. Intermediate nodes are auto-created on `put`.
- Iteration (`Iterable<Tree>`), deep `clone()`, merge (`copyFrom`), `find`/filter, streams, type checks (`getType`/`setType`), method chaining.
- The document root may hold an optional **metadata** node (`getMeta()`), serialized under the key from `Config.META` (default `_meta`).
- `toString()` / `toBytes()` / `new Tree(source, format)` route through the registries below to (de)serialize. Format defaults to JSON.

### 2. The DOM / adapter layer (`io.datatree.dom`)
Format name → implementation, resolved at runtime:
- `TreeReaderRegistry` and `TreeWriterRegistry` map a lowercase format string (`"json"`, `"yaml"`, …) to a `TreeReader` / `TreeWriter`. `AbstractAdapter` implements both interfaces; built-ins live in `io.datatree.dom.builtin`.
- **Resolution order** when a format isn't already registered (`TreeReaderRegistry.getReader`): explicit `setReader`/`setWriter` registration → `-Ddatatree.<format>.reader=<class>` system property → `PackageScanner` discovery. Missing/unloadable adapters fall back to the built-in JSON adapter, and `suggestDependency` prints a hint about which Maven artifact to add.
- `PackageScanner` scans `io.datatree.dom.builtin`, `io.datatree.dom.adapters`, and any packages from `-Ddatatree.adapter.packages`, instantiating each class and registering it under `getFormat()`. When multiple adapters claim the same format, the one with the highest `@Priority(value)` wins (default 1000; built-in JSON is `@Priority(1)` so any third-party adapter overrides it).

### Type conversion (`io.datatree.dom.converters`)
`DataConverterRegistry.convert(targetClass, value)` is the central value-coercion mechanism used throughout `Tree` accessors and adapters. Converters are registered FROM→TO (e.g. `Date`→`String`) or as a default-for-TO. `BasicConverterSet` covers standard Java/Cassandra types; `BsonConverterSet` adds MongoDB BSON types (`BsonString`, `BsonInt32`, `BsonNull`, …). Both are loaded statically by the registry.

### Configuration (`io.datatree.dom.Config`)
All tunables are read once from `-Ddatatree.*` system properties at class-load time: time zone, timestamp format, `use.timestamps`, cache/pool sizes, `adapter.packages`, `base64.codec`, and `meta.name`. Change behavior via JVM system properties, not code.

## Conventions
- Every source file carries the Apache 2.0 license header — preserve it on new files.
- Adapter and converter selection is reflection/classpath driven; adding a new format means creating an `AbstractAdapter` subclass with the right `getFormat()` and `@Priority`, placed in a scanned package — no central registration edit required.
- `*Test.java` files and `docs/`, `site/` are excluded from Codacy analysis (`.codacy.yaml`).
- `site/*.md` are the documentation sources (per-format guides) published to GitHub Pages under `docs/`.
