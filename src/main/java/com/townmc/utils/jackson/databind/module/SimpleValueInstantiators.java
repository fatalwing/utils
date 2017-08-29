package com.townmc.utils.jackson.databind.module;

import java.util.HashMap;

import com.townmc.utils.jackson.databind.BeanDescription;
import com.townmc.utils.jackson.databind.DeserializationConfig;
import com.townmc.utils.jackson.databind.deser.ValueInstantiator;
import com.townmc.utils.jackson.databind.deser.ValueInstantiators;
import com.townmc.utils.jackson.databind.type.ClassKey;

public class SimpleValueInstantiators
    extends ValueInstantiators.Base
    implements java.io.Serializable
{
    private static final long serialVersionUID = -8929386427526115130L;

    /**
     * Mappings from raw (type-erased, i.e. non-generic) types
     * to matching {@link ValueInstantiator} instances.
     */
    protected HashMap<ClassKey,ValueInstantiator> _classMappings;

    /*
    /**********************************************************
    /* Life-cycle, construction and configuring
    /**********************************************************
     */

    public SimpleValueInstantiators()
    {
        _classMappings = new HashMap<ClassKey,ValueInstantiator>();        
    }
    
    public SimpleValueInstantiators addValueInstantiator(Class<?> forType,
            ValueInstantiator inst)
    {
        _classMappings.put(new ClassKey(forType), inst);
        return this;
    }
    
    @Override
    public ValueInstantiator findValueInstantiator(DeserializationConfig config,
            BeanDescription beanDesc, ValueInstantiator defaultInstantiator)
    {
        ValueInstantiator inst = _classMappings.get(new ClassKey(beanDesc.getBeanClass()));
        return (inst == null) ? defaultInstantiator : inst;
    }
}
