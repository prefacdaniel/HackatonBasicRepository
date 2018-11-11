package ro.ase.ism.ibm;

import java.io.IOException;
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

public class AuthenticationService  {

	public static final String URL = "https://secitc5iotjls-secitc.gbcom-south-1.oraclecloud.com/iot/api/v2/oauth2/token";
	
	public void authenticate() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        
       JSONObject header = new JSONObject();
       header.put("alg", "HS256");
       header.put( "typ", "JWT");
       
       JSONObject payload = new JSONObject();
       payload.put("iss", "87A70FF4-65CE-4914-AA99-5E2EC002A19E-NewRandomDeviceSerialNumber");
       payload.put("exp", 1541886038L);
       payload.put("aud", "oracle/iot/oauth2/token");
       
       String key = "acubv24kbimsj";
       
       String headerEncoded = Base64.getUrlEncoder().encodeToString(header.toString().getBytes());
       String payloadEncoded = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes());
       
       String data = headerEncoded + "." + payloadEncoded;
        
       String signature = encode(key, data);
       
       String signatureEncoded = Base64.getUrlEncoder().encodeToString(signature.toString().getBytes());
       
       String clientAssertion = headerEncoded + "." + payloadEncoded + "."+ signatureEncoded;
      
       	System.out.println(clientAssertion);
        
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        nvps.add(new BasicNameValuePair("client_assertion", clientAssertion));
        nvps.add(new BasicNameValuePair("scope", "oracle/iot/activation"));
        
 
        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

 
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        
        try {
            HttpResponse httpResponse = httpclient.execute(httpPost);
            System.out.println( EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
	
	public static String encode(String key, String data) throws Exception {
		  Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		  SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		  sha256_HMAC.init(secret_key);

		  return new String(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
		}
}
