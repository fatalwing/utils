package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;

import com.townmc.utils.jackson.core.*;
import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.JsonMappingException;
import com.townmc.utils.jackson.databind.JsonSerializable;
import com.townmc.utils.jackson.databind.SerializerProvider;
import com.townmc.utils.jackson.databind.annotation.JacksonStdImpl;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.townmc.utils.jackson.databind.jsontype.TypeSerializer;
import com.townmc.utils.jackson.databind.ser.std.StdSerializer;

/**
 * Generic handler for types that implement {@link JsonSerializable}.
 *<p>
 * Note: given that this is used for anything that implements
 * interface, cannot be checked for direct class equivalence.
 */
@JacksonStdImpl
@SuppressWarnings("serial")
public class SerializableSerializer
    extends StdSerializer<JsonSerializable>
{
    public final static SerializableSerializer instance = new SerializableSerializer();

    protected SerializableSerializer() { super(JsonSerializable.class); }

    @Override
    public boolean isEmpty(SerializerProvider serializers, JsonSerializable value) {
        if (value instanceof JsonSerializable.Base) {
            return ((JsonSerializable.Base) value).isEmpty(serializers);
        }
        return false;
    }

    @Override
    public void serialize(JsonSerializable value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        value.serialize(gen, serializers);
    }

    @Override
    public final void serializeWithType(JsonSerializable value, JsonGenerator gen, SerializerProvider serializers,
            TypeSerializer typeSer) throws IOException {
        value.serializeWithType(gen, serializers, typeSer);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        visitor.expectAnyFormat(typeHint);
    }
}
