## CSV format

Comma-separated values (CSV) file stores tabular data (numbers and text) in plain text.
Each line of the file is a data record. Each record consists of one or more fields,
separated by commas. The use of the comma as a field separator is the source of the name for this file format. 

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
 
## Reading and writing CSV documents

CSV is **tabular**, so the document is a list of rows and each row is a list of cells (the first
row is typically the header). Build one, then convert it to CSV text:

```java
import io.datatree.Tree;

// A table = a list of rows; each row = a list of cells. The root is a list:
Tree table = new Tree().setList();
table.addList().add("name").add("age").add("active");   // header row
table.addList().add("Alice").add(30).add(true);
table.addList().add("Bob").add(25).add(false);

// Convert the Tree to CSV text:
String csv = table.toString("csv");
System.out.println(csv);
```

The printed output (OpenCSV quotes every field):

```
"name","age","active"
"Alice","30","true"
"Bob","25","false"
```

Parse a CSV string back into a `Tree`, then read a cell by row/column index, or iterate:

```java
Tree parsed = new Tree(csv, "csv");
String cell = parsed.get(1).get(0).asString();   // "Alice" (row 1, column 0)

for (Tree row : parsed) {
    for (Tree value : row) {
        // value.asString()
    }
}
```