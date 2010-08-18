/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.portal.portletcontainer.admin.registry.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 *
 * @author Deepak
 */
@Entity(name = "PORTLET_APP_REGISTRY")
@NamedQuery( name="findAllPortletApp", query="SELECT p FROM PORTLET_APP_REGISTRY p" )
public class PortletAppRegistryModel implements Serializable {
	@Id
	@Column(name = "Name", nullable = false)
	private String name;

	@Column(name = "PortletName", nullable = false)
	private String portletName;

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

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

}
