package au.edu.anu.portal.portlets.basiclti.support;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.signature.OAuthSignatureMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A set of OAuth methods
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class OAuthSupport {

	private static final Log log = LogFactory.getLog(OAuthSupport.class.getName());

	/**
	 * Charset to encode params with 
	 */
	private final static String CHARSET= "UTF-8";
	
	
	/**
	 * Sign a property Map with OAuth.
	 * @param url		the url where the request is to be made
	 * @param props		the map of properties to sign
	 * @param method	the HTTP request method, eg POST
	 * @param key		the oauth_consumer_key
	 * @param secret	the shared secret
	 * @return
	 */
	public static Map<String,String> signProperties(String url, Map<String,String> props, String method, String key, String secret) {
        
        if (key == null || secret == null) {
            log.error("Error in signProperties - key and secret must be specified");
            return null;
        }

        OAuthMessage oam = new OAuthMessage(method, url,props.entrySet());
        OAuthConsumer cons = new OAuthConsumer("about:blank",key, secret, null);
        OAuthAccessor acc = new OAuthAccessor(cons);
        try {
            oam.addRequiredParameters(acc);
            log.info("Base Message String\n"+OAuthSignatureMethod.getBaseString(oam)+"\n");

            List<Map.Entry<String, String>> params = oam.getParameters();
    
            Map<String,String> headers = new HashMap<String,String>();
            for (Map.Entry<String,String> p : params) {
            	//as per the spec, params must be encoded
            	String param = URLEncoder.encode(p.getValue(), CHARSET);
            	String value = p.getValue();
                String encodedValue = value != null ? URLEncoder.encode(value, CHARSET) : "";
            	headers.put(param, encodedValue);
            }
            return headers;
        } catch (OAuthException e) {
            log.error(e.getClass() + ":"+ e.getMessage());
            return null;
        } catch (IOException e) {
            log.error(e.getClass() + ":"+ e.getMessage());
            return null;
        } catch (URISyntaxException e) {
            log.error(e.getClass() + ":"+ e.getMessage());
            return null;
        }
    
    }
	
}
