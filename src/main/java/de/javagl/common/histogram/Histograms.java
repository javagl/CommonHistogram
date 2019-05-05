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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Methods to create {@link Histogram} instances
 */
public class Histograms
{
    /**
     * Creates a new {@link Histogram} with the given elements.
     * 
     * @param <T> The element type
     *  
     * @param elements The elements
     * @return The {@link Histogram}
     */
    public static <T> Histogram<T> create(Collection<? extends T> elements)
    {
        return create(elements, Function.identity());
    }
    
    /**
     * Create a new {@link Histogram} that shows the given elements, 
     * distributed among the bins based on the value that is returned 
     * by the given key extractor.<br>
     * <br>
     * 
     * @param <T> The element type
     *  
     * @param elements The elements
     * @param keyExtractor The key extractor
     * @return The {@link Histogram}
     */
    public static <T> Histogram<T> create(
        Collection<? extends T> elements, 
        Function<? super T, Object> keyExtractor)
    {
        Objects.requireNonNull(
            elements, "The elements may not be null");
        Objects.requireNonNull(
            keyExtractor, "The keyExtractor may not be null");
        Set<Object> keySet = new LinkedHashSet<Object>();
        List<String> binLabels = new ArrayList<String>();
        for (T element : elements)
        {
            Object key = keyExtractor.apply(element);
            if (!keySet.contains(key))
            {
                keySet.add(key);
                binLabels.add(String.valueOf(key));
            }
        }
        Binning<T> binning =
            Binnings.createGeneralBinning(elements, keyExtractor);
        JFreeChartHistogram<T> histogram =
            new JFreeChartHistogram<T>(binning, binLabels::get);
        histogram.setElements(elements, null);
        return histogram;
    }

    /**
     * Create a new {@link Histogram} that shows the given numeric elements.
     * The returned histogram will have a control component for changing
     * the number of bins. The initial number of bins is not specified,
     * but will be "reasonable" for many application cases.
     * 
     * @param <T> The element type
     * 
     * @param elements The elements
     * @return The {@link Histogram}
     */
    public static <T extends Number> NumberHistogram<T> 
        createNumeric(Collection<? extends T> elements)
    {
        return createNumeric(elements, Function.identity());
    }

    
    /**
     * Create a new {@link Histogram} that shows the given elements, 
     * distributed among the bins based on the numeric value that
     * is returned by the given key extractor.<br>
     * <br>
     * The returned histogram will have a control component for changing
     * the number of bins. The initial number of bins is not specified,
     * but will be "reasonable" for many application cases.
     * 
     * @param <T> The element type
     * @param <K> The key type
     * 
     * @param elements The elements
     * @param keyExtractor The key extractor
     * @return The {@link Histogram}
     */
    public static <T, K extends Number> NumberHistogram<T> 
        createForDate(Collection<? extends T> elements, 
            Function<? super T, ? extends K> keyExtractor)
    {
        return createForDate(elements, keyExtractor, 
        		"yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    /**
     * Create a new {@link Histogram} that shows the given elements, 
     * distributed among the bins based on the numeric value that
     * is returned by the given key extractor.<br>
     * <br>
     * The returned histogram will have a control component for changing
     * the number of bins. The initial number of bins is not specified,
     * but will be "reasonable" for many application cases.
     * 
     * @param <T> The element type
     * @param <K> The key type
     * 
     * @param elements The elements
     * @param keyExtractor The key extractor
     * @param pattern The pattern for the date-time formatter
     * @return The {@link Histogram}
     */
    public static <T, K extends Number> NumberHistogram<T> 
        createForDate(Collection<? extends T> elements, 
            Function<? super T, ? extends K> keyExtractor,
            String pattern)
    {
        return createNumeric(elements, null, null, keyExtractor, 
            dateBinLabelFunctionProvider(pattern));
    }

    /**
     * Create a new {@link Histogram} that shows the given elements, 
     * distributed among the bins based on the numeric value that
     * is returned by the given key extractor.<br>
     * <br>
     * The returned histogram will have a control component for changing
     * the number of bins. The initial number of bins is not specified,
     * but will be "reasonable" for many application cases.
     * 
     * @param <T> The element type
     * @param <K> The key type
     * 
     * @param elements The elements
     * @param min The optional minimum value for the binning
     * @param max The optional maximum value for the binning
     * @param keyExtractor The key extractor
     * @param pattern The pattern for the date-time formatter
     * @return The {@link Histogram}
     */
    public static <T, K extends Number> NumberHistogram<T> 
        createForDate(Collection<? extends T> elements, 
        	T min, T max, Function<? super T, ? extends K> keyExtractor,
            String pattern)
    {
        return createNumeric(elements, min, max, keyExtractor, 
            dateBinLabelFunctionProvider(pattern));
    }
    

    /**
     * Create a new {@link Histogram} that shows the given elements, 
     * distributed among the bins based on the numeric value that
     * is returned by the given key extractor.<br>
     * <br>
     * The returned histogram will have a control component for changing
     * the number of bins. The initial number of bins is not specified,
     * but will be "reasonable" for many application cases.
     * 
     * @param <T> The element type
     * @param <K> The key type
     * 
     * @param elements The elements
     * @param keyExtractor The key extractor
     * @return The {@link Histogram}
     */
    public static <T, K extends Number> NumberHistogram<T> 
        createNumeric(Collection<? extends T> elements, 
            Function<? super T, ? extends K> keyExtractor)
    {
        return createNumeric(elements, null, null, keyExtractor, 
            defaultBinLabelFunctionProvider());
    }
    
    /**
     * Create a new {@link Histogram} that shows the given elements, 
     * distributed among the bins based on the numeric value that
     * is returned by the given key extractor.<br>
     * <br>
     * The returned histogram will have a control component for changing
     * the number of bins. The initial number of bins is not specified,
     * but will be "reasonable" for many application cases.
     * 
     * @param <T> The element type
     * @param <K> The key type
     * 
     * @param elements The elements
     * @param min The optional minimum value for the binning
     * @param max The optional maximum value for the binning
     * @param keyExtractor The key extractor
     * @param binLabelFunctionProvider the function that, for a given 
     * {@link NumberBinning}, returns the function that provides 
     * the bin labels
     * @return The {@link Histogram}
     */
    private static <T, K extends Number> NumberHistogram<T> 
        createNumeric(Collection<? extends T> elements,
            T min, T max, Function<? super T, ? extends K> keyExtractor,
            Function<? super NumberBinning<?>, ? extends IntFunction<String>> 
                binLabelFunctionProvider)
    {
        Objects.requireNonNull(
            elements, "The elements may not be null");
        Objects.requireNonNull(
            keyExtractor, "The keyExtractor may not be null");
        Objects.requireNonNull(
            binLabelFunctionProvider, 
            "The binLabelFunctionProvider may not be null");
        
        int initialNumBins = computeNumBins(elements.size());
        
        IntFunction<NumberBinning<T>> binningProvider = binCount ->
            Binnings.createSimpleNumberBinning(
                elements, keyExtractor, binCount, min, max);
        NumberBinning<T> binning = binningProvider.apply(initialNumBins);
        IntFunction<String> binLabelFunction = 
            binLabelFunctionProvider.apply(binning);
        JFreeChartHistogram<T> histogram = 
            new JFreeChartHistogram<T>(binning, binLabelFunction);
        
        JPanel controlPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Bins:");
        label.setFont(label.getFont().deriveFont(10.f));
        controlPanel.add(label, BorderLayout.WEST);
        JSpinner spinner = new JSpinner(
            new SpinnerNumberModel(initialNumBins, 1, 100000, 1));
        controlPanel.add(spinner, BorderLayout.CENTER);
        spinner.setFont(spinner.getFont().deriveFont(10.f));

        // The bin count handling should be kept out of the 
        // JFreeChartHistogram implementation. But the spinner 
        // should still be updated when the bin count changes.
        // This solution is a bit odd, but not visible to the user:
        JFreeChartNumberHistogram<T> numberHistogram = 
            new JFreeChartNumberHistogram<T>(
                histogram, binningProvider, binLabelFunctionProvider)
        {
            @Override
            public void setBinCount(int binCount) 
            {
                super.setBinCount(binCount);
                spinner.setValue(binCount);
            }
        };
        
        spinner.addChangeListener(e -> 
        {
            Object value = spinner.getValue();
            Number number = (Number)value;
            int binCount = number.intValue();
            numberHistogram.setBinCount(binCount);
        });
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.add(controlPanel);
        histogram.addControlComponent(p);
        histogram.setElements(elements, null);
        return numberHistogram;
    }
    
    /**
     * Compute the number of bins for the given number of elements
     * 
     * @param n The number of elements
     * @return The number of bins
     */
    private static int computeNumBins(int n)
    {
    	// Sturges rule for the number of bins:
        int numBins = (int)(Math.ceil(Math.log(n) / Math.log(2)) + 1);
        return numBins;
    }
    

    /**
     * Returns the function that, for a given {@link NumberBinning}, returns
     * the function that provides the bin labels. The function assumes that 
     * the values in the given binning are milliseconds since the epoch,
     * and returns them as a date formatted with the given pattern
     * 
     * @param pattern The date-time formatter pattern
     * @return The bin label function provider
     */
    static Function<NumberBinning<?>, IntFunction<String>> 
        dateBinLabelFunctionProvider(String pattern)
    {
        return binning -> createDateBinLabelFunction(binning, pattern);
    }
    
    /**
     * Create the function that returns the label for each bin of the given
     * binning
     * 
     * @param binning The {@link NumberBinning}
     * @param pattern The pattern for the date-time formatter
     * @return The label function
     */
    private static IntFunction<String> createDateBinLabelFunction(
        NumberBinning<?> binning, String pattern)
    {
        DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern(pattern);
        return bin -> 
        {
            double min = binning.getBinMin(bin);
            double max = binning.getBinMax(bin);
            String minString = createDateBinLimitLabel(min, formatter);
            String maxString = createDateBinLimitLabel(max, formatter);
            return minString + "\n" + maxString;
        };
    }

    
    /**
     * Creates the label for the given bin limit value
     * 
     * @param value The bin limit value
     * @param formatter The formatter
     * @return The label
     */
    private static String createDateBinLimitLabel(
        double value, DateTimeFormatter formatter)
    {
        long millis = (long)value;
        Instant instant = Instant.ofEpochMilli(millis);
        OffsetDateTime offsetDateTime = 
            OffsetDateTime.ofInstant(instant, ZoneOffset.UTC); 
        return offsetDateTime.format(formatter);
    }
    
    /**
     * Returns the function that, for a given {@link NumberBinning}, returns
     * the function that provides the bin labels
     * 
     * @return The bin label function provider
     */
    static Function<NumberBinning<?>, IntFunction<String>> 
        defaultBinLabelFunctionProvider()
    {
        return binning -> createBinLabelFunction(binning);
    }
    
    /**
     * Create the function that returns the label for each bin of the given
     * binning
     * 
     * @param binning The {@link NumberBinning}
     * @return The label function
     */
    private static IntFunction<String> createBinLabelFunction(
        NumberBinning<?> binning)
    {
        double totalMin = binning.getBinMin(0);
        double totalMax = binning.getBinMax(binning.getBinCount() - 1);
        double difference = totalMax - totalMin;
        String formatString = formatStringFor(difference);
        return bin -> 
        {
            double min = binning.getBinMin(bin);
            double max = binning.getBinMax(bin);
            String minString = String.format(Locale.ENGLISH, formatString, min);
            String maxString = String.format(Locale.ENGLISH, formatString, max);
            return minString + "\n" + maxString;
        };
    }
    
    
    /**
     * Returns a format string that can be used in <code>String#format</code> 
     * to format values of the given order. The exact meaning of this is 
     * intentionally left unspecified, but for numbers that are "reasonable" 
     * to be displayed as decimal numbers (without scientific notation),
     * this function will return a format with the "appropriate" number of
     * decimal digits in order to format axis labels.  
     * 
     * @param order The order
     * @return The format string
     */
    static String formatStringFor(double order)
    {
        if (order < 1e-100 || !Double.isFinite(order))
        {
            return "%f";
        }
        double exponent = Math.floor(Math.log10(order));
        int digits = (int)Math.abs(exponent) + 1;
        if (order >= 1.0)
        {
            digits = 0;
        }
        String result = "%."+digits+"f";
        return result;
    }
    
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private Histograms()
    {
        // Private constructor to prevent instantiation
    }
    
}
