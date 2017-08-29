package com.townmc.utils.jackson.core;

import com.townmc.utils.jackson.core.JsonGenerator;
import com.townmc.utils.jackson.core.JsonParser;
import com.townmc.utils.jackson.core.JsonProcessingException;
import com.townmc.utils.jackson.core.TreeNode;

import java.io.IOException;

/**
 * Interface that defines objects that can read and write
 * {@link com.townmc.utils.jackson.core.TreeNode} instances using Streaming API.
 * 
 * @since 2.3
 */
public abstract class TreeCodec
{
    public abstract <T extends com.townmc.utils.jackson.core.TreeNode> T readTree(JsonParser p) throws IOException, JsonProcessingException;
    public abstract void writeTree(JsonGenerator g, com.townmc.utils.jackson.core.TreeNode tree) throws IOException, JsonProcessingException;
    public abstract com.townmc.utils.jackson.core.TreeNode createArrayNode();
    public abstract com.townmc.utils.jackson.core.TreeNode createObjectNode();
    public abstract JsonParser treeAsTokens(TreeNode node);
}
