package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;

import com.townmc.utils.jackson.core.*;

import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.JsonMappingException;
import com.townmc.utils.jackson.databind.JsonNode;
import com.townmc.utils.jackson.databind.SerializerProvider;
import com.townmc.utils.jackson.databind.annotation.JacksonStdImpl;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.townmc.utils.jackson.databind.jsontype.TypeSerializer;
import com.townmc.utils.jackson.databind.ser.std.StdScalarSerializer;

/**
 * This is the special serializer for regular {@link String}s.
 *<p>
 * Since this is one of "native" types, no type information is ever
 * included on serialization (unlike for most scalar types as of 1.5)
 */
@JacksonStdImpl
public final class StringSerializer
// NOTE: generic parameter changed from String to Object in 2.6, to avoid
//   use of bridge methods
// In 2.9, removed use of intermediate type `NonTypedScalarSerializerBase`
    extends StdScalarSerializer<Object>
{
    private static final long serialVersionUID = 1L;

    public StringSerializer() { super(String.class, false); }

    @Override
    public boolean isEmpty(SerializerProvider prov, Object value) {
        String str = (String) value;
        return str.length() == 0;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString((String) value);
    }

    @Override
    public final void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        // no type info, just regular serialization
        gen.writeString((String) value);
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("string", true);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitStringFormat(visitor, typeHint);
    }
}
