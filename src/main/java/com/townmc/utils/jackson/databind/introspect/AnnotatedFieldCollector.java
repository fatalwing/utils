package com.townmc.utils.jackson.databind.introspect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import com.townmc.utils.jackson.databind.AnnotationIntrospector;
import com.townmc.utils.jackson.databind.JavaType;
import com.townmc.utils.jackson.databind.introspect.AnnotatedField;
import com.townmc.utils.jackson.databind.introspect.AnnotationCollector;
import com.townmc.utils.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.townmc.utils.jackson.databind.introspect.CollectorBase;
import com.townmc.utils.jackson.databind.introspect.TypeResolutionContext;
import com.townmc.utils.jackson.databind.type.TypeFactory;
import com.townmc.utils.jackson.databind.util.ClassUtil;

public class AnnotatedFieldCollector
    extends com.townmc.utils.jackson.databind.introspect.CollectorBase
{
    // // // Configuration

    private final TypeFactory _typeFactory;
    private final MixInResolver _mixInResolver;

    // // // Collected state

    AnnotatedFieldCollector(AnnotationIntrospector intr,
            TypeFactory types, MixInResolver mixins)
    {
        super(intr);
        _typeFactory = types;
        _mixInResolver = (intr == null) ? null : mixins;
    }

    public static List<com.townmc.utils.jackson.databind.introspect.AnnotatedField> collectFields(AnnotationIntrospector intr,
                                                                                               com.townmc.utils.jackson.databind.introspect.TypeResolutionContext tc,
                                                                                               MixInResolver mixins, TypeFactory types,
                                                                                               JavaType type)
    {
        return new AnnotatedFieldCollector(intr, types, mixins).collect(tc, type);
    }

    List<com.townmc.utils.jackson.databind.introspect.AnnotatedField> collect(com.townmc.utils.jackson.databind.introspect.TypeResolutionContext tc, JavaType type)
    {
        Map<String,FieldBuilder> foundFields = _findFields(tc, type, null);
        if (foundFields == null) {
            return Collections.emptyList();
        }
        List<com.townmc.utils.jackson.databind.introspect.AnnotatedField> result = new ArrayList<>(foundFields.size());
        for (FieldBuilder b : foundFields.values()) {
            result.add(b.build());
        }
        return result;
    }

    private Map<String,FieldBuilder> _findFields(com.townmc.utils.jackson.databind.introspect.TypeResolutionContext tc,
                                                 JavaType type, Map<String,FieldBuilder> fields)
    {
        // First, a quick test: we only care for regular classes (not interfaces,
        //primitive types etc), except for Object.class. A simple check to rule out
        // other cases is to see if there is a super class or not.
        JavaType parent = type.getSuperClass();
        if (parent == null) {
            return fields;
        }
        final Class<?> cls = type.getRawClass();
        // Let's add super-class' fields first, then ours.
        fields = _findFields(new com.townmc.utils.jackson.databind.introspect.TypeResolutionContext.Basic(_typeFactory, parent.getBindings()),
                parent, fields);
        for (Field f : ClassUtil.getDeclaredFields(cls)) {
            // static fields not included (transients are at this point, filtered out later)
            if (!_isIncludableField(f)) {
                continue;
            }
            // Ok now: we can (and need) not filter out ignorable fields at this point; partly
            // because mix-ins haven't been added, and partly because logic can be done
            // when determining get/settability of the field.
            if (fields == null) {
                fields = new LinkedHashMap<>();
            }
            FieldBuilder b = new FieldBuilder(tc, f);
            if (_intr != null) {
                b.annotations = collectAnnotations(b.annotations, f.getDeclaredAnnotations());
            }
            fields.put(f.getName(), b);
        }
        // And then... any mix-in overrides?
        if (_mixInResolver != null) {
            Class<?> mixin = _mixInResolver.findMixInClassFor(cls);
            if (mixin != null) {
                _addFieldMixIns(mixin, cls, fields);
            }
        }
        return fields;
    }

    /**
     * Method called to add field mix-ins from given mix-in class (and its fields)
     * into already collected actual fields (from introspected classes and their
     * super-classes)
     */
    private void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass,
            Map<String,FieldBuilder> fields)
    {
        List<Class<?>> parents = ClassUtil.findSuperClasses(mixInCls, targetClass, true);
        for (Class<?> mixin : parents) {
            for (Field mixinField : ClassUtil.getDeclaredFields(mixin)) {
                // there are some dummy things (static, synthetic); better ignore
                if (!_isIncludableField(mixinField)) {
                    continue;
                }
                String name = mixinField.getName();
                // anything to mask? (if not, quietly ignore)
                FieldBuilder b = fields.get(name);
                if (b != null) {
                    b.annotations = collectAnnotations(b.annotations, mixinField.getDeclaredAnnotations());
                }
            }
        }
    }

    private boolean _isIncludableField(Field f)
    {
        // Most likely synthetic fields, if any, are to be skipped similar to methods
        if (f.isSynthetic()) {
            return false;
        }
        // Static fields are never included. Transient are (since 2.6), for
        // purpose of propagating removal
        int mods = f.getModifiers();
        if (Modifier.isStatic(mods)) {
            return false;
        }
        return true;
    }

    private final static class FieldBuilder {
        public final com.townmc.utils.jackson.databind.introspect.TypeResolutionContext typeContext;
        public final Field field;

        public com.townmc.utils.jackson.databind.introspect.AnnotationCollector annotations;

        public FieldBuilder(TypeResolutionContext tc, Field f) {
            typeContext = tc;
            field = f;
            annotations = AnnotationCollector.emptyCollector();
        }

        public com.townmc.utils.jackson.databind.introspect.AnnotatedField build() {
            return new AnnotatedField(typeContext, field, annotations.asAnnotationMap());
        }
    }
}
