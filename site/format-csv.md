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
    <version>1.0.13</version>
</dependency>

<!-- CSV API -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.0</version>
</dependency>
```
 
## Reading and writing CSV documents

```java
// Parsing CSV document
String csv = " ... CSV document ... ";
Tree document = new Tree(csv, "csv");

// Getting / setting values
for (Tree row: document) {
  for (Tree cell: row) {
    ...
  }
}

// Generating CSV string from Tree
String csv = document.toString("csv");
```  