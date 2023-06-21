package com.uniba.mobile.cddgl.laureapp.ui.home;

import static com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment.TESI_VISUALIZE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentHomeBinding;
import com.uniba.mobile.cddgl.laureapp.ui.home.menu.HomeMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment che si occupa della visualizzazione e della gestione della schermata home dell'applicazione
 * Visulizza una serie di tesi recuperate dal mainViewModel e un grafico per i task
 */
public class HomeFragment extends Fragment {

    public static final int NOTIFICATION_APP_BAR = R.id.notification_app_bar;
    public static final int CREATE_TESI_APP_BAR = R.id.crea_tesi;

    private FragmentHomeBinding binding;
    private HomeMenu provider;
    private HomeViewModel homeViewModel;
    private PieChart pieChart;
    private MainViewModel mainViewModel;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        LoggedInUser currentUser = mainViewModel.getUser().getValue();
        mainViewModel.getLastTheses().observe(getViewLifecycleOwner(), this::setCardLastThesis);

        List<Task> tasks = mainViewModel.getTasks().getValue();
        pieChart = root.findViewById(R.id.pie_chart);
        manageViewGraphTask(tasks);

        if(currentUser!=null){
            mainViewModel.readTask(currentUser.getRole(), currentUser.getId());
            mainViewModel.loadTesiByRole(currentUser.getRole());
            mainViewModel.loadThesesAmbito(currentUser.getAmbiti());
        }
        mainViewModel.loadLastTheses();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        navController = NavHostFragment.findNavController(this);

        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }

        if(mainViewModel.getUser().getValue()!=null){
            provider = new HomeMenu(navController, mainViewModel.getUser().getValue().getRole());
        }
        else{
            provider = new HomeMenu(navController, RoleUser.GUEST);
        }
        requireActivity().addMenuProvider(provider);

        provider.getMenu().observe(getViewLifecycleOwner(), menu -> {
            if (menu != null) {
                homeViewModel.getCountNotification().observe(getViewLifecycleOwner(), integer -> provider.setBadgeIcon(integer));
            }
        });

        mainViewModel.getTasks().observe(getViewLifecycleOwner(), this::manageViewGraphTask);
        mainViewModel.getLastTheses().observe(getViewLifecycleOwner(), this::setCardLastThesis);
        mainViewModel.getThesesRole().observe(getViewLifecycleOwner(), this::setCardThesisRole);
        mainViewModel.getThesesAmbito().observe(getViewLifecycleOwner(), this::setCardThesisAmbito);
    }

    private void manageViewGraphTask(List<Task> tasks) {
        if (tasks == null) {
            pieChart.setVisibility(View.GONE);
        } else if (tasks.size() == 0) {
            pieChart.setVisibility(View.GONE);

            TextView noTaskAssignedTv = binding.textNoTask;
            noTaskAssignedTv.setText(getString(R.string.there_are_no_assigned_tasks));
            noTaskAssignedTv.setVisibility(View.VISIBLE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            setGraphTask(tasks);
        }
    }

    private void setGraphTask(List<Task> taskList) {
        // Initialize the pie chart
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.color_hole_graph));
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterText(getString(R.string.task_graph, String.valueOf(taskList.size())));
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

        for (Task task : taskList) {
            switch (task.getStato()) {
                case NEW:
                    taskNew += 1;
                    break;
                case STARTED:
                    taskStarted += 1;
                    break;
                case COMPLETED:
                    taskCreated += 1;
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

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String numberTaskSelected = String.valueOf((int) e.getY());
                pieChart.setCenterText(getString(R.string.task_graph, numberTaskSelected));
            }

            @Override
            public void onNothingSelected() {
                pieChart.setCenterText(getString(R.string.task_graph, String.valueOf(taskList.size())));
            }
        });
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

    private void setCardLastThesis(QuerySnapshot querySnapshot) {
        int counter = 0;
        CardView cardView = null;
        ImageView img = null;
        TextView Titolo = null;
        TextView Descrizione = null;

        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
            switch (counter) {
                case 0:
                    cardView = binding.card1;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img1;
                    Titolo = binding.NomeTesi1;
                    Descrizione = binding.DescrizioneTesi1;
                    break;
                case 1:
                    cardView = binding.card2;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img2;
                    Titolo = binding.NomeTesi2;
                    Descrizione = binding.DescrizioneTesi2;
                    break;
                case 2:
                    cardView = binding.card3;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img3;
                    Titolo = binding.NomeTesi3;
                    Descrizione = binding.DescrizioneTesi3;
                    break;
                default:
                    break;
            }
            Tesi tesi = documentSnapshot.toObject(Tesi.class);
            tesi.setId(documentSnapshot.getId());

            String nome = tesi.getNomeTesi();
            String descrizione = tesi.getDescrizione();

            counter++;

            if (tesi.getImageTesi() != null) {
                Glide.with(this).load(tesi.getImageTesi()).into(img);
            } else {
                img.setImageResource(R.mipmap.no_image);
            }

            Titolo.setText(nome);
            Descrizione.setText(descrizione);

            cardView.setClickable(true);
            cardView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(TESI_VISUALIZE, tesi);
                navController.navigate(R.id.action_navigation_home_to_visualizeTesiFragment, bundle);
            });

        }
    }

    private void setCardThesisRole(QuerySnapshot querySnapshot) {

        if(!querySnapshot.isEmpty()) {
            TextView userThesis = binding.userThesis;
            userThesis.setVisibility(View.VISIBLE);
        }

        int counter = 0;
        CardView cardView = null;
        ImageView img = null;
        TextView Titolo = null;
        TextView Descrizione = null;

        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
            switch (counter) {
                case 0:
                    cardView = binding.card1UserThesis;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img1UserThesis;
                    Titolo = binding.NomeTesi1UserThesis;
                    Descrizione = binding.DescrizioneTesi1UserThesis;
                    break;
                case 1:
                    cardView = binding.card2UserThesis;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img2UserThesis;
                    Titolo = binding.NomeTesi2UserThesis;
                    Descrizione = binding.DescrizioneTesi2UserThesis;
                    break;
                case 2:
                    cardView = binding.card3UserThesis;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img3UserThesis;
                    Titolo = binding.NomeTesi3UserThesis;
                    Descrizione = binding.DescrizioneTesi3UserThesis;
                    break;
                default:
                    break;
            }
            Tesi tesi = documentSnapshot.toObject(Tesi.class);
            tesi.setId(documentSnapshot.getId());

            String nome = tesi.getNomeTesi();
            String descrizione = tesi.getDescrizione();

            counter++;

            if (tesi.getImageTesi() != null) {
                Glide.with(this).load(tesi.getImageTesi()).into(img);
            } else {
                img.setImageResource(R.mipmap.no_image);
            }

            Titolo.setText(nome);
            Descrizione.setText(descrizione);

            cardView.setClickable(true);
            cardView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(TESI_VISUALIZE, tesi);
                navController.navigate(R.id.action_navigation_home_to_visualizeTesiFragment, bundle);
            });
        }
    }

    private void setCardThesisAmbito(QuerySnapshot querySnapshot) {

        if(querySnapshot == null || querySnapshot.isEmpty()) {
            TextView userThesis = binding.textNoAmbitoTesi;
            userThesis.setVisibility(View.VISIBLE);

            return;
        }

        int counter = 0;
        CardView cardView = null;
        ImageView img = null;
        TextView Titolo = null;
        TextView Descrizione = null;

        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
            switch (counter) {
                case 0:
                    cardView = binding.card1AmbitoTesi;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img1AmbitoTesi;
                    Titolo = binding.NomeTesi1AmbitoTesi;
                    Descrizione = binding.DescrizioneTesi1AmbitoTesi;
                    break;
                case 1:
                    cardView = binding.card2AmbitoTesi;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img2AmbitoTesi;
                    Titolo = binding.NomeTesi2AmbitoTesi;
                    Descrizione = binding.DescrizioneTesi2AmbitoTesi;
                    break;
                case 2:
                    cardView = binding.card3AmbitoTesi;
                    cardView.setVisibility(View.VISIBLE);
                    img = binding.img3AmbitoTesi;
                    Titolo = binding.NomeTesi3AmbitoTesi;
                    Descrizione = binding.DescrizioneTesi3AmbitoTesi;
                    break;
                default:
                    break;
            }
            Tesi tesi = documentSnapshot.toObject(Tesi.class);
            tesi.setId(documentSnapshot.getId());

            String nome = tesi.getNomeTesi();
            String descrizione = tesi.getDescrizione();

            counter++;

            if (tesi.getImageTesi() != null) {
                Glide.with(this).load(tesi.getImageTesi()).into(img);
            } else {
                img.setImageResource(R.mipmap.no_image);
            }

            Titolo.setText(nome);
            Descrizione.setText(descrizione);

            cardView.setClickable(true);
            cardView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(TESI_VISUALIZE, tesi);
                navController.navigate(R.id.action_navigation_home_to_visualizeTesiFragment, bundle);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(provider);
        binding = null;
        provider = null;
    }
}
