## Creating templates directly

```java
TemplateEngine engine = new TemplateEngine();
engine.define("page.html", "Hello #{name}!");

Tree data = new Tree();
data.put("name", "Tom");

// The "out" contains "Hello Tom!"
String out = engine.process("page.html", data);
```

## Working from a directory

```java
TemplateEngine engine = new TemplateEngine();
engine.setRootDirectory("/www");
engine.setReloadTemplates(false);
engine.setCharset(StandardCharsets.UTF_8);

// The "data" can be Tree but can also be Map:
Map<String, Object> data = new HashMap<>();
data.put("key", "value");

// The "out" contains the merged "index.html"
String out = engine.process("index.html", data);
```

## Using custom loader

The default template-loader loads from classpath and file system.
You can create your own template-loader by implementing the "io.datatree.templates.ResourceLoader" interface.

```java
TemplateEngine engine = new TemplateEngine();
engine.setLoader(new CustomResourceLoader());
```

## Using custom preprocessor

Template Preprocessor runs after the loader loads a template. If the cache is enabled (~= engine.setReloadable(false)),
it will only run once per template. For example, this feature can be used to minimize HTML-pages.

```java
import io.datatree.templates.SimpleHtmlMinifier;

// ...

TemplateEngine engine = new TemplateEngine();
engine.setTemplatePreProcessor(new SimpleHtmlMinifier());
```

The following example shows how to embed Google's HtmlCompressor as a preprocessor:

```java
public class GoogleMinifier extends HtmlCompressor implements Function<String, String> {

	public GoogleHtmlMinifier() {
		setCompressCss(true);
		setCompressJavaScript(true);
	}

	@Override
	public String apply(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		return compress(text);
	}
}

// Use the HtmlCompressor:
TemplateEngine engine = new TemplateEngine();
engine.setTemplatePreProcessor(new GoogleMinifier());
```

The following two dependency is required for the example above:

```java
compile group: 'com.googlecode.htmlcompressor', name: 'htmlcompressor', version: '1.5.2'
compile group: 'com.yahoo.platform.yui', name: 'yuicompressor', version: '2.4.8'
```