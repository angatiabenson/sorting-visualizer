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
import com.sanj.sortingvisualizer.model.BarchartModel;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class PingeonholeSort {

    private final BarChart barChart;
    private final ArrayList<BarEntry> barEntryArrayList;
    private final ArrayList<String> label_names;
    private final ArrayList<BarchartModel> barChartModelArrayList;
    private final Slider slider;
    private final Context mContext;
    private final Handler pingHandler;

    Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                initializeThread();
                ArrayList<Integer> data = converterToIntegers();
                int n = data.size();
                int[] mData = new int[n];
                for (int i = 0; i < n; i++) {
                    mData[i] = data.get(i);
                }
                int min = mData[0];
                int max = mData[0];
                int range, i, j, index;

                for (int a = 0; a < n; a++) {
                    if (mData[a] > max)
                        max = mData[a];
                    if (mData[a] < min)
                        min = mData[a];
                }

                range = max - min + 1;
                int[] phole = new int[range];
                Arrays.fill(phole, 0);

                for (i = 0; i < n; i++)
                    phole[mData[i] - min]++;


                index = 0;

                for (j = 0; j < range; j++) {
                    while (phole[j]-- > 0) {
                        mData[index++] = j + min;
                    }
                    pingHandler.post(() -> converterArrayToModel(mData));

                    sleep(ThreadState.delayTime);
                }
            } catch (InterruptedException e) {
                ThreadState.threadAlive = false;
                pingHandler.post(() -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                finalizeThread();
            }
        }
    };

    public PingeonholeSort(BarChart barChart, ArrayList<BarEntry> barEntryArrayList, ArrayList<String> label_names, ArrayList<BarchartModel> barChartModelArrayList, Slider slider, Context mContext) {
        this.barChart = barChart;
        this.barEntryArrayList = barEntryArrayList;
        this.label_names = label_names;
        this.barChartModelArrayList = barChartModelArrayList;
        this.slider = slider;
        this.mContext = mContext;
        pingHandler = new Handler();
        new Thread(pingRunnable).start();
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
        pingHandler.post(() -> {
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
        pingHandler.post(() -> {
            ThreadState.threadAlive = true;
            Toast.makeText(mContext, "Sorting Process Initiated", Toast.LENGTH_LONG).show();
//        view_algorithm.setVisibility(View.VISIBLE);
//        sliderSpeed.setVisibility(View.GONE);
        });
    }

    private void converterArrayToModel(int[] data) {
        ArrayList<Integer> mData = new ArrayList<>();
        for (int datum : data) {
            mData.add(datum);
        }
        converterToModel(mData);
    }
}
