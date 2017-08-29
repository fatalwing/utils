package com.townmc.utils.jackson.databind.deser.impl;

import java.io.IOException;

import com.townmc.utils.jackson.core.JsonParser;
import com.townmc.utils.jackson.databind.DeserializationContext;
import com.townmc.utils.jackson.databind.JsonDeserializer;

/**
 * A deserializer that stores an {@link Error} caught during constructing
 * of the deserializer, which needs to be deferred and only during actual
 * attempt to deserialize a value of given type.
 * Note that null and empty values can be deserialized without error.
 * 
 * @since 2.9 Note: prior to this version was named <code>NoClassDefFoundDeserializer</code>
 */
public class ErrorThrowingDeserializer extends JsonDeserializer<Object>
{
    private final Error _cause;

    public ErrorThrowingDeserializer(NoClassDefFoundError cause) {
        _cause = cause;
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        throw _cause;
    }
}
