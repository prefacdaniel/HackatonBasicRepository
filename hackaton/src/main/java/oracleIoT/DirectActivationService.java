package oracleIoT;

import org.json.JSONArray;
import org.json.JSONObject;

public class DirectActivationService {

    public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/activation/indirect/deviceApp";


    private String generateBodyRequest(String deviceID, String publicKey, String signature) {
        JSONObject requestJson = new JSONObject();

        requestJson
                .put("deviceModels", new JSONArray())
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

    public void activateDevice(String authorizationToken, String deviceID) {

    }

}
