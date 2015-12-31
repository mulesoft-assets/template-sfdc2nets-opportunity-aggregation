/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.routing.AggregationContext;
import org.mule.routing.AggregationStrategy;

/**
 * This aggregation strategy will take two lists as input and create a third one that
 * will be the merge of the previous two.
 * 
 */
public class OpportunitiesMergeAggregationStrategy implements AggregationStrategy {	
	
	/**
	 * Implements the aggregation.
	 */
	@Override
	public MuleEvent aggregate(AggregationContext context) throws MuleException {
		List<MuleEvent> muleEventsWithoutException = context.collectEventsWithoutExceptions();
		int muleEventsWithoutExceptionCount = muleEventsWithoutException.size();
		
		// data should be loaded from both sources (SFDC and Netsuite)
		if (muleEventsWithoutExceptionCount != 2) {
			throw new IllegalStateException("Data from at least one source was not obtained correctly.");
		}
		
		// mule event that will be rewritten
		MuleEvent originalEvent = context.getOriginalEvent();
		// message which payload will be rewritten
		MuleMessage message = originalEvent.getMessage();
		
		// events are ordered so the event index corresponds to the index of each route
		List<Map<String, Object>> listSFDC = getList(muleEventsWithoutException, 0);
		List<Map<String, Object>> listNetsuite = getList(muleEventsWithoutException, 1);

		
		OpportunitiesMerger sfdcOpportunityMerger = new OpportunitiesMerger();
		List<Map<String, Object>> mergedOpportunityList = sfdcOpportunityMerger.mergeList(listSFDC, listNetsuite);
		
		message.setPayload(mergedOpportunityList.iterator());
		return new DefaultMuleEvent(message, originalEvent);
	}

	/**
	 * Retrieves list of Opportunity records. The records' values are String objects.
	 * @param events List of MuleEvent objects containing data from both connectors 
	 * @param index Index of MuleEvent to return data from as List
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getList(List<MuleEvent> events, int index) {
		Iterator<Map<String, Object>> iterator = (Iterator<Map<String, Object>>) events.get(index).getMessage().getPayload();
		List<Map<String,Object>> list = new ArrayList<>();
		
		// NetSuite returns some values as not String objects
		while (iterator.hasNext()) {			
			Map<String, Object> originalMap = (Map<String, Object>) iterator.next();
			list.add(originalMap);
		}
		return list;
	}
	
}
