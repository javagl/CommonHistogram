package de.javagl.common.histogram.test;

import java.awt.GridLayout;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.javagl.common.histogram.Histogram;
import de.javagl.common.histogram.HistogramMouseEvent;
import de.javagl.common.histogram.HistogramMouseListener;
import de.javagl.common.histogram.Histograms;
import de.javagl.common.histogram.NumberHistogram;

/**
 * Basic integration test for the {@link Histograms} class
 */
@SuppressWarnings("javadoc")
public class HistogramTest
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    private static void createAndShowGui()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new GridLayout(3, 0));
        
        Histogram<Double> numericHistogram = 
            createNumericHistogram();
        f.getContentPane().add(numericHistogram.getComponent());

        Histogram<Double> largeNumericHistogram = 
            createLargeNumericHistogram();
        f.getContentPane().add(largeNumericHistogram.getComponent());
        
        Histogram<String> categoricalHistogram =
            createCategoricalHistogram();
        f.getContentPane().add(categoricalHistogram.getComponent());
        
        Histogram<Person> personHistogram =
            createPersonHistogram();
        f.getContentPane().add(personHistogram.getComponent());
        
        Histogram<Double> mediumRangeNumericHistogram = 
            createMediumRangeNumericHistogram();
        f.getContentPane().add(mediumRangeNumericHistogram.getComponent());

        Histogram<Double> smallRangeNumericHistogram = 
            createSmallRangeNumericHistogram();
        f.getContentPane().add(smallRangeNumericHistogram.getComponent());

        Histogram<Double> constantNumericHistogram = 
            createConstantNumericHistogram();
        f.getContentPane().add(constantNumericHistogram.getComponent());

        Histogram<OffsetDateTime> dateHistogram = 
            createDateHistogram();
        f.getContentPane().add(dateHistogram.getComponent());
        
        Histogram<OffsetDateTime> customDateHistogram = 
            createCustomDateHistogram();
        f.getContentPane().add(customDateHistogram.getComponent());
        
        
        f.setSize(1700, 800);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    
    private static Histogram<Person> createPersonHistogram()
    {
        Random random = new Random(0);
        List<Person> elements = new ArrayList<Person>();
        for (int i=0; i<200; i++)
        {
            int age = random.nextInt(100);
            Person person = new Person("Person"+i, age);
            elements.add(person);
        }
        
        Histogram<Person> histogram = 
            Histograms.createNumeric(elements, Person::getAge);
        Collection<Person> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }

    private static Histogram<String> createCategoricalHistogram()
    {
        List<String> elements = Arrays.asList(
            "A", "A", "A", "A", "A", "B", "B", "B", "B", "B", "B", 
            "C", "C", "C", "C", "C", "C", "C", "C", "D", "D", "D");
        List<String> highlightedElements = Arrays.asList(
            "A", "A", "A", "B", "B", "B", "B", "C", "C", "D", "D");
        Histogram<String> histogram = Histograms.create(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }

    private static Histogram<Double> createNumericHistogram()
    {
        List<Double> elements = createNumericElements(1000);
        Histogram<Double> histogram = Histograms.createNumeric(elements);
        Collection<Double> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }
    
    private static Histogram<Double> createLargeNumericHistogram()
    {
        List<Double> elements = createNumericElements(1000000);
        Histogram<Double> histogram = Histograms.createNumeric(elements);
        Collection<Double> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }
    
    private static Histogram<Double> createMediumRangeNumericHistogram()
    {
        List<Double> elements = new ArrayList<Double>();
        elements.add(5.1);
        elements.add(5.2);
        elements.add(5.3);
        elements.add(6.4);
        elements.add(6.5);
        elements.add(6.6);
        elements.add(7.7);
        elements.add(7.8);
        elements.add(7.9);
        Histogram<Double> histogram = Histograms.createNumeric(elements);
        Collection<Double> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }
    
    private static Histogram<Double> createSmallRangeNumericHistogram()
    {
        List<Double> elements = new ArrayList<Double>();
        elements.add(100.001);
        elements.add(100.002);
        elements.add(100.003);
        elements.add(100.004);
        elements.add(100.005);
        elements.add(100.006);
        elements.add(100.007);
        elements.add(100.008);
        elements.add(100.009);
        Histogram<Double> histogram = Histograms.createNumeric(elements);
        Collection<Double> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }
    
    
    private static Histogram<Double> createConstantNumericHistogram()
    {
        List<Double> elements = Collections.nCopies(100, 100.0);
        Histogram<Double> histogram = Histograms.createNumeric(elements);
        Collection<Double> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        addLogging(histogram);
        return histogram;
    }
    
    private static Histogram<OffsetDateTime> createDateHistogram()
    {
        List<OffsetDateTime> elements = new ArrayList<OffsetDateTime>();
        Random random = new Random(0);
        OffsetDateTime min = OffsetDateTime.of(
            2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        for (int i = 0; i < 1000; i++)
        {
            long minutes = random.nextInt(600);
            elements.add(min.plusMinutes(minutes));
        }
        
        Function<OffsetDateTime, Double> keyExtractor = offsetDateTime -> 
        {
            ZonedDateTime zonedDateTime = offsetDateTime.toZonedDateTime();
            Instant instant = zonedDateTime.toInstant();
            return (double)instant.toEpochMilli();
        };
        NumberHistogram<OffsetDateTime> histogram = 
            Histograms.createForDate(elements, keyExtractor);
        Collection<OffsetDateTime> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        histogram.setBinCount(3);
        
        addLogging(histogram);
        return histogram;
    }
    
    private static Histogram<OffsetDateTime> createCustomDateHistogram()
    {
        List<OffsetDateTime> elements = new ArrayList<OffsetDateTime>();
        Random random = new Random(0);
        OffsetDateTime min = OffsetDateTime.of(
                2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime max = min.plusMinutes(600);
        for (int i = 0; i < 1000; i++)
        {
            long minutes = random.nextInt(600);
            elements.add(min.plusMinutes(minutes));
        }
        
        Function<OffsetDateTime, Double> keyExtractor = offsetDateTime -> 
        {
            ZonedDateTime zonedDateTime = offsetDateTime.toZonedDateTime();
            Instant instant = zonedDateTime.toInstant();
            return (double)instant.toEpochMilli();
        };
        NumberHistogram<OffsetDateTime> histogram = 
            Histograms.createForDate(
                elements, min, max, keyExtractor, "HH:mm");
        Collection<OffsetDateTime> highlightedElements = selectSome(elements);
        histogram.setElements(elements, highlightedElements);
        histogram.setBinCount(5);
        
        addLogging(histogram);
        return histogram;
    }
    
    
    private static <T> void addLogging(Histogram<T> histogram)
    {
        histogram.addHistogramMouseListener(new HistogramMouseListener<T>()
        {
            @Override
            public void clicked(HistogramMouseEvent<T> histogramMouseEvent)
            {
                System.out.println("Bin: " + histogramMouseEvent.getBin());
                System.out.println(
                    "Highlighted: " + histogramMouseEvent.isHighlighted());
                System.out.println(
                    "Elements: " + histogramMouseEvent.getBinElements());
                System.out.println("Highlighted Elements: "
                    + histogramMouseEvent.getHighlightedBinElements());
            }
        });
    }
    
    

    private static List<Double> createNumericElements(int n)
    {
        List<Double> elements = new ArrayList<Double>();
        Random random = new Random(0);
        double min = -10;
        double max = 20;
        for (int i = 0; i < n; i++)
        {
            double d = random.nextGaussian();
            double element = min + d * (max - min);
            elements.add(element);
        }
        return elements;
    }
    
    private static <T> List<T> selectSome(Iterable<? extends T> elements)
    {
        Random random = new Random(0);
        List<T> result = new ArrayList<T>();
        for (T t : elements)
        {
            if (random.nextDouble() < 0.1)
            {
                result.add(t);
            }
        }
        return result;
    }
    
    static class Person 
    {
        String name;
        int age;
        Person(String name, int age) 
        {
            this.name = name;
            this.age = age;
        }
        int getAge()
        {
            return age;
        }
        
        @Override
        public String toString()
        {
            return name;
        }
    }
    
}
