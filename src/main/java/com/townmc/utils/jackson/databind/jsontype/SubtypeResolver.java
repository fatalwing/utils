package com.townmc.utils.jackson.databind.jsontype;

import java.util.Collection;

import com.townmc.utils.jackson.databind.AnnotationIntrospector;
import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.cfg.MapperConfig;
import com.townmc.utils.jackson.databind.introspect.AnnotatedClass;
import com.townmc.utils.jackson.databind.introspect.AnnotatedMember;
import com.townmc.utils.jackson.databind.jsontype.NamedType;

/**
 * Helper object used for handling registration on resolving of super-types
 * to sub-types.
 */
public abstract class SubtypeResolver
{
    /*
    /**********************************************************
    /* Methods for registering external subtype definitions
    /**********************************************************
     */

    /**
     * Method for registering specified subtypes (possibly including type
     * names); for type entries without name, non-qualified class name
     * as used as name (unless overridden by annotation).
     */
    public abstract void registerSubtypes(com.townmc.utils.jackson.databind.jsontype.NamedType... types);

    public abstract void registerSubtypes(Class<?>... classes);

    /**
     * @since 2.9
     */
    public abstract void registerSubtypes(Collection<Class<?>> subtypes);
    
    /*
    /**********************************************************
    /* Subtype resolution
    /**********************************************************
     */

    /**
     * Method for finding out all reachable subtypes for a property specified
     * by given element (method or field),
     * such that access is by type,
     * typically needed for serialization (converting from type to type name).
     * 
     * @param baseType Effective property base type to use; may differ from
     *    actual type of property; for structured types it is content (value) type and NOT
     *    structured type.
     * 
     * @since 2.6
     */
    public Collection<com.townmc.utils.jackson.databind.jsontype.NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config,
                                                                                                          AnnotatedMember property, JavaType baseType) {
        // for backwards compatibility...
        return collectAndResolveSubtypes(property, config,
                config.getAnnotationIntrospector(), baseType);
    }

    /**
     * Method for finding out all reachable subtypes for given type,
     * such that access is by type,
     * typically needed for serialization (converting from type to type name).
     * 
     * @param baseType Effective property base type to use; may differ from
     *    actual type of property; for structured types it is content (value) type and NOT
     *    structured type.
     * 
     * @since 2.6
     */
    public Collection<com.townmc.utils.jackson.databind.jsontype.NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config,
                                                                                                          AnnotatedClass baseType) {
        // for backwards compatibility...
        return collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
    }

    /**
     * Method for finding out all reachable subtypes for a property specified
     * by given element (method or field),
     * such that access is by type id,
     * typically needed for deserialization (converting from type id to type).
     * 
     * @param baseType Effective property base type to use; may differ from
     *    actual type of property; for structured types it is content (value) type and NOT
     *    structured type.
     * 
     * @since 2.6
     */
    public Collection<com.townmc.utils.jackson.databind.jsontype.NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config,
                                                                                                           AnnotatedMember property, JavaType baseType) {
        // for backwards compatibility...
        return collectAndResolveSubtypes(property, config,
                config.getAnnotationIntrospector(), baseType);
    }

    /**
     * Method for finding out all reachable subtypes for given type,
     * such that access is by type id,
     * typically needed for deserialization (converting from type id to type).
     * 
     * @param baseType Effective property base type to use; may differ from
     *    actual type of property; for structured types it is content (value) type and NOT
     *    structured type.
     * 
     * @since 2.6
     */
    public Collection<com.townmc.utils.jackson.databind.jsontype.NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config,
                                                                                                           AnnotatedClass baseType) {
        // for backwards compatibility...
        return collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
    }
    
    /*
    /**********************************************************
    /* Deprecated methods
    /**********************************************************
     */
    
    /**
     * @deprecated Since 2.6 Use either
     *   {@link #collectAndResolveSubtypesByClass(MapperConfig, AnnotatedMember, JavaType)}
     *   or {@link #collectAndResolveSubtypesByTypeId(MapperConfig, AnnotatedMember, JavaType)}
     *   instead.
     */
    @Deprecated
    public Collection<com.townmc.utils.jackson.databind.jsontype.NamedType> collectAndResolveSubtypes(AnnotatedMember property,
                                                                                                   MapperConfig<?> config, AnnotationIntrospector ai, JavaType baseType) {
        return collectAndResolveSubtypesByClass(config, property, baseType);
    }

    /**
     * @deprecated Since 2.6 Use either
     *   {@link #collectAndResolveSubtypesByClass(MapperConfig, AnnotatedClass)}
     *   or {@link #collectAndResolveSubtypesByTypeId(MapperConfig, AnnotatedClass)}
     *   instead.
     */
    @Deprecated
    public Collection<NamedType> collectAndResolveSubtypes(AnnotatedClass baseType,
                                                           MapperConfig<?> config, AnnotationIntrospector ai) {
        return collectAndResolveSubtypesByClass(config, baseType);
    }
}
