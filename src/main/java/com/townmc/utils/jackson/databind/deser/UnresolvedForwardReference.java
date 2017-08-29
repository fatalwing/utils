package com.townmc.utils.jackson.databind.deser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.townmc.utils.jackson.core.JsonLocation;
import com.townmc.utils.jackson.core.JsonParser;

import com.townmc.utils.jackson.databind.JsonMappingException;
import com.townmc.utils.jackson.databind.deser.UnresolvedId;
import com.townmc.utils.jackson.databind.deser.impl.ReadableObjectId;

/**
 * Exception thrown during deserialization when there are object id that can't
 * be resolved.
 * 
 * @author pgelinas
 */
public class UnresolvedForwardReference extends JsonMappingException {
    private static final long serialVersionUID = 1L;
    private ReadableObjectId _roid;
    private List<com.townmc.utils.jackson.databind.deser.UnresolvedId> _unresolvedIds;

    /**
     * @since 2.7
     */
    public UnresolvedForwardReference(JsonParser p, String msg, JsonLocation loc, ReadableObjectId roid) {
        super(p, msg, loc);
        _roid = roid;
    }

    /**
     * @since 2.7
     */
    public UnresolvedForwardReference(JsonParser p, String msg) {
        super(p, msg);
        _unresolvedIds = new ArrayList<com.townmc.utils.jackson.databind.deser.UnresolvedId>();
    }

    /**
     * @deprecated Since 2.7
     */
    @Deprecated // since 2.7
    public UnresolvedForwardReference(String msg, JsonLocation loc, ReadableObjectId roid) {
        super(msg, loc);
        _roid = roid;
    }

    /**
     * @deprecated Since 2.7
     */
    @Deprecated // since 2.7
    public UnresolvedForwardReference(String msg) {
        super(msg);
        _unresolvedIds = new ArrayList<com.townmc.utils.jackson.databind.deser.UnresolvedId>();
    }

    /*
    /**********************************************************
    /* Accessor methods
    /**********************************************************
     */

    public ReadableObjectId getRoid() {
        return _roid;
    }

    public Object getUnresolvedId() {
        return _roid.getKey().key;
    }

    public void addUnresolvedId(Object id, Class<?> type, JsonLocation where) {
        _unresolvedIds.add(new com.townmc.utils.jackson.databind.deser.UnresolvedId(id, type, where));
    }

    public List<com.townmc.utils.jackson.databind.deser.UnresolvedId> getUnresolvedIds(){
        return _unresolvedIds;
    }
    
    @Override
    public String getMessage()
    {
        String msg = super.getMessage();
        if (_unresolvedIds == null) {
            return msg;
        }

        StringBuilder sb = new StringBuilder(msg);
        Iterator<com.townmc.utils.jackson.databind.deser.UnresolvedId> iterator = _unresolvedIds.iterator();
        while (iterator.hasNext()) {
            UnresolvedId unresolvedId = iterator.next();
            sb.append(unresolvedId.toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('.');
        return sb.toString();
    }
}
