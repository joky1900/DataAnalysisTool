package benchmarking_extension.graph;

import benchmarking_extension.Controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * Concrete class implementing a line graph
 *
 * @author John Kyrk
 * @version 1.0
 * @since 2023-04-23
 */
public class LineGraph extends Graph{
    protected double[][] data;
    protected double[][] data2;
    private boolean average = false;

    /**
     * Constructor for x or y-axis
     * @param title Title of the graph
     * @param xAxisLabel label
     * @param yAxisLabel label
     * @param orientation vertical or horizontal
     * @param data ball position array
     * @param data2 gaze position array
     */
    public LineGraph(String title, String xAxisLabel, String yAxisLabel, PlotOrientation orientation, double[][] data, double[][] data2) {
        super(title, xAxisLabel, yAxisLabel, orientation);
        this.data = data;
        this.data2 = data2;

        if(data.length != 0) {
            System.out.println("LAST DATA: " + data[data.length - 1][0] + " | " + data[data.length - 1][1]);
        }
        setupGUI();
        chart.getPlot().setBackgroundPaint(new Color(94, 171, 94));
    }

    /**
     * Constructor for average distance between pixels
     * @param title Title of the graph
     * @param xAxisLabel label
     * @param yAxisLabel label
     * @param orientation vertical or horizontal
     * @param data array of average distance
     */
    public LineGraph(String title, String xAxisLabel, String yAxisLabel, PlotOrientation orientation, double[][] data) {
        super(title, xAxisLabel, yAxisLabel, orientation);
        this.data = data;
        System.out.println("LAST DATA: " + data[data.length - 1][0] + " | " + data[data.length - 1][1]);
        average = true;
        setupGUI();
        chart.getPlot().setBackgroundPaint(new Color(94, 171, 94));
    }

    /**
     * Creates the dataset used by {@link JFreeChart}
     * @return {@link XYDataset}
     */
    private XYDataset createDataset() {
        var series = new XYSeries("Benchmark");
        var dataset = new XYSeriesCollection();

        for(double[] line : data){
            series.add(line[0], line[1]);
        }

        dataset.addSeries(series);

        if(!average){
            var series2 = new XYSeries("Ball");
            for(double[] line: data2){
                series2.add(line[0], line[1]);
            }
            dataset.addSeries(series2);
        }

        return dataset;
    }

    /**
     * Sets up the graphical elements
     */
    protected void setupGUI(){
        XYDataset dataset = createDataset();
        chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);
    }

    /**
     * Calculates the average value of the data array
     * @return Double indicating the average value
     */
    private double getAverage(){
        double total = 0;

        for(double[] i : data){
            total += i[1];
        }

        System.out.println(total/data.length);
        return total / data.length;
    }

    private double getMin(){
        double min = data[0][1];

        for(double[] i : data){
            if(i[1] < min){
                min = i[1];
            }
        }

        return min;
    }

    private double getMax(){
        double max = data[0][1];

        for(double[] i : data){
            System.out.println("MAX: " + i[1]);
            if(i[1] > max){
                max = i[1];
            }
        }

        return max;
    }

    private double getStandardDeviation(){
        double standardDeviation = 0;
        double average = getAverage();

        for (double[] datum : data) {
            standardDeviation += Math.pow(datum[1] - average, 2);
        }

        return Math.sqrt(standardDeviation / data.length);
    }


    /**
     * Creates the {@link JFreeChart} object
     * @param dataset data to be used in the chart
     * @return {@link JFreeChart}
     */
    private JFreeChart createChart(final XYDataset dataset) {

       // final XYDataset dataset2 = MovingAverage.createMovingAverage(dataset, "- Moving Average", 3 * 24 * 60 * 60 * 1000L, 0L);

        XYLineAndShapeRenderer renderer = new XYSplineRenderer();

        // Renderer for the benchmark
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShapesVisible(0, false);

        // Renderer for the moving average
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        renderer2.setSeriesPaint(0, Color.RED);
        renderer2.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer2.setSeriesShapesVisible(0, false);

        // Renderer for the ball
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(1, false);

        chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        plot.setRenderer(renderer);
        plot.setRenderer(0, renderer);

        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        // Moving average for average distance graph
        if(average){
            XYDataset movingAverage = MovingAverage.createMovingAverage(dataset, " Moving Average", 3 * 24 * 60 * 60 * 1000L, 0L);
            plot.setDataset(1, movingAverage);
            plot.setRenderer(1, renderer2);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        }

        System.out.println("Avg: " + getAverage() + " | SD: " + getStandardDeviation());

        String title = data2 == null ? "Euclidean distance between benchmark and ball coordinates over time \n"
                + "Avg: " + String.format("%.2f", getAverage()) + "| SD: "
                + String.format("%.2f", getStandardDeviation())
                + " | Min: " + getMin() + " | Max: " + getMax()
                : Controller.getSet()
                + "-coordinate over time";
        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(title,
                        new Font("Serif", java.awt.Font.BOLD, 18)));

        return chart;
    }
}
