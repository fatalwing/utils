package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;

import com.townmc.utils.jackson.core.JsonGenerator;

import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.JsonMappingException;
import com.townmc.utils.jackson.databind.JsonNode;
import com.townmc.utils.jackson.databind.SerializerProvider;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.townmc.utils.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Also: default bean access will not do much good with Class.class. But
 * we can just serialize the class name and that should be enough.
 */
@SuppressWarnings("serial")
public class ClassSerializer
    extends StdScalarSerializer<Class<?>>
{
    public ClassSerializer() { super(Class.class, false); }

    @Override
    public void serialize(Class<?> value, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        g.writeString(value.getName());
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
        return createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        visitStringFormat(visitor, typeHint);
    }
}