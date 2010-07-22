package au.edu.anu.portal.portlets.basiclti.models;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * An object to store information about a page in a site, and the List of tools contained within it.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
@Root(name="page")
public class Page {

	@Attribute(name="id")
	private String id;
	
	@Element(name="page-title")
	private String title;
	
	@ElementList(name="tools")
	private List<Tool> tools;
	
	public Page() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}
}
