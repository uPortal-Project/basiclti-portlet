package au.edu.anu.portal.portlets.basiclti.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * HTTP support class. Takes care of HTTP related methods
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class HttpSupport {

	
	/**
	 * Make a POST request with the given Map of parameters to be encoded
	 * @param address	address to POST to
	 * @param params	Map of params to use as the form parameters
	 * @return
	 */
	public static String doPost(String address, Map<String,String> params) {
		
		HttpClient httpclient = new DefaultHttpClient();
		
		try {
			
			HttpPost httppost = new HttpPost(address);
			
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	        
	        for (Map.Entry<String,String> entry : params.entrySet()) {
	        	formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, HTTP.UTF_8);
	        	        
	        httppost.setEntity(entity);
			
			HttpResponse response = httpclient.execute(httppost);
			String responseContent = EntityUtils.toString(response.getEntity());
			
			return responseContent;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		return null;
	}
	
	/**
	 * Serialise the given Map of parameters to a URL query string
	 * @param params Map of params
	 * @return
	 */
	public static String serialiseMapToQueryString(Map<String,String> params) {
		
		StringBuilder s = new StringBuilder();
		
		//iterate so we can check if we have more values in the map
		for (Iterator<Map.Entry<String,String>> it = params.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
			s.append(entry.getKey());
			s.append("=");
			s.append(entry.getValue()); //need to encode this one?
				
			if(it.hasNext()){
				s.append("&");
			}
		}
		
		return s.toString();
	}
	
	/**
	 * Deserialise the result of request.getParameterMap to convert a query string back into a single valued map.
	 * 
	 * <p> Note, only the first value is kept.
	 *
	 * @param map Map<String,String[]> map from request.getParameterMap
	 * @return
	 * 
	 */
	public static Map<String,String> deserialiseParameterMap(Map<String,String[]> params) {
		
		Map<String,String> map = new HashMap<String,String>();
		
		for (Map.Entry<String,String[]> entry : params.entrySet()) {
			map.put(entry.getKey(), entry.getValue()[0]);
			System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue()[0]);
		}
		
		return map;
	}
	
	
	
	/**
	 * Generate a HTML form that will automatically submit itself, based on the parameters supplied.
	 * This is so the client does the POST and gets the session cookie back from the endpoint.
	 * @param endpoint
	 * @param params
	 * @return
	 */
	public static String postLaunchHtml(String address, Map<String,String> params) {
        
		String BASICLTI_SUBMIT = "basiclti_submit";
		StringBuilder text = new StringBuilder();
        
		text.append("<div id=\"ltiLaunchFormSubmitArea\">\n");
        text.append("<form action=\""+address+"\" name=\"ltiLaunchForm\" id=\"ltiLaunchForm\" method=\"post\" encType=\"application/x-www-form-urlencoded\">\n" );
        for (Map.Entry<String,String> entry : params.entrySet()) {
        	String key = StringEscapeUtils.escapeHtml(entry.getKey());
        	String value = StringEscapeUtils.escapeHtml(entry.getValue());
	
        	if ( key.equals(BASICLTI_SUBMIT) ) {
                 text.append("<input type=\"submit\" name=\"");
               } else { 
                 text.append("<input type=\"hidden\" name=\"");
               }
        	
            text.append(key);
            text.append("\" value=\"");
            text.append(value);
            text.append("\"/>\n");
        }
        text.append("</form>\n" + "</div>\n");
        
        text.append(
                " <script language=\"javascript\"> \n" +
                "    document.getElementById(\"ltiLaunchFormSubmitArea\").style.display = \"none\";\n" + 
                "    nei = document.createElement('input');\n" +
                "    nei.setAttribute('type', 'hidden');\n" + 
                "    nei.setAttribute('name', '"+BASICLTI_SUBMIT+"');\n" + 
                "    nei.setAttribute('value', '"+params.get(BASICLTI_SUBMIT)+"');\n" + 
                "    document.getElementById(\"ltiLaunchForm\").appendChild(nei);\n" +
                "    document.ltiLaunchForm.submit(); \n" + 
                " </script> \n");
                
        return text.toString();
    }
	
	
	/*
	 * redo this using OAuth, not signpost.
	public static String doSignedPost(String address, Map<String,String> params, String key, String secret) {
		try {
			
			System.out.println("secret: " + secret);
			System.out.println("key: " + key);

			
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer(key,secret);
	        consumer.setTokenWithSecret(key, secret);

	        System.out.println("Request token: " + consumer.getToken());
	        System.out.println("Token secret: " + consumer.getTokenSecret());
	        
	        
	        HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(address);
			
	        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	        
	        for (Map.Entry<String,String> entry : params.entrySet()) {
	        	formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(entity);
			
			// sign the request
	        consumer.sign(httppost);
			
			HttpResponse response = httpclient.execute(httppost);
			String responseContent = Streams.asString(response.getEntity().getContent());
			
			return responseContent;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	*/
}
