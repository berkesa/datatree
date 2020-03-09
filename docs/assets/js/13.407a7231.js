(window.webpackJsonp=window.webpackJsonp||[]).push([[13],{223:function(t,e,a){"use strict";a.r(e);var s=a(56),r=Object(s.a)({},(function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[a("h2",{attrs:{id:"java-properties-format"}},[a("a",{staticClass:"header-anchor",attrs:{href:"#java-properties-format"}},[t._v("#")]),t._v(" Java Properties format")]),t._v(" "),a("p",[t._v("The Java Properties class, "),a("code",[t._v("java.util.Properties")]),t._v(",\nis like a Java Map of Java String key and value pairs.\nThe Java Properties class can write the key, value pairs to a properties file on disk,\nand read the properties back in again.\nThis is an often used mechanism for storing simple configuration properties for Java applications.")]),t._v(" "),a("h2",{attrs:{id:"dependencies"}},[a("a",{staticClass:"header-anchor",attrs:{href:"#dependencies"}},[t._v("#")]),t._v(" Dependencies")]),t._v(" "),a("p",[t._v("DataTree API supports 2 Java Property reader/writer implementations.\nThe default (built-in) Property adapter has no dependencies.")]),t._v(" "),a("div",{staticClass:"language-java extra-class"},[a("pre",{pre:!0,attrs:{class:"language-java"}},[a("code",[a("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Parsing Java Properties file")]),t._v("\n"),a("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("String")]),t._v(" properties "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"< ... properties ...>"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n"),a("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("Tree")]),t._v(" document "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("new")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("Tree")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),t._v("properties"),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"properties"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\n"),a("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Getting / setting values")]),t._v("\n"),a("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("boolean")]),t._v(" value "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" document"),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),a("span",{pre:!0,attrs:{class:"token function"}},[t._v("get")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"array[2].subItem.value"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token boolean"}},[t._v("false")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\ndocument"),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),a("span",{pre:!0,attrs:{class:"token function"}},[t._v("put")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"path.to.item"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token boolean"}},[t._v("true")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\n"),a("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Generating Java Properties string from Tree")]),t._v("\n"),a("span",{pre:!0,attrs:{class:"token class-name"}},[t._v("String")]),t._v(" properties "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" document"),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),a("span",{pre:!0,attrs:{class:"token function"}},[t._v("toString")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"properties"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n")])])]),a("p",[t._v("If there is more than one Promerties implementation on classpath, the preferred\nimplementation is adjustable with the following System Properties:")]),t._v(" "),a("div",{staticClass:"language- extra-class"},[a("pre",{pre:!0,attrs:{class:"language-text"}},[a("code",[t._v("// Using Jackson API:\n-Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesJackson\n-Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesJackson\n\n// Using built-in API:\n-Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesBuiltin\n-Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesBuiltin\n")])])]),a("h2",{attrs:{id:"required-dependencies-of-java-property-adapters"}},[a("a",{staticClass:"header-anchor",attrs:{href:"#required-dependencies-of-java-property-adapters"}},[t._v("#")]),t._v(" Required dependencies of Java Property adapters")]),t._v(" "),a("table",[a("thead",[a("tr",[a("th",[t._v("API Name")]),t._v(" "),a("th",[t._v("Adapter Class")]),t._v(" "),a("th",[t._v("Dependency")])])]),t._v(" "),a("tbody",[a("tr",[a("td",[t._v("Jackson Properties")]),t._v(" "),a("td",[t._v("PropertiesJackson")]),t._v(" "),a("td",[a("a",{attrs:{href:"https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-properties",target:"_blank",rel:"noopener noreferrer"}},[t._v("group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-properties', version: '2.10.1'"),a("OutboundLink")],1)])]),t._v(" "),a("tr",[a("td",[t._v("Built-in Properties")]),t._v(" "),a("td",[t._v("PropertiesBuiltin")]),t._v(" "),a("td",[t._v("-")])])])])])}),[],!1,null,null,null);e.default=r.exports},56:function(t,e,a){"use strict";function s(t,e,a,s,r,n,o,p){var i,c="function"==typeof t?t.options:t;if(e&&(c.render=e,c.staticRenderFns=a,c._compiled=!0),s&&(c.functional=!0),n&&(c._scopeId="data-v-"+n),o?(i=function(t){(t=t||this.$vnode&&this.$vnode.ssrContext||this.parent&&this.parent.$vnode&&this.parent.$vnode.ssrContext)||"undefined"==typeof __VUE_SSR_CONTEXT__||(t=__VUE_SSR_CONTEXT__),r&&r.call(this,t),t&&t._registeredComponents&&t._registeredComponents.add(o)},c._ssrRegister=i):r&&(i=p?function(){r.call(this,this.$root.$options.shadowRoot)}:r),i)if(c.functional){c._injectStyles=i;var v=c.render;c.render=function(t,e){return i.call(e),v(t,e)}}else{var l=c.beforeCreate;c.beforeCreate=l?[].concat(l,i):[i]}return{exports:t,options:c}}a.d(e,"a",(function(){return s}))}}]);