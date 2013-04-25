/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.vtype.io;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.epics.util.array.ListNumber;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.Alarm;
import org.epics.vtype.Time;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
public class CSVIO {

    // TODO: we should take these from a default place
    private static TimestampFormat timeFormat = new TimestampFormat(
            "yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$

    public void export(Object value, Writer writer) {
        if (!canExport(value)) {
            throw new IllegalArgumentException("Type " + value.getClass().getSimpleName() + " is not supported by this data export.");
        }

        try {
            Time time = ValueUtil.timeOf(value);
            if (time != null && time.getTimestamp() != null) {
                writer.append("\"")
                        .append(timeFormat.format(time.getTimestamp()))
                        .append("\" ");
            }

            Alarm alarm = ValueUtil.alarmOf(value);
            if (alarm != null) {
                writer.append(alarm.getAlarmSeverity().name())
                        .append(" ")
                        .append(alarm.getAlarmName());
            }

            if (value instanceof VNumber) {
                writer.append(" ")
                        .append(Double.toString(((VNumber) value).getValue().doubleValue()));
            }

            if (value instanceof VString) {
                writer.append(" \"")
                        .append(((VString) value).getValue())
                        .append("\"");
            }

            if (value instanceof VEnum) {
                writer.append(" \"")
                        .append(((VEnum) value).getValue())
                        .append("\"");
            }

            if (value instanceof VNumberArray) {
                ListNumber data = ((VNumberArray) value).getData();
                for (int i = 0; i < data.size(); i++) {
                    writer.append(" ")
                            .append(Double.toString(data.getDouble(i)));
                }
            }

            if (value instanceof VStringArray) {
                List<String> data = ((VStringArray) value).getData();
                for (int i = 0; i < data.size(); i++) {
                    writer.append(" \"")
                            .append(data.get(i))
                            .append("\"");
                }
            }

            if (value instanceof VEnumArray) {
                List<String> data = ((VEnumArray) value).getData();
                for (int i = 0; i < data.size(); i++) {
                    writer.append(" \"")
                            .append(data.get(i))
                            .append("\"");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Write failed", e);
        }
    }

    public boolean canExport(Object data) {
        if (data instanceof VNumber) {
            return true;
        }

        if (data instanceof VString) {
            return true;
        }

        if (data instanceof VEnum) {
            return true;
        }

        if (data instanceof VNumberArray) {
            return true;
        }

        if (data instanceof VStringArray) {
            return true;
        }

        if (data instanceof VEnumArray) {
            return true;
        }

        return false;
    }
}
