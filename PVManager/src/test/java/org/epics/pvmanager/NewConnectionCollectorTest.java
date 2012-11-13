/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author carcassi
 */
public class NewConnectionCollectorTest {
    
    public NewConnectionCollectorTest() {
    }

    @Test
    public void inputOutput() {
        NewConnectionCollector collector = new NewConnectionCollector();
        assertThat(collector.getValue(), equalTo(true));
        
        WriteFunction<Boolean> firstWriteFunction = collector.addChannel("first");
        assertThat(collector.getValue(), equalTo(false));

        firstWriteFunction.setValue(false);
        assertThat(collector.getValue(), equalTo(false));
        
        WriteFunction<Boolean> secondWriteFunction = collector.addChannel("second");
        secondWriteFunction.setValue(true);
        assertThat(collector.getValue(), equalTo(false));
        
        firstWriteFunction.setValue(true);
        assertThat(collector.getValue(), equalTo(true));
        
        collector.removeChannel("second");
        assertThat(collector.getValue(), equalTo(true));
        
        secondWriteFunction = collector.addChannel("second");
        assertThat(collector.getValue(), equalTo(false));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void removingUnknownChannel() {
        NewConnectionCollector collector = new NewConnectionCollector();
        collector.removeChannel("never");
    }
    
    @Test(expected=IllegalStateException.class)
    public void usingDeregisteredWriteFunction() {
        NewConnectionCollector collector = new NewConnectionCollector();
        WriteFunction<Boolean> channelWriteFunction = collector.addChannel("first");
        collector.removeChannel("first");
        channelWriteFunction.setValue(true);
    }
}