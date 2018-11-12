package ro.ase.ism.azure;

import java.io.IOException;

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
	private final static String ENDPOINT_URL = "https://HacktathonIoTHub.azure-devices.net/devices/103/messages/events?api-version=2018-06-30";

	public String generateJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("mesage", "Hello from the hackathon team!!!");
		return jsonObject.toString();
	}

	public void sendMessage() throws Exception {

		HttpEntity httpEntity = new StringEntity(generateJson(), ContentType.APPLICATION_JSON);

		HttpPost httpPost = new HttpPost(ENDPOINT_URL);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Authorization",
				"SharedAccessSignature sr=HacktathonIoTHub.azure-devices.net%2Fdevices%2F103&sig=1h%2FoQhYDS9yqyN9Gat15FlC9Md%2BnfefX9v9ODzbd%2B34%3D&se=1547430297");

		HttpClient httpclient = HttpClients.createDefault();
		httpPost.setEntity(httpEntity);
		try {
			HttpResponse httpResponse = httpclient.execute(httpPost);
			if (httpResponse.getEntity() != null) {
				System.out.println(EntityUtils.toString(httpResponse.getEntity()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		MessageService sendEvent = new MessageService();
		sendEvent.sendMessage();
	}
}
