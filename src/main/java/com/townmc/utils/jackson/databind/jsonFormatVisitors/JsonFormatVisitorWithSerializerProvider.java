/**
 * 
 */
package com.townmc.utils.jackson.databind.jsonFormatVisitors;

import com.townmc.utils.jackson.databind.SerializerProvider;

/**
 * @author jphelan
 */
public interface JsonFormatVisitorWithSerializerProvider {
    public SerializerProvider getProvider();
    public abstract void setProvider(SerializerProvider provider);
}
