/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.text.NumberFormat;
import java.util.List;
import org.epics.pvmanager.TimeStamp;

/**
 * Partial implementation for numeric types.
 *
 * @author carcassi
 */
class IVNumeric implements Alarm, Time, Display {
    
    private final AlarmSeverity alarmSeverity;
    private final AlarmStatus alarmStatus;
    private final TimeStamp timeStamp;
    private final Integer timeUserTag;
    private final Double lowerDisplayLimit;
    private final Double lowerCtrlLimit;
    private final Double lowerAlarmLimit;
    private final Double lowerWarningLimit;
    private final String units;
    private final NumberFormat format;
    private final Double upperWarningLimit;
    private final Double upperAlarmLimit;
    private final Double upperCtrlLimit;
    private final Double upperDisplayLimit;

    IVNumeric(AlarmSeverity alarmSeverity, AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        this.alarmSeverity = alarmSeverity;
        this.alarmStatus = alarmStatus;
        this.timeStamp = timeStamp;
        this.timeUserTag = timeUserTag;
        this.lowerDisplayLimit = lowerDisplayLimit;
        this.lowerCtrlLimit = lowerCtrlLimit;
        this.lowerAlarmLimit = lowerAlarmLimit;
        this.lowerWarningLimit = lowerWarningLimit;
        this.units = units;
        this.format = format;
        this.upperWarningLimit = upperWarningLimit;
        this.upperAlarmLimit = upperAlarmLimit;
        this.upperCtrlLimit = upperCtrlLimit;
        this.upperDisplayLimit = upperDisplayLimit;
    }

    @Override
    public AlarmSeverity getAlarmSeverity() {
        return alarmSeverity;
    }

    @Override
    public AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Integer getTimeUserTag() {
        return timeUserTag;
    }

    @Override
    public Double getLowerDisplayLimit() {
        return lowerDisplayLimit;
    }

    @Override
    public Double getLowerCtrlLimit() {
        return lowerCtrlLimit;
    }

    @Override
    public Double getLowerAlarmLimit() {
        return lowerAlarmLimit;
    }

    @Override
    public Double getLowerWarningLimit() {
        return lowerWarningLimit;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public NumberFormat getFormat() {
        return format;
    }

    @Override
    public Double getUpperWarningLimit() {
        return upperWarningLimit;
    }

    @Override
    public Double getUpperAlarmLimit() {
        return upperAlarmLimit;
    }

    @Override
    public Double getUpperCtrlLimit() {
        return upperCtrlLimit;
    }

    @Override
    public Double getUpperDisplayLimit() {
        return upperDisplayLimit;
    }

}