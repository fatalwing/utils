package com.townmc.utils.jackson.databind.deser;

import com.townmc.utils.jackson.databind.*;
import com.townmc.utils.jackson.databind.deser.ValueInstantiator;

/**
 * Interface for providers of {@link com.townmc.utils.jackson.databind.deser.ValueInstantiator} instances.
 * Implemented when an object wants to provide custom value instantiators,
 * usually to support custom value types with alternate constructors, or
 * which need specified post-processing after construction but before
 * binding data.
 */
public interface ValueInstantiators
{
    /**
     * Method called to find the {@link com.townmc.utils.jackson.databind.deser.ValueInstantiator} to use for creating
     * instances of specified type during deserialization.
     * Note that a default value instantiator is always created first and passed;
     * if an implementation does not want to modify or replace it, it has to return
     * passed instance as is (returning null is an error)
     * 
     * @param config Deserialization configuration in use
     * @param beanDesc Additional information about POJO type to be instantiated
     * @param defaultInstantiator Instantiator that will be used if no changes are made;
     *   passed to allow custom instances to use annotation-provided information
     *   (note, however, that earlier {@link ValueInstantiators} may have changed it to
     *   a custom instantiator already)
     *   
     * @return Instantiator to use; either <code>defaultInstantiator</code> that was passed,
     *   or a custom variant; cannot be null.
     */
    public com.townmc.utils.jackson.databind.deser.ValueInstantiator findValueInstantiator(DeserializationConfig config,
                                                                                        BeanDescription beanDesc, com.townmc.utils.jackson.databind.deser.ValueInstantiator defaultInstantiator);

    /**
     * Basic "NOP" implementation that can be used as the base class for custom implementations.
     * Safer to extend (instead of implementing {@link ValueInstantiators}) in case later
     * Jackson versions add new methods in base interface.
     */
    public static class Base implements ValueInstantiators
    {
        @Override
        public com.townmc.utils.jackson.databind.deser.ValueInstantiator findValueInstantiator(DeserializationConfig config,
                                                                                            BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
            return defaultInstantiator;
        }
    }
}
