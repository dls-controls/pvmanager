/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.concurrent.Executor;
import org.epics.pvmanager.util.TimeDuration;

/**
 * Allows to configure the type of read/write PV to create.
 *
 * @param <R> the read payload
 * @param <W> the write payload
 * @author carcassi
 */
public class PVConfiguration<R, W> extends CommonConfiguration {
    
    private final PVReaderConfiguration<R> pvReaderConfiguration;
    private final PVWriterConfiguration<W> pvWriterConfiguration;

    PVConfiguration(DesiredRateExpression<R> readExpression, WriteExpression<W> writeExpression) {
        pvReaderConfiguration = new PVReaderConfiguration<R>(readExpression);
        pvWriterConfiguration = new PVWriterConfiguration<W>(writeExpression);
    }
    
    @Override
    public PVConfiguration<R, W> from(DataSource dataSource) {
        pvReaderConfiguration.from(dataSource);
        pvWriterConfiguration.from(dataSource);
        return this;
    }

    @Override
    public PVConfiguration<R, W> notifyOn(Executor onThread) {
        pvReaderConfiguration.notifyOn(onThread);
        pvWriterConfiguration.notifyOn(onThread);
        return this;
    }

    /**
     * Forwards exception to the given exception handler. No thread switch
     * is done, so the handler is notified on the thread where the exception
     * was thrown.
     * <p>
     * Giving a custom exception handler will disable the default handler,
     * so {@link PV#lastException() } and {@link PV#lastWriteException() }
     * is no longer set and no notification
     * is done.
     *
     * @param exceptionHandler an exception handler
     * @return this
     */
    public PVConfiguration<R, W> routeExceptionsTo(ExceptionHandler exceptionHandler) {
        pvReaderConfiguration.routeExceptionsTo(exceptionHandler);
        pvWriterConfiguration.routeExceptionsTo(exceptionHandler);
        return this;
    }
    
    /**
     * Creates the pv such that writes are synchronous and read notifications
     * comes at most at the rate specified.
     * 
     * @param period minimum time between read notifications
     * @return a new PV
     */
    public PV<R, W> synchWriteAndReadEvery(TimeDuration period) {
        PVReader<R> pvReader = pvReaderConfiguration.every(period);
        PVWriter<W> pvWriter = pvWriterConfiguration.sync();
        return new PV<R, W>(pvReader, pvWriter);
    }
    
    /**
     * Creates the pv such that writes are asynchronous and read notifications
     * comes at most at the rate specified.
     * 
     * @param period minimum time between read notifications
     * @return a new PV
     */
    public PV<R, W> asynchWriteAndReadEvery(TimeDuration period) {
        PVReader<R> pvReader = pvReaderConfiguration.every(period);
        PVWriter<W> pvWriter = pvWriterConfiguration.async();
        return new PV<R, W>(pvReader, pvWriter);
    }
    
}