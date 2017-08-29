/**
 * Classes used for exposing logical structure of POJOs as Jackson
 * sees it, and exposed via
 * {@link com.townmc.utils.jackson.databind.ObjectMapper#acceptJsonFormatVisitor(Class, JsonFormatVisitorWrapper)}
 * and
 * {@link com.townmc.utils.jackson.databind.ObjectMapper#acceptJsonFormatVisitor(com.townmc.utils.jackson.databind.JavaType, JsonFormatVisitorWrapper)}
 * methods.
 *<p>
 * The main entrypoint for code, then, is {@link com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper} and other
 * types are recursively needed during traversal.
 */
package com.townmc.utils.jackson.databind.jsonFormatVisitors;

import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;