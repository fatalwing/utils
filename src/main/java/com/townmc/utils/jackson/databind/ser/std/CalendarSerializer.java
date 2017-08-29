package com.townmc.utils.jackson.databind.ser.std;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import com.townmc.utils.jackson.core.*;
import com.townmc.utils.jackson.databind.SerializerProvider;
import com.townmc.utils.jackson.databind.annotation.JacksonStdImpl;
import com.townmc.utils.jackson.databind.ser.std.DateTimeSerializerBase;

/**
 * Standard serializer for {@link Calendar}.
 * As with other time/date types, is configurable to produce timestamps
 * (standard Java 64-bit timestamp) or textual formats (usually ISO-8601).
 */
@JacksonStdImpl
@SuppressWarnings("serial")
public class CalendarSerializer
    extends DateTimeSerializerBase<Calendar>
{
    public static final CalendarSerializer instance = new CalendarSerializer();

    public CalendarSerializer() { this(null, null); }

    public CalendarSerializer(Boolean useTimestamp, DateFormat customFormat) {
        super(Calendar.class, useTimestamp, customFormat);
    }

    @Override
    public CalendarSerializer withFormat(Boolean timestamp, DateFormat customFormat) {
        return new CalendarSerializer(timestamp, customFormat);
    }

    @Override
    protected long _timestamp(Calendar value) {
        return (value == null) ? 0L : value.getTimeInMillis();
    }

    @Override
    public void serialize(Calendar value, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        if (_asTimestamp(provider)) {
            g.writeNumber(_timestamp(value));
            return;
        }
        _serializeAsString(value.getTime(), g, provider);
    }
}
