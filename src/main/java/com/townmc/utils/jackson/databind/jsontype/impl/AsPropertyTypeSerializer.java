package com.townmc.utils.jackson.databind.jsontype.impl;

import com.townmc.utils.jackson.annotation.JsonTypeInfo.As;

import com.townmc.utils.jackson.databind.BeanProperty;
import com.townmc.utils.jackson.databind.jsontype.TypeIdResolver;
import com.townmc.utils.jackson.databind.jsontype.impl.AsArrayTypeSerializer;

/**
 * Type serializer that preferably embeds type information as an additional
 * JSON Object property, if possible (when resulting serialization would
 * use JSON Object). If this is not possible (for JSON Arrays, scalars),
 * uses a JSON Array wrapper (similar to how
 * {@link As#WRAPPER_ARRAY} always works) as a fallback.
 */
public class AsPropertyTypeSerializer
    extends AsArrayTypeSerializer
{
    protected final String _typePropertyName;

    public AsPropertyTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName)
    {
        super(idRes, property);
        _typePropertyName = propName;
    }

    @Override
    public AsPropertyTypeSerializer forProperty(BeanProperty prop) {
        return (_property == prop) ? this :
            new AsPropertyTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }
    
    @Override
    public String getPropertyName() { return _typePropertyName; }

    @Override
    public As getTypeInclusion() { return As.PROPERTY; }
}
