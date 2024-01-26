package com.st2projects.ctmon4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st2projects.ctmon4j.model.SignedTreeHead;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Startup
@ApplicationScoped
public class StartInitialiser
{
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private HttpClient httpClient;

    private static final String SIG_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEKATl2B3SAbxyzGOfNRB+AytNTGvdF/FFY6HzWb+/HPE4lJ37vx2nEm99KYUy9SoNzF5VyTwCQG5nL/c5Q77yQQ==";

    @PostConstruct
    public void onStart() throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException
    {
        System.out.println("================================================= STARTED");

        HttpRequest request = HttpRequest.newBuilder()
                .uri( URI.create("https://ct.googleapis.com/logs/crucible/ct/v1/get-sth") )
                .GET().build();

        HttpResponse<InputStream> response = httpClient.send( request, HttpResponse.BodyHandlers.ofInputStream() );

        SignedTreeHead treeHead = objectMapper.readValue( response.body(), SignedTreeHead.class );

        System.out.println(treeHead);

        Signature signature = Signature.getInstance( "NONEwithECDSA" );
        PublicKey publicKey = getSignerKey();
        signature.initVerify( publicKey );
        byte[] messageBytes = treeHead.rootHash().getBytes( StandardCharsets.UTF_8 );
        signature.update( messageBytes );

        System.out.println("========================== " + signature.verify( treeHead.treeHeadSignature().getBytes(StandardCharsets.UTF_8) ));
    }

    static PublicKey getSignerKey() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] decodedKey = Base64.getDecoder().decode( SIG_KEY );
        KeyFactory keyFactory = KeyFactory.getInstance( "EC" );

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec( decodedKey );
        return keyFactory.generatePublic( keySpec );
    }

    public static void main( String[] args ) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, IOException
    {
        SignedTreeHead signedTreeHead = new SignedTreeHead( 3355507, 1706208612843L, "eta69dBfVaFkFXrVbPFtZtcIPJVm3vioYmDLd6+c9rM=", "BAMARzBFAiEA9cgRTrlHPEXTwb+BAmgEAkWu3PtvYhgrsSNgeWR969oCIBCgj4QQUY5S4yz7PnrwDbGf6il/YdR1ekCAhuvRStFZ" );

        Signature signature = Signature.getInstance( "SHA256withECDSAinP1363Format" );
        PublicKey publicKey = getSignerKey();

        byte[] msgB = Base64.getDecoder().decode( signedTreeHead.rootHash() );
        signature.initVerify( publicKey );
        signature.update( signedTreeHead.getBytes() );
        System.out.println(signature.verify( Base64.getDecoder().decode( signedTreeHead.treeHeadSignature() ) ));
    }
}
