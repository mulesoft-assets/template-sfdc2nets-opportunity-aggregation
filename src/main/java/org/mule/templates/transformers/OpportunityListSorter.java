/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.common.collect.Lists;

/**
 * This transformer will sort a list of map defining a weight for each map base
 * on the value of its keys.
 * 
 * @author damian.sima
 */
public class OpportunityListSorter extends AbstractMessageTransformer {

	/**
	 * Performs the transformation. In this case the sorting required.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		List<Map<String, String>> sortedOpportunityList = Lists.newArrayList((Iterator<Map<String, String>>) message.getPayload());
		Collections.sort(sortedOpportunityList, opportunityMapComparator);

		return sortedOpportunityList.iterator();
	}

	/**
	 * Custom comparator used to sort the Opportunities. Records are sorted in this order: SalesForce only, NetSuite only, records in both systems.
	 * Within the groups, records are sorted based on its names.
	 */
	public static Comparator<Map<String, String>> opportunityMapComparator = new Comparator<Map<String, String>>() {

		/**
		 * Compares the two objects
		 */
		public int compare(Map<String, String> opportunity1, Map<String, String> opportunity2) {
			String key1 = buildKey(opportunity1);
			String key2 = buildKey(opportunity2);
			return key1.compareTo(key2);
		}

		/**
		 * Builds key used to be used in the comparison. By prepending the characters we control the order of records.
		 * Within the group of records, these are ordered naturally by the Opportunity name.
		 * @param opportunity Merged Opportunity object (Map)
		 * @return
		 */
		private String buildKey(Map<String, String> opportunity) {
			StringBuilder key = new StringBuilder();
			//isBlank: whitespace, empty string, null
			String IDInSFDC = opportunity.get("IDInSFDC");
			String IDInNetsuite = opportunity.get("IDInNetsuite");
			
			if (StringUtils.isNotBlank(IDInSFDC) && StringUtils.isBlank(IDInNetsuite)) {
				key.append("~");
				key.append(opportunity.get("Name"));
			}
			
			if (StringUtils.isBlank(IDInSFDC) && StringUtils.isNotBlank(IDInNetsuite)) {
				key.append("~~");
				key.append(opportunity.get("Name"));
			}
			
			if (StringUtils.isNotBlank(IDInSFDC) && StringUtils.isNotBlank(IDInNetsuite)) {
				key.append("~~~");
				key.append(opportunity.get("Name"));
			}

			return key.toString();
		}

	};

}
