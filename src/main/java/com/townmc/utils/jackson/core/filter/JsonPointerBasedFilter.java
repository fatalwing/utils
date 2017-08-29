package com.townmc.utils.jackson.core.filter;

import com.townmc.utils.jackson.core.JsonPointer;
import com.townmc.utils.jackson.core.filter.TokenFilter;

/**
 * Simple {@link com.townmc.utils.jackson.core.filter.TokenFilter} implementation that takes a single
 * {@link JsonPointer} and matches a single value accordingly.
 * Instances are immutable and fully thread-safe, shareable,
 * and efficient to use.
 * 
 * @since 2.6
 */
public class JsonPointerBasedFilter extends com.townmc.utils.jackson.core.filter.TokenFilter
{
    protected final JsonPointer _pathToMatch;

    public JsonPointerBasedFilter(String ptrExpr) {
        this(JsonPointer.compile(ptrExpr));
    }

    public JsonPointerBasedFilter(JsonPointer match) {
        _pathToMatch = match;
    }

    @Override
    public com.townmc.utils.jackson.core.filter.TokenFilter includeElement(int index) {
        JsonPointer next = _pathToMatch.matchElement(index);
        if (next == null) {
            return null;
        }
        if (next.matches()) {
            return com.townmc.utils.jackson.core.filter.TokenFilter.INCLUDE_ALL;
        }
        return new JsonPointerBasedFilter(next);
    }

    @Override
    public com.townmc.utils.jackson.core.filter.TokenFilter includeProperty(String name) {
        JsonPointer next = _pathToMatch.matchProperty(name);
        if (next == null) {
            return null;
        }
        if (next.matches()) {
            return com.townmc.utils.jackson.core.filter.TokenFilter.INCLUDE_ALL;
        }
        return new JsonPointerBasedFilter(next);
    }

    @Override
    public com.townmc.utils.jackson.core.filter.TokenFilter filterStartArray() {
        return this;
    }
    
    @Override
    public TokenFilter filterStartObject() {
        return this;
    }
    
    @Override
    protected boolean _includeScalar() {
        // should only occur for root-level scalars, path "/"
        return _pathToMatch.matches();
    }

    @Override
    public String toString() {
        return "[JsonPointerFilter at: "+_pathToMatch+"]";
    }
}
