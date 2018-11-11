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


    public void registerDevice() {
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
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        KeyPair keyPair = newKeyPair();


        RegisterDevice registerDevice = new RegisterDevice(deviceID, hardwareId, deviceSecret);
        registerDevice.registerDevice();

        AuthenticationService auth = new AuthenticationService(deviceID, hardwareId, deviceSecret, keyPair.getPrivate().getEncoded());
        String token = auth.authenticate(true);

        System.out.println(token);


        DirectActivationService directActivationService = new DirectActivationService("87A70FF4-65CE-4914-AA99-5E2EC002A19E-NewRandomDeviceSerialNumber", "acubv24kbimsj", "urn:test:hackapp", keyPair.getPrivate(), keyPair.getPublic(), token);
        directActivationService.execute();

    }
}
