## Sub-template insertion:

```html
#{include ../parts/header.html}
```

Shorter syntax with "in":

```html
#{in parts/footer.txt}
```

## Inserting variables

Simple variable insertion:

```html
<p> Name: #{name} </p>
<p> Age:  #{age} </p>
<p> Can be used for JSON-path syntax:  #{users[0].address.zipcode} </p>
```

## FOR loops

For a cycle of elements of a JSON array:

```html
#{for item : list}
	#{item.email}
#{end}
```

The use of a colon is optional:

```html
#{for item list}
	<tr>
		<td> #{item.id} </td>
		<td> #{item.name} </td>
		<td> #{item.description} </td>
	</tr>
#{end}
```

There may be multiple levels of the cycle:

```html
#{for row : rows}
	#{for cell : row}
		#{cell.email}
	#{end}
#{end}
```

## Conditional blocks

Paste if there is a JSON parameter:

```html
#{exists email}
	<!-- appears if the "email" parameter exists -->
	Send mail to: {#email}
#{end}
```

Shorter syntax with "ex":

```html
#{ex email}
	Send mail to: {#email}
#{end}
```

Paste if NO value exists:

```html
#{!exists email}
	<!-- appears only when the parameter is missing -->
	No email address provided!
#{end}
```

Shorter syntax with "!ex":

```html
#{!ex email}
	No email address provided!
#{end}
```

Paste if the value of the parameter is the same:

```html
#{equals email admin@foo.com}
	<!-- appears when the "email" parameter is "admin@foo.com" -->
	An administrator email address is provided.
#{end}
```

Shorter syntax with "eq":

```html
#{eq email admin@foo.com}
	An administrator email address is provided.
#{end}
```

Paste if NOT the same value as the given value:

```html
#{!equals email admin@foo.com}
	<!-- appears when the "email" parameter is NOT "admin@foo.com" -->
	The administrator email address is not specified.
#{end}
```

Shorter syntax with "!eq":

```html
#{!eq email admin@foo.com}
	The administrator email address is not specified.
#{end}
```

## User-defined functions

Invoke user-defined renderer / function:

```html
#{function formatEmail email}
```

Create the "formatEmail" function:

```java
engine.addFunction("formatEmail", (out, data) -> {
  if (data != null) {
    out.append(data.asString().replace("@", "[at]");
  }
});
```

Shorter syntax with "fn", parameter is optional:

```html
#{fn time}
```

Create the "time" function:

```java
engine.addFunction("time", (out, data) -> {
  out.append(new Date());
});
```