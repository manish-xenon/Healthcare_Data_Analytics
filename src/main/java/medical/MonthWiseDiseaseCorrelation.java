package medical;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by manish on 4/30/15.
 */
public class MonthWiseDiseaseCorrelation {

    private static Map<String, Map<Integer, Map<Integer, Integer>>> disease_date_count_map;

    private static void csvRead(String file, String disease_column, String date_column) throws IOException {
        disease_date_count_map = new LinkedHashMap<>();

        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        Iterator<CSVRecord> recordIterator = records.iterator();

        if (!recordIterator.hasNext()) return;

        Map<String, Integer> headersMap = new HashMap<String, Integer>();
        int header_index = 0;
        for (String header : recordIterator.next()) {
            headersMap.put(header, header_index++);
        }

        while (recordIterator.hasNext()) {
            CSVRecord record = recordIterator.next();
            String disease_type = record.get(headersMap.get(disease_column));
            String time = record.get(headersMap.get(date_column));
            DateTime dateTime = DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime(time);
            //System.out.println("time " + dateTime.getMonthOfYear() + " disease " + disease_type);
            int year = dateTime.getYear();
            int month = dateTime.getMonthOfYear();
            int date = dateTime.getDayOfMonth();

            if (!disease_date_count_map.containsKey(disease_type)) {
                disease_date_count_map.put(disease_type, new LinkedHashMap<Integer, Map<Integer, Integer>>());
            }
            if (!disease_date_count_map.get(disease_type).containsKey(year)) {
                disease_date_count_map.get(disease_type).put(year, new LinkedHashMap<Integer, Integer>());
            }
            if (!disease_date_count_map.get(disease_type).get(year).containsKey(month)) {
                disease_date_count_map.get(disease_type).get(year).put(month, 0);
            }
            int count = disease_date_count_map.get(disease_type).get(year).get(month);
            disease_date_count_map.get(disease_type).get(year).put(month, count + 1);

        }

        for (String disease : disease_date_count_map.keySet()) {
            System.out.println("Plotting graph for " + disease);
            plotGraph(disease, disease_date_count_map.get(disease), 1920, 1080);
        }

    }

    private static void plotGraph(String disease, Map<Integer, Map<Integer, Integer>> year_month_count_map,
                                  Integer width, Integer height) throws IOException {
        DefaultXYDataset dataset = new DefaultXYDataset();
        int plot_count = 0;
        for (Integer year : year_month_count_map.keySet()) {
            for (Integer month : year_month_count_map.get(year).keySet()) {
                plot_count++;
            }
        }

        int i = 0;
        double[] time_index = new double[plot_count];
        String[] dates_index = new String[plot_count];
        double[] counts_index = new double[plot_count];
        List<Integer> ylist = new ArrayList<>(year_month_count_map.keySet());
        Collections.sort(ylist);
        for (Integer year : ylist) {
            List<Integer> mlist = new ArrayList<>(year_month_count_map.get(year).keySet());
            Collections.sort(mlist);
            for (Integer month : mlist) {
                String time = month.toString() + "/" + year.toString();
                time_index[i] = i;
                dates_index[i] = time;
                counts_index[i] = year_month_count_map.get(year).get(month);
                i++;
            }
        }
        dataset.addSeries("Count", new double[][]{time_index, counts_index});
        ValueAxis dateAxis = new SymbolAxis("Date", dates_index);
        dateAxis.setLabelFont(new Font("Tahoma", 0, 32));
        dateAxis.setTickLabelFont(new Font("Tahoma", 0, 32));
        dateAxis.setVerticalTickLabels(true);

        ValueAxis countAxis = new NumberAxis("Count");
        countAxis.setLabelFont(new Font("Tahoma", 0, 32));
        countAxis.setTickLabelFont(new Font("Tahoma", 0, 32));

        String title = "Plot of " + disease + " occurence per month";
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(dataset, dateAxis, countAxis, renderer);
        JFreeChart chart = new JFreeChart(title, new Font("Tahoma", 0, 48), plot, true);

        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        disease = disease.replaceAll("[/ ,.]", "_");
        File XYChart = new File("diseases_monthly_graph/" + disease + "_Stat.jpeg");
        ChartUtilities.saveChartAsJPEG(XYChart, chart, width, height);
//
//        JFrame frame = new JFrame("Medical Statistics of disease per month");
//        frame.setContentPane(new ChartPanel(chart));
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        csvRead("Table2_ED.csv", "Chief Complaint", "Presentation Date");
    }


}
