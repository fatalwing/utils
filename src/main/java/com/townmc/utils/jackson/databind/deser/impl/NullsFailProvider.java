package com.townmc.utils.jackson.databind.deser.impl;

import com.townmc.utils.jackson.databind.*;
import com.townmc.utils.jackson.databind.deser.NullValueProvider;
import com.townmc.utils.jackson.databind.exc.InvalidNullException;
import com.townmc.utils.jackson.databind.util.AccessPattern;

/**
 * Simple {@link NullValueProvider} that will always throw a
 * {@link InvalidNullException} when a null is encountered.
 */
public class NullsFailProvider
    implements NullValueProvider, java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    protected final PropertyName _name;
    protected final JavaType _type;

    protected NullsFailProvider(PropertyName name, JavaType type) {
        _name = name;
        _type = type;
    }

    public static NullsFailProvider constructForProperty(BeanProperty prop) {
        return new NullsFailProvider(prop.getFullName(), prop.getType());
    }

    public static NullsFailProvider constructForRootValue(JavaType t) {
        return new NullsFailProvider(null, t);
    }

    @Override
    public AccessPattern getNullAccessPattern() {
        // Must be called every time to effect the exception...
        return AccessPattern.DYNAMIC;
    }

    @Override
    public Object getNullValue(DeserializationContext ctxt)
            throws JsonMappingException {
        throw InvalidNullException.from(ctxt, _name, _type);
    }
}
