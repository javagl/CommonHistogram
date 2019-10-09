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
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.swing.JComponent;

/**
 * Implementation of a {@link NumberHistogram} based on a 
 * {@link JFreeChartHistogram}
 *
 * @param <T> The element type
 */
class JFreeChartNumberHistogram<T> implements NumberHistogram<T>
{
    /**
     * The delegate
     */
    private final JFreeChartHistogram<T> delegate;
    
    /**
     * The provider for the binnings
     */
    private final IntFunction<NumberBinning<T>> binningProvider;

    /**
     * The bin label function provider
     */
    private final Function<? super NumberBinning<?>, 
        ? extends IntFunction<String>> binLabelFunctionProvider;
    
    /**
     * Creates a new instance
     * 
     * @param delegate The delegate
     * @param binningProvider The provider for the binnings
     * @param binLabelFunctionProvider The bin label function provider
     */
    JFreeChartNumberHistogram(
        JFreeChartHistogram<T> delegate,
        IntFunction<NumberBinning<T>> binningProvider,
        Function<? super NumberBinning<?>, ? extends IntFunction<String>> 
            binLabelFunctionProvider)
    {
        this.delegate = Objects.requireNonNull(
            delegate, "The delegate may not be null");
        this.binningProvider = Objects.requireNonNull(
            binningProvider, "The binningProvider may not be null");
        this.binLabelFunctionProvider = Objects.requireNonNull(
            binLabelFunctionProvider, 
            "The binLabelFunctionProvider may not be null");
    }

    @Override
    public JComponent getComponent()
    {
        return delegate.getComponent();
    }

    @Override
    public void setElements(Collection<? extends T> elements,
        Collection<? extends T> highlightedElements)
    {
        delegate.setElements(elements, highlightedElements);

    }

    @Override
    public void addHistogramMouseListener(
        HistogramMouseListener<T> histogramMouseListener)
    {
        delegate.addHistogramMouseListener(histogramMouseListener);
    }

    @Override
    public void removeHistogramMouseListener(
        HistogramMouseListener<T> histogramMouseListener)
    {
        delegate.removeHistogramMouseListener(histogramMouseListener);
    }
    
    @Override
    public int getBinCount()
    {
        return delegate.getBinning().getBinCount();
    }

    @Override
    public void setBinCount(int binCount)
    {
        if (binCount < 1)
        {
            throw new IllegalArgumentException(
                "The bin count must be positive, but is " + binCount);
        }
        NumberBinning<T> newBinning = binningProvider.apply(binCount);
        IntFunction<String> newBinLabelFunction = 
            binLabelFunctionProvider.apply(newBinning);
        delegate.setBinning(newBinning, newBinLabelFunction);
    }
}
