/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The object of this class will take two lists as input and create a third one that
 * will be the merge of the previous two. The identity of list's element is
 * defined by its Name.
 * 
 * @author damian.sima
 */
public class OpportunitiesMerger {

	/**
	 * The method will merge the Opportunities from two lists creating a new one.
	 * 
	 * @param opportunitiesFromSFDC
	 *            opportunities from Salesforce
	 * @param opportunitiesFromNetsuite
	 *            opportunities from Netsuite
	 * @return a list with the merged content of the to input lists
	 */
	public List<Map<String, Object>> mergeList(List<Map<String, Object>> opportunitiesFromSFDC, List<Map<String, Object>> opportunitiesFromNetsuite) {
		List<Map<String, Object>> mergedOpportunityList = new ArrayList<Map<String, Object>>();
		
		// Put all opportunities from SFDC in the merged OpportunityList
		for (Map<String, Object> sfdcOpportunity : opportunitiesFromSFDC) {
			Map<String, Object> mergedOpportunity = createBlankMergedOpportunity();
			mergedOpportunity.put("Name", sfdcOpportunity.get("Name"));
			mergedOpportunity.put("IDInSFDC", sfdcOpportunity.get("Id"));
			mergedOpportunity.put("AmountInSFDC", sfdcOpportunity.get("Amount"));
			mergedOpportunityList.add(mergedOpportunity);
		}

		// Add the Opportunities from NetSuite and update the existing ones
		for (Map<String, Object> netsuiteOpportunity : opportunitiesFromNetsuite) {
			Map<String, Object> mergedOpportunity = findOpportunityInList(netsuiteOpportunity, mergedOpportunityList);
					
			//create blank Opportunity if not found in merged list
			if (mergedOpportunity == null) {
				mergedOpportunity = createBlankMergedOpportunity();
				mergedOpportunity.put("Name", netsuiteOpportunity.get("title"));
				mergedOpportunityList.add(mergedOpportunity);
			}
			mergedOpportunity.put("IDInNetsuite", netsuiteOpportunity.get("internalId"));
			mergedOpportunity.put("AmountInNetsuite",  netsuiteOpportunity.get("projectedTotal"));		
		}
				
		return mergedOpportunityList;
	}
	
	/**
	 * Finds Opportunity in the list of Opportunity objects (Maps). The needle is always Opportunity from Netsuite system.
	 * Haystack can contain opportunities from both systems. The objects are considered the same if names are equal
	 * @param needle Opportunity to search for in haystack
	 * @param haystack The list of Opportunities (Maps)
	 * @return
	 */
	private Map<String, Object> findOpportunityInList(Map<String, Object> needle, List<Map<String, Object>> haystack) {
		if (!needle.containsKey("title")) {
			return null;
		}
		
		for (Map<String, Object> opportunity : haystack) {
			if (opportunity.get("Name") != null && opportunity.get("Name").equals(needle.get("title"))) {
				return opportunity;
			}
		}
		return null;
	}

	/**
	 * Creates empty merged Opportunity Map.
	 * @return Map object representing the merged data.
	 */
	private Map<String, Object> createBlankMergedOpportunity() {
		Map<String, Object> mergedOpportunity = new HashMap<String, Object>();
		mergedOpportunity.put("Name", "");
		mergedOpportunity.put("IDInSFDC", "");
		mergedOpportunity.put("AmountInSFDC", "");
		mergedOpportunity.put("IDInNetsuite", "");
		mergedOpportunity.put("AmountInNetsuite", "");
		return mergedOpportunity;
	}

}
