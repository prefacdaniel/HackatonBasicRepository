package ro.ase.ism.ibm;

import java.io.IOException;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MessageService {
	private final static String ENDPOINT_URL = 
			"https://d6spto.messaging.internetofthings.ibmcloud.com:443/api/v0002/device/types/GatewaysIoT/devices/103/events/gicu";
	
	public String generateJson() {
		JSONObject json = new JSONObject();
		json.put("message", "Gicu has arrived!");
		return json.toString();
	}
	
	public void sendMessage() {
		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity httpEntity = new StringEntity(generateJson(), ContentType.APPLICATION_JSON);
		String encoding = Base64.getEncoder().encodeToString(("g/d6spto/GatewaysIoT/103:aIAO@ZoKfyjz_?3FS-").getBytes());

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

}
