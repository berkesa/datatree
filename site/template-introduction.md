## About DataTree Template Engine

DataTree Template Engine is a small and fast template engine capable of producing html, xml, and plain text files.
The template engine works with hierarchical collection structures - similar to the Mustache Engine but with expandable features.
Its operating logic is very simple which makes it pretty fast:

<div align="center">
    <img src="templates/chart.png" alt="Template Engines" />
</div>

## Capabilities
 
- Sub-templates (header/footer) insertion
- Simple insertion (multiple levels with JSON-path "user.address[2].city")
- Loop on elements of a JSON array/map (for creating tables and lists)
- Insert if a value exists or does not exist
- Insert if a value is the same or different from the specified value
- Can be used to generate TXT, HTML, XHTML or XML files (character escaping)
- User-defined functions/macros (special HTML-formatters and renderers)

## Limitations

Data must **NOT** contain POJO objects, only Collections (Maps, Lists, object arrays) with primitive types and Strings
(or any object that can be easily converted to String).
The contents of a POJO object can only be inserted into the templates with user-defined functions.
No built-in multilingual support. Syntax isn't flexible;
complicated logic conditions cannot be specified in the templates, only a few simpler condition types can be used.

## Download

**Maven**

```xml
<dependencies>
	<dependency>
		<groupId>com.github.berkesa</groupId>
		<artifactId>datatree-templates</artifactId>
		<version>1.1.4</version>
		<scope>runtime</scope>
	</dependency>
</dependencies>
```

**Gradle**

```gradle
dependencies {
	compile group: 'com.github.berkesa', name: 'datatree-templates', version: '1.1.4' 
}
```

## Requirements

The DataTree Templates API requires Java 8.

## License

DataTree Templates is licensed under the Apache License V2, you can use it in your commercial products for free.
