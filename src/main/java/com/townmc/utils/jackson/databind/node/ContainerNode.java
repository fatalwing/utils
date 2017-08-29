package com.townmc.utils.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.townmc.utils.jackson.core.*;
import com.townmc.utils.jackson.databind.JsonNode;
import com.townmc.utils.jackson.databind.node.ArrayNode;
import com.townmc.utils.jackson.databind.node.BaseJsonNode;
import com.townmc.utils.jackson.databind.node.BinaryNode;
import com.townmc.utils.jackson.databind.node.BooleanNode;
import com.townmc.utils.jackson.databind.node.JsonNodeCreator;
import com.townmc.utils.jackson.databind.node.JsonNodeFactory;
import com.townmc.utils.jackson.databind.node.NullNode;
import com.townmc.utils.jackson.databind.node.NumericNode;
import com.townmc.utils.jackson.databind.node.ObjectNode;
import com.townmc.utils.jackson.databind.node.TextNode;
import com.townmc.utils.jackson.databind.node.ValueNode;
import com.townmc.utils.jackson.databind.util.RawValue;

/**
 * This intermediate base class is used for all container nodes,
 * specifically, array and object nodes.
 */
public abstract class ContainerNode<T extends ContainerNode<T>>
    extends BaseJsonNode
    implements JsonNodeCreator
{
    /**
     * We will keep a reference to the Object (usually TreeMapper)
     * that can construct instances of nodes to add to this container
     * node.
     */
    protected final JsonNodeFactory _nodeFactory;

    protected ContainerNode(JsonNodeFactory nc) {
        _nodeFactory = nc;
    }

    // all containers are mutable: can't define:
//    @Override public abstract <T extends JsonNode> T deepCopy();

    @Override
    public abstract JsonToken asToken();

    @Override
    public String asText() { return ""; }

    /*
    /**********************************************************
    /* Methods reset as abstract to force real implementation
    /**********************************************************
     */

    @Override
    public abstract int size();

    @Override
    public abstract JsonNode get(int index);

    @Override
    public abstract JsonNode get(String fieldName);

    /*
    /**********************************************************
    /* JsonNodeCreator implementation, just dispatch to
    /* the real creator
    /**********************************************************
     */

    /**
     * Factory method that constructs and returns an empty {@link com.townmc.utils.jackson.databind.node.ArrayNode}
     * Construction is done using registered {@link JsonNodeFactory}.
     */
    @Override
    public final com.townmc.utils.jackson.databind.node.ArrayNode arrayNode() { return _nodeFactory.arrayNode(); }

    /**
     * Factory method that constructs and returns an {@link com.townmc.utils.jackson.databind.node.ArrayNode} with an initial capacity
     * Construction is done using registered {@link JsonNodeFactory}
     * @param capacity the initial capacity of the ArrayNode
     */
    @Override
    public final ArrayNode arrayNode(int capacity) { return _nodeFactory.arrayNode(capacity); }

    /**
     * Factory method that constructs and returns an empty {@link com.townmc.utils.jackson.databind.node.ObjectNode}
     * Construction is done using registered {@link JsonNodeFactory}.
     */
    @Override
    public final ObjectNode objectNode() { return _nodeFactory.objectNode(); }

    @Override
    public final NullNode nullNode() { return _nodeFactory.nullNode(); }

    @Override
    public final BooleanNode booleanNode(boolean v) { return _nodeFactory.booleanNode(v); }

    @Override
    public final com.townmc.utils.jackson.databind.node.NumericNode numberNode(byte v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.NumericNode numberNode(short v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.NumericNode numberNode(int v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.NumericNode numberNode(long v) {
        return _nodeFactory.numberNode(v);
    }

    @Override
    public final com.townmc.utils.jackson.databind.node.NumericNode numberNode(float v) { return _nodeFactory.numberNode(v); }
    @Override
    public final NumericNode numberNode(double v) { return _nodeFactory.numberNode(v); }

    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(BigInteger v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(BigDecimal v) { return (_nodeFactory.numberNode(v)); }

    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(Byte v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(Short v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(Integer v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(Long v) { return _nodeFactory.numberNode(v); }

    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(Float v) { return _nodeFactory.numberNode(v); }
    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode numberNode(Double v) { return _nodeFactory.numberNode(v); }

    @Override
    public final TextNode textNode(String text) { return _nodeFactory.textNode(text); }

    @Override
    public final com.townmc.utils.jackson.databind.node.BinaryNode binaryNode(byte[] data) { return _nodeFactory.binaryNode(data); }
    @Override
    public final BinaryNode binaryNode(byte[] data, int offset, int length) { return _nodeFactory.binaryNode(data, offset, length); }

    @Override
    public final com.townmc.utils.jackson.databind.node.ValueNode pojoNode(Object pojo) { return _nodeFactory.pojoNode(pojo); }

    @Override
    public final ValueNode rawValueNode(RawValue value) { return _nodeFactory.rawValueNode(value); }

    /*
    /**********************************************************
    /* Common mutators
    /**********************************************************
     */

    /**
     * Method for removing all children container has (if any)
     *
     * @return Container node itself (to allow method call chaining)
     */
    public abstract T removeAll();
}
