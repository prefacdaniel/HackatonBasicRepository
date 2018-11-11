package oracleIoT;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;


public class RegisterDevice {


    private final static String ENDPOINT_URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/devices";

    private String deviceID;
    private String secret;
    private String hardwareID;

    public RegisterDevice(String deviceID, String hardwareID, String secret) {
        this.deviceID = deviceID;
        this.hardwareID = hardwareID;
        this.secret = secret;
    }

    public String generateJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serialNumber", deviceID);
        jsonObject.put("hardwareId", hardwareID);
//        jsonObject.put("activationTimeAsString", Long.toString(System.currentTimeMillis()));
//        jsonObject.put("partnerName", "partnerName");
//        jsonObject.put("softwareRevision", "revision");
//        jsonObject.put("identityCert", "Endpoint Identity Cert");
//        jsonObject.put("description", "random descp");
//        jsonObject.put("type", "GATEWAY");
//        jsonObject.put("enabled", true);
//        jsonObject.put("manufacturer", "PrefINC");
//        jsonObject.put("hardwareId", "3513463e2efe-29b0");
//        jsonObject.put("hardwareRevision", "A string representing the hardware revision.");
//        jsonObject.put("directlyConnectedOwnerId", "6b3537ac34fd-3ed5");
//        jsonObject.put("state", "UNKNOWN");
        jsonObject.put("sharedSecret", secret);
//        jsonObject.put("softwareVersion", "v1.0");


        return jsonObject.toString();
//        return "{\"serialNumber\":\"A string (which should be unique across all devices of this modelNumber/manufacturer) uniquely identifying the specific device.\"," +
//                "\"activationTimeAsString\":\"2016-07-22T10:44:57.746Z\"," +
//                "\"partnerName\":\"Partner name\"," +
//                "\"softwareRevision\":\"A string representing the software revision.\"," +
//                "\"identityCert\":\"Endpoint Identity Cert\"," +
//                "\"description\":\"Endpoint description\"," +
//                "\"type\":\"One of [UNKNOWN, GATEWAY, DIRECTLY_CONNECTED_DEVICE, INDIRECTLY_CONNECTED_DEVICE].\"," +
//                "\"enabled\":false," +
//                "\"manufacturer\":\"A string, generally an Organizationally Unique Identifier (OUI), that describes the manufacturer of the device.\"," +
//                "\"hardwareId\":\"3513463e2efe-29b0\"," +
//                "\"hardwareRevision\":\"A string representing the hardware revision.\"," +
//                "\"name\":\"Endpoint name\"," +
//                "\"modelNumber\":\"A string representing the specific model of the device.\"," +
//                "\"directlyConnectedOwnerId\":\"6b3537ac34fd-3ed5\"," +
//                "\"state\":\"Endpoint stateOne of [REGISTERED, ACTIVATED, ENABLED, DISABLED, DECOMMISSIONED, UNKNOWN].\"," +
//                "\"sharedSecret\":\"Endpoint Shared Secret\"," +
//                "\"softwareVersion\":\"A string representing the software version.\"" +
//                "} ";
    }


    public String registerDevice() {
        HttpClient httpclient = HttpClients.createDefault();
        HttpEntity httpEntity = new StringEntity(generateJson(), ContentType.APPLICATION_JSON);

        String encoding = Base64.getEncoder().encodeToString(("stud:TestTestP_01").getBytes());

        HttpPost httpPost = new HttpPost(ENDPOINT_URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Basic " + encoding);


        httpPost.setEntity(httpEntity);
        try {
            HttpResponse httpResponse = httpclient.execute(httpPost);

            String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
            System.out.println(jsonResponse);

            JSONObject json = new JSONObject(jsonResponse);
            return json.getString("id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static KeyPair newKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        /* initialize with keySize: typically 2048 for RSA */
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    public static void main(String[] args) throws Exception {
        String deviceID = UUID.randomUUID().toString();
        String hardwareId = UUID.randomUUID().toString();
        String deviceSecret = "acubvxvkbimj";
        String deviceSecretInBase64 = Base64.getUrlEncoder().encodeToString(deviceSecret.getBytes("UTF-8"));

        KeyPair keyPair = newKeyPair();


        RegisterDevice registerDevice = new RegisterDevice(deviceID, hardwareId, deviceSecretInBase64);
        String id = registerDevice.registerDevice();

        System.out.println("\nDEVICE CREATED!\n");

        AuthenticationService auth = new AuthenticationService(id, hardwareId, deviceSecret, keyPair.getPrivate().getEncoded());
        String activationToken = auth.authenticate(true);

        System.out.println("\nAUTH TOKEN RECEIVED!\n");


        DirectActivationService directActivationService
                = new DirectActivationService(hardwareId,
                deviceSecret, "urn:test:hackapp",
                keyPair.getPrivate(),
                keyPair.getPublic(),
                activationToken);

        directActivationService.execute();

        System.out.println("\nDEVICE ACTIVATED!\n");


        String messageToken = auth.authenticate(false);

        System.out.println("\nMESSAGE TOKEN RECEIVED!\n");

        MessageSendService messageSendService = new MessageSendService(messageToken, id);
        messageSendService.execute();

        System.out.println("\nMESSAGE SENT!\n");


    }
}
