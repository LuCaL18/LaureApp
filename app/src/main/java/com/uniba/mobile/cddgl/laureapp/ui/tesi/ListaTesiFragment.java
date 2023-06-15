package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.ListAdapterTesi;
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

public class ListaTesiFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String FILTERS_KEY = "filters";

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

    private final int SEARCH_ITEM_MENU = R.id.search_tesi;
    private final int FILTER_ITEM_MENU = R.id.filter_tesi;

    private final int TAB_ALL = 0;
    private final int TAB_PERSONAL = 1;


    private ListAdapterTesi adapterAll;
    private ListAdapterTesi adapterPersonal;
    private List<Tesi> currentTesiList;
    private List<Tesi> allTesiList;
    private List<Tesi> personalTesiList;
    private VisualizeThesisViewModel visualizeThesisViewModel;
    private Query queryAll;
    private MenuProvider menuProvider;
    private SearchView searchView;
    private int currentTab;
    private LoggedInUser user;
    private Map<String, Set<String>> currentFilters;
    private ChipGroup filtersContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        allTesiList = new ArrayList<>();
        personalTesiList = new ArrayList<>();
        currentTesiList = new ArrayList<>();
        currentFilters = new HashMap<>();

        // Recupera la stringa JSON dalla memoria locale utilizzando SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String mappaJson = sharedPreferences.getString(FILTERS_KEY, null);
        // Converti la stringa JSON nella mappa originale
        if (mappaJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Set<String>>>() {
            }.getType();

            currentFilters = gson.fromJson(mappaJson, type);
        }

        if (savedInstanceState != null) {
            currentTab = Integer.parseInt(savedInstanceState.getString("current_tab"));
        } else {
            currentTab = 0;
        }

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        user = mainViewModel.getUser().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_tesi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.listatesi);

        filtersContainer = view.findViewById(R.id.filtersContainer);

        visualizeThesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);

        adapterAll = new ListAdapterTesi(getContext(), new ArrayList<>(), visualizeThesisViewModel, user);
        adapterPersonal = new ListAdapterTesi(getContext(), new ArrayList<>(), visualizeThesisViewModel, user);

        CollectionReference tesiRef = FirebaseFirestore.getInstance().collection("tesi");

        queryAll = tesiRef.whereEqualTo("isAssigned", false).orderBy("created_at", Query.Direction.DESCENDING);
        queryAll.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                allTesiList.clear();
                for (DocumentSnapshot doc : querySnapshot) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    allTesiList.add(tesi);
                }


                if (currentTab == TAB_ALL) {
                    currentTesiList.clear();
                    currentTesiList.addAll(allTesiList);
                    filterListTesi();

                    if (currentTesiList.isEmpty()) {
                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (user.getRole().equals(RoleUser.PROFESSOR)) {
            Query queryProf = tesiRef.whereEqualTo("relatore.id", user.getId()).orderBy("created_at", Query.Direction.DESCENDING);

            Query queryCoRelatori = tesiRef.whereArrayContains("coRelatori", new PersonaTesi(user.getId(), user.getDisplayName(), user.getEmail(), null))
                    .orderBy("created_at", Query.Direction.DESCENDING);

            com.google.android.gms.tasks.Task<QuerySnapshot> query1Task = queryProf.get();
            com.google.android.gms.tasks.Task<QuerySnapshot> query2Task = queryCoRelatori.get();

            Tasks.whenAllSuccess(query1Task, query2Task)
                    .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            personalTesiList.clear();
                            for (Object object : objects) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) object;

                                for (DocumentSnapshot doc : querySnapshot) {
                                    Tesi tesi = doc.toObject(Tesi.class);
                                    personalTesiList.add(tesi);
                                }

                                if (currentTab == TAB_PERSONAL) {
                                    currentTesiList.clear();
                                    currentTesiList.addAll(personalTesiList);
                                    filterListTesi();

                                    if (currentTesiList.isEmpty()) {
                                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                                        listView.setVisibility(View.GONE);
                                    } else {
                                        view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                                        listView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else if (user.getRole().equals(RoleUser.STUDENT)) {
            Query queryStudent = tesiRef.whereEqualTo("student.id", user.getId()).orderBy("created_at", Query.Direction.DESCENDING);

            queryStudent.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    personalTesiList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Tesi tesi = doc.toObject(Tesi.class);
                        personalTesiList.add(tesi);
                    }

                    if (currentTab == TAB_PERSONAL) {
                        currentTesiList.clear();
                        currentTesiList.addAll(personalTesiList);
                        filterListTesi();

                        if (currentTesiList.isEmpty()) {
                            view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            FirebaseFirestore.getInstance().collection("tesi_classifiche")
                    .document(user.getId()).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            TesiClassifica classification = documentSnapshot.toObject(TesiClassifica.class);
                            if (classification != null) {
                                adapterAll.setClassficaTesi(classification.getTesi());
                                adapterPersonal.setClassficaTesi(classification.getTesi());
                            }
                        }
                    });

        } else {
            personalTesiList = new ArrayList<>();

            List<String> classificaGuest = Utility.getTesiList(getContext());
            adapterAll.setClassficaTesi(classificaGuest);
            adapterPersonal.setClassficaTesi(classificaGuest);

            if (currentTab == TAB_PERSONAL) {
                currentTesiList.clear();
                currentTesiList.addAll(personalTesiList);
                filterListTesi();

                if (currentTesiList.isEmpty()) {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        }

        if (currentTab == TAB_ALL) {
            listView.setAdapter(adapterAll);
        } else {
            listView.setAdapter(adapterPersonal);
        }

        TabLayout listTesiTabLayout = view.findViewById(R.id.tab_layout);
        listTesiTabLayout.selectTab(listTesiTabLayout.getTabAt(currentTab));
        listTesiTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();

                if (currentTab == TAB_ALL) {
                    currentTesiList.clear();
                    currentTesiList.addAll(allTesiList);
                    filterListTesi();
                    adapterAll.setTesiList(currentTesiList);
                    listView.setAdapter(adapterAll);
                } else {
                    currentTesiList.clear();
                    currentTesiList.addAll(personalTesiList);
                    filterListTesi();
                    adapterPersonal.setTesiList(currentTesiList);
                    listView.setAdapter(adapterPersonal);
                }

                if (currentTesiList.isEmpty()) {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.text_no_tesi_available).setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        NavController navController = NavHostFragment.findNavController(this);
        visualizeThesisViewModel.getThesis().observe(getViewLifecycleOwner(), tesi -> {
            if (tesi == null) {
                return;
            }

            navController.navigate(R.id.action_navigation_lista_tesi_to_visualizeTesiFragment);
        });

        if (menuProvider == null) {
            ListaTesiFragment queryTextListener = this;
            menuProvider = new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    menuInflater.inflate(R.menu.app_bar_list_tesi, menu);
                    MenuItem searchItem = menu.findItem(R.id.search_tesi);
                    searchView = (SearchView) searchItem.getActionView();
                    searchView.setQueryHint(getString(R.string.search_hint));
                    searchView.setOnQueryTextListener(queryTextListener);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case SEARCH_ITEM_MENU:
                            searchView.setQuery("", false);

                            if (currentTab == TAB_PERSONAL) {
                                adapterPersonal.setTesiList(currentTesiList);
                                return true;
                            }

                            adapterAll.setTesiList(currentTesiList);
                            return true;

                        case FILTER_ITEM_MENU:
                            setFilter(requireActivity().findViewById(FILTER_ITEM_MENU));
                            return true;
                        default:
                            return false;
                    }
                }
            };

            requireActivity().addMenuProvider(menuProvider);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Tesi> filteredList = new ArrayList<>();
        for (Tesi thesis : currentTesiList) {
            if (thesis.getNomeTesi().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(thesis);
            }
        }

        if (currentTab == TAB_ALL) {
            adapterAll.setTesiList(filteredList);
            return true;
        }

        adapterPersonal.setTesiList(filteredList);
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_tab", String.valueOf(currentTab));
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
                    currentTesiList.sort(Comparator.comparing(t -> t.getNomeTesi().toLowerCase()));
                    break;
                case TESI_Z_A:
                    if (currentFilters.get(ORDINAMENTO) != null) {
                        currentFilters.replace(ORDINAMENTO, Collections.singleton("tesi_Z_A"));
                    } else {
                        currentFilters.put(ORDINAMENTO, Collections.singleton("tesi_Z_A"));
                    }
                    currentTesiList.sort(Comparator.comparing(t -> ((Tesi) t).getNomeTesi().toLowerCase()).reversed());
                    break;
                default:
                    return true;
            }

            Log.d("CURRENT LIST", currentTesiList.toString());
            if (currentTab == TAB_ALL) {
                adapterAll.setTesiList(currentTesiList);
                return true;
            }

            adapterPersonal.setTesiList(currentTesiList);
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
        currentTesiList.clear();

        if (currentTab == TAB_ALL) {
            currentTesiList.addAll(allTesiList);
        } else {
            currentTesiList.addAll(personalTesiList);
        }

        if (ambito != null && !ambito.isEmpty()) {
            currentTesiList = filterByAmbito(ambito, currentTesiList);
            isAtLeastFilter = true;
        }

        if (tempisitiche != null && !tempisitiche.isEmpty()) {
            currentTesiList = filterByTempistiche(tempisitiche, currentTesiList);
            isAtLeastFilter = true;
        }

        if (keyWord != null && !keyWord.isEmpty()) {
            currentTesiList = filterByKeyWord(keyWord, currentTesiList);
            isAtLeastFilter = true;
        }

        if (mediaVoto != null && !mediaVoto.isEmpty()) {
            currentTesiList = filterByMediaVoto(mediaVoto, currentTesiList);
            isAtLeastFilter = true;
        }

        if (isAtLeastFilter) {
            filtersContainer.setVisibility(View.VISIBLE);
        } else {
            filtersContainer.setVisibility(View.GONE);
        }

        if (currentTab == TAB_ALL) {
            adapterAll.setTesiList(currentTesiList);
        } else {
            adapterPersonal.setTesiList(currentTesiList);
        }
    }

    private void createChips(Set<String> filters) {

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(menuProvider);

        String mappaJson = new Gson().toJson(currentFilters);
        // Salva la stringa JSON nella memoria locale utilizzando SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FILTERS_KEY, mappaJson);
        editor.apply();

        menuProvider = null;
        queryAll = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



