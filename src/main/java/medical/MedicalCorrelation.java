package medical;

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
import java.util.*;

/**
 * Created by manish on 3/30/15.
 */
public class MedicalCorrelation {
    private static Map<String, String> CONFIG_MAP;

    private static void readConfig(String config) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(config));
        CONFIG_MAP = new HashMap<String, String>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            CONFIG_MAP.put(line.split("=")[0], line.split("=")[1]);
        }
    }

    private static Map<DateTime, Integer> csvRead(String file, String date_column) throws IOException {
        Map<DateTime, Integer> dateCountMap = new HashMap<DateTime, Integer>();

        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        Iterator<CSVRecord> recordIterator = records.iterator();

        if (!recordIterator.hasNext()) return null;

        Map<String, Integer> headersMap = new HashMap<String, Integer>();
        int header_index = 0;
        for (String header : recordIterator.next()) {
            headersMap.put(header, header_index++);
        }

        while (recordIterator.hasNext()) {
            CSVRecord record = recordIterator.next();
            String time = record.get(headersMap.get(date_column));
            try {
                DateTime dateTime = DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime(time);
                if (!dateCountMap.containsKey(dateTime)) dateCountMap.put(dateTime, 0);
                dateCountMap.put(dateTime, dateCountMap.get(dateTime) + 1);
            } catch (IllegalArgumentException iae) {
                continue;
            }
        }
        return dateCountMap;
    }

    private static Map<DateTime, Integer[]> getDataPoints(Map<DateTime, Integer> dateCountMap1,
                                                          Map<DateTime, Integer> dateCountMap2, int day_diff) {
        Map<DateTime, Integer[]> dateTimeMap = new TreeMap<DateTime, Integer[]>();
        for (DateTime dateTime : dateCountMap1.keySet()) {
            DateTime nextDate = dateTime.plusDays(day_diff);
            if (dateCountMap2.containsKey(nextDate)) {
                dateTimeMap.put(dateTime, new Integer[]{dateCountMap1.get(dateTime), dateCountMap2.get(nextDate)});
            }
        }
        return dateTimeMap;
    }

    private static void plotGraph(Map<DateTime, Integer[]> dateTimeMap, Integer width, Integer height,
                                  double r) throws IOException {
        DefaultXYDataset dataset = new DefaultXYDataset();

        int i = 0;
        String[] dateTimes = new String[dateTimeMap.size()];
        double[] pan_data = new double[dateTimeMap.size()];
        double[] cc_data = new double[dateTimeMap.size()];
        double[] time_index = new double[dateTimeMap.size()];

        for (DateTime dateTime : dateTimeMap.keySet()) {
            dateTimes[i] = dateTime.toString("MM/dd/yy");
            time_index[i] = i;
            pan_data[i] = dateTimeMap.get(dateTime)[0];
            cc_data[i] = dateTimeMap.get(dateTime)[1];
            i++;
        }
        dataset.addSeries("PAN", new double[][]{time_index, pan_data});
        dataset.addSeries("CC", new double[][]{time_index, cc_data});
        ValueAxis dateAxis = new SymbolAxis("PAN Date", dateTimes);
        dateAxis.setVerticalTickLabels(true);

        ValueAxis countAxis = new NumberAxis("Count");

        String title = "Medical Statistics with R = " + String.valueOf(r);
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(dataset, dateAxis, countAxis, renderer);
        JFreeChart chart = new JFreeChart(title, new Font("Tahoma", 0, 18), plot, true);

        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        File XYChart = new File( "Medical_Stat.jpeg" );
        ChartUtilities.saveChartAsJPEG(XYChart, chart, width, height);

        JFrame frame = new JFrame("Medical Statistics");
        frame.setContentPane(new ChartPanel(chart));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
}

    private static double calculateR(Map<DateTime, Integer[]> dateTimeMap) {
        int n = dateTimeMap.size();
        double sig_x = 0.0, sig_y = 0.0, sig_xy = 0.0, sig_xx = 0.0, sig_yy = 0.0;
        for (DateTime dateTime : dateTimeMap.keySet()) {
            sig_x += dateTimeMap.get(dateTime)[0];
            sig_y += dateTimeMap.get(dateTime)[1];
            sig_xy += (dateTimeMap.get(dateTime)[0] * dateTimeMap.get(dateTime)[1]);
            sig_xx += (dateTimeMap.get(dateTime)[0] * dateTimeMap.get(dateTime)[0]);
            sig_yy += (dateTimeMap.get(dateTime)[1] * dateTimeMap.get(dateTime)[1]);
        }
        //System.out.println(sig_x + " " + sig_y + " " + sig_xy + " " + sig_xx + " " + sig_yy);

        return ( (n * sig_xy) - (sig_x * sig_y) ) /
                ( (Math.sqrt(n * sig_xx - (sig_x * sig_x))) * (Math.sqrt(n * sig_yy - (sig_y * sig_y))) );
    }

    public static void performCorrelation(String config) throws IOException {
        readConfig(config);

        String file1 = CONFIG_MAP.get("FILE_1");
        String file2 = CONFIG_MAP.get("FILE_2");
        String date_col1 = CONFIG_MAP.get("DATE_COL_1");
        String date_col2 = CONFIG_MAP.get("DATE_COL_2");
        Integer day_diff = Integer.parseInt(CONFIG_MAP.get("DAY_DIFF"));
        Integer width = Integer.parseInt(CONFIG_MAP.get("GRAPH_WIDTH"));
        Integer height = Integer.parseInt(CONFIG_MAP.get("GRAPH_HEIGHT"));


        Map<DateTime, Integer> dateCountMap1 = csvRead(file1, date_col1);
        Map<DateTime, Integer> dateCountMap2 = csvRead(file2, date_col2);

        Map<DateTime, Integer[]> dateTimeMap = getDataPoints(dateCountMap1, dateCountMap2, day_diff);
        double r = calculateR(dateTimeMap);

        System.out.println("Correlation = " + r);

        plotGraph(dateTimeMap, width, height, r);
    }

    public static void main(String[] args) throws IOException {
        String config = args[0];
        performCorrelation(config);
    }
}
