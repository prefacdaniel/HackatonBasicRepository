package oracleIoT;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class AuthenticationService {

	public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/oauth2/token";
	
	private byte[] privateKey;
	
	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	public String authenticate(boolean isActivationToken) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();

		JSONObject header = new JSONObject();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		JSONObject payload = new JSONObject();
		
		String iss = "87A70FF4-65CE-4914-AA99-5E2EC002A19E";
		if(isActivationToken) {
			iss = "87A70FF4-65CE-4914-AA99-5E2EC002A19E-NewRandomDeviceSerialNumber";
		}
		payload.put("iss", iss);
		payload.put("exp", 1541950801L);
		payload.put("aud", "oracle/iot/oauth2/token");

		String key = "acubv24kbimsj";

		String headerEncoded = Base64.getUrlEncoder().encodeToString(header.toString().getBytes("UTF-8"));
		String payloadEncoded = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes("UTF-8"));

		String data = headerEncoded + "." + payloadEncoded;

		String signatureEncoded = null;
		if(isActivationToken) {
			signatureEncoded = Base64.getUrlEncoder().encodeToString(sha256HMAC(key, data));
		}else {
			signatureEncoded = signSHA256RSA(data, privateKey);
		}

		System.out.println("Signature encoded: " + signatureEncoded);

		String clientAssertion = headerEncoded + "." + payloadEncoded + "." + signatureEncoded;

		System.out.println(clientAssertion);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
		nvps.add(new BasicNameValuePair("client_assertion_type",
				"urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
		nvps.add(new BasicNameValuePair("client_assertion", clientAssertion));
		
		String scope = "";
		if(isActivationToken) {
			scope = "oracle/iot/activation";
		}
		
		nvps.add(new BasicNameValuePair("scope", scope));

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

	public static byte[] sha256HMAC(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		return sha256_HMAC.doFinal(data.getBytes());
	}

	public static String signSHA256RSA(String data, byte[] encodedPrivateKey) throws Exception {

//		String realPK = privateKey.replaceAll("-----END PRIVATE KEY-----", "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
//				.replaceAll("\n", "");
//
//		byte[] keyByte = Base64.getDecoder().decode(realPK);
//		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedPrivateKey);
//
//		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//		Signature privateSignature = Signature.getInstance("SHA256withRSA");
//
//		privateSignature.initSign(keyFactory.generatePrivate(spec));
//		privateSignature.update(data.getBytes("UTF-8"));
//
//		byte[] signature = privateSignature.sign();
//		return Base64.getEncoder().encodeToString(signature);

		KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
		PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey );
		signature.update(data.getBytes("UTF-8"),0,data.length());
		byte[] rsa_text= signature.sign();


		return Base64.getEncoder().encodeToString(rsa_text);

	}
}
