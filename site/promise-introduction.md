## Promise for Java

ES6-like Promise, based on the Java8's CompletableFuture API. A Promise is an
object that may produce a single value some time in the future: either a
resolved value, or a reason that it's not resolved (e.g., a network error
occurred). Promise users can attach callbacks to handle the fulfilled value
or the reason for rejection.

## Download

If you use Maven, add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-promise</artifactId>
    <version>1.0.6</version>
</dependency>
```

[...or download the JARs directly from the Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.berkesa%22)

## Requirements

The DataTree API requires Java 8.

## License

DataTree is licensed under the Apache License V2, you can use it in your commercial products for free.