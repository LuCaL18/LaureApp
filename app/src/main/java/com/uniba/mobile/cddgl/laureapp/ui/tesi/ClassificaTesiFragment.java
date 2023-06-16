package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.ClassificaTesiAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.TesiListViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.util.Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Fragment che si occupa della gestione della visualizzazione
 * di una lista di task visibli dall'utente
 */

public class ClassificaTesiFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String CLASS_ID = "ClassificaTesiFragment";

    public static final String SHARED_PREFS_NAME = "MY_SHARED_PREF";
    public static final String TESI_LIST_KEY_PREF = "list_tesi_pref";
    private static final String FILTERS_KEY = "filters_ranking";

    private static final int AMBITO = R.id.ambito2;
    private static final int KEY_WORD = R.id.chiave2;
    private static final int MEDIA_VOTO = R.id.mediaVoto2;
    private static final int TEMPISTICHE = R.id.tempistiche2;
    private static final int TESI_A_Z = R.id.tesi_a_z2;
    private static final int TESI_Z_A = R.id.tesi_z_a2;

    private static final int MIN_VOTO = 18;

    private static final String AMBITO_FILTER = "AMBITO";
    private static final String KEY_WORD_FILTER = "KEY_WORD";
    private static final String MEDIA_VOTO_FILTER = "MEDIA_VOTO";
    private static final String TEMPISTICHE_FILTER = "TEMPISTICHE";
    private static final String ORDINAMENTO = "ORDINAMENTO";

    private final int SEARCH_ITEM_MENU = R.id.search_tesi_classifica;
    private final int FILTER_ITEM_MENU = R.id.filter_tesi_classifica;
    private final int SHARE_ITEM_MENU = R.id.share_tesi_classifica;

    private ListView listView;

    /* Adapter per la gestione di ClassificaTesiAdapter */
    private ClassificaTesiAdapter adapter;

    private List<Tesi> tesiList;
    private List<Tesi> filteredList;
    private BottomNavigationView navBar;
    private LoggedInUser user;
    private VisualizeThesisViewModel thesisViewModel;
    private ChipGroup filtersContainer;
    private Map<String, Set<String>> currentFilters;
    private MenuProvider menuProvider;
    private SearchView searchView;
    private TesiListViewModel tesiListViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        user = mainViewModel.getUser().getValue();

        thesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);
        tesiListViewModel = new ViewModelProvider(requireParentFragment()).get(TesiListViewModel.class);

        currentFilters = new HashMap<>();
        filteredList = new ArrayList<>();
    }

    /**
     * Metodo "onCreateView" che si occupa della creazione della view che visualizzerà il
     * layout relativo alla classifica tesi, presenta al suo interno l'implementazione
     * relativa alla condivisione della classifica oppure al filtraggio tramite vincoli o
     * condizioni che sono rispettivamente:
     * <p>
     * 1. Ambito, ovvero il contesto in cui è incentrato la tesi
     * 2. Chiave, parola chiave associata alla tesi
     * 3. Media Voto, in base alla media minima richiesta e selezionata dall'utente
     * 4. Tempistiche, mesi necessari per il completamento della tesi
     * 5. Tesi A-Z o Z-A, ordinamento per nome tesi in ordine alfabetico A-Z o inverso
     * 6. Relatore A-Z o Z-A, ordinamento per nome relatore in ordine alfabetico A-Z o inverso
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Creazione della view responsabile della gestione della visualizzazione del layout */
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);

        // Recupera la stringa JSON dalla memoria locale utilizzando SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String mappaJson = sharedPreferences.getString(FILTERS_KEY, null);
        // Converti la stringa JSON nella mappa originale
        if (mappaJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Set<String>>>() {
            }.getType();

            currentFilters = gson.fromJson(mappaJson, type);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            CollectionReference mCollection = FirebaseFirestore.getInstance().collection("tesi_classifiche");
            mCollection.whereEqualTo("studentId", currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tesiList = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        TesiClassifica classificaTesi = doc.toObject(TesiClassifica.class);
                        fetchDataTesi(classificaTesi.getTesi());
                    }
                }
            });
        } else {
            fetchDataTesi(getTesiList());
        }

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Tesi> searchedList = new ArrayList<>();
        for (Tesi thesis : filteredList) {
            if (thesis.getNomeTesi().toLowerCase().contains(newText.toLowerCase())) {
                searchedList.add(thesis);
            }
        }

        adapter.setmDataList(searchedList);
        return true;
    }

    private void setFilter(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_classifica_layout, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case AMBITO:
                    String[] opzioni2 = getResources().getStringArray(R.array.ambiti);
                    showOptionDialog(AMBITO_FILTER, getString(R.string.filter_for_scope), getString(R.string.select_an_option), opzioni2);
                    break;
                case KEY_WORD:
                    showInputDialog(getString(R.string.filter_by_key), getString(R.string.insert_key_word_filter));
                    break;
                case MEDIA_VOTO:
                    showSeekBarDialog(MEDIA_VOTO_FILTER, getString(R.string.filter_by_minimum_grade));
                    break;
                case TEMPISTICHE:
                    showSeekBarDialog(TEMPISTICHE_FILTER, getString(R.string.Filter_by_minimum_execution_time));
                    break;
                case TESI_A_Z:
                    if (currentFilters.get(ORDINAMENTO) != null) {
                        currentFilters.replace(ORDINAMENTO, Collections.singleton("tesi_A_Z"));
                    } else {
                        currentFilters.put(ORDINAMENTO, Collections.singleton("tesi_A_Z"));
                    }
                    filteredList.sort(Comparator.comparing(t -> t.getNomeTesi().toLowerCase()));
                    break;
                case TESI_Z_A:
                    if (currentFilters.get(ORDINAMENTO) != null) {
                        currentFilters.replace(ORDINAMENTO, Collections.singleton("tesi_Z_A"));
                    } else {
                        currentFilters.put(ORDINAMENTO, Collections.singleton("tesi_Z_A"));
                    }
                    filteredList.sort(Comparator.comparing(t -> ((Tesi) t).getNomeTesi().toLowerCase()).reversed());
                    break;
                default:
                    return true;
            }

            adapter.setmDataList(filteredList);
            return true;
        });

        popup.show();
    }

    private void showOptionDialog(String filterKey, String title, String message, String[] options) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        Set<String> filtersAlreadyUsed = currentFilters.get(filterKey);
        for (String option : options) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(option);

            if (filtersAlreadyUsed != null) {
                checkBox.setChecked(filtersAlreadyUsed.contains(option));
            }
            layout.addView(checkBox);
        }

        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            Set<String> selectedOptions = new HashSet<>();
            for (int i = 0; i < layout.getChildCount(); i++) {
                View view = layout.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    if (checkBox.isChecked()) {
                        selectedOptions.add(checkBox.getText().toString());
                    }
                }
            }

            if (currentFilters.get(filterKey) != null) {
                currentFilters.replace(filterKey, selectedOptions);
            } else {
                currentFilters.put(filterKey, selectedOptions);
            }

            filterListTesi();
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void showInputDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        EditText inputField = new EditText(getActivity());
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputField);

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            String inputText = inputField.getText().toString();

            if (inputText.isEmpty()) return;

            Set<String> keyWordFilter = currentFilters.get(KEY_WORD_FILTER);
            if (keyWordFilter != null) {
                keyWordFilter.add(inputText);
                currentFilters.replace(KEY_WORD_FILTER, keyWordFilter);
            } else {
                keyWordFilter = new HashSet<>();
                keyWordFilter.add(inputText);
                currentFilters.put(KEY_WORD_FILTER, keyWordFilter);
            }
            filterListTesi();
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showSeekBarDialog(String filterKey, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View seekBarPopup;
        TextView resultText;
        Slider sliderBar;
        int filter;

        if (TEMPISTICHE_FILTER.equals(filterKey)) {
            seekBarPopup = getLayoutInflater().inflate(R.layout.sidebar_tempistiche, null);
            resultText = seekBarPopup.findViewById(R.id.temp_value_sliderbar);
            filter = 3;
            if (currentFilters.get(filterKey) != null) {
                filter = Utility.getNumberFromString(currentFilters.get(filterKey).stream().findFirst().orElse(null), filter);
            }

            resultText.setText(getString(R.string.timeline_value_weeks, String.valueOf(filter)));
            sliderBar = seekBarPopup.findViewById(R.id.tempistiche_slider_bar_layout);

        } else {
            seekBarPopup = getLayoutInflater().inflate(R.layout.slidebar_layout, null);
            resultText = seekBarPopup.findViewById(R.id.voto_edit_constraint_layout);
            filter = MIN_VOTO;
            if (currentFilters.get(MEDIA_VOTO_FILTER) != null) {
                filter = Utility.getNumberFromString(currentFilters.get(MEDIA_VOTO_FILTER).stream().findFirst().orElse(null), filter);
            }

            resultText.setText(String.valueOf(filter));
            sliderBar = seekBarPopup.findViewById(R.id.media_edit_slider_bar_layout);
        }

        builder.setView(seekBarPopup);
        builder.setTitle(title);

        sliderBar.setValue(filter);

        sliderBar.addOnChangeListener((slider, value, fromUser) -> {

            if (TEMPISTICHE_FILTER.equals(filterKey)) {
                resultText.setText(getString(R.string.timeline_value_weeks, String.valueOf(value)));
            } else {
                resultText.setText(String.valueOf(value));
            }
        });

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            String value = (String) resultText.getText();
            Set<String> filters = new HashSet<>();
            filters.add(value);
            if (currentFilters.get(filterKey) != null) {
                currentFilters.replace(filterKey, filters);
            } else {
                currentFilters.put(filterKey, filters);
            }
            filterListTesi();
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private List<Tesi> filterByAmbito(Set<String> ambito, List<Tesi> tesiList) {
        createChips(ambito);

        List<Tesi> filteredList = new ArrayList<>();
        for (Tesi t : tesiList) {
            if (t.getAmbito() != null && ambito.contains(Utility.translateScope(getResources(), t.getAmbito()))) {
                filteredList.add(t);
            }
        }
        return filteredList;
    }

    private List<Tesi> filterByKeyWord(Set<String> keyword, List<Tesi> tesiList) {
        createChips(keyword);
        List<Tesi> filteredList = new ArrayList<>();

        for (Tesi t : tesiList) {
            if (t.getChiavi() != null && keyword.containsAll(t.getChiavi())) {
                filteredList.add(t);
            }
        }

        return filteredList;
    }

    private List<Tesi> filterByMediaVoto(Set<String> filterVoto, List<Tesi> tesiList) {

        List<Tesi> filteredList = new ArrayList<>();
        AtomicInteger voto = new AtomicInteger();
        try {
            filterVoto.forEach(s -> voto.set((int) Float.parseFloat(s)));
        } catch (NumberFormatException e) {
            Log.e("filterByMediaVoto", e.getMessage());
        }

        createChips(filterVoto);

        for (Tesi t : tesiList) {
            if (t.getMediaVoto() <= voto.get()) {
                filteredList.add(t);
            }
        }
        return filteredList;
    }

    private List<Tesi> filterByTempistiche(Set<String> tempistiche, List<Tesi> tesiList) {
        List<Tesi> filteredList = new ArrayList<>();
        AtomicInteger time = new AtomicInteger();

        try {
            tempistiche.forEach(s -> time.set(Utility.getNumberFromString(s, 3)));
        } catch (NumberFormatException e) {
            Log.e("filterByTempistiche", e.getMessage());
        }

        createChips(tempistiche);

        for (Tesi t : tesiList) {
            if (t.getTempistiche() >= time.get()) {
                filteredList.add(t);
            }
        }
        return filteredList;
    }

    private void filterListTesi() {

        boolean isAtLeastFilter = false;
        Set<String> ambito = currentFilters.getOrDefault(AMBITO_FILTER, null);
        Set<String> tempisitiche = currentFilters.getOrDefault(TEMPISTICHE_FILTER, null);
        Set<String> keyWord = currentFilters.getOrDefault(KEY_WORD_FILTER, null);
        Set<String> mediaVoto = currentFilters.getOrDefault(MEDIA_VOTO_FILTER, null);

        filtersContainer.removeAllViews();
        filteredList.clear();

        filteredList.addAll(tesiList);

        if (ambito != null && !ambito.isEmpty()) {
            filteredList = filterByAmbito(ambito, filteredList);
            isAtLeastFilter = true;
        }

        if (tempisitiche != null && !tempisitiche.isEmpty()) {
            filteredList = filterByTempistiche(tempisitiche, filteredList);
            isAtLeastFilter = true;
        }

        if (keyWord != null && !keyWord.isEmpty()) {
            filteredList = filterByKeyWord(keyWord, filteredList);
            isAtLeastFilter = true;
        }

        if (mediaVoto != null && !mediaVoto.isEmpty()) {
            filteredList = filterByMediaVoto(mediaVoto, filteredList);
            isAtLeastFilter = true;
        }

        if (isAtLeastFilter) {
            filtersContainer.setVisibility(View.VISIBLE);
        } else {
            filtersContainer.setVisibility(View.GONE);
        }

        adapter.setmDataList(filteredList);
    }

    private void createChips(Set<String> filters) {

        try {
            for (String text : filters) {
                Chip chip = new Chip(getContext());
                chip.setText(text);

                // Imposta l'aspetto della chip
                chip.setChipBackgroundColorResource(R.color.primary_green);
                chip.setTextColor(getResources().getColor(R.color.white));

                // Aggiungi un'icona "x" per consentire l'eliminazione del filtro
                chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                chip.setCloseIconVisible(true);

                // Listener per gestire l'eliminazione del filtro
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Rimuovi la chip dal container
                        filtersContainer.removeView(chip);
                        String selectedFilter = (String) chip.getText();

                        // Rimuovi il filtro dalla mappa dei filtri (se necessario)
                        for (String key : currentFilters.keySet()) {
                            Set<String> filters = currentFilters.get(key);

                            if (filters == null || filters.isEmpty()) {
                                continue;
                            }

                            if (filters.contains(selectedFilter)) {
                                filters.remove(selectedFilter);

                                currentFilters.replace(key, filters);
                                filterListTesi();
                            }
                        }
                    }
                });

                filtersContainer.addView(chip);
            }
        } catch (NullPointerException e) {
            Log.e("createChips", e.getMessage());
        }

    }

    /**
     * Metodo per chiamare l'intent che si occupa della gestione della condivisione
     * della classifica tesi
     */
    private void shareClassifica() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ecco la mia classifica di tesi preferite: " + "\n");

        for (Tesi tesi : tesiList) {
            sb.append("Nome tesi: " + tesi.getNomeTesi() + "\n");
            sb.append("Description: " + tesi.getDescrizione() + "\n");
        }

        String message = sb.toString();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Condividi con:"));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filtersContainer = view.findViewById(R.id.filters_classifica_container);

        listView = view.findViewById(R.id.classifica_tesi);

        adapter = new ClassificaTesiAdapter(getContext(), thesisViewModel, tesiListViewModel);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, null, 0);
                } else {
                    view.startDrag(data, shadowBuilder, null, 0);
                }

                return true;
            }
        });

        listView.setOnDragListener(new View.OnDragListener() {

            int previousPos = -1;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        listView.setNestedScrollingEnabled(false);
                        previousPos = listView.pointToPosition((int) event.getX(), (int) event.getY());
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // highlight list item
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        int currentPos = listView.pointToPosition((int) event.getX(), (int) event.getY());
                        if (currentPos != AdapterView.INVALID_POSITION) {
                            if (currentPos != previousPos) {
                                // update the list item position
                                Tesi currentItem = (Tesi) adapter.getItem(currentPos);

                                adapter.removeItem(currentItem);
                                adapter.insertItem(previousPos, currentItem);

                                previousPos = currentPos;
                            }
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // unhighlight list item
                        return true;
                    case DragEvent.ACTION_DROP:
                        filteredList = adapter.getmDataList();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        listView.setNestedScrollingEnabled(true);
                        return true;
                }
                return false;
            }
        });


        if (menuProvider == null) {
            ClassificaTesiFragment queryTextListener = this;
            menuProvider = new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    menuInflater.inflate(R.menu.app_bar_classifica, menu);
                    MenuItem searchItem = menu.findItem(R.id.search_tesi_classifica);
                    searchView = (SearchView) searchItem.getActionView();
                    searchView.setQueryHint(getString(R.string.search_hint));
                    searchView.setOnQueryTextListener(queryTextListener);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case SEARCH_ITEM_MENU:
                            searchView.setQuery("", false);
                            adapter.setmDataList(filteredList);
                            return true;

                        case FILTER_ITEM_MENU:
                            setFilter(requireActivity().findViewById(FILTER_ITEM_MENU));
                            return true;
                        case SHARE_ITEM_MENU:
                            shareClassifica();
                        default:
                            return false;
                    }
                }
            };
            requireActivity().addMenuProvider(menuProvider);
        }

        NavController navController = NavHostFragment.findNavController(this);
        thesisViewModel.getThesis().observe(getViewLifecycleOwner(), tesi -> {
            if (tesi == null) {
                return;
            }

            navController.navigate(R.id.action_nav_classifica_tesi_to_visualizeTesiFragment);
        });

        tesiListViewModel.getTesiList().observe(getViewLifecycleOwner(), tesiList1 -> {

            Log.d("tesiViewModel", tesiList1.toString());
            if (tesiList1 == null) {
                return;
            } else if (tesiList1.isEmpty()) {
                tesiList = tesiList1;
                filteredList.addAll(tesiList1);
                filterListTesi();
                adapter.setmDataList(filteredList);
                view.findViewById(R.id.text_no_tesi_available_classifica).setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                tesiList = tesiList1;
                filteredList.addAll(tesiList1);
                filterListTesi();
                adapter.setmDataList(filteredList);
                view.findViewById(R.id.text_no_tesi_available_classifica).setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        /* Rimozione della navBar dallo schermo */
        navBar = requireActivity().findViewById(R.id.nav_view);
        if(navBar != null) {
            navBar.setVisibility(View.GONE);
        }
    }

    private void fetchDataTesi(List<String> thesisId) {
        if (thesisId == null || thesisId.isEmpty()) {
            tesiList = new ArrayList<>();
            tesiListViewModel.getTesiList().setValue(tesiList);
        } else {
            try {
                FirebaseFirestore.getInstance().collection("tesi").whereIn("id", thesisId)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Tesi> thesisList = new ArrayList<>();
                                try {

                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        for (String id : thesisId) {
                                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                // Convert each document snapshot to a Thesis object
                                                Tesi thesis = document.toObject(Tesi.class);
                                                if (thesis != null && id != null && id.equals(thesis.getId())) {
                                                    thesisList.add(thesisId.indexOf(thesis.getId()), thesis);
                                                }
                                            }
                                        }
                                    }
                                }catch (Exception e) {
                                    Log.e(CLASS_ID, "Error during fetchDataTesi --> " + e);
                                } finally {
                                    tesiList = thesisList;
                                    tesiListViewModel.getTesiList().setValue(tesiList);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(CLASS_ID, e.getMessage());
                            }
                        });
            } catch (Exception e) {
                Log.e(CLASS_ID, "Error during fetchDataTesi --> " + e);
            }
        }
    }

    public boolean saveListofThesis(List<Tesi> tesiList) {
        try {
            SharedPreferences sp = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor mEdit1 = sp.edit();
            List<String> list = new ArrayList<>();

            for (Tesi tesi : tesiList) {
                list.add(tesi.getId());
            }

            mEdit1.putString(TESI_LIST_KEY_PREF, new Gson().toJson(list));
            return mEdit1.commit();
        } catch (Exception e) {
            Log.e("ClassificaTesiFragment", e.getMessage());

            return false;
        }
    }

    public ArrayList<String> getTesiList() {
        SharedPreferences sp = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        String listJson = sp.getString(TESI_LIST_KEY_PREF, null);

        // Converti la stringa JSON nella mappa originale
        if (listJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();

            return gson.fromJson(listJson, type);
        }

        return new ArrayList<>();
    }

    @Override
    public void onStop() {
        super.onStop();
        List<Tesi> newTesiList = new ArrayList<>();
        newTesiList.addAll(filteredList);
        newTesiList.addAll(tesiList.stream().filter(tesi -> !newTesiList.contains(tesi)).collect(Collectors.toList()));

        if (user == null || user.getRole().equals(RoleUser.GUEST)) {
            saveListofThesis(newTesiList);
        } else if (user != null) {
            tesiListViewModel.saveTesiList(user.getId(), newTesiList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(navBar != null) {
            navBar.setVisibility(View.VISIBLE);
        }

        requireActivity().removeMenuProvider(menuProvider);
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.CLASSIFICA_TESI).setChecked(false);

        String mappaJson = new Gson().toJson(currentFilters);
        // Salva la stringa JSON nella memoria locale utilizzando SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FILTERS_KEY, mappaJson);
        editor.apply();

        menuProvider = null;
    }
}


