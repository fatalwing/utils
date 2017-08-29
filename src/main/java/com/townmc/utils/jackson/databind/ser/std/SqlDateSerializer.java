package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;

import com.townmc.utils.jackson.core.JsonGenerator;

import com.townmc.utils.jackson.databind.*;
import com.townmc.utils.jackson.databind.annotation.JacksonStdImpl;
import com.townmc.utils.jackson.databind.ser.std.DateTimeSerializerBase;

/**
 * Compared to regular {@link java.util.Date} serialization, we do use String
 * representation here. Why? Basically to truncate of time part, since
 * that should not be used by plain SQL date.
 */
@JacksonStdImpl
@SuppressWarnings("serial")
public class SqlDateSerializer
    extends DateTimeSerializerBase<Date>
{
    public SqlDateSerializer() {
        // 11-Oct-2016, tatu: As per [databind#219] fixed for 2.9; was passing `false` prior
        this(null, null);
    }

    protected SqlDateSerializer(Boolean useTimestamp, DateFormat customFormat) {
        super(Date.class, useTimestamp, customFormat);
    }

    @Override
    public SqlDateSerializer withFormat(Boolean timestamp, DateFormat customFormat) {
    	return new SqlDateSerializer(timestamp, customFormat);
    }

    @Override
    protected long _timestamp(Date value) {
        return (value == null) ? 0L : value.getTime();
    }
    
    @Override
    public void serialize(Date value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        if (_asTimestamp(provider)) {
            g.writeNumber(_timestamp(value));
            return;
        }
        // Alas, can't just call `_serializeAsString()`....
        if (_customFormat == null) {
            // 11-Oct-2016, tatu: For backwards-compatibility purposes, we shall just use
            //    the awful standard JDK serialization via `sqlDate.toString()`... this
            //    is problematic in multiple ways (including using arbitrary timezone...)
            g.writeString(value.toString());
            return;
        }
        _serializeAsString(value, g, provider);
    }
}
