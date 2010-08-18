/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.portal.portletcontainer.admin.registry.model;

import com.sun.portal.portletcontainer.admin.PortletRegistryElement;
import com.sun.portal.portletcontainer.admin.PropertiesContext;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Deepak
 */
public class PortletDataPersistenceHelper {

    private static EntityManagerFactory emf;

	public static List<PortletAppRegistryModel> getPortletAppRegistryModels() {

		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("findAllPortletApp");
		List<PortletAppRegistryModel> list = query.getResultList();
		if(list == null) {
			list = Collections.emptyList();
		}
		em.close();
		return list;
	}

	public static void removePortletAppRegistryModel(String name) {

		EntityManager em = getEntityManager();
		PortletAppRegistryModel model = getPortletAppRegistryModel(em, name);
		if(model != null) {
			EntityTransaction userTransaction = em.getTransaction();
			userTransaction.begin();
			em.remove(model);
			userTransaction.commit();
		}
		em.close();
	}

	public static void updatePortletAppRegistryModel(PortletRegistryElement portletApp) {

		EntityManager em = getEntityManager();
		EntityTransaction userTransaction = em.getTransaction();
		userTransaction.begin();
		PortletAppRegistryModel model = getPortletAppRegistryModel(em, portletApp.getName());
		if(model == null) {
			model = new PortletAppRegistryModel();
			populatePortletAppRegistryModel(portletApp, model);
			em.persist(model);
		} else {
			populatePortletAppRegistryModel(portletApp, model);
		}
		userTransaction.commit();
		em.close();
	}

	private static PortletAppRegistryModel getPortletAppRegistryModel(EntityManager em, String name) {

		PortletAppRegistryModel model = em.find(PortletAppRegistryModel.class, name);
		return model;
	}

	private static void populatePortletAppRegistryModel(
		PortletRegistryElement portletApp, PortletAppRegistryModel model) {

		model.setName(portletApp.getName());
		model.setPortletName(portletApp.getPortletName());
		model.setProperties(portletApp.getProperties());
	}

	public static List<PortletWindowRegistryModel> getPortletWindowRegistryModels() {

		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("findAllPortletWindow");
		List<PortletWindowRegistryModel> list = query.getResultList();
		if(list == null) {
			list = Collections.emptyList();
		}
		em.close();
		return list;
	}

	public static void updatePortletWindowRegistryModel(PortletRegistryElement portletWindow) {

		EntityManager em = getEntityManager();
		EntityTransaction userTransaction = em.getTransaction();
		userTransaction.begin();
		PortletWindowRegistryModel model = getPortletWindowRegistryModel(em, portletWindow.getName());
		if(model == null) {
			model = new PortletWindowRegistryModel();
			populatePortletWindowRegistryModel(portletWindow, model);
			em.persist(model);
		} else {
			populatePortletWindowRegistryModel(portletWindow, model);
		}
		userTransaction.commit();
		em.close();
	}

	public static void removePortletWindowRegistryModel(String name) {

		EntityManager em = getEntityManager();
		PortletWindowRegistryModel model = getPortletWindowRegistryModel(em, name);
		if(model != null) {
			EntityTransaction userTransaction = em.getTransaction();
			userTransaction.begin();
			em.remove(model);
			userTransaction.commit();
		}
		em.close();
	}

	private static PortletWindowRegistryModel getPortletWindowRegistryModel(EntityManager em, String name) {

		Query query = em.createQuery("SELECT p FROM PORTLET_WINDOW_REGISTRY p WHERE p.name = :name");
		query.setParameter("name", name);
		PortletWindowRegistryModel model = null;
		try {
			model = (PortletWindowRegistryModel)query.getSingleResult();
		} catch (NoResultException nre) {
		}
		return model;
	}

	private static void populatePortletWindowRegistryModel(
		PortletRegistryElement portletWindow, PortletWindowRegistryModel model) {

		model.setName(portletWindow.getName());
		model.setPortletName(portletWindow.getPortletName());
		model.setLang(portletWindow.getLang());
		model.setRemote(portletWindow.getRemote());
		model.setProperties(portletWindow.getProperties());
	}

	public static List<PortletWindowPreferenceRegistryModel> getPortletWindowPreferenceRegistryModels() {

		EntityManager em = getEntityManager();
		Query query = em.createNamedQuery("findAllPortletWindowPreference");
		List<PortletWindowPreferenceRegistryModel> list = query.getResultList();
		if(list == null) {
			list = Collections.emptyList();
		}
		em.close();
		return list;
	}

	public static void updatePortletWindowPreferenceRegistryModel(PortletRegistryElement portletWindowPreference) {

		EntityManager em = getEntityManager();
		EntityTransaction userTransaction = em.getTransaction();
		userTransaction.begin();
		List<PortletWindowPreferenceRegistryModel> models =
			getPortletWindowPreferenceRegistryModel(em, portletWindowPreference.getName(), portletWindowPreference.getUserName());
		if(models == null || models.isEmpty()) {
			PortletWindowPreferenceRegistryModel model = new PortletWindowPreferenceRegistryModel();
			populatePortletWindowPreferenceRegistryModel(portletWindowPreference, model);
			em.persist(model);
		} else {
			populatePortletWindowPreferenceRegistryModel(portletWindowPreference, models.get(0));
		}
		userTransaction.commit();
		em.close();
	}

	public static void removePortletWindowPreferenceRegistryModel(String name) {

		EntityManager em = getEntityManager();
		List<PortletWindowPreferenceRegistryModel> models =
			getPortletWindowPreferenceRegistryModel(em, name, null);
		if(models != null && !models.isEmpty()) {
			EntityTransaction userTransaction = em.getTransaction();
			userTransaction.begin();
			for(PortletWindowPreferenceRegistryModel model : models) {
				em.remove(model);
			}
			userTransaction.commit();
		}
		em.close();
	}

	private static List<PortletWindowPreferenceRegistryModel>
		getPortletWindowPreferenceRegistryModel(EntityManager em, String name, String username) {

		String queryString = "SELECT p FROM PORTLET_WINDOW_PREF_REGISTRY p WHERE p.name = :name";
		Query query = null;
		if(username == null) {
			query = em.createQuery(queryString);
		} else {
			query = em.createQuery(queryString + " AND p.userName = :username");
			query.setParameter("username", username);
		}
		query.setParameter("name", name);
		return query.getResultList();
	}

	private static void populatePortletWindowPreferenceRegistryModel(
		PortletRegistryElement portletWindowPreference, PortletWindowPreferenceRegistryModel model) {

		model.setName(portletWindowPreference.getName());
		model.setPortletName(portletWindowPreference.getPortletName());
		model.setUserName(portletWindowPreference.getUserName());
		model.setProperties(portletWindowPreference.getProperties());
	}

	private static EntityManager getEntityManager() {

		if(emf == null) {
			synchronized(PortletDataPersistenceHelper.class) {
				if(emf == null)
					emf = Persistence.createEntityManagerFactory(
							"PortletContainer", PropertiesContext.getDatabaseProperties());
			}
		}

		if(emf == null) {
			return null;
		}

        return emf.createEntityManager();
	}

	public static void releaseEntityManagerFactory() {

		if(emf == null) {
			return;
		}

		if(emf.isOpen()) {
			emf.close();
			emf = null;
		}
	}
}
