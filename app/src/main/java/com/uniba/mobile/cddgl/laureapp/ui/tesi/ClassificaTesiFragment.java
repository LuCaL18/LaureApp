package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.ClassificaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.ClassificaTesiAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Fragment che si occupa della gestione della visualizzazione
 * di una lista di task visibli dall'utente
 *
 */

public class ClassificaTesiFragment extends Fragment {

    public static final String SHARED_PREFS_NAME = "MY_SHARED_PREF";
    public static final String TESI_LIST_KEY_PREF = "list_tesi_pref";

    private ListView listView;
    /* Adapter per la gestione di ClassificaTesiAdapter */
    private ClassificaTesiAdapter adapter;
    private List<Tesi> tesiList;
    private BottomNavigationView navBar;
    private LoggedInUser user;
    private VisualizeThesisViewModel thesisViewModel;
    /* Lista delle tesi originali della classifica da visualizzare a schermo */
    private List<Tesi> listaTesiOriginale;

    /**
     *
     * Metodo "onCreateView" che si occupa della creazione della view che visualizzerà il
     * layout relativo alla classifica tesi, presenta al suo interno l'implementazione
     * relativa alla condivisione della classifica oppure al filtraggio tramite vincoli o
     * condizioni che sono rispettivamente:
     *
     *   1. Ambito, ovvero il contesto in cui è incentrato la tesi
     *   2. Chiave, parola chiave associata alla tesi
     *   3. Media Voto, in base alla media minima richiesta e selezionata dall'utente
     *   4. Tempistiche, mesi necessari per il completamento della tesi
     *   5. Tesi A-Z o Z-A, ordinamento per nome tesi in ordine alfabetico A-Z o inverso
     *   6. Relatore A-Z o Z-A, ordinamento per nome relatore in ordine alfabetico A-Z o inverso
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

        thesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);

        listView = view.findViewById(R.id.classifica_tesi);
        /* Rimozione della navBar dallo schermo */
        navBar = getActivity().findViewById(R.id.nav_view);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new ClassificaTesiAdapter(getContext(), thesisViewModel);
        listView.setAdapter(adapter);

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
                        tesiList = adapter.getmDataList();
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        listView.setNestedScrollingEnabled(true);
                        return true;
                }
                return false;
            }
        });
        /* imageButon per la condivisione della classifica tesi */
        ImageButton shareClassifica = view.findViewById(R.id.shareClassifica);
        shareClassifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ecco la mia classifica di tesi preferite: " + "\n");
                for (Tesi tesi : dataList.getTesi()) {
                    sb.append("Nome tesi: " + tesi.getNomeTesi() + "\n");
                    sb.append("Professore: " + tesi.getRelatore().getDisplayName() + "\n");
                }
                String message = sb.toString();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Condividi con:"));
            }
        });
        /* imageButton per la visualizzazione di tutti i possibili filtri o interrogazioni sui vincoli delle tesi */
        ImageButton menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), menuButton);
                popup.getMenuInflater().inflate(R.menu.menu_classifica_layout, popup.getMenu());
                /* Crea una nuova lista ordinata a partire dalla lista originale */
                List<Tesi> listaTesiOrdinata = new ArrayList<>(listaTesiOriginale);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.defaultClassifica:
                                /* Pulisci la lista ordinata e aggiungi nuovamente tutti gli elementi dalla lista originale */
                                listaTesiOrdinata.clear();
                                listaTesiOrdinata.addAll(listaTesiOriginale);
                                break;
                            case R.id.ambito2:
                                /* Salva la classifica tesi originaria */
                                TesiClassifica copia2 = dataList;
                                /* Crea una lista di opzioni per il RadioGroup */
                                final String[] opzioni2 = {"Ingegneria","Informatica","Economia","Medicina","Psicologia","Lettere","Architettura","Biologia","Giurisprudenza"};
                                /* Crea un nuovo RadioGroup */
                                RadioGroup radioGroup2 = new RadioGroup(getActivity());
                                radioGroup2.setOrientation(RadioGroup.VERTICAL);
                                /* Cicla attraverso le opzioni e crea un nuovo RadioButton per ciascuna di esse */
                                for (String opzione : opzioni2) {
                                    RadioButton radioButton2 = new RadioButton(getActivity());
                                    radioButton2.setText(opzione);
                                    radioGroup2.addView(radioButton2);
                                }
                                /* Aggiungi il RadioGroup al LinearLayout del dialog */
                                LinearLayout layout2 = new LinearLayout(getActivity());
                                layout2.setOrientation(LinearLayout.VERTICAL);
                                layout2.addView(radioGroup2);
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                                builder2.setTitle("Filtra per tempistiche");
                                builder2.setMessage("Seleziona una delle seguenti opzioni:");
                                /* Aggiungi il layout personalizzato al dialog */
                                builder2.setView(layout2);
                                /* Aggiungi il pulsante "OK" al dialog */
                                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int radioButtonID = radioGroup2.getCheckedRadioButtonId();
                                        View radioButton = radioGroup2.findViewById(radioButtonID);
                                        int index = radioGroup2.indexOfChild(radioButton);
                                        String selectedOption = opzioni2[index];
                                        /* Qui devi implementare il filtro per le tempistiche utilizzando il testo inserito dall'utente
                                         * e aggiornare la lista delle tesi visualizzate di conseguenza */
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        /* Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di tempistiche */
                                        for (Tesi t : copia2.getTesi()) {
                                            String ambito = t.getAmbito();
                                            if (selectedOption.startsWith(String.valueOf(ambito))) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        /* Aggiorna la lista delle tesi visualizzate con quelle filtrate */
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                /* Aggiungi il pulsante "Annulla" al dialog */
                                builder2.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                /* Visualizza il dialog */
                                builder2.show();
                                break;
                            case R.id.chiave2:
                                /* Salva classifica tesi originaria */
                                TesiClassifica copia3 = dataList;
                                /* Codice da inserire nel metodo onClick per l'ambito */
                                final EditText input3 = new EditText(getActivity());
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                                builder3.setTitle("Filtra per chiave");
                                builder3.setMessage("Inserisci il campo di ricerca per la chiave:");
                                /* Aggiungi il campo di testo personalizzato all'interno del dialog */
                                builder3.setView(input3);
                                /* Aggiungi il pulsante "OK" al dialog */
                                builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String text3 = input3.getText().toString();
                                        /* Qui devi implementare il filtro per l'ambito utilizzando il testo inserito dall'utente
                                         * e aggiornare la lista delle tesi visualizzate di conseguenza */
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        /* Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di ambito */
                                        for (Tesi t : dataList.getTesi()) {
                                            if (t.getChiavi().contains(text3)) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        /* Aggiorna la lista delle tesi visualizzate con quelle filtrate */
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                /* Aggiungi il pulsante "Annulla" al dialog */
                                builder3.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                /* Visualizza il dialog */
                                builder3.show();
                                dataList.equals(copia3);
                                break;
                            case R.id.mediaVoto2:
                                /* Salva la classifica tesi originaria */
                                TesiClassifica copia4 = dataList;
                                /* Crea il layout del dialog */
                                LayoutInflater inflater4 = LayoutInflater.from(getActivity());
                                View dialogView4 = inflater4.inflate(R.layout.dialog_seekbar, null);
                                /* Inizializza la seekbar */
                                final TextView textView4 = dialogView4.findViewById(R.id.seekbar_value);
                                final SeekBar seekBar4 = dialogView4.findViewById(R.id.seekbar);
                                seekBar4.setMax(24);
                                seekBar4.setMin(0);
                                seekBar4.setProgress(0);
                                textView4.setText(String.valueOf((seekBar4.getProgress() * 0.5f) + 18.0f));
                                /* Crea il dialog */
                                AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
                                builder4.setTitle("Filtra per media voto");
                                builder4.setView(dialogView4);
                                /* Aggiungi il pulsante "OK" al dialog */
                                builder4.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        float voto = (seekBar4.getProgress() * 0.5f) + 18.0f;
                                        /* Qui devi implementare il filtro per il media voto utilizzando la seekbar
                                         * e aggiornare la lista delle tesi visualizzate di conseguenza */
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        /* Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di media voto */
                                        for (Tesi t : dataList.getTesi()) {
                                            if (t.getMediaVoto() >= voto) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        /* Aggiorna la lista delle tesi visualizzate con quelle filtrate */
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                /* Aggiungi il pulsante "Annulla" al dialog */
                                builder4.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                /* Aggiungi un listener per aggiornare il valore della seekbar sulla textview */
                                seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        textView4.setText(String.valueOf((progress * 0.5f) + 18.0f));
                                    }
                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {}
                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {}
                                });
                                /* Visualizza il dialog */
                                builder4.show();
                                dataList.equals(copia4);
                                break;
                            case R.id.tempistiche2:
                                /* Salva la classifica tesi originaria */
                                TesiClassifica copia = dataList;
                                /* Crea una lista di opzioni per il RadioGroup */
                                final String[] opzioni = {"1 mese","2 mesi", "3 mesi", "4 mesi", "5 mesi","6 mesi"};
                                /* Crea un nuovo RadioGroup */
                                RadioGroup radioGroup = new RadioGroup(getActivity());
                                radioGroup.setOrientation(RadioGroup.VERTICAL);
                                /* Cicla attraverso le opzioni e crea un nuovo RadioButton per ciascuna di esse */
                                for (String opzione : opzioni) {
                                    RadioButton radioButton = new RadioButton(getActivity());
                                    radioButton.setText(opzione);
                                    radioGroup.addView(radioButton);
                                }
                                /* Aggiungi il RadioGroup al LinearLayout del dialog */
                                LinearLayout layout = new LinearLayout(getActivity());
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.addView(radioGroup);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Filtra per tempistiche");
                                builder.setMessage("Seleziona una delle seguenti opzioni:");
                                /* Aggiungi il layout personalizzato al dialog */
                                builder.setView(layout);
                                /* Aggiungi il pulsante "OK" al dialog */
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                                        View radioButton = radioGroup.findViewById(radioButtonID);
                                        int index = radioGroup.indexOfChild(radioButton);
                                        String selectedOption = opzioni[index];
                                        /* Qui devi implementare il filtro per le tempistiche utilizzando il testo inserito dall'utente
                                         * e aggiornare la lista delle tesi visualizzate di conseguenza */
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        /* Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di tempistiche */
                                        for (Tesi t : copia.getTesi()) {
                                            int tempistiche = t.getTempistiche();
                                            if (selectedOption.startsWith(String.valueOf(tempistiche))) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        /* Aggiorna la lista delle tesi visualizzate con quelle filtrate */
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                /* Aggiungi il pulsante "Annulla" al dialog */
                                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                /* Visualizza il dialog */
                                builder.show();
                                break;
                            case R.id.tesi_a_z2:
                                Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                                    @Override
                                    public int compare(Tesi t1, Tesi t2) {
                                        return t1.getNomeTesi().compareTo(t2.getNomeTesi());
                                    }
                                });
                                break;
                            case R.id.tesi_z_a2:
                                Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                                    @Override
                                    public int compare(Tesi t1, Tesi t2) {
                                        return t2.getNomeTesi().compareTo(t1.getNomeTesi());
                                    }
                                });
                                break;
                            case R.id.relatore_a_z2:
                                Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                                    @Override
                                    public int compare(Tesi t1, Tesi t2) {
                                        return t1.getRelatore().getDisplayName().compareTo(t2.getRelatore().getDisplayName());
                                    }
                                });
                                break;
                            case R.id.relatore_z_a2:
                                Collections.sort(listaTesiOrdinata, new Comparator<Tesi>() {
                                    @Override
                                    public int compare(Tesi t1, Tesi t2) {
                                        return t2.getRelatore().getDisplayName().compareTo(t1.getRelatore().getDisplayName());
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        /* Aggiorna l'adapter con la nuova lista ordinata */
                        adapter.addTesi(listaTesiOrdinata);
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                });
                popup.show();
            }
        });
        /* Ritorno della view da mostrare a schermo */
        return view;
    }

    /**
     *
     * Metodo per chiamare l'intent che si occupa della gestione della condivisione
     * della classifica tesi
     *
     */
    private void shareClassifica() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Classifica Tesi");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ecco la mia classifica di tesi: " + tesiList);
        startActivity(Intent.createChooser(shareIntent, "Condividi la classifica tramite"));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> user = loggedInUser);

        NavController navController = NavHostFragment.findNavController(this);
        thesisViewModel.getThesis().observe(getViewLifecycleOwner(), tesi -> {
            if (tesi == null) {
                return;
            }

            navController.navigate(R.id.action_nav_classifica_tesi_to_visualizeTesiFragment);
        });
    }

    private List<String> getIdOfThesis(List<Tesi> tesiList) {

        List<String> listId = new ArrayList<>();

        for (Tesi tesi : tesiList) {
            listId.add(tesi.getId());
        }

        return listId;
    }

    private void fetchDataTesi(List<String> thesisId) {
        if (thesisId == null || thesisId.isEmpty()) {
            tesiList = new ArrayList<>();
            adapter.setmDataList(tesiList);
        } else {
            FirebaseFirestore.getInstance().collection("tesi").whereIn("id", thesisId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Tesi> thesisList = new ArrayList<>();
                            if(!queryDocumentSnapshots.isEmpty()) {
                                for(String id : thesisId) {
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                        // Convert each document snapshot to a Thesis object
                                        Tesi thesis = document.toObject(Tesi.class);
                                        if(id.equals(thesis.getId())) {
                                            thesisList.add(thesisId.indexOf(thesis.getId()),thesis);
                                        }
                                    }
                                }
                            }

                            tesiList = thesisList;
                            adapter.setmDataList(tesiList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Classifica tesi", e.getMessage());
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar.setVisibility(View.GONE);
    }

    public boolean saveListofThesis() {
        try {
            SharedPreferences sp = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor mEdit1 = sp.edit();
            Set<String> set = new HashSet<>();

            for (Tesi tesi : tesiList) {
                set.add(tesi.getId());
            }

            mEdit1.putStringSet(TESI_LIST_KEY_PREF, set);
            return mEdit1.commit();
        } catch (Exception e) {
            Log.e("ClassificaTesiFragment", e.getMessage());

            return false;
        }
    }

    public ArrayList<String> getTesiList() {
        SharedPreferences sp = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet(TESI_LIST_KEY_PREF, new HashSet<>());

        return new ArrayList<>(set);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (user != null && user.getRole().equals(RoleUser.GUEST)) {
            saveListofThesis();
        } else {
            Map<String, Object> updates = new HashMap<>();
            List<Tesi> newList = adapter.getmDataList();
            updates.put("tesi", getIdOfThesis(newList));

            FirebaseFirestore.getInstance().collection("tesi_classifiche").document(user.getId()).update(updates);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);

        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(MainActivity.CLASSIFICA_TESI).setChecked(false);
    }
}


