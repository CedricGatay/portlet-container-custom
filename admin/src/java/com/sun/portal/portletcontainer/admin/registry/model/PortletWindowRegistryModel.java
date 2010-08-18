/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.portal.portletcontainer.admin.registry.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 *
 * @author Deepak
 */
@Entity(name = "PORTLET_WINDOW_REGISTRY")
@NamedQuery( name="findAllPortletWindow", query="SELECT p FROM PORTLET_WINDOW_REGISTRY p" )
public class PortletWindowRegistryModel implements Serializable {
	
	@Id
	@Column(name = "RowNumber")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int rowNumber;

	@Column(name = "Name", nullable = false)
	private String name;

	@Column(name = "PortletName", nullable = false)
	private String portletName;

	@Column(name = "Lang")
	private String lang;

	@Column(name = "Remote", nullable = false)
	private String remote;

	@Column(name = "Properties", nullable = false, length = 4000)
	private String properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPortletName() {
		return portletName;
	}

	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getRemote() {
		return remote;
	}

	public void setRemote(String remote) {
		this.remote = remote;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

}
