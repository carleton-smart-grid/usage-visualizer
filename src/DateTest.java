
import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class DateTest extends ApplicationFrame {

    private static final long serialVersionUID = 1L;

    public DateTest(String title) {
        super(title);
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                "StackOverflow26556268", "Date", "Time", dataset);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        DateAxis xAxis = new DateAxis("Date");
        xAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(new DateAxis("Time"));
        return chart;
    }

    private static XYDataset createDataset() {
        XYSeries s1 = new XYSeries("S1");
        s1.add(new Day(1, 10, 2014).getMiddleMillisecond(), 720000);
        s1.add(new Day(1, 10, 2014).getMiddleMillisecond(), 820000);
        s1.add(new Day(2, 10, 2014).getMiddleMillisecond(), 1020000);
        s1.add(new Day(2, 10, 2014).getMiddleMillisecond(), 920000);
        s1.add(new Day(3, 10, 2014).getMiddleMillisecond(), 1220000);
        s1.add(new Day(3, 10, 2014).getMiddleMillisecond(), 1320000);
        s1.add(new Day(4, 10, 2014).getMiddleMillisecond(), 1620000);
        s1.add(new Day(4, 10, 2014).getMiddleMillisecond(), 1520000);
        s1.add(new Day(5, 10, 2014).getMiddleMillisecond(), 1320000);
        s1.add(new Day(5, 10, 2014).getMiddleMillisecond(), 1820000);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s1);
        return dataset;
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    public static void main(String[] args) {
        DateTest demo = new DateTest(
                "StackOverflow26556268/Example");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}