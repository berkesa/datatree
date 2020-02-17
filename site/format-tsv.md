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
    <version>1.0.13</version>
</dependency>

<!-- CSV API -->
<dependency>
    <groupId>net.sf.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>2.3</version>
</dependency>
```

## Reading and writing TSV documents

```java
// Parsing TSV document
String tsv = " ... TSV document ... ";
Tree document = new Tree(tsv, "tsv");

// Getting / setting values
for (Tree row: document) {
  for (Tree cell: row) {
    ...
  }
}

// Generating TSV string from Tree
String tsv = document.toString("tsv");
``` 