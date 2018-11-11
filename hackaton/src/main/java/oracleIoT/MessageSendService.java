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
import java.util.UUID;

public class MessageSendService {

    private final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/messages";
    private String token;
    private String deviceID;


    public MessageSendService(String token, String deviceID) {
        this.token = token;
        this.deviceID = deviceID;
    }

    private String generateJson() {
        JSONArray jsonArray = new JSONArray()
                .put(new JSONObject()
                        .put("clientId", UUID.randomUUID().toString())
                        .put("source", deviceID)
                        .put("destination", "")
                        .put("priority", "LOW")
                        .put("reliability", "BEST_EFFORT")
                        .put("eventTime", 1453427902124L)
                        .put("sender", "")
                        .put("type", "DATA")
                        .put("properties", new JSONObject())
                        .put("payload", new JSONObject()
                                .put("format", "urn:test:hackapp:messagedeliverytoapps:format:_608232f1")
                                .put("data", new JSONObject()
                                        .put("msgText", "Messages from Pref!!")
                                )
                        )
                );

        return jsonArray.toString();
    }

    public void execute() throws IOException {

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + token);
        httpPost.setHeader("X-EndpointId", deviceID);

        httpPost.setEntity(new StringEntity(generateJson(), ContentType.APPLICATION_JSON));

        HttpClient httpClient = HttpClients.createDefault();


        HttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));

    }


}
