package ro.ase.ism.ibm;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class RegisterDevice {

	private final static String ENDPOINT_URL = 
			"https://d6spto.internetofthings.ibmcloud.com/api/v0002/device/types/HackathonDevices/devices";

	public String generateJson() {
		JSONObject deviceInfoJSON = new JSONObject();
		deviceInfoJSON.put("serialNumber", "1234500002");
		deviceInfoJSON.put("manufacturer", "Students & Co");
		deviceInfoJSON.put("model", "XXL");
		deviceInfoJSON.put("deviceClass", "Inferior");
		deviceInfoJSON.put("description", "Prototype");
		deviceInfoJSON.put("fwVersion", "1.5");
		deviceInfoJSON.put("hwVersion", "1.0");
		deviceInfoJSON.put("descriptiveLocation", "Bucharest, Pantelimon");

		LocalDateTime dateTime = LocalDateTime.now();
		String measuredDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

		JSONObject locationJSON = new JSONObject();
		locationJSON.put("longitude", "0");
		locationJSON.put("latitude", "0");
		locationJSON.put("elevation", "0");
		locationJSON.put("accuracy", "0");
		locationJSON.put("measuredDateTime", measuredDateTime);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("deviceId", "102");
		jsonObject.put("deviceInfo", deviceInfoJSON);
		jsonObject.put("location", locationJSON);
		jsonObject.put("metadata", new JSONObject());
		jsonObject.put("authToken", "aIAO@ZoKfyjz_?3FS-");


		return jsonObject.toString();
	}

	public void registerDevice() {
		HttpClient httpclient = HttpClients.createDefault();
		HttpEntity httpEntity = new StringEntity(generateJson(), ContentType.APPLICATION_JSON);
		String encoding = Base64.getEncoder().encodeToString(("a-d6spto-srhegxocel:aIAO@ZoKfyjz_?3FS-").getBytes());

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

	public static void main(String[] args) throws Exception {
		//RegisterDevice registerDevice = new RegisterDevice();
		//registerDevice.registerDevice();
		
		MessageService messageService = new MessageService();
		messageService.sendMessage();

	}
}
