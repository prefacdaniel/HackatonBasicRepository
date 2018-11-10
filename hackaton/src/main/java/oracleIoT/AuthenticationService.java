package oracleIoT;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class AuthenticationService {

	public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/oauth2/token";

	public String authenticate() throws Exception {
		HttpClient httpclient = HttpClients.createDefault();

		JSONObject header = new JSONObject();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		JSONObject payload = new JSONObject();
		payload.put("iss", "87A70FF4-65CE-4914-AA99-5E2EC002A19E-NewRandomDeviceSerialNumber");
		payload.put("exp", 1541893694L);
		payload.put("aud", "oracle/iot/oauth2/token");

		String key = "acubv24kbimsj";

		String headerEncoded = Base64.getUrlEncoder().encodeToString(header.toString().getBytes("UTF-8"));
		String payloadEncoded = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes("UTF-8"));

		String data = headerEncoded + "." + payloadEncoded;

		String signatureEncoded = Base64.getUrlEncoder().encodeToString(encode(key, data));

		System.out.println(signatureEncoded);

		String clientAssertion = headerEncoded + "." + payloadEncoded + "." + signatureEncoded;

		System.out.println(clientAssertion);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
		nvps.add(new BasicNameValuePair("client_assertion_type",
				"urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
		nvps.add(new BasicNameValuePair("client_assertion", clientAssertion));
		nvps.add(new BasicNameValuePair("scope", "oracle/iot/activation"));

		HttpPost httpPost = new HttpPost(URL);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		String token = "";
		try {
			HttpResponse httpResponse = httpclient.execute(httpPost);
			String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
			System.out.println(jsonResponse);
			JSONObject json = new JSONObject(jsonResponse);
			token = json.getString("access_token");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return token;
	}

	public static byte[] encode(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		return sha256_HMAC.doFinal(data.getBytes("UTF-8"));
	}

	public static String signSHA256RSA(String data, byte[] encodedPrivateKey) throws Exception {

//		String realPK = privateKey.replaceAll("-----END PRIVATE KEY-----", "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
//				.replaceAll("\n", "");
//
//		byte[] keyByte = Base64.getDecoder().decode(realPK);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedPrivateKey);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Signature privateSignature = Signature.getInstance("SHA256withRSA");

		privateSignature.initSign(keyFactory.generatePrivate(spec));
		privateSignature.update(data.getBytes("UTF-8"));

		byte[] signature = privateSignature.sign();
		return Base64.getEncoder().encodeToString(signature);

	}
}
