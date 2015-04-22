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
	public List<Map<String, String>> mergeList(List<Map<String, String>> opportunitiesFromSFDC, List<Map<String, String>> opportunitiesFromNetsuite) {
		List<Map<String, String>> mergedOpportunityList = new ArrayList<Map<String, String>>();
		
		// Put all opportunities from SFDC in the merged OpportunityList
		for (Map<String, String> sfdcOpportunity : opportunitiesFromSFDC) {
			Map<String, String> mergedOpportunity = createBlankMergedOpportunity();
			mergedOpportunity.put("Name", sfdcOpportunity.get("Name"));
			mergedOpportunity.put("IDInSFDC", sfdcOpportunity.get("Id"));
			mergedOpportunity.put("AmountInSFDC", sfdcOpportunity.get("Amount"));
			mergedOpportunityList.add(mergedOpportunity);
		}

		// Add the Opportunities from NetSuite and update the existing ones
		for (Map<String, String> netsuiteOpportunity : opportunitiesFromNetsuite) {
			Map<String, String> mergedOpportunity = findOpportunityInList(netsuiteOpportunity, mergedOpportunityList);
					
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
	private Map<String, String> findOpportunityInList(Map<String, String> needle, List<Map<String, String>> haystack) {
		if (!needle.containsKey("title")) {
			return null;
		}
		
		for (Map<String, String> opportunity : haystack) {
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
	private Map<String, String> createBlankMergedOpportunity() {
		Map<String, String> mergedOpportunity = new HashMap<String, String>();
		mergedOpportunity.put("Name", "");
		mergedOpportunity.put("IDInSFDC", "");
		mergedOpportunity.put("AmountInSFDC", "");
		mergedOpportunity.put("IDInNetsuite", "");
		mergedOpportunity.put("AmountInNetsuite", "");
		return mergedOpportunity;
	}

}
