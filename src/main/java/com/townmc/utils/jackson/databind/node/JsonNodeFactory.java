package com.townmc.utils.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.townmc.utils.jackson.databind.node.ArrayNode;
import com.townmc.utils.jackson.databind.node.BigIntegerNode;
import com.townmc.utils.jackson.databind.node.BinaryNode;
import com.townmc.utils.jackson.databind.node.BooleanNode;
import com.townmc.utils.jackson.databind.node.DecimalNode;
import com.townmc.utils.jackson.databind.node.DoubleNode;
import com.townmc.utils.jackson.databind.node.FloatNode;
import com.townmc.utils.jackson.databind.node.IntNode;
import com.townmc.utils.jackson.databind.node.JsonNodeCreator;
import com.townmc.utils.jackson.databind.node.LongNode;
import com.townmc.utils.jackson.databind.node.NullNode;
import com.townmc.utils.jackson.databind.node.NumericNode;
import com.townmc.utils.jackson.databind.node.ObjectNode;
import com.townmc.utils.jackson.databind.node.POJONode;
import com.townmc.utils.jackson.databind.node.ShortNode;
import com.townmc.utils.jackson.databind.node.TextNode;
import com.townmc.utils.jackson.databind.node.ValueNode;
import com.townmc.utils.jackson.databind.util.RawValue;

/**
 * Base class that specifies methods for getting access to
 * Node instances (newly constructed, or shared, depending
 * on type), as well as basic implementation of the methods.
 * Designed to be sub-classed if extended functionality (additions
 * to behavior of node types, mostly) is needed.
 */
public class JsonNodeFactory
    implements java.io.Serializable, // since 2.1
        JsonNodeCreator // since 2.3
{
    // with 2.2
    private static final long serialVersionUID = 1L;

    private final boolean _cfgBigDecimalExact;

    private static final JsonNodeFactory decimalsNormalized
        = new JsonNodeFactory(false);
    private static final JsonNodeFactory decimalsAsIs
        = new JsonNodeFactory(true);

    /**
     * Default singleton instance that construct "standard" node instances:
     * given that this class is stateless, a globally shared singleton
     * can be used.
     */
    public final static JsonNodeFactory instance = decimalsNormalized;

    /**
     * Main constructor
     *
     * <p>The only argument to this constructor is a boolean telling whether
     * {@link com.townmc.utils.jackson.databind.node.DecimalNode} instances must be built with exact representations of
     * {@link BigDecimal} instances.</p>
     *
     * <p>This has quite an influence since, for instance, a BigDecimal (and,
     * therefore, a DecimalNode) constructed from input string {@code "1.0"} and
     * another constructed with input string {@code "1.00"} <b>will not</b> be
     * equal, since their scale differs (1 in the first case, 2 in the second
     * case).</p>
     *
     * <p>Note that setting the argument to {@code true} does <i>not</i>
     * guarantee a strict inequality between JSON representations: input texts
     * {@code "0.1"} and {@code "1e-1"}, for instance, yield two equivalent
     * BigDecimal instances since they have the same scale (1).</p>
     *
     * <p>The no-arg constructor (and the default {@link #instance}) calls this
     * constructor with {@code false} as an argument.</p>
     *
     * @param bigDecimalExact see description
     *
     * @see BigDecimal
     */
    public JsonNodeFactory(boolean bigDecimalExact)
    {
        _cfgBigDecimalExact = bigDecimalExact;
    }

    /**
     * Default constructor
     *
     * <p>This calls {@link #JsonNodeFactory(boolean)} with {@code false}
     * as an argument.</p>
     */
    protected JsonNodeFactory()
    {
        this(false);
    }

    /**
     * Return a factory instance with the desired behavior for BigDecimals
     * <p>See {@link #JsonNodeFactory(boolean)} for a full description.</p>
     *
     * @param bigDecimalExact see description
     * @return a factory instance
     */
    public static JsonNodeFactory withExactBigDecimals(boolean bigDecimalExact)
    {
        return bigDecimalExact ? decimalsAsIs : decimalsNormalized;
    }

    /*
    /**********************************************************
    /* Factory methods for literal values
    /**********************************************************
     */

    /**
     * Factory method for getting an instance of JSON boolean value
     * (either literal 'true' or 'false')
     */
    @Override
    public com.townmc.utils.jackson.databind.node.BooleanNode booleanNode(boolean v) {
        return v ? com.townmc.utils.jackson.databind.node.BooleanNode.getTrue() : BooleanNode.getFalse();
    }

    /**
     * Factory method for getting an instance of JSON null node (which
     * represents literal null value)
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NullNode nullNode() { return NullNode.getInstance(); }

    /*
    /**********************************************************
    /* Factory methods for numeric values
    /**********************************************************
     */

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given 8-bit value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NumericNode numberNode(byte v) { return com.townmc.utils.jackson.databind.node.IntNode.valueOf(v); }

    /**
     * Alternate factory method that will handle wrapper value, which may
     * be null.
     * Due to possibility of null, returning type is not guaranteed to be
     * {@link com.townmc.utils.jackson.databind.node.NumericNode}, but just {@link com.townmc.utils.jackson.databind.node.ValueNode}.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Byte value) {
        return (value == null) ? nullNode() : com.townmc.utils.jackson.databind.node.IntNode.valueOf(value.intValue());
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given 16-bit integer value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NumericNode numberNode(short v) { return com.townmc.utils.jackson.databind.node.ShortNode.valueOf(v); }

    /**
     * Alternate factory method that will handle wrapper value, which may
     * be null.
     * Due to possibility of null, returning type is not guaranteed to be
     * {@link com.townmc.utils.jackson.databind.node.NumericNode}, but just {@link com.townmc.utils.jackson.databind.node.ValueNode}.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Short value) {
        return (value == null) ? nullNode() : ShortNode.valueOf(value);
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given 32-bit integer value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NumericNode numberNode(int v) { return com.townmc.utils.jackson.databind.node.IntNode.valueOf(v); }

    /**
     * Alternate factory method that will handle wrapper value, which may
     * be null.
     * Due to possibility of null, returning type is not guaranteed to be
     * {@link com.townmc.utils.jackson.databind.node.NumericNode}, but just {@link com.townmc.utils.jackson.databind.node.ValueNode}.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Integer value) {
        return (value == null) ? nullNode() : IntNode.valueOf(value.intValue());
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given 64-bit integer value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NumericNode numberNode(long v) {
        return com.townmc.utils.jackson.databind.node.LongNode.valueOf(v);
    }

    /**
     * Alternate factory method that will handle wrapper value, which may be null.
     * Due to possibility of null, returning type is not guaranteed to be
     * {@link com.townmc.utils.jackson.databind.node.NumericNode}, but just {@link com.townmc.utils.jackson.databind.node.ValueNode}.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Long v) {
        if (v == null) {
            return nullNode();
        }
        return LongNode.valueOf(v.longValue());
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given unlimited range integer value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(BigInteger v) {
        if (v == null) {
            return nullNode();
        }
        return BigIntegerNode.valueOf(v);
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given 32-bit floating point value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NumericNode numberNode(float v) { return com.townmc.utils.jackson.databind.node.FloatNode.valueOf((float) v); }

    /**
     * Alternate factory method that will handle wrapper value, which may
     * be null.
     * Due to possibility of null, returning type is not guaranteed to be
     * {@link com.townmc.utils.jackson.databind.node.NumericNode}, but just {@link com.townmc.utils.jackson.databind.node.ValueNode}.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Float value) {
        return (value == null) ? nullNode() : FloatNode.valueOf(value.floatValue());
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given 64-bit floating point value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.NumericNode numberNode(double v) { return com.townmc.utils.jackson.databind.node.DoubleNode.valueOf(v); }

    /**
     * Alternate factory method that will handle wrapper value, which may
     * be null.
     * Due to possibility of null, returning type is not guaranteed to be
     * {@link NumericNode}, but just {@link com.townmc.utils.jackson.databind.node.ValueNode}.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(Double value) {
        return (value == null) ? nullNode() : DoubleNode.valueOf(value.doubleValue());
    }

    /**
     * Factory method for getting an instance of JSON numeric value
     * that expresses given unlimited precision floating point value
     *
     * <p>In the event that the factory has been built to normalize decimal
     * values, the BigDecimal argument will be stripped off its trailing zeroes,
     * using {@link BigDecimal#stripTrailingZeros()}.</p>
     *
     * @see #JsonNodeFactory(boolean)
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode numberNode(BigDecimal v)
    {
        if (v == null) {
            return nullNode();
        }

        /*
         * If the user wants the exact representation of this big decimal,
         * return the value directly
         */
        if (_cfgBigDecimalExact)
            return com.townmc.utils.jackson.databind.node.DecimalNode.valueOf(v);

        /*
         * If the user has asked to strip trailing zeroes, however, there is
         * this bug to account for:
         *
         * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6480539
         *
         * In short: zeroes are never stripped out of 0! We therefore _have_
         * to compare with BigDecimal.ZERO...
         */
        return v.compareTo(BigDecimal.ZERO) == 0 ? com.townmc.utils.jackson.databind.node.DecimalNode.ZERO
            : DecimalNode.valueOf(v.stripTrailingZeros());
    }

    /*
    /**********************************************************
    /* Factory methods for textual values
    /**********************************************************
     */

    /**
     * Factory method for constructing a node that represents JSON
     * String value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.TextNode textNode(String text) { return TextNode.valueOf(text); }

    /**
     * Factory method for constructing a node that represents given
     * binary data, and will get serialized as equivalent base64-encoded
     * String value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.BinaryNode binaryNode(byte[] data) { return com.townmc.utils.jackson.databind.node.BinaryNode.valueOf(data); }

    /**
     * Factory method for constructing a node that represents given
     * binary data, and will get serialized as equivalent base64-encoded
     * String value
     */
    @Override
    public com.townmc.utils.jackson.databind.node.BinaryNode binaryNode(byte[] data, int offset, int length) {
        return BinaryNode.valueOf(data, offset, length);
    }

    /*
    /**********************************************************
    /* Factory method for structured values
    /**********************************************************
     */

    /**
     * Factory method for constructing an empty JSON Array node
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ArrayNode arrayNode() { return new com.townmc.utils.jackson.databind.node.ArrayNode(this); }

    /**
     * Factory method for constructing a JSON Array node with an initial capacity
     *
     * @since 2.8
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ArrayNode arrayNode(int capacity) { return new ArrayNode(this, capacity); }

    /**
     * Factory method for constructing an empty JSON Object ("struct") node
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ObjectNode objectNode() { return new ObjectNode(this); }

    /**
     * Factory method for constructing a wrapper for POJO
     * ("Plain Old Java Object") objects; these will get serialized
     * using data binding, usually as JSON Objects, but in some
     * cases as JSON Strings or other node types.
     */
    @Override
    public com.townmc.utils.jackson.databind.node.ValueNode pojoNode(Object pojo) { return new com.townmc.utils.jackson.databind.node.POJONode(pojo); }

    @Override
    public ValueNode rawValueNode(RawValue value) {
        return new POJONode(value);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected boolean _inIntRange(long l)
    {
        int i = (int) l;
        long l2 = (long) i;
        return (l2 == l);
    }
}

