package com.townmc.utils.jackson.databind;

import com.townmc.utils.jackson.databind.JsonMappingException;

/**
 * Wrapper used when interface does not allow throwing a checked
 * {@link com.townmc.utils.jackson.databind.JsonMappingException}
 */
@SuppressWarnings("serial")
public class RuntimeJsonMappingException extends RuntimeException
{
    public RuntimeJsonMappingException(com.townmc.utils.jackson.databind.JsonMappingException cause) {
        super(cause);
    }

    public RuntimeJsonMappingException(String message) {
        super(message);
    }

    public RuntimeJsonMappingException(String message, JsonMappingException cause) {
        super(message, cause);
    }
}
