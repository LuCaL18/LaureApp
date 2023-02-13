package com.uniba.mobile.cddgl.laureapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentHomeBinding;
import com.uniba.mobile.cddgl.laureapp.ui.home.menu.HomeMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final int NOTIFICATION_APP_BAR = R.id.notification_app_bar;

    private FragmentHomeBinding binding;
    private HomeMenu provider;
    private HomeViewModel homeViewModel;
    private PieChart pieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        NavController navController = NavHostFragment.findNavController(this);

        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name_upperCase);
        }

        homeViewModel.readTask();

        provider = new HomeMenu(navController);

        requireActivity().addMenuProvider(provider);

        provider.getMenu().observe(getViewLifecycleOwner(), menu -> {
            if (menu != null) {
                homeViewModel.getCountNotification().observe(getViewLifecycleOwner(), integer -> provider.setBadgeIcon(integer));
            }
        });

        homeViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            pieChart = view.findViewById(R.id.pie_chart);

            if(tasks == null) {
                pieChart.setVisibility(View.GONE);
                return;
            }

            if(tasks.size() == 0) {
                pieChart.setVisibility(View.GONE);

                TextView noTaskAssignedTv = view.findViewById(R.id.text_no_task);
                noTaskAssignedTv.setText(getString(R.string.there_are_no_assigned_tasks));
                noTaskAssignedTv.setVisibility(View.VISIBLE);
                return;
            }

            pieChart.setVisibility(View.VISIBLE);
            setGraphTask();
        });


    }

    private void setGraphTask() {
        // Initialize the pie chart
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.color_hole_graph));
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterText(getString(R.string.task_graph, String.valueOf(homeViewModel.getTasks().getValue().size())));
        pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.legend_text_color));
        pieChart.setDrawEntryLabels(false);

        //set legend
        int blueGraph = ContextCompat.getColor(getContext(), R.color.blue_graph);
        int greenGraph = ContextCompat.getColor(getContext(), R.color.green_graph);
        int greyGraph = ContextCompat.getColor(getContext(), R.color.grey_graph);

        List<String> labels = Arrays.asList(getString(R.string.state_new), getString(R.string.started), getString(R.string.completed));
        List<Integer> colors = Arrays.asList(greyGraph, blueGraph, greenGraph);

        Legend legend = pieChart.getLegend();
        legend.setFormSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12f);
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.legend_text_color));
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(15f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setCustom(getLegendEntries(labels, colors));

        // Create the data set

        int taskNew = 0, taskStarted = 0, taskCreated = 0;

        for(Task task : homeViewModel.getTasks().getValue()) {
            switch (task.getStato()) {
                case NEW:
                    taskNew +=1;
                    break;
                case STARTED:
                    taskStarted +=1;
                    break;
                case COMPLETED:
                    taskCreated +=1;
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(taskNew, getString(R.string.state_new)));
        entries.add(new PieEntry(taskStarted, getString(R.string.started)));
        entries.add(new PieEntry(taskCreated, getString(R.string.completed)));
        PieDataSet dataSet = new PieDataSet(entries, "");

        // Set the colors for the data set
        dataSet.setColors(greyGraph, blueGraph, greenGraph);

        // Set the data for the chart
        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private List<LegendEntry> getLegendEntries(List<String> labels, List<Integer> colors) {
        List<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.label = labels.get(i);
            entry.formColor = colors.get(i);
            entries.add(entry);
        }
        return entries;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(provider);
        binding = null;
        provider = null;
    }
}