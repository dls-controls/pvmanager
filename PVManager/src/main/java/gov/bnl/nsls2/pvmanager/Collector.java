/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.bnl.nsls2.pvmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collects the data at the CA rate and allows a client to get all values
 * since last check. The class needs to be thread safe, and it function
 * as a queue where the CA threads post data, and a timer based thread
 * collects the data.
 * <p>
 * There are two locks used: one (the object itself) must be used whenever
 * one is either calculating the function or preparing the inputs for the function.
 * The other (the buffer) is used whenever the buffer is modified. The idea
 * is that the new calculation may not block the scanning and reading of the
 * values in the buffer.
 *
 * @author carcassi
 */
public abstract class Collector<T> {

    /**
     * Calculates the next value and puts it in the queue.
     */
    public abstract void collect();

    /**
     * Returns all values since last check and removes values from the queue.
     * @return a new array with the value; never null
     */
    public abstract List<T> getData();
}