package au.edu.anu.portal.portlets.basiclti.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A convenience object to hold a list of pages, in turn holding a list of tools,
 * retrieved from the web service call, when the XML is parsed.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */

@Root(name="site")
public class SitePageList {

	@Attribute(name="id")
	private String id;
	
	@Element(name="pages")
	private PageList pageList;
	
	public SitePageList() {
	}
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PageList getPageList() {
		return pageList;
	}

	public void setPageList(PageList pageList) {
		this.pageList = pageList;
	}

	
}

