package com.sanj.sortingvisualizer.algorithm;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.Slider;
import com.sanj.sortingvisualizer.R;
import com.sanj.sortingvisualizer.model.BarchartModel;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MergeSort {

    private final BarChart barChart;
    private final ArrayList<BarEntry> barEntryArrayList;
    private final ArrayList<String> label_names;
    private final ArrayList<BarchartModel> barChartModelArrayList;
    private final Slider slider;
    private final Context mContext;
    private final Handler mergeHandler;

    Runnable mergeRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                initializeThread();
                ArrayList<Integer> data = converterToIntegers();
                mergeSort(data, 0, data.size() - 1);
            } catch (InterruptedException e) {
                ThreadState.threadAlive = false;
                mergeHandler.post(() -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                finalizeThread();
            }
        }
    };

    public MergeSort(BarChart barChart, ArrayList<BarEntry> barEntryArrayList, ArrayList<String> label_names, ArrayList<BarchartModel> barChartModelArrayList, Slider slider, Context mContext) {
        this.barChart = barChart;
        this.barEntryArrayList = barEntryArrayList;
        this.label_names = label_names;
        this.barChartModelArrayList = barChartModelArrayList;
        this.slider = slider;
        this.mContext = mContext;
        mergeHandler = new Handler();
        new Thread(mergeRunnable).start();
    }

    private ArrayList<Integer> converterToIntegers() {
        ArrayList<Integer> data = new ArrayList<>();
        int k = barChartModelArrayList.size();
        for (int m = 0; m < k; m++) {
            data.add(barChartModelArrayList.get(m).getValue());
        }
        return data;
    }

    private void displayGraph() {
        barEntryArrayList.clear();
        label_names.clear();

        for (int i = 0; i < barChartModelArrayList.size(); i++) {
            int val = barChartModelArrayList.get(i).getValue();
            barEntryArrayList.add(new BarEntry(i, val));
            label_names.add(" ");
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, " ");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Description description = new Description();
        description.setText(" ");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label_names));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(label_names.size());
        barChart.invalidate();
    }

    private void converterToModel(ArrayList<Integer> data) {
        int l = data.size();
        barChartModelArrayList.clear();
        for (int p = 0; p < l; p++) {
            int randomValue = data.get(p);
            barChartModelArrayList.add(new BarchartModel(String.valueOf(p), randomValue));
        }
        displayGraph();
    }

    private void finalizeThread() {
        mergeHandler.post(() -> {
            ThreadState.threadAlive = false;
            displayGraph();
            slider.setEnabled(true);
            barChart.setEnabled(true);
            Toast.makeText(mContext,"Sorting Process Completed",Toast.LENGTH_LONG).show();
//        view_algorithm.setVisibility(View.VISIBLE);
//        sliderSpeed.setVisibility(View.GONE);
        });
    }
    private void initializeThread() {
        mergeHandler.post(() -> {
            ThreadState.threadAlive = true;
            Toast.makeText(mContext, "Sorting Process Initiated", Toast.LENGTH_LONG).show();
//        view_algorithm.setVisibility(View.VISIBLE);
//        sliderSpeed.setVisibility(View.GONE);
        });
    }

    void merge(ArrayList<Integer> data, int l, int m, int r) throws InterruptedException {
        int n1 = m - l + 1;
        int n2 = r - m;

        ArrayList<Integer> left_data = new ArrayList<>();

        ArrayList<Integer> right_data = new ArrayList<>();

        for (int i = 0; i < n1; ++i) {
            if (i > left_data.size() - 1) {
                left_data.add(data.get(l + i));
            } else {
                left_data.set(i, data.get(l + i));
            }
        }
        for (int j = 0; j < n2; ++j) {
            if (j > right_data.size() - 1) {
                right_data.add(data.get(m + 1 + j));
            } else {
                right_data.set(j, data.get(m + 1 + j));
            }
        }
        int i = 0, j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            if (left_data.get(i) <= right_data.get(j)) {
                data.set(k, left_data.get(i));
                i++;
            } else {
                data.set(k, right_data.get(j));
                j++;
            }
            k++;
            mergeHandler.post(() -> converterToModel(data));
            sleep(ThreadState.delayTime);
        }
        while (i < n1) {
            data.set(k, left_data.get(i));
            i++;
            k++;
            mergeHandler.post(() -> converterToModel(data));
            sleep(ThreadState.delayTime);
        }
        while (j < n2) {
            data.set(k, right_data.get(j));
            j++;
            k++;
            mergeHandler.post(() -> converterToModel(data));
            sleep(ThreadState.delayTime);
        }
    }

    void mergeSort(ArrayList<Integer> data, int l, int r) throws InterruptedException {

        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(data, l, m);
            mergeHandler.post(() -> converterToModel(data));
            sleep(ThreadState.delayTime);
            mergeSort(data, m + 1, r);
            mergeHandler.post(() -> converterToModel(data));
            sleep(ThreadState.delayTime);
            merge(data, l, m, r);
            mergeHandler.post(() -> converterToModel(data));
            sleep(ThreadState.delayTime);
        }
    }
}
