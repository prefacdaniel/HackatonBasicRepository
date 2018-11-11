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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DirectActivationService {
    public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/activation/direct";


    String authorizationToken;
    String signatureAlgorithm = "SHA256withRSA";
    String secretHashAlgorithm = "HmacSHA256";
    String keyAlgorithm = "RSA";
    String keyFormat = "X.509";
    String directActivationDeviceModel = "urn:oracle:iot:dcd:capability:direct_activation";
    String uniqueURN;
    String endpointId;
    String activationSecret;
    PublicKey publicKey;
    PrivateKey privateKey;

    /**
     * Prepare the parameters for direct activation
     *
     * @param endpointId         device end point id
     * @param activationSecret   shared secret for activating device
     * @param urn                device model URN attached to the device while activating
     */
    public DirectActivationService(String endpointId, String activationSecret, String urn, PrivateKey privateKey, PublicKey publicKey, String authorizationToken) {
        this.endpointId = endpointId;
        this.activationSecret = activationSecret;
        this.uniqueURN = urn;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.authorizationToken = authorizationToken;
    }

    /**
     * Generate activation payload and write to .json file
     */
    public String printDirectActivationPayload() {
        byte[] publicKeyByte = publicKey.getEncoded();
        byte[] signature = getSignature();

        JSONArray deviceModels = new JSONArray()
                .put(directActivationDeviceModel)
                .put(uniqueURN);


        JSONObject subjectPublicKeyInfo = new JSONObject()
                .put("algorithm", keyAlgorithm)
                .put("publicKey", Base64Encode(publicKeyByte))
                .put("format", keyFormat)
                .put("secretHashAlgorithm", secretHashAlgorithm);

        JSONObject certificationRequestInfo = new JSONObject()
                .put("subject", endpointId)
                .put("subjectPublicKeyInfo", subjectPublicKeyInfo)
                .put("attributes", JSONObject.NULL);


        JSONObject activationPayload = new JSONObject()
                .put("deviceModels", deviceModels)
                .put("certificationRequestInfo", certificationRequestInfo)
                .put("signatureAlgorithm", signatureAlgorithm)
                .put("signature", Base64Encode(signature));

        return activationPayload.toString();
    }

    private byte[] getSecretHash() {
        byte[] activationSecretByte = toUTF8(activationSecret);
        SecretKeySpec key = new SecretKeySpec(activationSecretByte, secretHashAlgorithm);
        Mac mac = null;
        try {
            mac = Mac.getInstance(secretHashAlgorithm);
            mac.init(key);
            mac.update(toUTF8(endpointId));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return mac.doFinal();
    }

    public static byte[] toUTF8(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static String Base64Encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    private byte[] getSignaturePayload() {
        byte[] secretHash = getSecretHash();
        byte[] publicKeyByte = publicKey.getEncoded();
        String payload = endpointId + "\n" + keyAlgorithm + "\n" + keyFormat + "\n" + secretHashAlgorithm + "\n";
        byte[] payloadBytes = toUTF8(payload);
        byte[] signatureBytes = new byte[payloadBytes.length + secretHash.length + publicKeyByte.length];
        System.arraycopy(payloadBytes, 0, signatureBytes, 0, payloadBytes.length);
        System.arraycopy(secretHash, 0, signatureBytes, payloadBytes.length, secretHash.length);
        System.arraycopy(publicKeyByte, 0, signatureBytes, secretHash.length + payloadBytes.length,
                publicKeyByte.length);
        return signatureBytes;
    }

    private byte[] getSignature() {
        byte[] signaturePayload = getSignaturePayload();
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(privateKey);
            signature.update(signaturePayload);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void execute() throws IOException {
        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + authorizationToken);
        httpPost.setHeader("X-ActivationId", endpointId);

//        String jsonRequest = (deviceID, Base64.getEncoder().encodeToString(publicKey), signature);
        String jsonRequest = printDirectActivationPayload();

        System.out.println(jsonRequest);

        httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));

        HttpClient httpClient = HttpClients.createDefault();


        HttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));

    }
}
