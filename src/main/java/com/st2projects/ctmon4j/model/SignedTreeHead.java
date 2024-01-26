package com.st2projects.ctmon4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Base64;

public record SignedTreeHead(@JsonProperty("tree_size") long treeSize,
                             @JsonProperty("timestamp") long timestamp,
                             @JsonProperty("sha256_root_hash") String rootHash,
                             @JsonProperty("tree_head_signature") String treeHeadSignature)
{
    public byte[] getBytes() throws IOException
    {

        ByteBuffer byteBufferT = ByteBuffer.allocate( Long.BYTES );
        byteBufferT.putLong( timestamp );

        ByteBuffer byteBufferS = ByteBuffer.allocate( Long.BYTES );
        byteBufferS.putLong( treeSize );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( "v1".getBytes() );
        outputStream.write( byteBufferT.array() );
        outputStream.write( byteBufferS.array() );
        outputStream.write( Base64.getDecoder().decode( rootHash ) );

        return outputStream.toByteArray();
    }
}