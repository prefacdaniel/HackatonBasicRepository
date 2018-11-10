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
import java.util.Base64;

public class RegisterDevice {


    private final static String ENDPOINT_URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/devices";


    public String generateJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serialNumber", "NewRandomDeviceSerialNumber");
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
//        jsonObject.put("sharedSecret", "mySharedSecret");
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
            System.out.println( EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {
        //RegisterDevice registerDevice = new RegisterDevice();
       // registerDevice.registerDevice();
    	
    	AuthenticationService auth = new AuthenticationService();
    	auth.authenticate();

    }
}
