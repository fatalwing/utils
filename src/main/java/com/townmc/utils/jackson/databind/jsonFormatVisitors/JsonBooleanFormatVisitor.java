package com.townmc.utils.jackson.databind.jsonFormatVisitors;

import com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonValueFormatVisitor;

public interface JsonBooleanFormatVisitor extends com.townmc.utils.jackson.databind.jsonFormatVisitors.JsonValueFormatVisitor
{
    /**
     * Default "empty" implementation, useful as the base to start on;
     * especially as it is guaranteed to implement all the method
     * of the interface, even if new methods are getting added.
     */
    public static class Base extends JsonValueFormatVisitor.Base
        implements JsonBooleanFormatVisitor { }
}
