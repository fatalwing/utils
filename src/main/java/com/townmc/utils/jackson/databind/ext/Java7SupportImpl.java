package com.townmc.utils.jackson.databind.ext;

import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.nio.file.Path;

import com.townmc.utils.jackson.databind.JsonDeserializer;
import com.townmc.utils.jackson.databind.JsonSerializer;
import com.townmc.utils.jackson.databind.PropertyName;
import com.townmc.utils.jackson.databind.ext.Java7Support;
import com.townmc.utils.jackson.databind.ext.NioPathDeserializer;
import com.townmc.utils.jackson.databind.ext.NioPathSerializer;
import com.townmc.utils.jackson.databind.introspect.Annotated;
import com.townmc.utils.jackson.databind.introspect.AnnotatedParameter;
import com.townmc.utils.jackson.databind.introspect.AnnotatedWithParams;

/**
 * @since 2.8
 */
public class Java7SupportImpl extends Java7Support
{
    @SuppressWarnings("unused") // compiler warns, just needed side-effects
    private final Class<?> _bogus;

    public Java7SupportImpl() {
        // Trigger loading of annotations that only JDK 7 has...
        Class<?> cls = Transient.class;
        cls = ConstructorProperties.class;
        _bogus = cls;
    }

    @Override
    public Class<?> getClassJavaNioFilePath() {
        return Path.class;
    }

    @Override
    public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType) {
        if (rawType == Path.class) {
            return new NioPathDeserializer();
        }
        return null;
    }

    @Override
    public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType) {
        if (Path.class.isAssignableFrom(rawType)) {
            return new NioPathSerializer();
        }
        return null;
    }

    @Override
    public Boolean findTransient(Annotated a) {
        Transient t = a.getAnnotation(Transient.class);
        if (t != null) {
            return t.value();
        }
        return null;
    }

    @Override
    public Boolean hasCreatorAnnotation(Annotated a) {
        ConstructorProperties props = a.getAnnotation(ConstructorProperties.class);
        // 08-Nov-2015, tatu: One possible check would be to ensure there is at least
        //    one name iff constructor has arguments. But seems unnecessary for now.
        if (props != null) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override
    public PropertyName findConstructorName(AnnotatedParameter p)
    {
        AnnotatedWithParams ctor = p.getOwner();
        if (ctor != null) {
            ConstructorProperties props = ctor.getAnnotation(ConstructorProperties.class);
            if (props != null) {
                String[] names = props.value();
                int ix = p.getIndex();
                if (ix < names.length) {
                    return PropertyName.construct(names[ix]);
                }
            }
        }
        return null;
    }
}
