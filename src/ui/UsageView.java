/**
*Class:             UsageView.java
*Project:          	Usage Visualizer
*Author:            Jason Van Kerkhoven
*Date of Update:    26/10/2017
*Version:           1.0.0
*
*Purpose:           Displays graph information and other data analytics to user.
*					Can also act as an interface for user.
*					
* 
*Update Log			v1.0.0
*						- broken into tabbed view
*						- time of last updated added for linegraph view
*					v0.1.0
*						- proof of concept for demo	
*						- display line graph
*						- room added for text display/console
*/
package ui;



//import external libraries
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class UsageView extends JFrame
{
	//declaring class constants
	private static final Font FONT_NORMAL = new Font("DialogInput", Font.BOLD, 18);;
	
	//declaring instance constants
	public final String WINDOW_TITLE;
	
	//declaring local instance variables
	private JPanel contentPane;
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private JTabbedPane tabs;
	private JTextField lineGraphTxt;


	//generic constructor
	public UsageView(boolean fullscreen, String title) 
	{
		//configure frame
		this.WINDOW_TITLE = title;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 1000, 600);
		this.contentPane = new JPanel();
		this.setContentPane(contentPane);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		this.setTitle(WINDOW_TITLE);
		
		//setup tabbed view
		tabs = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabs, BorderLayout.CENTER);
		
		
		//add and configure panel for chart	
		chartPanel = new ChartPanel(chart);
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setRangeZoomable(false);
		
		JPanel lineGraphPanel = new JPanel();
		lineGraphPanel.setLayout(new BorderLayout(0, 0));
		lineGraphPanel.add(chartPanel, BorderLayout.CENTER);
		
		lineGraphTxt = new JTextField( );
		lineGraphTxt.setEditable(false);
		lineGraphTxt.setBorder(null);
		lineGraphTxt.setFont(FONT_NORMAL);
		lineGraphTxt.setBackground(Color.WHITE);
		lineGraphTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		lineGraphPanel.add(lineGraphTxt, BorderLayout.NORTH);
		
		tabs.addTab("Line Graph", null, lineGraphPanel, null);
	}

	
	//update the displayed graph
	public void updateLineChart(XYSeriesCollection seriesSet, String title, String xLabel, String yLabel)
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
		
		//update text
		Calendar c = Calendar.getInstance();
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
		lineGraphTxt.setText("Updated at: " + tf.format(c.getTime()) + " ");
		
		//set axis to display ms from epoch as human-readable date-times
		chart.getXYPlot().setDomainAxis(new DateAxis(xLabel));
	}	
}
