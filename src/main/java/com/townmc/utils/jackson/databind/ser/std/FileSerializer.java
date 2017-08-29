package com.townmc.utils.jackson.databind.ser.std;

import java.io.File;
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
 * For now, File objects get serialized by just outputting
 * absolute (but not canonical) name as String value
 */
@SuppressWarnings("serial")
public class FileSerializer
    extends StdScalarSerializer<File>
{
    public FileSerializer() { super(File.class); }

    @Override
    public void serialize(File value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeString(value.getAbsolutePath());
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        visitStringFormat(visitor, typeHint);
    }
}