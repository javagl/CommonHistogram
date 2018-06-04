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

import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

/**
 * An event that indicates that a mouse interaction occurred on a 
 * {@link Histogram}
 * 
 * @param <T> The type of the elements
 */
public final class HistogramMouseEvent<T> extends EventObject
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The mouse event that caused this event
     */
    private final MouseEvent mouseEvent;
    
    /**
     * The index of the bin that the action appeared on
     */
    private final int bin;
    
    /**
     * Whether the action appeared on the highlighted part of a bin
     */
    private final boolean highlighted;
    
    /**
     * The elements in the bin that the action appeared on
     */
    private final List<T> binElements;
    
    /**
     * The highlighted elements in the bin that the action appeared on
     */
    private final List<T> highlightedBinElements;
    
    /**
     * Creates a new instance. References to the given lists will be stored
     * and returned, so they should be unmodifiable and may not change after
     * this instance has been created 
     * 
     * @param histogram The {@link Histogram}
     * @param mouseEvent The original mouse event
     * @param bin The bin index
     * @param highlighted Whether the action appeared on the highlighted part
     * @param binElements The elements in the bin 
     * @param highlightedBinElements The highlighted elements in the bin
     */
    HistogramMouseEvent(Histogram<T> histogram,
        MouseEvent mouseEvent, int bin, boolean highlighted, 
        List<T> binElements, List<T> highlightedBinElements)
    {
        super(histogram);
        this.mouseEvent = mouseEvent;
        this.bin = bin;
        this.highlighted = highlighted;
        this.binElements = binElements;
        this.highlightedBinElements = highlightedBinElements;
    }
    
    /**
     * Returns the mouse event that caused this event
     * 
     * @return The mouse event
     */
    public MouseEvent getMouseEvent()
    {
        return mouseEvent;
    }

    /**
     * Returns the index of the bin that the action appeared on
     * 
     * @return The index of the bin
     */
    public int getBin()
    {
        return bin;
    }

    /**
     * Returns whether the action appeared on the highlighted part of a bin
     * 
     * @return The highlighting state
     */
    public boolean isHighlighted()
    {
        return highlighted;
    }

    /**
     * Returns an unmodifiable list of the elements in the bin that the action 
     * appeared on
     * 
     * @return The elements in the bin
     */
    public List<T> getBinElements()
    {
        return binElements;
    }

    /**
     * Returns an unmodifiable list of the highlighted elements in the bin 
     * that the action appeared on
     * 
     * @return The highlighted elements in the bin
     */
    public List<T> getHighlightedBinElements()
    {
        return highlightedBinElements;
    }
}

