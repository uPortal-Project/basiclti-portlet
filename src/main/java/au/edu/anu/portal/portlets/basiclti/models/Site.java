package au.edu.anu.portal.portlets.basiclti.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * An object that holds information about a site, retrieved from the web service call
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */

@Root(name="item")
public class Site {

	@Element(name="siteId")
	private String id;
	
	@Element(name="siteTitle")
	private String title;
	

	public Site() {
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
}
