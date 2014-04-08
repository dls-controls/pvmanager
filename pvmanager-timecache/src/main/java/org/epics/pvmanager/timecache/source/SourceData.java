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
package org.epics.pvmanager.timecache.source;

import org.epics.pvmanager.timecache.Data;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/**
 * {@link Data} from {@link DataSource}.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SourceData implements Data {

	private final Timestamp timestamp;
	private final VType value;

	public SourceData(Timestamp timestamp, VType value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	/** {@inheritDoc} */
	@Override
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/** {@inheritDoc} */
	@Override
	public VType getValue() {
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceData other = (SourceData) obj;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(Data d) {
		return timestamp.compareTo(d.getTimestamp());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "SourceData [timestamp=" + timestamp + ", value=" + value + "]";
	}

}