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

import java.util.Objects;
import java.util.function.ToDoubleFunction;

/**
 * Implementation of a {@link Binning} for numerical values
 * 
 * @param <T> The element type
 */
class NumberBinning<T> implements Binning<T>
{
    /**
     * A machine epsilon. Values that are larger than the maximum will
     * be considered to be equal to the maximum when they are within
     * this threshold 
     */
    private static final double EPSILON = 1e-6;
    
    /**
     * The key extractor
     */
    private final ToDoubleFunction<? super T> keyExtractor;
    
    /**
     * The minimum value. This is the start value of the first bin
     */
    private final double min;
    
    /**
     * The maximum value. This is the end value of the last bin
     */
    private final double max;
    
    /**
     * The number of bins
     */
    private final int binCount;
    
    /**
     * Creates a new instance
     * 
     * @param keyExtractor The key extractor
     * @param min The minimum value
     * @param max The maximum value
     * @param binCount The number of bins
     */
    NumberBinning(ToDoubleFunction<? super T> keyExtractor, 
        double min, double max, int binCount)
    {
        this.keyExtractor = Objects.requireNonNull(
            keyExtractor, "The keyExtractor may not be null");
        this.min = min;
        this.max = max;
        this.binCount = binCount;
    }
    
    /**
     * Returns the minimum value of the bin with the given index, inclusive. 
     * 
     * @param bin The bin index
     * @return The minimum value
     */
    double getBinMin(int bin)
    {
        double step = (max - min) / binCount;
        return min + step * bin;
    }

    /**
     * Returns the maximum value of the bin with the given index. For all
     * bins except for the last one, this value is exclusive. For the last
     * bin, this value is inclusive. 
     * 
     * @param bin The bin index
     * @return The maximum value
     */
    double getBinMax(int bin)
    {
        double step = (max - min) / binCount;
        return min + step * bin + step;
    }
    
    @Override
    public int getBinCount()
    {
        return binCount;
    }

    @Override
    public int computeBin(T object)
    {
        double value = keyExtractor.applyAsDouble(object);
        if (value >= max && value < max + EPSILON)
        {
            return binCount - 1;
        }
        double alpha = (value - min) / (max - min);
        int bin = (int) (alpha * binCount);
        if (bin < 0 || bin >= binCount)
        {
            return -1;
        }
        return bin;
    }
    
    @Override
    public int[] compute(Iterable<? extends T> objects, boolean ignoreInvalid)
    {
        int bins[] = new int[getBinCount()];
        if (objects != null)
        {
            for (T object : objects)
            {
                int bin = computeBin(object);
                if (bin < 0)
                {
                    if (!ignoreInvalid)
                    {
                        throw new IllegalArgumentException("The object "
                            + object + " was not part of the binning");
                    }
                }
                else
                {
                    bins[bin]++;
                }
            }
        }
        return bins;
    }
    
}
