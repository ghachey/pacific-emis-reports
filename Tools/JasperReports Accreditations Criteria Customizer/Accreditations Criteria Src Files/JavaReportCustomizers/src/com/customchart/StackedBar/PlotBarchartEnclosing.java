package com.customchart.StackedBar;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;

/**
 * @author Helical Admin
 * this class is utilized to 
 * 			1. increase the category label width so that label can not be truncated
 */
public class PlotBarchartEnclosing implements JRChartCustomizer {
	public void customize(JFreeChart chart, JRChart jasperChart) {
		Log log = LogFactory.getLog(PlotBarchartEnclosing.class);

		final CategoryPlot plot = (CategoryPlot)chart.getPlot();
		
		// <Helical> to get the category axis so that category label width can be increased
		final CategoryAxis categoryAxis = plot.getDomainAxis();
		
		//<Helical> to increase the category label displayed .earlier it was truncating 
		categoryAxis.setMaximumCategoryLabelLines(3);
		categoryAxis.setMaximumCategoryLabelWidthRatio(3f);
		
	}
}