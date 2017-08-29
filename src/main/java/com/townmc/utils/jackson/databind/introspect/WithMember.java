package com.townmc.utils.jackson.databind.introspect;

import com.townmc.utils.jackson.databind.introspect.AnnotatedMember;

public interface WithMember<T>
{
    public T withMember(AnnotatedMember member);
}
