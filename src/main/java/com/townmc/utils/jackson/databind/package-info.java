/**
Basic data binding (mapping) functionality that
allows for reading JSON content into Java Objects (POJOs)
and JSON Trees ({@link com.townmc.utils.jackson.databind.JsonNode}), as well as
writing Java Objects and trees as JSON.
Reading and writing (as well as related additional functionality) is accessed through
{@link com.townmc.utils.jackson.databind.ObjectMapper},
{@link com.townmc.utils.jackson.databind.ObjectReader} and
{@link com.townmc.utils.jackson.databind.ObjectWriter}
 classes.
 In addition to reading and writing JSON content, it is also possible to use the
 general databinding functionality for many other data formats, using
 Jackson extension modules that provide such support: if so, you typically
 simply construct an {@link com.townmc.utils.jackson.databind.ObjectMapper} with
 different underlying streaming parser, generator implementation.
<p>
The main starting point for operations is {@link com.townmc.utils.jackson.databind.ObjectMapper},
which can be used either directly (via multiple overloaded
<code>readValue</code>,
<code>readTree</code>,
<code>writeValue</code> and
<code>writeTree</code> methods, or it can be used as a configurable factory for constructing
fully immutable, thread-safe and reusable {@link com.townmc.utils.jackson.databind.ObjectReader}
and {@link com.townmc.utils.jackson.databind.ObjectWriter} objects.
<p>
In addition to simple reading and writing of JSON as POJOs or JSON trees (represented as
{@link com.townmc.utils.jackson.databind.JsonNode}, and configurability needed to change aspects
of reading/writing, mapper contains additional functionality such as:
<ul>
 <li>Value conversions using {@link com.townmc.utils.jackson.databind.ObjectMapper#convertValue(Object, Class)},
  {@link com.townmc.utils.jackson.databind.ObjectMapper#valueToTree(Object)} and
  {@link com.townmc.utils.jackson.databind.ObjectMapper#treeToValue(com.townmc.utils.jackson.core.TreeNode, Class)} methods.
  </li>
 <li>Type introspection needed for things like generation of Schemas (like JSON Schema, Avro Schema, or protoc
   definitions), using {@link com.townmc.utils.jackson.databind.ObjectMapper#acceptJsonFormatVisitor(Class, com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper)}
   (note: actual handles are usually provided by various Jackson modules: mapper simply initiates calling of
   callbacks, based on serializers registered)
  </li>
</ul>
<p>
Simplest usage is of form:
<pre>
  final ObjectMapper mapper = new ObjectMapper(); // can use static singleton, inject: just make sure to reuse!
  MyValue value = new MyValue();
  // ... and configure
  File newState = new File("my-stuff.json");
  mapper.writeValue(newState, value); // writes JSON serialization of MyValue instance
  // or, read
  MyValue older = mapper.readValue(new File("my-older-stuff.json"), MyValue.class);

  // Or if you prefer JSON Tree representation:
  JsonNode root = mapper.readTree(newState);
  // and find values by, for example, using a {@link com.townmc.utils.jackson.core.JsonPointer} expression:
  int age = root.at("/personal/age").getValueAsInt(); 
</pre>
<p>
For more usage, refer to
{@link com.townmc.utils.jackson.databind.ObjectMapper},
{@link com.townmc.utils.jackson.databind.ObjectReader} and
{@link com.townmc.utils.jackson.databind.ObjectWriter}
Javadocs.
*/

package com.townmc.utils.jackson.databind;
