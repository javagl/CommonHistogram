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
     * @return The {@link Binning}
     * @throws IllegalArgumentException If the bin count is not positive
     */
    static <T, K extends Number> NumberBinning<T> createSimpleNumberBinning(
        Collection<? extends T> elements, 
        Function<? super T, ? extends K> keyExtractor, 
        int binCount)
    {
        if (binCount <= 0)
        {
            throw new IllegalArgumentException(
                "The binCount must be positive, but is " + binCount);
        }
        
        ToDoubleFunction<T> valueExtractor = 
            t -> keyExtractor.apply(t).doubleValue();
        double min = 0;
        double max = 1;
        if (!elements.isEmpty())
        {
            min = elements.stream()
                .mapToDouble(valueExtractor)
                .min()
                .getAsDouble();
            max = elements.stream()
                .mapToDouble(valueExtractor)
                .max()
                .getAsDouble();
        }
        
        //System.out.println("Create binning with "+min+" "+max);

        NumberBinning<T> numberBinning = 
            new NumberBinning<T>(valueExtractor, min, max, binCount);
        return numberBinning;
    }
    /**
     * @param binCount The number of bins
     * @return The {@link Binning}
     * @throws IllegalArgumentException If the bin count is not positive
     */

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