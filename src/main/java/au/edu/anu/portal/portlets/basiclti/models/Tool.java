package au.edu.anu.portal.portlets.basiclti.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * An object to store information about a tool.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
@Root(name="tool")
public class Tool {

	@Attribute(name="id")
	private String id;
	
	@Element(name="tool-id")
	private String registrationId;
	
	@Element(name="tool-title")
	private String title;
	
	public Tool() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	
}
