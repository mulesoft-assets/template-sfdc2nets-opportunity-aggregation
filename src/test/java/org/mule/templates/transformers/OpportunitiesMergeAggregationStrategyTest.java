/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.routing.AggregationContext;
import org.mule.api.transformer.TransformerException;
import org.mule.templates.integration.AbstractTemplateTestCase;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class OpportunitiesMergeAggregationStrategyTest extends AbstractTemplateTestCase {

	@Mock
	private MuleContext muleContext;
	
	/**
	 * Tests the aggregation strategy. The two lists should be merged into one and opportunities which represent the same object should be combined.
	 * It compares the expected data with the aggregation output.
	 * @throws TransformerException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAggregate() throws Exception {
		List<Map<String, String>> opportunitiesSFDC = createSFDCOpportunityList(0, 1);
		List<Map<String, String>> opportunitiesNetsuite = createNetsuiteOpportunityList(1, 2);
		
		MuleEvent testOriginalEvent = getTestEvent("");
		MuleEvent testEventSFDC = getTestEvent("");
		MuleEvent testEventNetsuite = getTestEvent("");
		
		testEventSFDC.getMessage().setPayload(opportunitiesSFDC.iterator());
		testEventNetsuite.getMessage().setPayload(opportunitiesNetsuite.iterator());
		
		List<MuleEvent> testEvents = new ArrayList<MuleEvent>();
		testEvents.add(testEventSFDC);
		testEvents.add(testEventNetsuite);
		
		AggregationContext aggregationContext = new AggregationContext(testOriginalEvent, testEvents);
		
		OpportunitiesMergeAggregationStrategy sfdcOpportunityMerge = new OpportunitiesMergeAggregationStrategy();
		List<Map<String, String>> mergedList = Lists.newArrayList((Iterator<Map<String, String>>) sfdcOpportunityMerge.aggregate(aggregationContext).getMessage().getPayload());

		System.out.println(mergedList);
		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), mergedList);

	}

	/**
	 * Creates list of Opportunity Map objects as expected product the aggregation.
	 * @return List of Opportunity Map objects
	 */
	private List<Map<String, String>> createExpectedList() {

		Map<String, String> sfdcOnly = createEmptyMergedRecord(0);
		sfdcOnly.put("IDInSFDC", "0");
		sfdcOnly.put("Name", "SomeName_0");
		sfdcOnly.put("AmountInSFDC", "500");

		Map<String, String> both = createEmptyMergedRecord(1);
		both.put("IDInSFDC", "1");
		both.put("Name", "SomeName_1");
		both.put("AmountInSFDC", "500");
		both.put("IDInNetsuite", "1");
		both.put("AmountInNetsuite", "500");
		both.put("extIDInNetsuite","1");

		Map<String, String> netsuiteOnly = createEmptyMergedRecord(2);
		netsuiteOnly.put("IDInNetsuite", "2");
		netsuiteOnly.put("Name", "SomeName_2");
		netsuiteOnly.put("AmountInNetsuite", "500");
		netsuiteOnly.put("extIDInNetsuite","2");

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
	private Map<String, String> createEmptyMergedRecord(Integer secuense) {
		Map<String, String> opportunity = new HashMap<String, String>();
		opportunity.put("Name", "SomeName_" + secuense);
		opportunity.put("IDInSFDC", "");
		opportunity.put("AmountInSFDC", "");
		opportunity.put("IDInNetsuite", "");
		opportunity.put("AmountInNetsuite", "");
		opportunity.put("extIDInNetsuite", "");
		return opportunity;

	}
	
	/**
	 * Creates a list of opportunity maps as if they were outputs from Salesforce connector.
	 * Number of opportunities created and their names and IDs are defined by the input arguments.
	 * @param start ID of first opportunity
	 * @param end ID of last opportunity
	 * @return List of opportunity Map objects
	 */
	private List<Map<String, String>> createSFDCOpportunityList(int start, int end) {
		List<Map<String, String>> oppList = new ArrayList<Map<String, String>>();
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
	private Map<String, String> createSFDCOpportunity(int sequence) {
		Map<String, String> opportunity = new HashMap<String, String>();

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
	private List<Map<String, String>> createNetsuiteOpportunityList(int start, int end) {
		List<Map<String, String>> oppList = new ArrayList<Map<String, String>>();
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
	private Map<String, String> createNetsuiteOpportunity(int sequence) {
		Map<String, String> opportunity = new HashMap<String, String>();

		opportunity.put("internalId", new Integer(sequence).toString()); // irrelevant here - can be the same as externalId
		opportunity.put("title", "SomeName_" + sequence);
		opportunity.put("projectedTotal", "500");
		opportunity.put("externalId", new Integer(sequence).toString());

		return opportunity;
	}
}
