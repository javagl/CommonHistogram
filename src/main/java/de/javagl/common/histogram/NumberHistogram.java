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
 * Interface for a {@link Histogram} for values that are essentially 
 * numeric, and where the bin count may be modified 
 *  
 * @param <T> The type of the elements
 */
public interface NumberHistogram<T> extends Histogram<T>
{
    /**
     * Returns the current bin count of this histogram
     * 
     * @return The bin count
     */
    int getBinCount();
    
    /**
     * Set the number of bins for this histogram
     * 
     * @param binCount The bin count
     * @throws IllegalArgumentException If the bin count is not positive
     */
    void setBinCount(int binCount);
}
