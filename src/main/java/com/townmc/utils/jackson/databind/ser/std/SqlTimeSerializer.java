package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;

import com.townmc.utils.jackson.core.JsonGenerator;
import com.townmc.utils.jackson.databind.*;
import com.townmc.utils.jackson.databind.annotation.JacksonStdImpl;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.townmc.utils.jackson.databind.ser.std.StdScalarSerializer;

@JacksonStdImpl
@SuppressWarnings("serial")
public class SqlTimeSerializer
    extends StdScalarSerializer<Time>
{
    public SqlTimeSerializer() { super(Time.class); }

    @Override
    public void serialize(Time value, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        g.writeString(value.toString());
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        visitStringFormat(visitor, typeHint, JsonValueFormat.DATE_TIME);
    }
}