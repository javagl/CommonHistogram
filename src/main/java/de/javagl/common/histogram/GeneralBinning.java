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
import java.util.function.Function;

/**
 * General implementation of a {@link Binning} for categorical data
 * 
 * @param <T> The type of the elements
 */
class GeneralBinning<T> implements Binning<T>
{
    /**
     * The bin count
     */
    private final int binCount;
    
    /**
     * The function that provides the bin indices for the objects
     */
    private final Function<? super T, Integer> binningFunction;
    
    /**
     * Creates a new instance. This will store references to the given 
     * collections.
     * 
     * @param binCount The bin count
     * @param binningFunction The mapping from objects to bin indices
     */
    GeneralBinning(int binCount, Function<? super T, Integer> binningFunction)
    {
        this.binCount = binCount;
        this.binningFunction = Objects.requireNonNull(
            binningFunction, "The binningFunction may not be null");
    }
    
    @Override
    public int getBinCount()
    {
        return binCount;
    }

    @Override
    public int computeBin(T object)
    {
        Integer bin = binningFunction.apply(object);
        if (bin == null)
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
