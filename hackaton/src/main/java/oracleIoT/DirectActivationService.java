package oracleIoT;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


public class DirectActivationService {

    public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/activation/indirect/deviceApp";


    private String generateBodyRequest(String deviceID, String publicKey, String signature) {
        JSONObject requestJson = new JSONObject();

        requestJson
                .put("deviceModels", new JSONArray().put("urn:oracle:iot:dcd:capability:direct_activation"))
                .put("certificationRequestInfo", new JSONObject()
                        .put("subject", deviceID)
                        .put("subjectPublicKeyInfo", new JSONObject()
                                .put("algorithm", "RSA")
                                .put("publicKey", publicKey)
                                .put("format", "X.509")
                                .put("secretHashAlgorithm", "HmacSHA256")
                        )
                        .put("attributes", "")
                )
                .put("signatureAlgorithm", "SHA256withRSA")
                .put("signature", signature);

        return requestJson.toString();
    }




    public void activateDevice(String authorizationToken, String deviceID, String secret, String publicKey, String privateKey) throws Exception {

        String secret_hash = new String(AuthenticationService.encode(secret,deviceID));

        String payload_string = deviceID + "\n" + "RSA" + "\n" + "X.509" + "\n" + "HmacSHA256";

        String signature = AuthenticationService.signSHA256RSA(payload_string+secret_hash+publicKey, privateKey);

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", authorizationToken);
        httpPost.setHeader("X-ActivationId", deviceID + generateBodyRequest(deviceID,publicKey,signature ));



        HttpClient httpClient = HttpClients.createDefault();


        HttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println( EntityUtils.toString(httpResponse.getEntity()));


    }


    public KeyPair newKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        /* initialize with keySize: typically 2048 for RSA */
        kpg.initialize(2048 );
        return kpg.generateKeyPair();
    }


}
