package com.townmc.utils.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.townmc.utils.jackson.annotation.JacksonInject;
import com.townmc.utils.jackson.annotation.JsonCreator;
import com.townmc.utils.jackson.annotation.JsonFormat;
import com.townmc.utils.jackson.annotation.JsonIgnoreProperties;
import com.townmc.utils.jackson.annotation.JsonInclude;
import com.townmc.utils.jackson.annotation.JsonProperty;
import com.townmc.utils.jackson.annotation.JsonSetter;
import com.townmc.utils.jackson.core.Version;
import com.townmc.utils.jackson.databind.*;
import com.townmc.utils.jackson.databind.annotation.JsonPOJOBuilder;
import com.townmc.utils.jackson.databind.annotation.JsonSerialize;
import com.townmc.utils.jackson.databind.cfg.MapperConfig;
import com.townmc.utils.jackson.databind.introspect.Annotated;
import com.townmc.utils.jackson.databind.introspect.AnnotatedClass;
import com.townmc.utils.jackson.databind.introspect.AnnotatedMember;
import com.townmc.utils.jackson.databind.introspect.AnnotatedMethod;
import com.townmc.utils.jackson.databind.introspect.ObjectIdInfo;
import com.townmc.utils.jackson.databind.introspect.VisibilityChecker;
import com.townmc.utils.jackson.databind.jsontype.NamedType;
import com.townmc.utils.jackson.databind.jsontype.TypeResolverBuilder;
import com.townmc.utils.jackson.databind.ser.BeanPropertyWriter;
import com.townmc.utils.jackson.databind.util.ClassUtil;
import com.townmc.utils.jackson.databind.util.NameTransformer;

/**
 * Helper class that allows using 2 introspectors such that one
 * introspector acts as the primary one to use; and second one
 * as a fallback used if the primary does not provide conclusive
 * or useful result for a method.
 *<p>
 * An obvious consequence of priority is that it is easy to construct
 * longer chains of introspectors by linking multiple pairs.
 * Currently most likely combination is that of using the default
 * Jackson provider, along with JAXB annotation introspector.
 *<p>
 * Note: up until 2.0, this class was an inner class of
 * {@link AnnotationIntrospector}; moved here for convenience.
 * 
 * @since 2.1
 */
public class AnnotationIntrospectorPair
    extends AnnotationIntrospector
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    protected final AnnotationIntrospector _primary, _secondary;

    public AnnotationIntrospectorPair(AnnotationIntrospector p, AnnotationIntrospector s)
    {
        _primary = p;
        _secondary = s;
    }

    @Override
    public Version version() {
        return _primary.version();
    }

    /**
     * Helper method for constructing a Pair from two given introspectors (if
     * neither is null); or returning non-null introspector if one is null
     * (and return just null if both are null)
     */
    public static AnnotationIntrospector create(AnnotationIntrospector primary,
            AnnotationIntrospector secondary)
    {
        if (primary == null) {
            return secondary;
        }
        if (secondary == null) {
            return primary;
        }
        return new AnnotationIntrospectorPair(primary, secondary);
    }

    @Override
    public Collection<AnnotationIntrospector> allIntrospectors() {
        return allIntrospectors(new ArrayList<AnnotationIntrospector>());
    }

    @Override
    public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result)
    {
        _primary.allIntrospectors(result);
        _secondary.allIntrospectors(result);
        return result;
    }
    
    // // // Generic annotation properties, lookup
    
    @Override
    public boolean isAnnotationBundle(Annotation ann) {
        return _primary.isAnnotationBundle(ann) || _secondary.isAnnotationBundle(ann);
    }
    
    /*
    /******************************************************
    /* General class annotations
    /******************************************************
     */

    @Override
    public PropertyName findRootName(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac)
    {
        PropertyName name1 = _primary.findRootName(ac);
        if (name1 == null) {
            return _secondary.findRootName(ac);
        }
        if (name1.hasSimpleName()) {
            return name1;
        }
        // name1 is empty; how about secondary?
        PropertyName name2 = _secondary.findRootName(ac);
        return (name2 == null) ? name1 : name2;
    }

    @Override
    public JsonIgnoreProperties.Value findPropertyIgnorals(com.townmc.utils.jackson.databind.introspect.Annotated a)
    {
        JsonIgnoreProperties.Value v2 = _secondary.findPropertyIgnorals(a);
        JsonIgnoreProperties.Value v1 = _primary.findPropertyIgnorals(a);
        return (v2 == null) // shouldn't occur but
            ? v1 : v2.withOverrides(v1);
    }

    @Override
    public Boolean isIgnorableType(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac)
    {
        Boolean result = _primary.isIgnorableType(ac);
        if (result == null) {
            result = _secondary.isIgnorableType(ac);
        }
        return result;
    }

    @Override
    public Object findFilterId(com.townmc.utils.jackson.databind.introspect.Annotated ann)
    {
        Object id = _primary.findFilterId(ann);
        if (id == null) {
            id = _secondary.findFilterId(ann);
        }
        return id;
    }
    
    @Override
    public Object findNamingStrategy(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac)
    {
        Object str = _primary.findNamingStrategy(ac);
        if (str == null) {
            str = _secondary.findNamingStrategy(ac);
        }
        return str;
    }

    @Override
    public String findClassDescription(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac) {
        String str = _primary.findClassDescription(ac);
        if ((str == null) || str.isEmpty()) {
            str = _secondary.findClassDescription(ac);
        }
        return str;
    }

    @Override
    @Deprecated // since 2.6
    public String[] findPropertiesToIgnore(com.townmc.utils.jackson.databind.introspect.Annotated ac) {
        String[] result = _primary.findPropertiesToIgnore(ac);
        if (result == null) {
            result = _secondary.findPropertiesToIgnore(ac);
        }
        return result;            
    }

    @Override
    @Deprecated // since 2.8
    public String[] findPropertiesToIgnore(com.townmc.utils.jackson.databind.introspect.Annotated ac, boolean forSerialization) {
        String[] result = _primary.findPropertiesToIgnore(ac, forSerialization);
        if (result == null) {
            result = _secondary.findPropertiesToIgnore(ac, forSerialization);
        }
        return result;            
    }

    @Override
    @Deprecated // since 2.8
    public Boolean findIgnoreUnknownProperties(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac)
    {
        Boolean result = _primary.findIgnoreUnknownProperties(ac);
        if (result == null) {
            result = _secondary.findIgnoreUnknownProperties(ac);
        }
        return result;
    }        

    /*
    /******************************************************
    /* Property auto-detection
    /******************************************************
    */
    
    @Override
    public com.townmc.utils.jackson.databind.introspect.VisibilityChecker<?> findAutoDetectVisibility(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac,
                                                                                                   VisibilityChecker<?> checker)
    {
        /* Note: to have proper priorities, we must actually call delegatees
         * in reverse order:
         */
        checker = _secondary.findAutoDetectVisibility(ac, checker);
        return _primary.findAutoDetectVisibility(ac, checker);
    }

    /*
    /******************************************************
    /* Type handling
    /******************************************************
     */

    @Override
    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config,
                                                   com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac, JavaType baseType)
    {
        TypeResolverBuilder<?> b = _primary.findTypeResolver(config, ac, baseType);
        if (b == null) {
            b = _secondary.findTypeResolver(config, ac, baseType);
        }
        return b;
    }

    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config,
                                                           com.townmc.utils.jackson.databind.introspect.AnnotatedMember am, JavaType baseType)
    {
        TypeResolverBuilder<?> b = _primary.findPropertyTypeResolver(config, am, baseType);
        if (b == null) {
            b = _secondary.findPropertyTypeResolver(config, am, baseType);
        }
        return b;
    }

    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config,
                                                                  com.townmc.utils.jackson.databind.introspect.AnnotatedMember am, JavaType baseType)
    {
        TypeResolverBuilder<?> b = _primary.findPropertyContentTypeResolver(config, am, baseType);
        if (b == null) {
            b = _secondary.findPropertyContentTypeResolver(config, am, baseType);
        }
        return b;
    }
    
    @Override
    public List<NamedType> findSubtypes(com.townmc.utils.jackson.databind.introspect.Annotated a)
    {
        List<NamedType> types1 = _primary.findSubtypes(a);
        List<NamedType> types2 = _secondary.findSubtypes(a);
        if (types1 == null || types1.isEmpty()) return types2;
        if (types2 == null || types2.isEmpty()) return types1;
        ArrayList<NamedType> result = new ArrayList<NamedType>(types1.size() + types2.size());
        result.addAll(types1);
        result.addAll(types2);
        return result;
    }

    @Override
    public String findTypeName(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac)
    {
        String name = _primary.findTypeName(ac);
        if (name == null || name.length() == 0) {
            name = _secondary.findTypeName(ac);                
        }
        return name;
    }
    /*
    /******************************************************
    /* General member (field, method/constructor) annotations
    /******************************************************
     */
    
    @Override        
    public ReferenceProperty findReferenceType(com.townmc.utils.jackson.databind.introspect.AnnotatedMember member) {
        ReferenceProperty r = _primary.findReferenceType(member);
        return (r == null) ? _secondary.findReferenceType(member) : r;
    }

    @Override        
    public NameTransformer findUnwrappingNameTransformer(com.townmc.utils.jackson.databind.introspect.AnnotatedMember member) {
        NameTransformer r = _primary.findUnwrappingNameTransformer(member);
        return (r == null) ? _secondary.findUnwrappingNameTransformer(member) : r;
    }

    @Override
    public JacksonInject.Value findInjectableValue(com.townmc.utils.jackson.databind.introspect.AnnotatedMember m) {
        JacksonInject.Value r = _primary.findInjectableValue(m);
        return (r == null) ? _secondary.findInjectableValue(m) : r;
    }

    @Override
    public boolean hasIgnoreMarker(com.townmc.utils.jackson.databind.introspect.AnnotatedMember m) {
        return _primary.hasIgnoreMarker(m) || _secondary.hasIgnoreMarker(m);
    }

    @Override
    public Boolean hasRequiredMarker(com.townmc.utils.jackson.databind.introspect.AnnotatedMember m) {
        Boolean r = _primary.hasRequiredMarker(m);
        return (r == null) ? _secondary.hasRequiredMarker(m) : r;
    }

    @Override
    @Deprecated // since 2.9
    public Object findInjectableValueId(com.townmc.utils.jackson.databind.introspect.AnnotatedMember m) {
        Object r = _primary.findInjectableValueId(m);
        return (r == null) ? _secondary.findInjectableValueId(m) : r;
    }

    // // // Serialization: general annotations

    @Override
    public Object findSerializer(com.townmc.utils.jackson.databind.introspect.Annotated am) {
        Object r = _primary.findSerializer(am);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findSerializer(am),
                JsonSerializer.None.class);
    }
    
    @Override
    public Object findKeySerializer(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object r = _primary.findKeySerializer(a);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findKeySerializer(a),
                JsonSerializer.None.class);
    }

    @Override
    public Object findContentSerializer(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object r = _primary.findContentSerializer(a);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findContentSerializer(a),
                JsonSerializer.None.class);
    }
    
    @Override
    public Object findNullSerializer(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object r = _primary.findNullSerializer(a);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findNullSerializer(a),
                JsonSerializer.None.class);
    }
    
    @Deprecated
    @Override
    public JsonInclude.Include findSerializationInclusion(com.townmc.utils.jackson.databind.introspect.Annotated a,
                                                          JsonInclude.Include defValue)
    {
        // note: call secondary first, to give lower priority
        defValue = _secondary.findSerializationInclusion(a, defValue);
        defValue = _primary.findSerializationInclusion(a, defValue);
        return defValue;
    }

    @Deprecated
    @Override
    public JsonInclude.Include findSerializationInclusionForContent(com.townmc.utils.jackson.databind.introspect.Annotated a, JsonInclude.Include defValue)
    {
        // note: call secondary first, to give lower priority
        defValue = _secondary.findSerializationInclusionForContent(a, defValue);
        defValue = _primary.findSerializationInclusionForContent(a, defValue);
        return defValue;
    }

    @Override
    public JsonInclude.Value findPropertyInclusion(com.townmc.utils.jackson.databind.introspect.Annotated a)
    {
        JsonInclude.Value v2 = _secondary.findPropertyInclusion(a);
        JsonInclude.Value v1 = _primary.findPropertyInclusion(a);

        if (v2 == null) { // shouldn't occur but
            return v1;
        }
        return v2.withOverrides(v1);
    }

    @Override
    public JsonSerialize.Typing findSerializationTyping(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        JsonSerialize.Typing r = _primary.findSerializationTyping(a);
        return (r == null) ? _secondary.findSerializationTyping(a) : r;
    }

    @Override
    public Object findSerializationConverter(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object r = _primary.findSerializationConverter(a);
        return (r == null) ? _secondary.findSerializationConverter(a) : r;
    }

    @Override
    public Object findSerializationContentConverter(com.townmc.utils.jackson.databind.introspect.AnnotatedMember a) {
        Object r = _primary.findSerializationContentConverter(a);
        return (r == null) ? _secondary.findSerializationContentConverter(a) : r;
    }

    @Override
    public Class<?>[] findViews(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        /* Theoretically this could be trickier, if multiple introspectors
         * return non-null entries. For now, though, we'll just consider
         * first one to return non-null to win.
         */
        Class<?>[] result = _primary.findViews(a);
        if (result == null) {
            result = _secondary.findViews(a);
        }
        return result;
    }

    @Override
    public Boolean isTypeId(com.townmc.utils.jackson.databind.introspect.AnnotatedMember member) {
        Boolean b = _primary.isTypeId(member);
        return (b == null) ? _secondary.isTypeId(member) : b;
    }

    @Override
    public com.townmc.utils.jackson.databind.introspect.ObjectIdInfo findObjectIdInfo(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        com.townmc.utils.jackson.databind.introspect.ObjectIdInfo r = _primary.findObjectIdInfo(ann);
        return (r == null) ? _secondary.findObjectIdInfo(ann) : r;
    }

    @Override
    public com.townmc.utils.jackson.databind.introspect.ObjectIdInfo findObjectReferenceInfo(com.townmc.utils.jackson.databind.introspect.Annotated ann, ObjectIdInfo objectIdInfo) {
        // to give precedence for primary, must start with secondary:
        objectIdInfo = _secondary.findObjectReferenceInfo(ann, objectIdInfo);
        objectIdInfo = _primary.findObjectReferenceInfo(ann, objectIdInfo);
        return objectIdInfo;
    }

    @Override
    public JsonFormat.Value findFormat(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        JsonFormat.Value v1 = _primary.findFormat(ann);
        JsonFormat.Value v2 = _secondary.findFormat(ann);
        if (v2 == null) { // shouldn't occur but just in case
            return v1;
        }
        return v2.withOverrides(v1);
    }

    @Override
    public PropertyName findWrapperName(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        PropertyName name = _primary.findWrapperName(ann);
        if (name == null) {
            name = _secondary.findWrapperName(ann);
        } else if (name == PropertyName.USE_DEFAULT) {
            // does the other introspector have a better idea?
            PropertyName name2 = _secondary.findWrapperName(ann);
            if (name2 != null) {
                name = name2;
            }
        }
        return name;
    }

    @Override
    public String findPropertyDefaultValue(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        String str = _primary.findPropertyDefaultValue(ann);
        return (str == null || str.isEmpty()) ? _secondary.findPropertyDefaultValue(ann) : str;
    }

    @Override
    public String findPropertyDescription(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        String r = _primary.findPropertyDescription(ann);
        return (r == null) ? _secondary.findPropertyDescription(ann) : r;
    }

    @Override
    public Integer findPropertyIndex(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        Integer r = _primary.findPropertyIndex(ann);
        return (r == null) ? _secondary.findPropertyIndex(ann) : r;
    }

    @Override
    public String findImplicitPropertyName(com.townmc.utils.jackson.databind.introspect.AnnotatedMember ann) {
        String r = _primary.findImplicitPropertyName(ann);
        return (r == null) ? _secondary.findImplicitPropertyName(ann) : r;
    }

    @Override
    public List<PropertyName> findPropertyAliases(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        List<PropertyName> r = _primary.findPropertyAliases(ann);
        return (r == null) ? _secondary.findPropertyAliases(ann) : r;
    }

    @Override
    public JsonProperty.Access findPropertyAccess(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        JsonProperty.Access acc = _primary.findPropertyAccess(ann);
        if ((acc != null) && (acc != JsonProperty.Access.AUTO)) {
            return acc;
        }
        acc = _secondary.findPropertyAccess(ann);
        if (acc != null) {
            return acc;
        }
        return JsonProperty.Access.AUTO;
    }

    @Override // since 2.7
    public com.townmc.utils.jackson.databind.introspect.AnnotatedMethod resolveSetterConflict(MapperConfig<?> config,
                                                                                           com.townmc.utils.jackson.databind.introspect.AnnotatedMethod setter1, com.townmc.utils.jackson.databind.introspect.AnnotatedMethod setter2)
    {
        com.townmc.utils.jackson.databind.introspect.AnnotatedMethod res = _primary.resolveSetterConflict(config, setter1, setter2);
        if (res == null) {
            res = _secondary.resolveSetterConflict(config, setter1, setter2);
        }
        return res;
    }

    // // // Serialization: type refinements

    @Override // since 2.7
    public JavaType refineSerializationType(MapperConfig<?> config,
                                            com.townmc.utils.jackson.databind.introspect.Annotated a, JavaType baseType) throws JsonMappingException
    {
        JavaType t = _secondary.refineSerializationType(config, a, baseType);
        return _primary.refineSerializationType(config, a, t);
    }
    
    @Override
    @Deprecated
    public Class<?> findSerializationType(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Class<?> r = _primary.findSerializationType(a);
        return (r == null) ? _secondary.findSerializationType(a) : r;
    }

    @Override
    @Deprecated
    public Class<?> findSerializationKeyType(com.townmc.utils.jackson.databind.introspect.Annotated am, JavaType baseType) {
        Class<?> r = _primary.findSerializationKeyType(am, baseType);
        return (r == null) ? _secondary.findSerializationKeyType(am, baseType) : r;
    }

    @Override
    @Deprecated
    public Class<?> findSerializationContentType(com.townmc.utils.jackson.databind.introspect.Annotated am, JavaType baseType) {
        Class<?> r = _primary.findSerializationContentType(am, baseType);
        return (r == null) ? _secondary.findSerializationContentType(am, baseType) : r;
    }
    
    // // // Serialization: class annotations

    @Override
    public String[] findSerializationPropertyOrder(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac) {
        String[] r = _primary.findSerializationPropertyOrder(ac);
        return (r == null) ? _secondary.findSerializationPropertyOrder(ac) : r;
    }

    @Override
    public Boolean findSerializationSortAlphabetically(com.townmc.utils.jackson.databind.introspect.Annotated ann) {
        Boolean r = _primary.findSerializationSortAlphabetically(ann);
        return (r == null) ? _secondary.findSerializationSortAlphabetically(ann) : r;
    }

    @Override
    public void findAndAddVirtualProperties(MapperConfig<?> config, com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac,
            List<BeanPropertyWriter> properties) {
        // first secondary, then primary, to give proper precedence
        _primary.findAndAddVirtualProperties(config, ac, properties);
        _secondary.findAndAddVirtualProperties(config, ac, properties);
    }

    // // // Serialization: property annotations
    
    @Override
    public PropertyName findNameForSerialization(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        PropertyName n = _primary.findNameForSerialization(a);
        // note: "use default" should not block explicit answer, so:
        if (n == null) {
            n = _secondary.findNameForSerialization(a);
        } else if (n == PropertyName.USE_DEFAULT) {
            PropertyName n2 = _secondary.findNameForSerialization(a);
            if (n2 != null) {
                n = n2;
            }
        }
        return n;
    }

    @Override
    public Boolean hasAsValue(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Boolean b = _primary.hasAsValue(a);
        if (b == null) {
            b = _secondary.hasAsValue(a);
        }
        return b;
    }

    @Override
    public Boolean hasAnyGetter(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Boolean b = _primary.hasAnyGetter(a);
        if (b == null) {
            b = _secondary.hasAnyGetter(a);
        }
        return b;
    }

    @Override
    public  String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        // reverse order to give _primary higher precedence
        names = _secondary.findEnumValues(enumType, enumValues, names);
        names = _primary.findEnumValues(enumType, enumValues, names);
        return names;
    }

    @Override
    public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
        Enum<?> en = _primary.findDefaultEnumValue(enumCls);
        return (en == null) ? _secondary.findDefaultEnumValue(enumCls) : en;
    }

    @Override
    @Deprecated // since 2.8
    public String findEnumValue(Enum<?> value) {
        String r = _primary.findEnumValue(value);
        return (r == null) ? _secondary.findEnumValue(value) : r;
    }        

    @Override
    @Deprecated // since 2.9
    public boolean hasAsValueAnnotation(com.townmc.utils.jackson.databind.introspect.AnnotatedMethod am) {
        return _primary.hasAsValueAnnotation(am) || _secondary.hasAsValueAnnotation(am);
    }
    
    @Override
    @Deprecated // since 2.9
    public boolean hasAnyGetterAnnotation(com.townmc.utils.jackson.databind.introspect.AnnotatedMethod am) {
        return _primary.hasAnyGetterAnnotation(am) || _secondary.hasAnyGetterAnnotation(am);
    }

    // // // Deserialization: general annotations

    @Override
    public Object findDeserializer(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object r = _primary.findDeserializer(a);
        if (_isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findDeserializer(a),
                JsonDeserializer.None.class);
    }

    @Override
    public Object findKeyDeserializer(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object r = _primary.findKeyDeserializer(a);
        if (_isExplicitClassOrOb(r, KeyDeserializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findKeyDeserializer(a),
                KeyDeserializer.None.class);
    }

    @Override
    public Object findContentDeserializer(com.townmc.utils.jackson.databind.introspect.Annotated am) {
        Object r = _primary.findContentDeserializer(am);
        if (_isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(_secondary.findContentDeserializer(am),
                JsonDeserializer.None.class);
                
    }

    @Override
    public Object findDeserializationConverter(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Object ob = _primary.findDeserializationConverter(a);
        return (ob == null) ? _secondary.findDeserializationConverter(a) : ob;
    }

    @Override
    public Object findDeserializationContentConverter(AnnotatedMember a) {
        Object ob = _primary.findDeserializationContentConverter(a);
        return (ob == null) ? _secondary.findDeserializationContentConverter(a) : ob;
    }

    // // // Deserialization: type refinements

    // since 2.7
    @Override
    public JavaType refineDeserializationType(MapperConfig<?> config,
                                              com.townmc.utils.jackson.databind.introspect.Annotated a, JavaType baseType) throws JsonMappingException
    {
        JavaType t = _secondary.refineDeserializationType(config, a, baseType);
        return _primary.refineDeserializationType(config, a, t);
    }
    
    @Override
    @Deprecated
    public Class<?> findDeserializationType(com.townmc.utils.jackson.databind.introspect.Annotated am, JavaType baseType) {
        Class<?> r = _primary.findDeserializationType(am, baseType);
        return (r != null) ? r : _secondary.findDeserializationType(am, baseType);
    }

    @Override
    @Deprecated
    public Class<?> findDeserializationKeyType(com.townmc.utils.jackson.databind.introspect.Annotated am, JavaType baseKeyType) {
        Class<?> result = _primary.findDeserializationKeyType(am, baseKeyType);
        return (result == null) ? _secondary.findDeserializationKeyType(am, baseKeyType) : result;
    }

    @Override
    @Deprecated
    public Class<?> findDeserializationContentType(com.townmc.utils.jackson.databind.introspect.Annotated am, JavaType baseContentType) {
        Class<?> result = _primary.findDeserializationContentType(am, baseContentType);
        return (result == null) ? _secondary.findDeserializationContentType(am, baseContentType) : result;
    }
    
    // // // Deserialization: class annotations

    @Override
    public Object findValueInstantiator(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac) {
        Object result = _primary.findValueInstantiator(ac);
        return (result == null) ? _secondary.findValueInstantiator(ac) : result;
    }

    @Override
    public Class<?> findPOJOBuilder(com.townmc.utils.jackson.databind.introspect.AnnotatedClass ac) {
        Class<?> result = _primary.findPOJOBuilder(ac);
        return (result == null) ? _secondary.findPOJOBuilder(ac) : result;
    }

    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        JsonPOJOBuilder.Value result = _primary.findPOJOBuilderConfig(ac);
        return (result == null) ? _secondary.findPOJOBuilderConfig(ac) : result;
    }

    // // // Deserialization: method annotations

    @Override
    public PropertyName findNameForDeserialization(com.townmc.utils.jackson.databind.introspect.Annotated a)
    {
        // note: "use default" should not block explicit answer, so:
        PropertyName n = _primary.findNameForDeserialization(a);
        if (n == null) {
            n = _secondary.findNameForDeserialization(a);
        } else if (n == PropertyName.USE_DEFAULT) {
            PropertyName n2 = _secondary.findNameForDeserialization(a);
            if (n2 != null) {
                n = n2;
            }
        }
        return n;
    }

    @Override
    public Boolean hasAnySetter(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Boolean b = _primary.hasAnySetter(a);
        if (b == null) {
            b = _secondary.hasAnySetter(a);
        }
        return b;
    }

    @Override
    public JsonSetter.Value findSetterInfo(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        JsonSetter.Value v2 = _secondary.findSetterInfo(a);
        JsonSetter.Value v1 = _primary.findSetterInfo(a);
        return (v2 == null) // shouldn't occur but
            ? v1 : v2.withOverrides(v1);
    }

    @Override // since 2.9
    public Boolean findMergeInfo(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        Boolean b = _primary.findMergeInfo(a);
        if (b == null) {
            b = _secondary.findMergeInfo(a);
        }
        return b;
    }

    @Override
    @Deprecated // since 2.9
    public boolean hasCreatorAnnotation(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        return _primary.hasCreatorAnnotation(a) || _secondary.hasCreatorAnnotation(a);
    }

    @Override
    @Deprecated // since 2.9
    public JsonCreator.Mode findCreatorBinding(com.townmc.utils.jackson.databind.introspect.Annotated a) {
        JsonCreator.Mode mode = _primary.findCreatorBinding(a);
        if (mode != null) {
            return mode;
        }
        return _secondary.findCreatorBinding(a);
    }

    @Override
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        JsonCreator.Mode mode = _primary.findCreatorAnnotation(config, a);
        return (mode == null) ? _secondary.findCreatorAnnotation(config, a) : mode;
    }

    @Override
    @Deprecated // since 2.9
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return _primary.hasAnySetterAnnotation(am) || _secondary.hasAnySetterAnnotation(am);
    }

    protected boolean _isExplicitClassOrOb(Object maybeCls, Class<?> implicit) {
        if ((maybeCls == null) || (maybeCls == implicit)) {
            return false;
        }
        if (maybeCls instanceof Class<?>) {
            return !ClassUtil.isBogusClass((Class<?>) maybeCls);
        }
        return true;
    }

    // @since 2.9
    protected Object _explicitClassOrOb(Object maybeCls, Class<?> implicit) {
        if ((maybeCls == null) || (maybeCls == implicit)) {
            return null;
        }
        if ((maybeCls instanceof Class<?>) && ClassUtil.isBogusClass((Class<?>) maybeCls)) {
            return null;
        }
        return maybeCls;
    }
}
