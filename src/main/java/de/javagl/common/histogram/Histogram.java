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

import java.util.Collection;

import javax.swing.JComponent;

/**
 * Interface for a histogram. A histogram is the visualization of a 
 * binning that is applied to a set of elements.
 *  
 * @param <T> The type of the elements
 */
public interface Histogram<T>
{
    /**
     * Returns the visualization component for this histogram
     * 
     * @return The visualization component
     */
    JComponent getComponent();
    
    /**
     * Set the elements that this histogram should be shown for.<br>
     * <br>
     * The highlighted elements will generally be a subset of the elements,
     * which will usually be represented with a different color.<br>
     * <br>
     * If any of the given collections is <code>null</code>, then the
     * empty collection will be used instead.
     * 
     * @param elements The elements
     * @param highlightedElements The highlighted elements
     */
    void setElements(
        Collection<? extends T> elements, 
        Collection<? extends T> highlightedElements);
    
    /**
     * Add the given {@link HistogramMouseListener} to be informed about
     * mouse events on the histogram
     * 
     * @param histogramMouseListener The listener to add
     */
    void addHistogramMouseListener(
        HistogramMouseListener<T> histogramMouseListener);

    /**
     * Remove the given {@link HistogramMouseListener}
     * 
     * @param histogramMouseListener The listener to remove
     */
    void removeHistogramMouseListener(
        HistogramMouseListener<T> histogramMouseListener);
}
