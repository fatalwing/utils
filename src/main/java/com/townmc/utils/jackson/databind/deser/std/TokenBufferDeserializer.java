package com.townmc.utils.jackson.databind.deser.std;

import java.io.IOException;

import com.townmc.utils.jackson.core.JsonParser;

import com.townmc.utils.jackson.databind.DeserializationContext;
import com.townmc.utils.jackson.databind.annotation.JacksonStdImpl;
import com.townmc.utils.jackson.databind.deser.std.StdScalarDeserializer;
import com.townmc.utils.jackson.databind.util.TokenBuffer;

/**
 * We also want to directly support deserialization of {@link TokenBuffer}.
 *<p>
 * Note that we use scalar deserializer base just because we claim
 * to be of scalar for type information inclusion purposes; actual
 * underlying content can be of any (Object, Array, scalar) type.
 *<p>
 * Since 2.3, another important thing is that possible native ids
 * (type id, object id) should be properly copied even when converting
 * with {@link TokenBuffer}. Such ids are supported if (and only if!)
 * source {@link JsonParser} supports them.
 */
@JacksonStdImpl
public class TokenBufferDeserializer extends StdScalarDeserializer<TokenBuffer> {
    private static final long serialVersionUID = 1L;
    
    public TokenBufferDeserializer() { super(TokenBuffer.class); }

    @Override
    public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return createBufferInstance(p).deserialize(p, ctxt);
    }

    protected TokenBuffer createBufferInstance(JsonParser p) {
        return new TokenBuffer(p);
    }
}