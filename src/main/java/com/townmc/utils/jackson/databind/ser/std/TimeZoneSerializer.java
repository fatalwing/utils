package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;
import java.util.TimeZone;

import com.townmc.utils.jackson.core.*;
import com.townmc.utils.jackson.core.type.WritableTypeId;
import com.townmc.utils.jackson.databind.SerializerProvider;
import com.townmc.utils.jackson.databind.jsontype.TypeSerializer;
import com.townmc.utils.jackson.databind.ser.std.StdScalarSerializer;

@SuppressWarnings("serial")
public class TimeZoneSerializer extends StdScalarSerializer<TimeZone>
{
    public TimeZoneSerializer() { super(TimeZone.class); }

    @Override
    public void serialize(TimeZone value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeString(value.getID());
    }

    @Override
    public void serializeWithType(TimeZone value, JsonGenerator g,
            SerializerProvider provider, TypeSerializer typeSer) throws IOException
    {
        // Better ensure we don't use specific sub-classes:
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, TimeZone.class, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
