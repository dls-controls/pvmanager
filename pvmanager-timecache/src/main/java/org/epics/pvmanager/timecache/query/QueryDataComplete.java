/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.epics.pvmanager.timecache.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/**
 * Represents a completed chunk with all data available.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class QueryDataComplete implements QueryData {

	private final TimeInterval timeInterval;
	private final SortedMap<Timestamp, VType> dataMap;

	QueryDataComplete(TimeInterval timeInterval, SortedMap<Timestamp, VType> dataMap) {
		this.dataMap = Collections.unmodifiableSortedMap(dataMap);
		this.timeInterval = timeInterval;
	}

	/** {@inheritDoc} */
	@Override
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	/** {@inheritDoc} */
	@Override
	public int getCount() {
		return dataMap.size();
	}

	/** {@inheritDoc} */
	@Override
	public List<VType> getData() {
		return new ArrayList<VType>(dataMap.values());
	}

	/** {@inheritDoc} */
	@Override
	public List<Timestamp> getTimestamps() {
		return new ArrayList<Timestamp>(dataMap.keySet());
	}

}