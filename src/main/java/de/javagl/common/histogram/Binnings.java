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

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Methods to create {@link Binning} instances
 */
class Binnings
{
    /**
     * Compute a simple numerical {@link Binning} for the given elements.
     * 
     * @param elements The values
     * @param keyExtractor The key extractor
     * @param binCount The number of bins
     * @param min The optional minimum value for the binning
     * @param max The optional maximum value for the binning
     * @return The {@link Binning}
     * @throws IllegalArgumentException If the bin count is not positive
     */
    static <T, K extends Number> NumberBinning<T> createSimpleNumberBinning(
        Collection<? extends T> elements, 
        Function<? super T, ? extends K> keyExtractor, 
        int binCount, T min, T max)
    {
        if (binCount <= 0)
        {
            throw new IllegalArgumentException(
                "The binCount must be positive, but is " + binCount);
        }
        
        ToDoubleFunction<T> valueExtractor = 
            t -> keyExtractor.apply(t).doubleValue();
        Point2D range = computeRange(elements, min, max, valueExtractor);
        
        NumberBinning<T> numberBinning = new NumberBinning<T>(
            valueExtractor, range.getX(), range.getY(), binCount);
        return numberBinning;
    }
    
    /**
     * Returns whether the given range is empty, meaning that its maximum
     * is less than a machine epsilon larger than the minimum
     * 
     * @param range The range
     * @return Whether the range is empty
     */
    static boolean isEmpty(Point2D range)
    {
        return (range.getY() - range.getX()) < NumberBinning.EPSILON;
    }

    /**
     * Compute the range of the values that are extracted from the elements
     * in the given collection. If the given minimum and maximum are not
     * <code>null</code>, they will be used to determine the range. Otherwise,
     * the minimum and maximum will be computed from the given elements.
     * If the collection of elements is empty and no minimum or maximum 
     * have been given, then an unspecified best-effort approach will be
     * made to return a reasonable range.
     * 
     * @param elements The elements
     * @param min The minimum value
     * @param max The maximum value
     * @param valueExtractor The value extractor
     * @return The range
     */
    static <T> Point2D computeRange(
        Collection<? extends T> elements, T min, T max, 
        ToDoubleFunction<T> valueExtractor)
    {
        Double actualMin = null;
        Double actualMax = null;
        if (min != null)
        {
            actualMin = valueExtractor.applyAsDouble(min);
        }
        else if (!elements.isEmpty())
        {
            actualMin = elements.stream()
                .mapToDouble(valueExtractor)
                .min()
                .getAsDouble();
        }
        if (max != null)
        {
            actualMax = valueExtractor.applyAsDouble(max);
        }
        else if (!elements.isEmpty())
        {
            actualMax = elements.stream()
                .mapToDouble(valueExtractor)
                .max()
                .getAsDouble();
        }
        if (actualMin != null)
        {
            if (actualMax == null)
            {
                actualMax = actualMin + 1.0;
            }
        }
        if (actualMax != null)
        {
            if (actualMin == null)
            {
                actualMin = actualMax - 1.0;
            }
        }
        if (actualMin == null && actualMax == null)
        {
            actualMin = 0.0;
            actualMax = 1.0;
        }
        return new Point2D.Double(actualMin, actualMax);
    }

    /**
     * Compute a simple {@link Binning} for the given elements. <br>
     * <br>
     * The number of bins will be equal to the number of distinct values
     * that are extracted from the elements using the given function.<br>
     * <br>
     * The bins will be added in the same order as these distinct values 
     * appear in the collection.
     * 
     * @param elements The elements
     * @param keyExtractor The key extractor
     * @return The {@link Binning}
     */
    static <T, K> Binning<T> createGeneralBinning(
        Collection<? extends T> elements, 
        Function<? super T, ? extends K> keyExtractor)
    {
        Map<Object, Integer> bins = new LinkedHashMap<Object, Integer>();
        int index = 0;
        
        Set<K> keySet = elements.stream()
            .map(keyExtractor)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<K, Integer> keyIndices = new LinkedHashMap<K, Integer>();
        for (K key : keySet)
        {
            bins.put(key, index);
            keyIndices.put(key, index);
            index++;
        }
        return new GeneralBinning<T>(bins.size(), 
            keyExtractor.andThen(k -> keyIndices.get(k)));
    }
 
    /**
     * Private constructor to prevent instantiation
     */
    private Binnings()
    {
        // Private constructor to prevent instantiation
    }
    
}