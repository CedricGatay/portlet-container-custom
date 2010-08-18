/*
 * CDDL HEADER START
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html and legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package com.sun.portal.portletcontainer.appengine.filter;

import com.sun.portal.container.ContainerLogger;
import com.sun.portal.portletcontainer.common.PortletActions;
import com.sun.portal.portletcontainer.common.descriptor.FilterDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.FilterMappingDescriptor;
import com.sun.portal.portletcontainer.common.descriptor.PortletAppDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.PortletFilter;

/**
 * Filter chain factory class for portlet web app
 * It is responsible for creating FilterChain objects.
 */
public class FilterChainFactory {
        
    //Map containg filter objects. Key is filter name
    private Map<String, Object> filterObjectMap = null;
    
    //Map containg filter lifecycle phases. Key is filter name
    private Map<String, Set<String>> filterNameAndLifecycleMap =  null;
    
    //Map containg PortletFilterMapInfo objects. Key is portlet name
    //A PortletFilterMapInfo object will contain reference to filter objects
    //which are applibale to a particular portlet.
    private Map<String, PortletFilterMapInfo> portletFilterMapInfoMap = 
            new HashMap<String, PortletFilterMapInfo>();
            
    private static Logger logger = ContainerLogger.getLogger(FilterChainFactory.class, "PAELogMessages");
    
    
    /*
     * constructor will create, initialize and store all valid Filters.
     * It also reads filter maping to decide which filter is application to 
     * what portlets and creates PortletFilterMapInfo objects for each portlet.
     * 
     * @param portletAppDescriptor data from portlet.xml
     * @param context PortletContext associated with potlet webapp.
     */
    public  FilterChainFactory(PortletAppDescriptor portletAppDescriptor, PortletContext context) {
        //Check whether filter and filter mapping both are specified.
        //A portlet.xml can have no entry for filters.
        if(portletAppDescriptor.getFilterDescriptors()!=null && 
                portletAppDescriptor.getFilterMappingDescriptors()!=null){
            //create, initialize and store filters
            createFilters(portletAppDescriptor, context);
            //Now create PortletFilterMapInfo objects for various portlets
            //This will help in getting list of applicable filters for a 
            //particular portlet for a particular lifecycle_phase.
            populatePortletFilterMapInfoMap(portletAppDescriptor);
        }
    }
    
    
    /**
     * Method which iterate over declared <filter> tags and create filter objects
     * @param portletAppDescriptor 
     * @param context PortletContext
     */
    private void createFilters(PortletAppDescriptor portletAppDescriptor, PortletContext context){
        //Obtain list of declared filters
        List<FilterDescriptor> filterDescriptorList = portletAppDescriptor.getFilterDescriptors();
        //Following map will contain the filter name vs lifecycle phase for convenience
        filterNameAndLifecycleMap = new HashMap<String, Set<String>>(filterDescriptorList.size());
        //Following map will contain the filter name vs filter object        
        filterObjectMap = new HashMap<String, Object>(filterDescriptorList.size());
        //iterate over filter descriptor list
        for(FilterDescriptor filterDescriptor : filterDescriptorList){
            //create filter object
            Object filter = createFilter(filterDescriptor, context);
            //if filter object is not null, store filter object
            if(filter!=null){
                filterObjectMap.put(filterDescriptor.getFilterName(), filter);
                filterNameAndLifecycleMap.put(filterDescriptor.getFilterName(), filterDescriptor.getFilterLifecycles());
            }
        }//end of for
    }
    

    
    /**
     * Returns a list of portlet names matching with the specified wildchar string 
     * @param portletAppDescriptor 
     * @param portletNameWithWildCharacter 
     * @return 
     */
    private List<String> getMatchingPortletNames(PortletAppDescriptor portletAppDescriptor, String portletNameWithWildCharacter){
        if(portletNameWithWildCharacter.equals("*")){
            //filter is available for all portlets
            return portletAppDescriptor.getPortletsDescriptor().getPortletNames();
        }else{
            //Filter is available for selected portlets (but not all portlets)
            List<String> matchingPortletList = new ArrayList<String>();
            //String after removing * character
            String portletNameStartsWith = portletNameWithWildCharacter.substring(0, portletNameWithWildCharacter.length()-1);
            //iterate over all portlet names to get the matching portlets
            for(String portletName : portletAppDescriptor.getPortletsDescriptor().getPortletNames()){
                if(portletName.startsWith(portletNameStartsWith)){
                    matchingPortletList.add(portletName);
                }
            }
            
            return matchingPortletList;
        }
    }
    
    /**
     * Creates a filter object using the filter class name defined in the
     * deployment descriptor.
     * This method can return null if :
     * Filter class name is missing
     * Filter class doesn't exists
     * Filter class is not implementing correct interface
     * lifecycle method declaration is not correct
     * @param filterDescriptor
     * @throws com.sun.portal.portletcontainer.portletappengine.impl.LifecycleManagerException
     * @return
     */
    private Object createFilter( FilterDescriptor filterDescriptor, PortletContext context ){
        
        String filterName = filterDescriptor.getFilterName();
        String filterClassName = filterDescriptor.getFilterClass();
        Set lifecycleMethods = filterDescriptor.getFilterLifecycles();
        
        try {
            
            //validate filter-class property
            if (filterClassName == null || filterClassName.equals("")) {
                //warning message... filter class-name is missing for filter: {0}
                logger.log(Level.WARNING, "PSPL_PAECSPPAF0001", filterName);
                return null;
            }
            
            //Validate lifecycle property
            //First remove invalid lifecycle enteries
            validateFilterLifecycleNames(lifecycleMethods);
            //Now check whether there is any entry in the lifecycle Set
            if((lifecycleMethods==null)|| lifecycleMethods.isEmpty()){
                //warning message... lifecycle method is either missing or is not proper for filter: {0}
                //Change above message to -> No valid lifecycle phase specified for portlet.
                logger.log(Level.WARNING, "PSPL_PAECSPPAF0002", filterName);
                return null;
            }else{
                
            }
            
            //information that filter object is going to be created
            logger.log(Level.FINEST, "PSPL_PAECSPPAF0003", filterName);
            
            
            Class filterClass = Thread.currentThread().getContextClassLoader().loadClass(filterClassName);
            Object filter = (filterClass.newInstance());
            
            if(filter instanceof PortletFilter){
                ((PortletFilter)filter).init(new FilterConfigImpl(context, filterDescriptor));
            } else {
                logger.log(Level.SEVERE, "PSPL_PAECSPPAF0004", filterName);                           
            }

            logger.log(Level.INFO, "PSPL_PAECSPPAF0005", filterName);                

            return filter;
            
            
        } catch (Exception e) {
			//TODO - If the exception is of type UnavailableException,
			// the container may examine the isPermanent attribute of the
			// exception and may choose to retry the filter at some later time.
            if(logger.isLoggable(Level.SEVERE)){
                LogRecord record = new LogRecord(Level.SEVERE,"PSPL_PAECSPPAF0006");
                record.setThrown(e);
                record.setParameters(new String[] {filterName});
                logger.log(record);
            }            
        }
        
        return null;
        
    }
    
    /**
     * Returns an implementation of FilterChain interface based on portlet
     * and lifecycle method. Return null if no filter is available for the
     * specified lifecycle phase. 
     * @param portletName
     * @param p javax.portlet.portlet
     * @param lifecycleMethod
     * @return
     */
    public FilterChain getPortletFilterChain(String portletName, Portlet p, String lifecyclePhase){
        //Obtain filter list and see if that is not null.  
        List applicableFilters = getApplicableFiltersForPortlet(portletName, lifecyclePhase);
        if(applicableFilters!=null){
            return new FilterChainImpl(p, applicableFilters);
        }
        return null;
    }
        
    
    /**
     * This method iterate over FilterMappingDescriptor list and populates 
     * various PortletFilterMapInfo objects.  
     * This will help in finding the list of filters for each portlet for a 
     * give lifecycle.
     *
     * @param portletAppDescriptor 
     */
    private void populatePortletFilterMapInfoMap(PortletAppDescriptor portletAppDescriptor){
        //List of filter mapping descriptors
        List<FilterMappingDescriptor> filterMappingDescriptorList = 
                portletAppDescriptor.getFilterMappingDescriptors();        

        //iterate over filter mapping descriptors
        for(FilterMappingDescriptor filterMappingDescriptor : filterMappingDescriptorList){
            String filterName = filterMappingDescriptor.getFilterName();
            Set portletNames = filterMappingDescriptor.getPortletNames();
            //First check the filter existence as filter mapping be pointing to any non existing filter
            //Also, check portlet names collection
            if(filterObjectMap.containsKey(filterName) && (portletNames!=null)){
                String portletName;
                for(Iterator<String> iter = portletNames.iterator(); iter.hasNext();){
                    portletName = iter.next();
                    //Check whether portlet name has wild character
                    if(portletName.endsWith("*")){
                        addFilterToPortlets(filterName, getMatchingPortletNames(portletAppDescriptor, portletName), filterObjectMap.get(filterName));
                    }else{
                        addFilterToPortlet(filterName, portletName, filterObjectMap.get(filterName));
                    }
                }
            }else{
                logger.log(Level.WARNING, "PSPL_PAECSPPAF0008", filterName);
            }
        }
    }    
    

    /**
     * Returns a list if applicable filters for a given portlet and lifecycle
     * @param portletName for which filter list is required
     * @param lifecycleMethod for which filter list is required
     * @return java.util.List List of filter objects
     */
    
    private List<Object> getApplicableFiltersForPortlet(String portletName, String lifecyclePhase){
        if(portletFilterMapInfoMap.containsKey(portletName)){
            return portletFilterMapInfoMap.get(portletName).getApplicableFilters(lifecyclePhase);
        }
        return null;
    }
    
    
    /**
     * Add filter object to all applicable portlets
     * @param lifecycleFilterMap
     * @param portletList
     * @param filter
     */
    private void addFilterToPortlets(String filterName, List<String> portletList, Object filter){
        for(String portletName : portletList){
            addFilterToPortlet(filterName, portletName, filter);
        }
    }
    
    
    /**
     * Method to store filter object in the corresponding lifecycle Filter map
     * @param lifecycleFilterMap
     * @param portletName
     * @param filter
     */
    private void addFilterToPortlet(String filterName, String portletName, Object filter){
        if(portletFilterMapInfoMap.containsKey(portletName)){
            //If PortletFilterMapInfo exists, add new filter
            portletFilterMapInfoMap.get(portletName).addFilter(filter, filterNameAndLifecycleMap.get(filterName));
        }else{
            //create a new PortletFilterMapInfo to store Filter information
            PortletFilterMapInfo filterMapInfo = new PortletFilterMapInfo(portletName);
            filterMapInfo.addFilter(filter, filterNameAndLifecycleMap.get(filterName));
            portletFilterMapInfoMap.put(portletName, filterMapInfo);
        }
    }
    
    /**
     * To call destroy method on all the portets
     */
    public void clear(){
        if(filterObjectMap!=null){
            for(Object filter : filterObjectMap.values()){
                ((PortletFilter)filter).destroy();
            }//end of for
        }
        filterObjectMap = null;
    }
    
    private void validateFilterLifecycleNames(Set lifecycleMethods){
        if(lifecycleMethods!=null){
            for(Iterator<String> iter=lifecycleMethods.iterator(); iter.hasNext(); ){
                if(!PortletActions.isValidLifecyclePhase(iter.next())){
                    iter.remove();
                }
            }
        }
    }
}



