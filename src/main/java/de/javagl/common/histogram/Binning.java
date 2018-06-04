/*
 * www.javagl.de - Histogram
 *
 * Copyright (c) 2013-2018 Marco Hutter - http://www.javagl.de
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.common.histogram;

/**
 * Interface for a binning of objects. It associates each object with the
 * index of the bin that the object belongs to.
 * 
 * @param <T> The type of the objects
 */
interface Binning<T>
{
    /**
     * Returns the number of bins that this binning consists of
     * 
     * @return The number of bins
     */
    int getBinCount();
    
    /**
     * Computes the index of the bin that the given object belongs to.
     * The return value will be negative if the given object cannot
     * be associated with a bin.
     * 
     * @param object The object
     * @return The bin index
     */
    int computeBin(T object);
    
    /**
     * Computes the binning for the given objects. The return value will be
     * an array with {@link #getBinCount()} elements. Each element of the 
     * array will be the number of times that any of the given objects 
     * was associated with the respective bin.
     *   
     * @param objects The objects
     * @param ignoreInvalid Whether invalid objects should be ignored
     * @return The binning
     * @throws IllegalArgumentException If <code>ignoreInvalid</code> is
     * <code>false</code> and any object was not associated with a valid
     * bin.
     */
    int[] compute(Iterable<? extends T> objects, boolean ignoreInvalid);
}
