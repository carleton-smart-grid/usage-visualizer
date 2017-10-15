package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class UsageView extends JFrame
{
	//declaring local instance variables
	private JPanel contentPane;
	private JTextArea infoText;
	private JFreeChart chart;
	private ChartPanel chartPanel;


	//generic constructor
	public UsageView(boolean fullscreen) 
	{
		//configure frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 1000, 600);
		contentPane = new JPanel();
		this.setContentPane(contentPane);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		/* TEST CONFIG FOR CHART */
		XYSeries s1 = new XYSeries("series");
		XYSeries s2 = new XYSeries("series 2");
		for (float i=0; i<=10; i++)
		{
			s1.add(i, i);
			s2.add(i, 0.25*i);
		}
		XYSeriesCollection x = new XYSeriesCollection();
		x.addSeries(s1);
		x.addSeries(s2);
		chart = ChartFactory.createXYLineChart(
		         "Test Chart", 
		         "LABEL 1",
		         "LABEL 2", 
		         x,
		         PlotOrientation.VERTICAL, 
		         true, true, false);
		/* TEST CONFIG FOR CHART */
		
		//add and configure panel for chart
		chartPanel = new ChartPanel(chart);
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setRangeZoomable(false);
        contentPane.add(chartPanel, BorderLayout.CENTER);
		
		//add panels for other info
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout(0, 0));
		contentPane.add(infoPanel, BorderLayout.EAST);
		
		//add scrolling text area for misc info display
		JScrollPane scrollPane = new JScrollPane();
		infoText = new JTextArea();
		infoText.setEditable(false);
		scrollPane.setPreferredSize(new Dimension(300,0));
		scrollPane.setViewportView(infoText);
		infoPanel.add(scrollPane, BorderLayout.CENTER);
	}
	
	
	//update the info text
	public void setInfoText(String txt)
	{
		infoText.setText("\n  " + txt.replace("\n", "\n  "));
	}
	
	
	//update the displayed graph
	public void displayDateLineChart(XYSeriesCollection seriesSet, String title, String xLabel, String yLabel)
	{
		//create and add new chart
		chart = ChartFactory.createXYLineChart(
		         title, 
		         xLabel,
		         yLabel, 
		         seriesSet,
		         PlotOrientation.VERTICAL, 
		         true, true, false);
		chartPanel.setChart(chart);
		chartPanel.setRangeZoomable(false);
		
		//set axis to display ms from epoch as human-readable date-times
		chart.getXYPlot().setDomainAxis(new DateAxis(xLabel));
	}	
}
