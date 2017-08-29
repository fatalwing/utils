/**
 * Main public API classes of the core streaming JSON
 * processor: most importantly {@link com.townmc.utils.jackson.core.JsonFactory}
 * used for constructing
 * JSON parser ({@link com.townmc.utils.jackson.core.JsonParser})
 * and generator
 * ({@link com.townmc.utils.jackson.core.JsonGenerator})
 * instances.
 * <p>
 * Public API of the higher-level mapping interfaces ("Mapping API")
 * is found from the "jackson-databind" bundle, except for following
 * base interfaces that are defined here:
 * <ul>
 *<li>{@link com.townmc.utils.jackson.core.TreeNode} is included
 *within Streaming API to support integration of the Tree Model
 *(which is based on <code>JsonNode</code>) with the basic
 *parsers and generators (iff using mapping-supporting factory: which
 *is part of Mapping API, not core)
 *  </li>
 *<li>{@link com.townmc.utils.jackson.core.ObjectCodec} is included so that
 *  reference to the object capable of serializing/deserializing
 *  Objects to/from JSON (usually, <code>com.townmc.utils.jackson.databind.ObjectMapper</code>)
 *  can be exposed, without adding direct dependency to implementation.
 *  </li>
 *</ul>
 */

package com.townmc.utils.jackson.core;
