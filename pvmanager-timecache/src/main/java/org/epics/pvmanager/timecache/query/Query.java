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

import org.epics.util.time.TimeInterval;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface Query {

	/**
	 * Changes the parameters for the query. This method is thread safe and
	 * returns immediately. It may schedule asynchronous execution of service
	 * calls to fetch the data.
	 * @param queryParameters
	 */
	public void update(QueryParameters queryParameters);

	/**
	 * This method returns all data available for the specified
	 * {@link TimeInterval}.
	 * @return
	 */
	public QueryResult getResult();

	/**
	 * This method can be polled at regular interval and should return right
	 * away with the data is already available, even if incomplete.
	 * @return
	 */
	public QueryResult getUpdate();

	/**
	 * Query can be disposed. Future calls to getResult() and getUpdate() will
	 * result in IllegalStateException.
	 */
	public void close();

}