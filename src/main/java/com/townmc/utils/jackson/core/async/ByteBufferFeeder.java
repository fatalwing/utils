package com.townmc.utils.jackson.core.async;

import com.townmc.utils.jackson.core.async.NonBlockingInputFeeder;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * {@link NonBlockingInputFeeder} implementation used when feeding data
 * as {@link ByteBuffer} contents.
 *
 * @since 2.9
 */
public interface ByteBufferFeeder extends NonBlockingInputFeeder
{
     /**
      * Method that can be called to feed more data, if (and only if)
      * {@link NonBlockingInputFeeder#needMoreInput} returns true.
      * 
      * @param buffer Buffer that contains additional input to read
      * 
      * @throws IOException if the state is such that this method should not be called
      *   (has not yet consumed existing input data, or has been marked as closed)
      */
     public void feedInput(ByteBuffer buffer) throws IOException;
}
