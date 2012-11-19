/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a {@link ChannelHandler} on top of a single subscription and
 * multiplexes all reads on top of it.
 *
 * @param <ConnectionPayload> type of the payload for the connection
 * @param <MessagePayload> type of the payload for each message
 * @author carcassi
 */
public abstract class MultiplexedChannelHandler<ConnectionPayload, MessagePayload> extends ChannelHandler {

    private static final Logger log = Logger.getLogger(MultiplexedChannelHandler.class.getName());
    private int readUsageCounter = 0;
    private int writeUsageCounter = 0;
    private boolean connected = false;
    private boolean writeConnected = false;
    private MessagePayload lastMessage;
    private ConnectionPayload connectionPayload;
    private Map<ValueCache<?>, MonitorHandler> monitors = new ConcurrentHashMap<>();
    private Map<WriteCache<?>, ChannelHandlerWriteSubscription> writeCaches = new ConcurrentHashMap<>();

    private class MonitorHandler {

        private final ChannelHandlerReadSubscription subscription;
        private DataSourceTypeAdapter<ConnectionPayload, MessagePayload> typeAdapter;

        public MonitorHandler(ChannelHandlerReadSubscription subscription) {
            this.subscription = subscription;
        }
        
        public final void processConnection(boolean connection) {
            subscription.getConnectionWriteFunction().setValue(connection);
        }

        public final void processValue(MessagePayload payload) {
            if (typeAdapter == null)
                return;
            
            // Lock the collector and prepare the new value.
            try {
                typeAdapter.updateCache(subscription.getValueCache(), getConnectionPayload(), payload);
            } catch (RuntimeException e) {
                subscription.getExceptionWriteFunction().setValue(e);
            }
        }
        
        public final void findTypeAdapter() {
            if (getConnectionPayload() == null) {
                typeAdapter = null;
            } else {
                try {
                    typeAdapter = MultiplexedChannelHandler.this.findTypeAdapter(subscription.getValueCache(), getConnectionPayload());
                } catch(RuntimeException ex) {
                    subscription.getExceptionWriteFunction().setValue(ex);
                }
            }
        }
        
    }
    
    /**
     * Notifies all readers and writers of an error condition.
     * 
     * @param ex the exception to notify
     */
    protected synchronized final void reportExceptionToAllReadersAndWriters(Exception ex) {
        for (MonitorHandler monitor : monitors.values()) {
            monitor.subscription.getExceptionWriteFunction().setValue(ex);
        }
        for (ChannelHandlerWriteSubscription subscription : writeCaches.values()) {
            subscription.getExceptionWriteFunction().setValue(ex);
        }
    }
    
    private void reportConnectionStatus(boolean connected) {
        for (MonitorHandler monitor : monitors.values()) {
            monitor.processConnection(connected);
        }
    }
    
    private void reportWriteConnectionStatus(boolean writeConnected) {
        for (ChannelHandlerWriteSubscription subscription : writeCaches.values()) {
            subscription.getConnectionWriteFunction().setValue(writeConnected);
        }
    }

    /**
     * The last processes connection payload.
     * 
     * @return the connection payload or null
     */
    protected synchronized final ConnectionPayload getConnectionPayload() {
        return connectionPayload;
    }

    /**
     * The last processed message payload.
     * 
     * @return the message payload or null
     */
    protected synchronized final MessagePayload getLastMessagePayload() {
        return lastMessage;
    }

    /**
     * Process the next connection payload. This should be called whenever
     * the connection state has changed.
     * 
     * @param connectionPayload 
     */
    protected synchronized final void processConnection(ConnectionPayload connectionPayload) {
        this.connectionPayload = connectionPayload;
        setConnected(isConnected(connectionPayload));
        setWriteConnected(isWriteConnected(connectionPayload));
        
        for (MonitorHandler monitor : monitors.values()) {
            monitor.findTypeAdapter();
        }
        
        if (lastMessage != null) {
            processMessage(lastMessage);
        }
    }
    
    private static DataSourceTypeAdapter<?, ?> defaultTypeAdapter = new DataSourceTypeAdapter<Object, Object>() {

            @Override
            public int match(ValueCache<?> cache, Object connection) {
                return 1;
            }

            @Override
            public Object getSubscriptionParameter(ValueCache<?> cache, Object connection) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean updateCache(ValueCache cache, Object connection, Object message) {
                Object oldValue = cache.getValue();
                cache.setValue(message);
                if ((message == oldValue) || (message != null && message.equals(oldValue)))
                    return false;
                return true;
            }
        };
    
    /**
     * Finds the right adapter to use for the particular cache given the information
     * of the channels in the connection payload. By overriding this method
     * a datasource can implement their own matching logic. One
     * can use the logic provided in {@link DataSourceTypeSupport} as
     * a good first implementation.
     * 
     * @param cache the cache that will store the data
     * @param connection the connection payload
     * @return the matched type adapter
     */
    @SuppressWarnings("unchecked")
    protected DataSourceTypeAdapter<ConnectionPayload, MessagePayload> findTypeAdapter(ValueCache<?> cache, ConnectionPayload connection) {
        return (DataSourceTypeAdapter<ConnectionPayload, MessagePayload>) (DataSourceTypeAdapter) defaultTypeAdapter;
    }
    
    /**
     * Creates a new channel handler.
     * 
     * @param channelName the name of the channel this handler will be responsible of
     */
    public MultiplexedChannelHandler(String channelName) {
        super(channelName);
    }

    /**
     * Returns how many read or write PVs are open on
     * this channel.
     * 
     * @return the number of open read/writes
     */
    @Override
    public synchronized int getUsageCounter() {
        return readUsageCounter + writeUsageCounter;
    }
    
    /**
     * Returns how many read PVs are open on this channel.
     * 
     * @return the number of open reads
     */
    @Override
    public synchronized int getReadUsageCounter() {
        return readUsageCounter;
    }
    
    /**
     * Returns how many write PVs are open on this channel.
     * 
     * @return the number of open writes
     */
    @Override
    public synchronized int getWriteUsageCounter() {
        return writeUsageCounter;
    }

    /**
     * Used by the data source to add a read request on the channel managed
     * by this handler.
     * 
     * @param subscription the data required for a subscription
     */
    @Override
    protected synchronized void addReader(ChannelHandlerReadSubscription subscription) {
        readUsageCounter++;
        MonitorHandler monitor = new MonitorHandler(subscription);
        monitors.put(subscription.getValueCache(), monitor);
        monitor.findTypeAdapter();
        guardedConnect();
        if (readUsageCounter > 1) {
            if (connectionPayload != null) {
            monitor.processConnection(isConnected());
            }
            if (lastMessage != null) {
                monitor.processValue(lastMessage);
            }
        } 
    }

    /**
     * Used by the data source to remove a read request.
     * 
     * @param subscription the collector that does not need to be notified anymore
     */
    @Override
    protected synchronized void removeReader(ChannelHandlerReadSubscription subscription) {
        monitors.remove(subscription);
        readUsageCounter--;
        guardedDisconnect();
    }
    
    /**
     * Used by the data source to prepare the channel managed by this handler
     * for write.
     * 
     * @param handler to be notified in case of errors
     */
    @Override
    protected synchronized void addWriter(ChannelHandlerWriteSubscription subscription) {
        writeUsageCounter++;
        writeCaches.put(subscription.getWriteCache(), subscription);
        guardedConnect();
        if (connectionPayload != null) {
            subscription.getConnectionWriteFunction().setValue(isWriteConnected());
        }
    }

    /**
     * Used by the data source to conclude writes to the channel managed by this handler.
     * 
     * @param exceptionHandler to be notified in case of errors
     */
    @Override
    protected synchronized void removeWrite(ChannelHandlerWriteSubscription subscription) {
        writeUsageCounter--;
        writeCaches.remove(subscription.getWriteCache());
        guardedDisconnect();
    }

    /**
     * Process the payload for this channel. This should be called whenever
     * a new value needs to be processed. The handler will take care of
     * using the correct {@link DataSourceTypeAdapter}
     * for each read monitor that was setup.
     * 
     * @param payload the payload of for this type of channel
     */
    protected synchronized final void processMessage(MessagePayload payload) {
        lastMessage = payload;
        for (MonitorHandler monitor : monitors.values()) {
            monitor.processValue(payload);
        }
    }

    private void guardedConnect() {
        if (getUsageCounter() == 1) {
            try {
                connect();
            } catch(RuntimeException ex) {
                reportExceptionToAllReadersAndWriters(ex);
            }
        }
    }

    private void guardedDisconnect() {
        if (getUsageCounter() == 0) {
            try {
                disconnect();
                lastMessage = null;
                connectionPayload = null;
            } catch (RuntimeException ex) {
                reportExceptionToAllReadersAndWriters(ex);
                log.log(Level.WARNING, "Couldn't disconnect channel " + getChannelName(), ex);
           }
        }
    }

    /**
     * Used by the handler to open the connection. This is called whenever
     * the first read or write request is made.
     */
    protected abstract void connect();
    
    /**
     * Used by the handler to close the connection. This is called whenever
     * the last reader or writer is de-registered.
     */
    protected abstract void disconnect();

    /**
     * Implements a write operation. Write the newValues to the channel
     * and call the callback when done.
     * 
     * @param newValue new value to be written
     * @param callback called when done or on error
     */
    @Override
    protected abstract void write(Object newValue, ChannelWriteCallback callback);

    private void setConnected(boolean connected) {
        this.connected = connected;
        reportConnectionStatus(connected);
    }
    
    private void setWriteConnected(boolean writeConnected) {
        this.writeConnected = writeConnected;
        reportWriteConnectionStatus(writeConnected);
    }
    
    /**
     * Determines from the payload whether the channel is connected or not.
     * <p>
     * By default, this uses the usage counter to determine whether it's
     * connected or not. One should override this to use the actual
     * connection payload to check whether the actual protocol connection
     * has been established.
     * 
     * @param payload the connection payload
     * @return true if connected
     */
    protected boolean isConnected(ConnectionPayload  payload) {
        return getUsageCounter() > 0;
    }
    
    /**
     * Determines from the payload whether the channel can be written to.
     * <p>
     * By default, this always return false. One should override this
     * if it's implementing a write-able data source.
     * 
     * @param payload
     * @return 
     */
    protected boolean isWriteConnected(ConnectionPayload payload) {
        return false;
    }
    
    /**
     * Returns true if it is connected.
     * 
     * @return true if underlying channel is connected
     */
    @Override
    public synchronized final boolean isConnected() {
        return connected;
    }
    
    /**
     * Returns true if it is channel can be written to.
     * 
     * @return true if underlying channel is write ready
     */
    public synchronized final boolean isWriteConnected() {
        // TODO: push this in ChannleHandler?
        return writeConnected;
    }
}
