(window.webpackJsonp=window.webpackJsonp||[]).push([[14],{216:function(t,a,e){"use strict";e.r(a);var s=e(0),r=Object(s.a)({},(function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[e("h2",{attrs:{id:"java-properties-format"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#java-properties-format"}},[t._v("#")]),t._v(" Java Properties format")]),t._v(" "),e("p",[t._v("The Java Properties class, "),e("code",[t._v("java.util.Properties")]),t._v(",\nis like a Java Map of Java String key and value pairs.\nThe Java Properties class can write the key, value pairs to a properties file on disk,\nand read the properties back in again.\nThis is an often used mechanism for storing simple configuration properties for Java applications.")]),t._v(" "),e("h2",{attrs:{id:"dependencies"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#dependencies"}},[t._v("#")]),t._v(" Dependencies")]),t._v(" "),e("p",[t._v("DataTree API supports 2 Java Property reader/writer implementations.\nThe default (built-in) Property adapter has no dependencies.")]),t._v(" "),e("div",{staticClass:"language-java extra-class"},[e("pre",{pre:!0,attrs:{class:"language-java"}},[e("code",[e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Parsing Java Properties file")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("String")]),t._v(" properties "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"< ... properties ...>"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("Tree")]),t._v(" document "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("new")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("Tree")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),t._v("properties"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"properties"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\n"),e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Getting / setting values")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("boolean")]),t._v(" value "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" document"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("get")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"array[2].subItem.value"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token boolean"}},[t._v("false")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\ndocument"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("put")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"path.to.item"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token boolean"}},[t._v("true")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\n"),e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Generating Java Properties string from Tree")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("String")]),t._v(" properties "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" document"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("toString")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v('"properties"')]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n")])])]),e("p",[t._v("If there is more than one Promerties implementation on classpath, the preferred\nimplementation is adjustable with the following System Properties:")]),t._v(" "),e("div",{staticClass:"language- extra-class"},[e("pre",{pre:!0,attrs:{class:"language-text"}},[e("code",[t._v("// Using Jackson API:\n-Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesJackson\n-Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesJackson\n\n// Using built-in API:\n-Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesBuiltin\n-Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesBuiltin\n")])])]),e("h2",{attrs:{id:"required-dependencies-of-java-property-adapters"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#required-dependencies-of-java-property-adapters"}},[t._v("#")]),t._v(" Required dependencies of Java Property adapters")]),t._v(" "),e("table",[e("thead",[e("tr",[e("th",[t._v("API Name")]),t._v(" "),e("th",[t._v("Adapter Class")]),t._v(" "),e("th",[t._v("Dependency")])])]),t._v(" "),e("tbody",[e("tr",[e("td",[t._v("Jackson Properties")]),t._v(" "),e("td",[t._v("PropertiesJackson")]),t._v(" "),e("td",[e("a",{attrs:{href:"https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-properties",target:"_blank",rel:"noopener noreferrer"}},[t._v("group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-properties', version: '2.10.1'"),e("OutboundLink")],1)])]),t._v(" "),e("tr",[e("td",[t._v("Built-in Properties")]),t._v(" "),e("td",[t._v("PropertiesBuiltin")]),t._v(" "),e("td",[t._v("-")])])])])])}),[],!1,null,null,null);a.default=r.exports}}]);