package oracleIoT;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class DirectActivationService {

    public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/activation/direct";


    private String generateBodyRequest(String deviceID, String publicKey, String signature) {
        JSONObject requestJson = new JSONObject();

        requestJson
                .put("deviceModels", new JSONArray().put("urn:test:hackapp").put("urn:oracle:iot:dcd:capability:direct_activation"))
                .put("certificationRequestInfo", new JSONObject()
                        .put("subject", deviceID)
                        .put("subjectPublicKeyInfo", new JSONObject()
                                .put("algorithm", "RSA")
                                .put("publicKey", publicKey+"\r\n")
                                .put("format", "X.509")
                                .put("secretHashAlgorithm", "HmacSHA256")
                        )
                        .put("attributes", JSONObject.NULL)
                )
                .put("signatureAlgorithm", "SHA256withRSA")
                .put("signature", signature+"\r\n");

        return requestJson.toString();
    }


    public void activateDevice(String authorizationToken, String deviceID, String secret, byte[] publicKey, byte[] privateKey) throws Exception {

        String secret_hash = Base64.getEncoder().encodeToString(AuthenticationService.encode(secret, deviceID));

        String payload_string = deviceID + "\n" + "RSA" + "\n" + "X.509" + "\n" + "HmacSHA256";

        String signature = AuthenticationService.signSHA256RSA(payload_string + secret_hash + publicKey, privateKey);

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + authorizationToken);
        httpPost.setHeader("X-ActivationId", deviceID);

        String jsonRequest = generateBodyRequest(deviceID, Base64.getEncoder().encodeToString(publicKey), signature);

        System.out.println(jsonRequest);

        httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

        HttpClient httpClient = HttpClients.createDefault();


        HttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));


    }


    public KeyPair newKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        /* initialize with keySize: typically 2048 for RSA */
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }


}
