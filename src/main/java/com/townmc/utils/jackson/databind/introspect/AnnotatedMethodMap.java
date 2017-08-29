package com.townmc.utils.jackson.databind.introspect;

import com.townmc.utils.jackson.databind.introspect.AnnotatedMethod;
import com.townmc.utils.jackson.databind.introspect.MemberKey;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Simple helper class used to keep track of collection of
 * {@link com.townmc.utils.jackson.databind.introspect.AnnotatedMethod}s, accessible by lookup. Lookup
 * is usually needed for augmenting and overriding annotations.
 */
public final class AnnotatedMethodMap
    implements Iterable<com.townmc.utils.jackson.databind.introspect.AnnotatedMethod>
{
    protected Map<com.townmc.utils.jackson.databind.introspect.MemberKey, com.townmc.utils.jackson.databind.introspect.AnnotatedMethod> _methods;

    public AnnotatedMethodMap() { }

    /**
     * @since 2.9
     */
    public AnnotatedMethodMap(Map<com.townmc.utils.jackson.databind.introspect.MemberKey, com.townmc.utils.jackson.databind.introspect.AnnotatedMethod> m) {
        _methods = m;
    }

    public int size() {
        return (_methods == null) ? 0 : _methods.size();
    }

    public com.townmc.utils.jackson.databind.introspect.AnnotatedMethod find(String name, Class<?>[] paramTypes)
    {
        if (_methods == null) {
            return null;
        }
        return _methods.get(new com.townmc.utils.jackson.databind.introspect.MemberKey(name, paramTypes));
    }

    public com.townmc.utils.jackson.databind.introspect.AnnotatedMethod find(Method m)
    {
        if (_methods == null) {
            return null;
        }
        return _methods.get(new MemberKey(m));
    }

    /*
    /**********************************************************
    /* Iterable implementation (for iterating over values)
    /**********************************************************
     */

    @Override
    public Iterator<AnnotatedMethod> iterator()
    {
        if (_methods == null) {
            return Collections.emptyIterator();
        }
        return _methods.values().iterator();
    }
}
