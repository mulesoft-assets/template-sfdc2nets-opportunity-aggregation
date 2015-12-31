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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.transformer.TransformerException;

@RunWith(MockitoJUnitRunner.class)
public class OpportunitiesMergerTest {
	
	private static final Logger LOGGER = LogManager.getLogger(OpportunitiesMergerTest.class);
		
	@Mock
	private MuleContext muleContext;

	/**
	 * Tests the merging operation. The two lists should be merged into one and opportunities which represent the same object should be combined.
	 * It compares the expected data with the OpportunitiesMerger output.
	 * @throws TransformerException
	 */
	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, Object>> opportunitiesFromSFDC = createSFDCOpportunityList(0, 1);
		List<Map<String, Object>> opportunitiesFromNetsuite = createNetsuiteOpportunityList(1, 2);

		OpportunitiesMerger merger = new OpportunitiesMerger();
		List<Map<String, Object>> mergedList = (List<Map<String, Object>>) merger.mergeList(opportunitiesFromSFDC, opportunitiesFromNetsuite);

		LOGGER.info("\n" + opportunitiesFromSFDC);
		LOGGER.info("\n" + opportunitiesFromNetsuite);
		LOGGER.info("\n" + mergedList);

		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), mergedList);
	}

	/**
	 * Creates list of Opportunity Map objects as expected product the OpportunitiesTestMerger.
	 * @return List of Opportunity Map objects
	 */
	private List<Map<String, String>> createExpectedList() {

		// only in SFDC
		Map<String, String> sfdcOnly = createEmptyMergedRecord(0);
		sfdcOnly.put("IDInSFDC", "0");
		sfdcOnly.put("Name", "SomeName_0");
		sfdcOnly.put("AmountInSFDC", "500");

		// in both systems
		Map<String, String> both = createEmptyMergedRecord(1);
		both.put("IDInSFDC", "1");
		both.put("Name", "SomeName_1");
		both.put("AmountInSFDC", "500");
		both.put("IDInNetsuite", "1");
		both.put("AmountInNetsuite", "500");
		
		// only in NetSuite
		Map<String, String> netsuiteOnly = createEmptyMergedRecord(2);
		netsuiteOnly.put("IDInNetsuite", "2");
		netsuiteOnly.put("Name", "SomeName_2");
		netsuiteOnly.put("AmountInNetsuite", "500");
		
		List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
		expectedList.add(sfdcOnly);
		expectedList.add(both);
		expectedList.add(netsuiteOnly);

		return expectedList;
	}

	/**
	 * Creates empty merged opportunity Map.
	 * @param sequence Is used in generation of test opportunity name.
	 * @return
	 */
	private Map<String, String> createEmptyMergedRecord(int sequence) {
		Map<String, String> opportunity = new HashMap<String, String>();
		opportunity.put("Name", "SomeName_" + sequence);
		opportunity.put("IDInSFDC", "");
		opportunity.put("AmountInSFDC", "");
		opportunity.put("IDInNetsuite", "");
		opportunity.put("AmountInNetsuite", "");
		return opportunity;
	}

	/**
	 * Creates a list of opportunity maps as if they were outputs from Salesforce connector.
	 * Number of opportunities created and their names and IDs are defined by the input arguments.
	 * @param start ID of first opportunity
	 * @param end ID of last opportunity
	 * @return List of opportunity Map objects
	 */
	private List<Map<String, Object>> createSFDCOpportunityList(int start, int end) {
		List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
		for (int i = start; i <= end; i++) {
			oppList.add(createSFDCOpportunity(i));
		}
		return oppList;
	}

	/**
	 * This will create a new Salesforce opportunity object.
	 * @param sequence Number to be used for both IDs and title generation.
	 * @return Map representing the opportunity as if it is received from Salesforce system.
	 */
	private Map<String, Object> createSFDCOpportunity(int sequence) {
		Map<String, Object> opportunity = new HashMap<String, Object>();

		opportunity.put("Id", new Integer(sequence).toString());
		opportunity.put("Name", "SomeName_" + sequence);
		opportunity.put("Amount", "500");

		return opportunity;
	}
	
	
	/**
	 * Creates a list of opportunity maps as if they were outputs from Netsuite connector.
	 * Number of opportunities created and their names and IDs are defined by the input arguments.
	 * @param start ID of first opportunity
	 * @param end ID of last opportunity
	 * @return List of opportunity Map objects
	 */
	private List<Map<String, Object>> createNetsuiteOpportunityList(int start, int end) {
		List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
		for (int i = start; i <= end; i++) {
			oppList.add(createNetsuiteOpportunity(i));
		}
		return oppList;
	}
	
	/**
	 * This will create a new NetSuite opportunity object.
	 * @param sequence Number to be used for both IDs and title generation.
	 * @return Map representing the opportunity as if it is received from Netsuite system.
	 */
	private Map<String, Object> createNetsuiteOpportunity(int sequence) {
		Map<String, Object> opportunity = new HashMap<String, Object>();

		opportunity.put("internalId", new Integer(sequence).toString()); 
		opportunity.put("title", "SomeName_" + sequence);
		opportunity.put("projectedTotal", "500");

		return opportunity;
	}
}
