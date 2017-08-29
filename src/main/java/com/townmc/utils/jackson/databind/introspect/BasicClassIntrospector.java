package com.townmc.utils.jackson.databind.introspect;

import java.util.Collection;
import java.util.Map;

import com.townmc.utils.jackson.databind.AnnotationIntrospector;
import com.townmc.utils.jackson.databind.DeserializationConfig;
import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.SerializationConfig;
import com.townmc.utils.jackson.databind.annotation.JsonPOJOBuilder;
import com.townmc.utils.jackson.databind.cfg.MapperConfig;
import com.townmc.utils.jackson.databind.introspect.AnnotatedClass;
import com.townmc.utils.jackson.databind.introspect.AnnotatedClassResolver;
import com.townmc.utils.jackson.databind.introspect.BasicBeanDescription;
import com.townmc.utils.jackson.databind.introspect.ClassIntrospector;
import com.townmc.utils.jackson.databind.introspect.POJOPropertiesCollector;
import com.townmc.utils.jackson.databind.type.SimpleType;
import com.townmc.utils.jackson.databind.util.ClassUtil;
import com.townmc.utils.jackson.databind.util.LRUMap;

public class BasicClassIntrospector
    extends ClassIntrospector
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    /* We keep a small set of pre-constructed descriptions to use for
     * common non-structured values, such as Numbers and Strings.
     * This is strictly performance optimization to reduce what is
     * usually one-time cost, but seems useful for some cases considering
     * simplicity.
     *
     * @since 2.4
     */
    protected final static com.townmc.utils.jackson.databind.introspect.BasicBeanDescription STRING_DESC;
    static {
        STRING_DESC = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class),
                com.townmc.utils.jackson.databind.introspect.AnnotatedClassResolver.createPrimordial(String.class));
    }
    protected final static com.townmc.utils.jackson.databind.introspect.BasicBeanDescription BOOLEAN_DESC;
    static {
        BOOLEAN_DESC = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE),
                com.townmc.utils.jackson.databind.introspect.AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
    }
    protected final static com.townmc.utils.jackson.databind.introspect.BasicBeanDescription INT_DESC;
    static {
        INT_DESC = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE),
                com.townmc.utils.jackson.databind.introspect.AnnotatedClassResolver.createPrimordial(Integer.TYPE));
    }
    protected final static com.townmc.utils.jackson.databind.introspect.BasicBeanDescription LONG_DESC;
    static {
        LONG_DESC = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE),
                com.townmc.utils.jackson.databind.introspect.AnnotatedClassResolver.createPrimordial(Long.TYPE));
    }

    /*
    /**********************************************************
    /* Life cycle
    /**********************************************************
     */

    /**
     * Looks like 'forClassAnnotations()' gets called so frequently that we
     * should consider caching to avoid some of the lookups.
     * 
     * @since 2.5
     */
    protected final LRUMap<JavaType, com.townmc.utils.jackson.databind.introspect.BasicBeanDescription> _cachedFCA;

    public BasicClassIntrospector() {
        // a small cache should go a long way here
        _cachedFCA = new LRUMap<JavaType, com.townmc.utils.jackson.databind.introspect.BasicBeanDescription>(16, 64);
    }
    
    /*
    /**********************************************************
    /* Factory method impls
    /**********************************************************
     */

    @Override
    public com.townmc.utils.jackson.databind.introspect.BasicBeanDescription forSerialization(SerializationConfig cfg,
                                                                                           JavaType type, MixInResolver r)
    {
        // minor optimization: for some JDK types do minimal introspection
        com.townmc.utils.jackson.databind.introspect.BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            // As per [databind#550], skip full introspection for some of standard
            // structured types as well
            desc = _findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forSerialization(collectProperties(cfg,
                        type, r, true, "set"));
            }
            // Also: this is a superset of "forClassAnnotations", so may optimize by optional add:
            _cachedFCA.putIfAbsent(type, desc);
        }
        return desc;
    }

    @Override
    public com.townmc.utils.jackson.databind.introspect.BasicBeanDescription forDeserialization(DeserializationConfig cfg,
                                                                                             JavaType type, MixInResolver r)
    {
        // minor optimization: for some JDK types do minimal introspection
        com.townmc.utils.jackson.databind.introspect.BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            // As per [Databind#550], skip full introspection for some of standard
            // structured types as well
            desc = _findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forDeserialization(collectProperties(cfg,
                        		type, r, false, "set"));
            }
            // Also: this is a superset of "forClassAnnotations", so may optimize by optional add:
            _cachedFCA.putIfAbsent(type, desc);
        }
        return desc;
    }

    @Override
    public com.townmc.utils.jackson.databind.introspect.BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig cfg,
                                                                                                        JavaType type, MixInResolver r)
    {
        // no std JDK types with Builders, so:

        com.townmc.utils.jackson.databind.introspect.BasicBeanDescription desc = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forDeserialization(collectPropertiesWithBuilder(cfg,
                type, r, false));
        // this is still a superset of "forClassAnnotations", so may optimize by optional add:
        _cachedFCA.putIfAbsent(type, desc);
        return desc;
    }
    
    @Override
    public com.townmc.utils.jackson.databind.introspect.BasicBeanDescription forCreation(DeserializationConfig cfg,
                                                                                      JavaType type, MixInResolver r)
    {
        com.townmc.utils.jackson.databind.introspect.BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {

            // As per [Databind#550], skip full introspection for some of standard
            // structured types as well
            desc = _findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forDeserialization(
            		collectProperties(cfg, type, r, false, "set"));
            }
        }
        // should this be cached for FCA?
        return desc;
    }

    @Override
    public com.townmc.utils.jackson.databind.introspect.BasicBeanDescription forClassAnnotations(MapperConfig<?> config,
                                                                                              JavaType type, MixInResolver r)
    {
        com.townmc.utils.jackson.databind.introspect.BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = _cachedFCA.get(type);
            if (desc == null) {
                desc = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forOtherUse(config, type,
                        _resolveAnnotatedClass(config, type, r));
                _cachedFCA.put(type, desc);
            }
        }
        return desc;
    }

    @Override
    public com.townmc.utils.jackson.databind.introspect.BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> config,
                                                                                                    JavaType type, MixInResolver r)
    {
        com.townmc.utils.jackson.databind.introspect.BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = com.townmc.utils.jackson.databind.introspect.BasicBeanDescription.forOtherUse(config, type,
                    _resolveAnnotatedWithoutSuperTypes(config, type, r));
        }
        return desc;
    }

    /*
    /**********************************************************
    /* Overridable helper methods
    /**********************************************************
     */

    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config,
                                                        JavaType type, MixInResolver r, boolean forSerialization,
                                                        String mutatorPrefix)
    {
        return constructPropertyCollector(config,
                _resolveAnnotatedClass(config, type, r),
                type, forSerialization, mutatorPrefix);
    }

    protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config,
            JavaType type, MixInResolver r, boolean forSerialization)
    {
        com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac = _resolveAnnotatedClass(config, type, r);
        AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
        JsonPOJOBuilder.Value builderConfig = (ai == null) ? null : ai.findPOJOBuilderConfig(ac);
        String mutatorPrefix = (builderConfig == null) ? JsonPOJOBuilder.DEFAULT_WITH_PREFIX : builderConfig.withPrefix;
        return constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix);
    }

    /**
     * Overridable method called for creating {@link POJOPropertiesCollector} instance
     * to use; override is needed if a custom sub-class is to be used.
     */
    protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config,
                                                                 com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix)
    {
        return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
    }

    /**
     * Method called to see if type is one of core JDK types
     * that we have cached for efficiency.
     */
    protected com.townmc.utils.jackson.databind.introspect.BasicBeanDescription _findStdTypeDesc(JavaType type)
    {
        Class<?> cls = type.getRawClass();
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                return BOOLEAN_DESC;
            }
            if (cls == Integer.TYPE) {
                return INT_DESC;
            }
            if (cls == Long.TYPE) {
                return LONG_DESC;
            }
        } else {
            if (cls == String.class) {
                return STRING_DESC;
            }
        }
        return null;
    }

    /**
     * Helper method used to decide whether we can omit introspection
     * for members (methods, fields, constructors); we may do so for
     * a limited number of container types JDK provides.
     */
    protected boolean _isStdJDKCollection(JavaType type)
    {
        if (!type.isContainerType() || type.isArrayType()) {
            return false;
        }
        Class<?> raw = type.getRawClass();
        String pkgName = ClassUtil.getPackageName(raw);
        if (pkgName != null) {
            if (pkgName.startsWith("java.lang")
                    || pkgName.startsWith("java.util")) {
                /* 23-Sep-2014, tatu: Should we be conservative here (minimal number
                 *    of matches), or ambitious? Let's do latter for now.
                 */
                if (Collection.class.isAssignableFrom(raw)
                        || Map.class.isAssignableFrom(raw)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected com.townmc.utils.jackson.databind.introspect.BasicBeanDescription _findStdJdkCollectionDesc(MapperConfig<?> cfg, JavaType type)
    {
        if (_isStdJDKCollection(type)) {
            return BasicBeanDescription.forOtherUse(cfg, type,
                    _resolveAnnotatedClass(cfg, type, cfg));
        }
        return null;
    }

    /**
     * @since 2.9
     */
    protected com.townmc.utils.jackson.databind.introspect.AnnotatedClass _resolveAnnotatedClass(MapperConfig<?> config,
                                                                                              JavaType type, MixInResolver r) {
        return com.townmc.utils.jackson.databind.introspect.AnnotatedClassResolver.resolve(config, type, r);
    }

    /**
     * @since 2.9
     */
    protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(MapperConfig<?> config,
                                                                JavaType type, MixInResolver r) {
        return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
    }
}
