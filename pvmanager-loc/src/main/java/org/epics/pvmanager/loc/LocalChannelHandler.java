/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.loc;

import java.util.Collections;
import java.util.List;
import org.epics.pvmanager.*;
import static org.epics.pvmanager.vtype.ValueFactory.*;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;

/**
 * Implementation for channels of a {@link LocalDataSource}.
 *
 * @author carcassi
 */
class LocalChannelHandler extends MultiplexedChannelHandler<Object, Object> {
    
    private final Object initialValue;

    LocalChannelHandler(String channelName) {
        super(channelName);
        initialValue = null;
    }

    LocalChannelHandler(String channelName, Object initialValue) {
        super(channelName);
        this.initialValue = wrapValue(initialValue);
    }

    @Override
    public void connect() {
        processConnection(new Object());
        if (initialValue != null)
            processMessage(initialValue);
    }

    @Override
    public void disconnect() {
        processConnection(null);
    }

    @Override
    protected synchronized void addReader(ChannelHandlerReadSubscription subscription) {
        // Override for test visibility purposes
        super.addReader(subscription);
    }

    @Override
    protected synchronized void addWriter(ChannelHandlerWriteSubscription subscription) {
        // Override for test visibility purposes
        super.addWriter(subscription);
    }

    @Override
    protected synchronized void removeReader(ChannelHandlerReadSubscription subscription) {
        // Override for test visibility purposes
        super.removeReader(subscription);
    }

    @Override
    protected synchronized void removeWrite(ChannelHandlerWriteSubscription subscription) {
        // Override for test visibility purposes
        super.removeWrite(subscription);
    }
    
    private Object wrapValue(Object value) {
        if (value instanceof Number) {
            // Special support for numbers
            return newVDouble(((Number) value).doubleValue(), alarmNone(), timeNow(),
                    displayNone());
        } else if (value instanceof String) {
            // Special support for strings
            return newVString(((String) value),
                    alarmNone(), timeNow());
        } else if (value instanceof double[]) {
            return newVDoubleArray(new ArrayDouble((double[]) value), alarmNone(), timeNow(), displayNone());
        } else if (value instanceof ListDouble) {
            return newVDoubleArray((ListDouble) value, alarmNone(), timeNow(), displayNone());
        } else if (value instanceof List) {
            boolean matches = true;
            List list = (List) value;
            for (Object object : list) {
                if (!(object instanceof String)) {
                    matches = false;
                }
            }
            if (matches) {
                @SuppressWarnings("unchecked")
                List<String> newList = (List<String>) list;
                return newVStringArray(Collections.unmodifiableList(newList), alarmNone(), timeNow());
            } else {
                throw new UnsupportedOperationException("Type " + value.getClass().getName() + " contains non Strings");
            }
        } else {
            // TODO: need to implement all the other arrays
            throw new UnsupportedOperationException("Type " + value.getClass().getName() + "  is not yet supported");
        }
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            newValue = wrapValue(newValue);
            processMessage(newValue);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }

    @Override
    protected boolean isWriteConnected(Object payload) {
        return isConnected(payload);
    }
    
}