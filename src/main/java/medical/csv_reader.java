package medical;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by manish on 3/30/15.
 */
public class csv_reader {
    public static void main(String[] args) throws IOException {
        Reader in = new FileReader("Table3_PAN.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        Iterator<CSVRecord> recordIterator = records.iterator();
        if (!recordIterator.hasNext()) return;
        Map<String, Integer> headersMap = new HashMap<String, Integer>();
        int header_index = 0;
        for (String header : recordIterator.next()) {
            System.out.print(header + ", ");
            headersMap.put(header, header_index++);
        }
        System.out.println();

        while (recordIterator.hasNext()) {
            CSVRecord record = recordIterator.next();
            String time = record.get(headersMap.get("Date of onset"));
            try {
                DateTime dateTime = DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime(time);
                System.out.println(time);
                System.out.println(dateTime.plusDays(1));
                break;
            } catch (IllegalArgumentException iae) {
                continue;
            }
        }

        final XYSeries firefox = new XYSeries( "Firefox" );
        firefox.add( 1.0 , 1.0 );
        firefox.add( 2.0 , 4.0 );
        firefox.add( 3.0 , 3.0 );
        final XYSeries chrome = new XYSeries( "Chrome" );
        chrome.add( 1.0 , 4.0 );
        chrome.add( 2.0 , 5.0 );
        chrome.add( 3.0 , 6.0 );
        final XYSeries iexplorer = new XYSeries( "InternetExplorer" );
        iexplorer.add( 3.0 , 4.0 );
        iexplorer.add( 4.0 , 5.0 );
        iexplorer.add(5.0, 4.0);

        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("Values 1",new double[][]{{1, 2, 3}, {2, 4, 1}});
        dataset.addSeries("Values 2",new double[][]{{4, 5, 6}, {0, 3, 2}});
        dataset.addSeries("Values 3",new double[][]{{1.5, 3.5, 5.5}, {0.5, 3.5, 2.5}});
        ValueAxis xAxis = new NumberAxis("x");
        ValueAxis yAxis = new SymbolAxis("Symbol", new String[]{"One","Two","Three","Four","Five"});
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart("Symbol Axis Demo", new Font("Tahoma", 0, 18), plot, true);
        JFrame frame = new JFrame("XY Plot Demo");
        frame.setContentPane(new ChartPanel(chart));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

//        final XYSeriesCollection dataset = new XYSeriesCollection( );
//        dataset.addSeries( firefox );
//        dataset.addSeries( chrome );
//        dataset.addSeries(iexplorer);
//
//        JFreeChart xylineChart = ChartFactory.createXYLineChart(
//                "Browser usage statastics",
//                "Category",
//                "Score",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true, true, false);
//
//        xylineChart.getXYPlot().setRenderer(new XYSplineRenderer());
//        int width = 640; /* Width of the image */
//        int height = 480; /* Height of the image */
//        File XYChart = new File( "XYLineChart.jpeg" );
//        ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);

//        for (CSVRecord record : records) {
//            System.out.println(record.toString());
//            System.out.println(record.get(2));
//            break;
//        }
//
    }
}
