package com.townmc.utils.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.introspect.AnnotatedMember;
import com.townmc.utils.jackson.databind.introspect.AnnotatedParameter;
import com.townmc.utils.jackson.databind.introspect.AnnotationMap;
import com.townmc.utils.jackson.databind.introspect.TypeResolutionContext;

/**
 * Intermediate base class that encapsulates features that
 * constructors and methods share.
 */
public abstract class AnnotatedWithParams
    extends AnnotatedMember
{
    private static final long serialVersionUID = 1L;

    /**
     * Annotations associated with parameters of the annotated
     * entity (method or constructor parameters)
     */
    protected final com.townmc.utils.jackson.databind.introspect.AnnotationMap[] _paramAnnotations;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    protected AnnotatedWithParams(TypeResolutionContext ctxt, com.townmc.utils.jackson.databind.introspect.AnnotationMap annotations, com.townmc.utils.jackson.databind.introspect.AnnotationMap[] paramAnnotations)
    {
        super(ctxt, annotations);
        _paramAnnotations = paramAnnotations;
    }

    /**
     * @since 2.8.1
     */
    protected AnnotatedWithParams(AnnotatedWithParams base, com.townmc.utils.jackson.databind.introspect.AnnotationMap[] paramAnnotations) {
        super(base);
        _paramAnnotations = paramAnnotations;
    }

    /**
     * Method called to override a method parameter annotation,
     * usually due to a mix-in
     * annotation masking or overriding an annotation 'real' method
     * has.
     */
    public final void addOrOverrideParam(int paramIndex, Annotation a)
    {
        com.townmc.utils.jackson.databind.introspect.AnnotationMap old = _paramAnnotations[paramIndex];
        if (old == null) {
            old = new com.townmc.utils.jackson.databind.introspect.AnnotationMap();
            _paramAnnotations[paramIndex] = old;
        }
        old.add(a);
    }

    /**
     * Method called by parameter object when an augmented instance is created;
     * needs to replace parameter with new instance
     */
    protected com.townmc.utils.jackson.databind.introspect.AnnotatedParameter replaceParameterAnnotations(int index, com.townmc.utils.jackson.databind.introspect.AnnotationMap ann)
    {
        _paramAnnotations[index] = ann;
        return getParameter(index);
    }

    /*
    /**********************************************************
    /* Extended API
    /**********************************************************
     */

    public final AnnotationMap getParameterAnnotations(int index)
    {
        if (_paramAnnotations != null) {
            if (index >= 0 && index < _paramAnnotations.length) {
                return _paramAnnotations[index];
            }
        }
        return null;
    }

    public final com.townmc.utils.jackson.databind.introspect.AnnotatedParameter getParameter(int index) {
        return new AnnotatedParameter(this, getParameterType(index),
                _typeContext, getParameterAnnotations(index), index);
    }

    public abstract int getParameterCount();

    public abstract Class<?> getRawParameterType(int index);

    /**
     * @since 2.7
     */
    public abstract JavaType getParameterType(int index);

    /**
     * @deprecated Since 2.7, remove in 2.9
     */
    @Deprecated
    public abstract Type getGenericParameterType(int index);

    public final int getAnnotationCount() { return _annotations.size(); }

    /**
     * Method that can be used to (try to) call this object without arguments.
     * This may succeed or fail, depending on expected number
     * of arguments: caller needs to take care to pass correct number.
     * Exceptions are thrown directly from actual low-level call.
     *<p>
     * Note: only works for constructors and static methods.
     */
    public abstract Object call() throws Exception;

    /**
     * Method that can be used to (try to) call this object with specified arguments.
     * This may succeed or fail, depending on expected number
     * of arguments: caller needs to take care to pass correct number.
     * Exceptions are thrown directly from actual low-level call.
     *<p>
     * Note: only works for constructors and static methods.
     */
    public abstract Object call(Object[] args) throws Exception;

    /**
     * Method that can be used to (try to) call this object with single arguments.
     * This may succeed or fail, depending on expected number
     * of arguments: caller needs to take care to pass correct number.
     * Exceptions are thrown directly from actual low-level call.
     *<p>
     * Note: only works for constructors and static methods.
     */
    public abstract Object call1(Object arg) throws Exception;
}
