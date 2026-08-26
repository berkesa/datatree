## TSV format

A tab-separated values (TSV) file is a simple text format for storing data in a tabular structure,
eg. database table or spreadsheet data, and a way of exchanging information between databases.
Each record in the table is one line of the text file.
Each field value of a record is separated from the next by a tab character.

## Dependencies

Add DataTree Adapters and OpenCSV JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- CSV API -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.12.0</version>
</dependency>
```

## Reading and writing TSV documents

TSV is **tabular** and identical to CSV except the field separator is a tab character. The document
is a list of rows and each row is a list of cells. Build one, then convert it to TSV text:

```java
import io.datatree.Tree;

// A table = a list of rows; each row = a list of cells. The root is a list:
Tree table = new Tree().setList();
table.addList().add("name").add("age").add("active");   // header row
table.addList().add("Alice").add(30).add(true);
table.addList().add("Bob").add(25).add(false);

// Convert the Tree to TSV text:
String tsv = table.toString("tsv");
System.out.println(tsv);
```

The printed output (columns separated by a tab character):

```
"name"	"age"	"active"
"Alice"	"30"	"true"
"Bob"	"25"	"false"
```

Parse a TSV string back into a `Tree`, then read a cell by row/column index, or iterate:

```java
Tree parsed = new Tree(tsv, "tsv");
String cell = parsed.get(1).get(0).asString();   // "Alice" (row 1, column 0)

for (Tree row : parsed) {
    for (Tree value : row) {
        // value.asString()
    }
}
```