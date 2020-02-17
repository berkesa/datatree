(window.webpackJsonp=window.webpackJsonp||[]).push([[6],{210:function(t,a,e){"use strict";e.r(a);var s=e(0),n=Object(s.a)({},(function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[e("h2",{attrs:{id:"datatree-api"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#datatree-api"}},[t._v("#")]),t._v(" DataTree API")]),t._v(" "),e("p",[t._v("Extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats.\nDataTree is NOT an another JSON parser. It's a top-level API layer that uses existing JSON implementations.\nEven though the JSON format is the default, DataTree supports other formats, such as XML, YAML, TOML, etc.\nDataTree enables you to replace the underlaying implementation (to a smaller, smarter, faster version)\nduring the software development without any code modifications.\nIn addition, the DataTree API provides you with a logical set of tools\nto manipulate (put, get, remove, insert, sort, find, stream, etc.) the content of the hierarchical documents.")]),t._v(" "),e("div",{attrs:{align:"center"}},[e("img",{attrs:{src:"architecture.png",alt:"DataTree architecture"}})]),t._v(" "),e("h2",{attrs:{id:"usage"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#usage"}},[t._v("#")]),t._v(" Usage")]),t._v(" "),e("div",{staticClass:"language-javascript extra-class"},[e("pre",{pre:!0,attrs:{class:"language-javascript"}},[e("code",[e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("import")]),t._v(" io"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),t._v("datatree"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),t._v("Tree"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\nTree document "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("new")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("Tree")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\ndocument"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("put")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"address.city"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"Phoenix"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\nString json "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" document"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("toString")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\nResult"),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v("\n\n"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"address"')]),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"city"')]),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"Phoenix"')]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n")])])]),e("h2",{attrs:{id:"download"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#download"}},[t._v("#")]),t._v(" Download")]),t._v(" "),e("p",[t._v("The DataTree Core API contains the complete Tree toolkit, and one built-in JSON reader/writer. If you use Maven, add the following dependency to your pom.xml:")]),t._v(" "),e("div",{staticClass:"language-xml extra-class"},[e("pre",{pre:!0,attrs:{class:"language-xml"}},[e("code",[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("dependency")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("groupId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("com.github.berkesa"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("groupId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("artifactId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("datatree-core"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("artifactId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("version")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("1.0.13"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("version")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("dependency")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n")])])]),e("p",[e("a",{attrs:{href:"https://search.maven.org/artifact/com.github.berkesa/datatree-core",target:"_blank",rel:"noopener noreferrer"}},[t._v("...or download the JARs directly from the Maven Central"),e("OutboundLink")],1)]),t._v(" "),e("h2",{attrs:{id:"features"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#features"}},[t._v("#")]),t._v(" Features")]),t._v(" "),e("ul",[e("li",[t._v("DataTree API supports 18 popular JSON implementations (Jackson, Gson, Boon, Jodd, FastJson, etc.)")]),t._v(" "),e("li",[t._v("DataTree API supports 10 other (non-JSON) formats (YAML, ION, BSON, MessagePack, etc.)")]),t._v(" "),e("li",[t._v("Single universal type (no type casting, everything is a "),e("code",[t._v("Tree")]),t._v(")")]),t._v(" "),e("li",[t._v("JSON path functions ("),e("code",[t._v('tree.get("cities[2].location")')]),t._v(")")]),t._v(" "),e("li",[t._v("Easy iteration over Java Collections and Maps ("),e("code",[t._v("for (Tree child: parent)")]),t._v(")")]),t._v(" "),e("li",[t._v("Recursive deep cloning ("),e("code",[t._v("Tree copy = tree.clone()")]),t._v(")")]),t._v(" "),e("li",[t._v("Support for all Java types of Appache Cassandra ("),e("code",[t._v("BigDecimal")]),t._v(", "),e("code",[t._v("UUID")]),t._v(", "),e("code",[t._v("InetAddress")]),t._v(", etc.)")]),t._v(" "),e("li",[t._v("Support for all Java types of MongoDB ("),e("code",[t._v("BsonNumber")]),t._v(", "),e("code",[t._v("BsonNull")]),t._v(", "),e("code",[t._v("BsonString")]),t._v(", "),e("code",[t._v("BsonBoolean")]),t._v(", etc.)")]),t._v(" "),e("li",[t._v("Root and parent pointers, methods to traverse the data structure ("),e("code",[t._v("tree.getParent()")]),t._v(" or "),e("code",[t._v("tree.getRoot()")]),t._v(")")]),t._v(" "),e("li",[t._v("Methods for type-check ("),e("code",[t._v("Class valueClass = tree.getType()")]),t._v(")")]),t._v(" "),e("li",[t._v("Methods for modify the type of the underlying Java value ("),e("code",[t._v("tree.setType(String.class)")]),t._v(")")]),t._v(" "),e("li",[t._v("Method chaining ("),e("code",[t._v('tree.put("name1", "value1").put("name2", "value2")')]),t._v(")")]),t._v(" "),e("li",[t._v("Merging, filtering structures ("),e("code",[t._v("tree.copyFrom(source)")]),t._v(", "),e("code",[t._v("tree.find(condition)")]),t._v(", etc.)")]),t._v(" "),e("li",[t._v("Documents have an optional metadata container ("),e("code",[t._v('tree.getMeta().put("name", "value")')]),t._v(")")]),t._v(" "),e("li",[t._v("Pretty printing ("),e("code",[t._v("String json = tree.toString(true / false)")]),t._v(")")])]),t._v(" "),e("h2",{attrs:{id:"supported-formats-and-apis"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#supported-formats-and-apis"}},[t._v("#")]),t._v(" Supported formats and APIs")]),t._v(" "),e("p",[t._v('The "datatree-adapters" artifact contains lot of text (JSON, XML, YAML, TOML, CSV, TSV, Properties)\nand binary (BSON, ION, CBOR, SMILE, MessagePack) adapters. If you\'d like to use these formats,\nadd the following dependency instead of the "datatree-core":')]),t._v(" "),e("div",{staticClass:"language-xml extra-class"},[e("pre",{pre:!0,attrs:{class:"language-xml"}},[e("code",[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("dependency")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("groupId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("com.github.berkesa"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("groupId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("artifactId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("datatree-adapters"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("artifactId")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("<")]),t._v("version")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("1.0.13"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("version")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token tag"}},[e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("</")]),t._v("dependency")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(">")])]),t._v("\n")])])]),e("p",[t._v("Supported JSON APIs:")]),t._v(" "),e("ul",[e("li",[t._v("Apache Jackson")]),t._v(" "),e("li",[t._v("Boon JSON API")]),t._v(" "),e("li",[t._v("FastJson")]),t._v(" "),e("li",[t._v("JsonIO")]),t._v(" "),e("li",[t._v("Google Gson")]),t._v(" "),e("li",[t._v('BSON ("extended JSON")')]),t._v(" "),e("li",[t._v("DSLJson")]),t._v(" "),e("li",[t._v("Flexjson")]),t._v(" "),e("li",[t._v("Genson")]),t._v(" "),e("li",[t._v("Jodd Json")]),t._v(" "),e("li",[t._v("Apache Johnzon")]),t._v(" "),e("li",[t._v("NanoJson")]),t._v(" "),e("li",[t._v("JSON.simple")]),t._v(" "),e("li",[t._v("Json-smart")]),t._v(" "),e("li",[t._v("SOJO")]),t._v(" "),e("li",[t._v("JsonUtil")]),t._v(" "),e("li",[t._v("Amazon Ion")]),t._v(" "),e("li",[t._v("Json-iterator")])]),t._v(" "),e("p",[t._v("Supported non-JSON text formats:")]),t._v(" "),e("ul",[e("li",[t._v("XML")]),t._v(" "),e("li",[t._v("YAML")]),t._v(" "),e("li",[t._v("TOML")]),t._v(" "),e("li",[t._v("Java Properties")]),t._v(" "),e("li",[t._v("CSV")]),t._v(" "),e("li",[t._v("TSV")]),t._v(" "),e("li",[t._v("XML-RPC")])]),t._v(" "),e("p",[t._v("Supported binary formats:")]),t._v(" "),e("ul",[e("li",[t._v("MessagePack")]),t._v(" "),e("li",[t._v("BSON (binary JSON)")]),t._v(" "),e("li",[t._v("Kryo")]),t._v(" "),e("li",[t._v("CBOR")]),t._v(" "),e("li",[t._v("SMILE")]),t._v(" "),e("li",[t._v("Amazon Ion")]),t._v(" "),e("li",[t._v("Java Object Serializaton")])]),t._v(" "),e("h2",{attrs:{id:"requirements"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#requirements"}},[t._v("#")]),t._v(" Requirements")]),t._v(" "),e("p",[t._v("The DataTree API requires Java 8.")]),t._v(" "),e("h2",{attrs:{id:"license"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#license"}},[t._v("#")]),t._v(" License")]),t._v(" "),e("p",[t._v("DataTree is licensed under the Apache License V2, you can use it in your commercial products for free.")])])}),[],!1,null,null,null);a.default=n.exports}}]);