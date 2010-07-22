package au.edu.anu.portal.portlets.basiclti.models;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * A convenience object to hold a list of pages retrieved from the web service call, when the XML is parsed.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
@Root(name="pages")
public class PageList {

	@ElementList(name="page",inline=true)
	private List<Page> pages;
	
	public PageList() {
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public List<Page> getPages() {
		return pages;
	}
	
	
	
}
