package com.townmc.utils.jackson.databind.jsonFormatVisitors;

import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.JsonMappingException;
import com.townmc.utils.jackson.databind.SerializerProvider;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWithSerializerProvider;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;

/**
 * Interface for visitor callbacks, when type in question can be any of
 * legal JSON types.
 *<p>
 * In most cases it will make more sense to extend {@link Base}
 * instead of directly implementing this interface.
 */
public interface JsonFormatVisitorWrapper extends JsonFormatVisitorWithSerializerProvider
{
    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException;

    /**
     * @param type Declared type of visited property (or List element) in Java
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException;

    /**
     * Method called when type is of Java {@link java.util.Map} type, and will
     * be serialized as a JSON Object.
     * 
     * @since 2.2
     */
    public com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException;

    /**
     * Empty "no-op" implementation of {@link JsonFormatVisitorWrapper}, suitable for
     * sub-classing. Does implement {@link #setProvider(SerializerProvider)} and
     * {@link #getProvider()} as expected; other methods simply return null
     * and do nothing.
     *
     * @since 2.5
     */
    public static class Base implements JsonFormatVisitorWrapper {
        protected SerializerProvider _provider;

        public Base() { }

        public Base(SerializerProvider p) {
            _provider = p;
        }

        @Override
        public SerializerProvider getProvider() {
            return _provider;
        }

        @Override
        public void setProvider(SerializerProvider p) {
            _provider = p;
        }

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type)
                  throws JsonMappingException {
            return null;
        }

        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonNullFormatVisitor expectNullFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }

        @Override
        public JsonMapFormatVisitor expectMapFormat(JavaType type)
                throws JsonMappingException {
            return null;
        }
   }
}
