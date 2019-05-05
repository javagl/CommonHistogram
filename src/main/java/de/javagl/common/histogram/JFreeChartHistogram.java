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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextMeasurer;
import org.jfree.ui.RectangleEdge;

/**
 * Implementation of a {@link Histogram} based on JFreeChart
 * 
 * @param <T> The type of the elements
 */
class JFreeChartHistogram<T> extends JPanel implements Histogram<T>
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The binning that is used for this histogram
     */
    private Binning<T> binning;
    
    /**
     * The label that will be called with the bin indices in order to 
     * determine the label for the respective bin
     */
    private IntFunction<String> binLabelFunction;
    
    /**
     * The s that are shown in this histogram
     */
    private List<T> elements;
    
    /**
     * The highlighted elements. Should always be a subset of the elements.
     */
    private List<T> highlightedElements;

    /**
     * The JFreeChart data set
     */
    private DefaultCategoryDataset dataset;
    
    /**
     * The list of registered {@link HistogramMouseListener} instances
     */
    private final List<HistogramMouseListener<T>> histogramMouseListeners;
    
    /**
     * The JFreeChart mouse listener that will translate JFreeChart mouse
     * events into {@link HistogramMouseEvent} instances
     */
    private final ChartMouseListener chartMouseListener = 
        new ChartMouseListener()
    {
        @Override
        public void chartMouseMoved(ChartMouseEvent chartMouseEvent)
        {
            // Nothing to do here
        }
        
        @Override
        public void chartMouseClicked(ChartMouseEvent chartMouseEvent)
        {
            handleChartMouseClicked(chartMouseEvent);
        }
    };    
    
    /**
     * Creates a new instance
     * 
     * @param binning The binning that is used for this histogram
     * @param binLabelFunction The function that will be called for each
     * bin index in order to determine the label that should be shown for
     * the respective bin
     */
    JFreeChartHistogram(
        Binning<T> binning, IntFunction<String> binLabelFunction)
    {
        this.binning = Objects.requireNonNull(
            binning, "The binning may not be null");
        this.binLabelFunction = Objects.requireNonNull(
            binLabelFunction, "The binLabelFunction may not be null");
        this.elements = Collections.emptyList();
        this.highlightedElements = Collections.emptyList();
        this.histogramMouseListeners = 
            new CopyOnWriteArrayList<HistogramMouseListener<T>>();
        this.dataset = new DefaultCategoryDataset();

        setLayout(new BorderLayout());

        ChartPanel chartPanel = createChartPanel();
        chartPanel.addChartMouseListener(chartMouseListener);
        add(chartPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create the main JFreeChart chart panel
     * 
     * @return The chart panel
     */
    private ChartPanel createChartPanel()
    {
        // Create the JFreeChart chart
        boolean legend = false;
        boolean tooltips = false;
        boolean urls = false;
        String title = null;
        String domainAxisLabel = null;
        String rangeAxisLabel = null;
        JFreeChart chart =
            ChartFactory.createStackedBarChart(title, 
                domainAxisLabel, rangeAxisLabel, dataset,
                PlotOrientation.VERTICAL, legend, tooltips, urls);
        chart.setBackgroundPaint(Color.WHITE);
        
        // Set basic colors
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Set the bar color and (flat, non-gradient) painter
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
        barRenderer.setSeriesPaint(0, new Color(128,255,128));
        barRenderer.setSeriesPaint(1, new Color(128,128,255));
        barRenderer.setBarPainter(new StandardBarPainter());
        CategoryToolTipGenerator generator = new CategoryToolTipGenerator()
        {
            @Override
            public String generateToolTip(
                CategoryDataset dataset, int row, int col)
            {
                Comparable<?> columnKey = dataset.getColumnKey(col);
                Integer binIndex = (Integer)columnKey;
                String label = binLabelFunction.apply(binIndex);
                Number highlightedCount = dataset.getValue(0, col);
                Number totalMinusHighlightedCount = dataset.getValue(1, col);
                int h = highlightedCount.intValue();
                int t = totalMinusHighlightedCount.intValue() + h;
                String result = "<html>" 
                    + label.replaceAll("\n", "<br>") 
                    + "<br>"
                    + h + "/" + t 
                    + "</html>";
                return result;
            }
        };
        barRenderer.setBaseToolTipGenerator(generator);

        // Only show integer ticks on the y-axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        // Create the domain/category (x-) axis, which contains labels that
        // are obtained from the binLabelFunction
        CategoryAxis domainCategoryAxis = new CategoryAxis()
        {
            /**
             * Serial UID
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected TextBlock createLabel(
                @SuppressWarnings("rawtypes") Comparable category, float 
                width, RectangleEdge edge, Graphics2D g2) 
            {
                Integer binIndex = (Integer)category;
                String string = binLabelFunction.apply(binIndex);
                TextBlock label = createTextBlock(string,
                    getTickLabelFont(category), getTickLabelPaint(category), 
                    width, new G2TextMeasurer(g2));
                return label;
            }
        };
        domainCategoryAxis.setMaximumCategoryLabelLines(2);
        plot.setDomainAxis(domainCategoryAxis);
        
        // Avoid empty spaces. What are we living for?
        domainCategoryAxis.setLowerMargin(0.01);
        domainCategoryAxis.setUpperMargin(0.01);
        domainCategoryAxis.setCategoryMargin(0.01);
        rangeAxis.setLowerMargin(0.01);
        rangeAxis.setUpperMargin(0.01);
        
        // Set some small font for the axis tick labels
        Font font = new Font("Dialog", Font.PLAIN, 10);
        domainCategoryAxis.setTickLabelFont(font);
        rangeAxis.setTickLabelFont(font);        
        
        ChartPanel chartPanel = new ChartPanel(chart);
        
        // Make sure the font isn't stretched
        chartPanel.setMaximumDrawHeight(2000);
        chartPanel.setMinimumDrawHeight(100);
        chartPanel.setMaximumDrawWidth(2000);
        chartPanel.setMinimumDrawWidth(100); 
        
        return chartPanel;
    }
    
    /**
     * Create a text block from the given text. This is a specialized
     * variant of the JFreeChart TextUtilites method. It splits the
     * given text into lines at the line separator character, and
     * returns a text block that contains the longest <b>suffix</b>
     * of each line that fits into the given width.
     * 
     * @param text The text
     * @param font The font
     * @param paint The paint
     * @param maxWidth The maximum width
     * @param measurer The text measurer
     * @return The text block
     */
    private static TextBlock createTextBlock(String text, Font font,
           Paint paint, float maxWidth, TextMeasurer measurer) 
    {
        String[] lines = text.split("\n");
        TextBlock result = new TextBlock();
        for (String line : lines)
        {
            int start = 0;
            while (true)
            {
                float w = measurer.getStringWidth(line, start, line.length());
                if (w < maxWidth)
                {
                    break;
                }
                start++;
            }
            if (start > 0)
            {
                int min = Math.min(start + 3, line.length());
                String part = "..." + line.substring(min, line.length());
                result.addLine(part, font, paint);
            }
            else
            {
                result.addLine(line, font, paint);
            }
        }
        return result;
    }


    /**
     * Add the given control component to this component. Only for internal use.
     * 
     * @param component The control component
     */
    void addControlComponent(JComponent component)
    {
        add(component, BorderLayout.NORTH);
    }
    

    /**
     * Called by the {@link #chartMouseListener} when a mouse click occurred
     * in the JFreeChart chart. Will extract the relevant information from
     * the given event and pass it to {@link #fireClicked}.
     * 
     * @param chartMouseEvent The chart mouse event.
     */
    private void handleChartMouseClicked(ChartMouseEvent chartMouseEvent)
    {
        ChartEntity entity = chartMouseEvent.getEntity();
        if (entity instanceof CategoryItemEntity)
        {
            CategoryItemEntity categoryItemEntity = (CategoryItemEntity) entity;
            Object columnKey = categoryItemEntity.getColumnKey();
            Integer bin = (Integer)columnKey;
            int rowIndex = dataset.getRowIndex(categoryItemEntity.getRowKey());
            if (bin != null)
            {
                fireClicked(chartMouseEvent.getTrigger(), bin, rowIndex == 0);
            }
        }
    }                
    
    /**
     * Fire a {@link HistogramMouseEvent} to all registered listeners
     * 
     * @param mouseEvent The mouse event that caused the event
     * @param bin The index of the clicked bin
     * @param highlighted Whether the highlighted part of the bin was clicked
     */
    private void fireClicked(
        MouseEvent mouseEvent, int bin, boolean highlighted)
    {
        if (!histogramMouseListeners.isEmpty())
        {
            List<T> clickedElements = computeElementsInBin(bin, elements);
            List<T> clickedHighlightedElements = computeElementsInBin(
                bin, highlightedElements);
            HistogramMouseEvent<T> histogramMouseEvent = 
                new HistogramMouseEvent<T>(this, mouseEvent, bin, highlighted, 
                    Collections.unmodifiableList(clickedElements), 
                    Collections.unmodifiableList(clickedHighlightedElements));
            
            for (HistogramMouseListener<T> listener : histogramMouseListeners)
            { 
                listener.clicked(histogramMouseEvent);
            }
        }
    }
    
    /**
     * Compute the list of elements in the given sequence that are contained
     * in the bin with the given index
     * 
     * @param bin The bin index
     * @param elements The input elements 
     * @return The elements in the given bin
     */
    private List<T> computeElementsInBin(
        int bin, Iterable<? extends T> elements)
    {
        List<T> elementsInBin = new ArrayList<T>();
        for (T element : elements)
        {
            int elementBin = binning.computeBin(element);
            if (bin == elementBin)
            {
                elementsInBin.add(element);
            }
        }
        return elementsInBin;
    }
    
    /**
     * Set the {@link Binning} that should be used for this histogram
     * 
     * @param binning The {@link Binning}
     * @param binLabelFunction The bin label function
     */
    void setBinning(Binning<T> binning, IntFunction<String> binLabelFunction)
    {
        this.binning = Objects.requireNonNull(
            binning, "The binning may not be null");
        this.binLabelFunction = Objects.requireNonNull(
            binLabelFunction, "The binLabelFunction may not be null");
        performUpdate();
    }
    
    @Override
    public JComponent getComponent()
    {
        return this;
    }
    
    @Override
    public void setElements(
        Collection<? extends T> elements, 
        Collection<? extends T> highlightedElements)
    {
        if (elements == null)
        {
            this.elements = Collections.emptyList();
        }
        else
        {
            this.elements = new ArrayList<T>(elements);
        }
        if (highlightedElements == null)
        {
            this.highlightedElements = Collections.emptyList();
        }
        else
        {
            this.highlightedElements = 
                new ArrayList<T>(highlightedElements);
        }
        performUpdate();
    }

    /**
     * Update the JFreeChart chart based on the current binning and values
     */
    private void performUpdate()
    {
        dataset.clear();
        
        boolean ignoreInvalid = true;
        int bins[] = binning.compute(elements, ignoreInvalid);
        int highlightedBins[] = binning.compute(
            highlightedElements, ignoreInvalid);
        
        for (int i = 0; i < binning.getBinCount(); i++)
        {
            dataset.addValue(
                highlightedBins[i], "highlightedElements", Integer.valueOf(i));
        }
        for (int i = 0; i < binning.getBinCount(); i++)
        {
            dataset.addValue(
                bins[i] - highlightedBins[i], "elements", Integer.valueOf(i));
        }
        
    }
    
    
    @Override
    public void addHistogramMouseListener(
        HistogramMouseListener<T> histogramMouseListener)
    {
        histogramMouseListeners.add(histogramMouseListener);
    }
    
    @Override
    public void removeHistogramMouseListener(
        HistogramMouseListener<T> histogramMouseListener)
    {
        histogramMouseListeners.remove(histogramMouseListener);
    }
    
}
