package com.townmc.utils.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.townmc.utils.jackson.databind.node.ArrayNode;
import com.townmc.utils.jackson.databind.node.ContainerNode;
import com.townmc.utils.jackson.databind.node.JsonNodeFactory;
import com.townmc.utils.jackson.databind.node.ObjectNode;
import com.townmc.utils.jackson.databind.node.ValueNode;
import com.townmc.utils.jackson.databind.util.RawValue;

/**
 * Interface that defines common "creator" functionality implemented
 * both by {@link JsonNodeFactory} and {@link ContainerNode} (that is,
 * JSON Object and Array nodes).
 *
 * @since 2.3
 */
public interface JsonNodeCreator
{
    // Enumerated/singleton types

    public com.townmc.utils.jackson.databind.node.ValueNode booleanNode(boolean v);
    public com.townmc.utils.jackson.databind.node.ValueNode nullNode();

    // Numeric types

    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(byte v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Byte value);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(short v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Short value);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(int v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Integer value);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(long v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Long value);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(BigInteger v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(float v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Float value);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(double v);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Double value);
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(BigDecimal v);

    // Textual nodes

    public com.townmc.utils.jackson.databind.node.ValueNode textNode(String text);

    // Other value (non-structured) nodes

    public com.townmc.utils.jackson.databind.node.ValueNode binaryNode(byte[] data);
    public com.townmc.utils.jackson.databind.node.ValueNode binaryNode(byte[] data, int offset, int length);
    public com.townmc.utils.jackson.databind.node.ValueNode pojoNode(Object pojo);

    /**
     * Factory method to use for adding "raw values"; pre-encoded values
     * that are included exactly as-is when node is serialized.
     * This may be used, for example, to include fully serialized JSON
     * sub-trees.
     * Note that the concept may not work with all backends, and since
     * no translation of any kinds is done it will not work when converting
     * between data formats.
     *
     * @since 2.6
     */
    public ValueNode rawValueNode(RawValue value);

    // Structured nodes:
    // (bit unkosher, due to forward references... but has to do for now)

    public com.townmc.utils.jackson.databind.node.ArrayNode arrayNode();

    /**
     * Factory method for constructing a JSON Array node with an initial capacity
     *
     * @since 2.8
     */
    public ArrayNode arrayNode(int capacity);

    public ObjectNode objectNode();
}
