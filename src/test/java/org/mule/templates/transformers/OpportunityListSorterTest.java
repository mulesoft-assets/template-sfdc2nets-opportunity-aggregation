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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

import com.google.common.collect.Lists;

/**
 * The test validates that the {@link OpportunityListSorter} properly order a
 * list of maps based on its internal criteria.
 * 
 * @author damiansima
 */
@RunWith(MockitoJUnitRunner.class)
public class OpportunityListSorterTest {

	@Mock
	private MuleContext muleContext;

	/**
	 * Tests functionality of sorting transformer.
	 * Original and expected lists should match.
	 * @throws TransformerException
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testSort() throws TransformerException {
		MuleMessage message = new DefaultMuleMessage(createOriginalList().iterator(), muleContext);

		OpportunityListSorter transformer = new OpportunityListSorter();
		List<Map<String, String>> sortedList = Lists.newArrayList((Iterator<Map<String, String>>) transformer.transform(message, "UTF-8"));

		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), sortedList);

	}

	/**
	 * Creates a list that is expected as the output of sorting transformer processing.
	 * @return
	 */
	private List<Map<String, String>> createExpectedList() {
		Map<String, String> record0 = createEmptyMergedRecord(0);
		record0.put("IDInSFDC", "0");
		
		Map<String, String> record2 = createEmptyMergedRecord(2);
		record2.put("IDInNetsuite", "2");

		Map<String, String> record1 = createEmptyMergedRecord(1);
		record1.put("IDInSFDC", "1");
		record1.put("IDInNetsuite", "1");
		record1.put("extIDInNetsuite", "1");

		Map<String, String> record3 = createEmptyMergedRecord(3);
		record3.put("IDInSFDC", "3");
		record3.put("IDInNetsuite", "3");
		record3.put("extIDInNetsuite", "3");

		List<Map<String, String>> expectedMergedList = new ArrayList<Map<String, String>>();
		expectedMergedList.add(record0);
		expectedMergedList.add(record2);
		expectedMergedList.add(record1);
		expectedMergedList.add(record3);

		return expectedMergedList;
	}

	/**
	 * Creates list of opportunity Map objects as it is likely to be a product of gatherDataFlow.
	 * @return
	 */
	private List<Map<String, String>> createOriginalList() {
		Map<String, String> record0 = createEmptyMergedRecord(0);
		record0.put("IDInSFDC", "0");

		Map<String, String> record1 = createEmptyMergedRecord(1);
		record1.put("IDInSFDC", "1");
		record1.put("IDInNetsuite", "1");
		record1.put("extIDInNetsuite", "1");

		Map<String, String> record2 = createEmptyMergedRecord(2);
		record2.put("IDInNetsuite", "2");

		Map<String, String> record3 = createEmptyMergedRecord(3);
		record3.put("IDInSFDC", "3");
		record3.put("IDInNetsuite", "3");
		record3.put("extIDInNetsuite", "3");

		List<Map<String, String>> recordList = new ArrayList<Map<String, String>>();
		recordList.add(record0);
		recordList.add(record1);
		recordList.add(record2);
		recordList.add(record3);

		return recordList;
	}

	/**
	 * Creates an empty record with Name based on the sequence number.
	 * @param sequence number used to build a name
	 * @return
	 */
	private Map<String, String> createEmptyMergedRecord(int sequence) {
		Map<String, String> mergedRecord = new HashMap<String, String>();
		mergedRecord.put("Name", "SomeName_" + sequence);
		mergedRecord.put("IDInSFDC", "");
		mergedRecord.put("AmountInSFDC", "");
		mergedRecord.put("IDInNetsuite", "");
		mergedRecord.put("AmountInNetsuite", "");
		mergedRecord.put("extIDInNetsuite", "");

		return mergedRecord;
	}

}
