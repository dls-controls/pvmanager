/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVStructure;
import org.epics.pvmanager.vtype.VDouble;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVDouble extends AlarmTimeDisplayExtractor implements VDouble {

	protected final Double value;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVDouble(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		value = getDoubleValue(pvField, "value");
	}

    /* (non-Javadoc)
     * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
     */
	@Override
    public Double getValue()
    {
    	return value;
    }

}