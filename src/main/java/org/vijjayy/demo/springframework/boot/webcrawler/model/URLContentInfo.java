package org.vijjayy.demo.springframework.boot.webcrawler.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Holds the URL, title and child link nodes information
 * 
 * @author Vijjayy
 *
 */
public class URLContentInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2634986020265963134L;

	private String url;

	private String title;

	private Set<String> linkNodes;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<String> getLinkNodes() {
		if (linkNodes == null) {
			linkNodes = new HashSet<>();
		}
		return linkNodes;
	}

	public void setLinkNodes(Set<String> linkNodes) {
		this.linkNodes = linkNodes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("URLContentInfo [url=");
		builder.append(url);
		builder.append(", title=");
		builder.append(title);
		builder.append(", linkNodes=");
		builder.append(linkNodes);
		builder.append("]");
		return builder.toString();
	}
	
}
