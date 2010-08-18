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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Deepak
 */
@Entity(name = "PORTLET_WINDOW_PREF_REGISTRY")
@Table(
    name="PORTLET_WINDOW_PREF_REGISTRY",
    uniqueConstraints=@UniqueConstraint(columnNames={"Name", "UserName"})
)
@NamedQuery( name="findAllPortletWindowPreference", query="SELECT p FROM PORTLET_WINDOW_PREF_REGISTRY p" )
public class PortletWindowPreferenceRegistryModel implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "Name", nullable = false)
	private String name;

	@Column(name = "PortletName", nullable = false)
	private String portletName;

	@Column(name = "UserName", nullable = false)
	private String userName;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
