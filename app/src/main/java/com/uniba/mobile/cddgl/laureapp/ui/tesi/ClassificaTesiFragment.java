package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificaTesiFragment extends Fragment {

    private ListView listView;
    private ClassificaTesiAdapter adapter;
    private Map<String, TesiClassifica> classifica;
    private TesiClassifica dataList;
    private CollectionReference mCollection;
    private BottomNavigationView navBar;
    private List<Tesi> listaTesiOriginale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_tesi, container, false);
        listView = view.findViewById(R.id.classifica_tesi);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        classifica = new HashMap<>();
        dataList = null;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String studenteId = currentUser.getUid();
        mCollection = FirebaseFirestore.getInstance().collection("tesi_classifiche");
        adapter = new ClassificaTesiAdapter(getActivity(), mCollection);
        Log.d("ClassificaTesiFragment", "onCreateView() method called");
        listView.setAdapter(adapter);
        mCollection.whereEqualTo("studenteId", studenteId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                classifica.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    TesiClassifica tesiClassifica = doc.toObject(TesiClassifica.class);
                    dataList = tesiClassifica;
                }
                listaTesiOriginale = dataList.getTesi();
                classifica.put("classificaTesi", dataList);
                Log.d("ClassificaTesiFragment", "onCreateView() method called");
                adapter.notifyDataSetChanged();
            }
        });
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
        ImageButton menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), menuButton);
                popup.getMenuInflater().inflate(R.menu.menu_classifica_layout, popup.getMenu());
                List<Tesi> listaTesiOrdinata = new ArrayList<>(listaTesiOriginale); // crea una nuova lista ordinata a partire dalla lista originale
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.defaultClassifica:
                                listaTesiOrdinata.clear(); // pulisci la lista ordinata e aggiungi nuovamente tutti gli elementi dalla lista originale
                                listaTesiOrdinata.addAll(listaTesiOriginale);
                                break;
                            case R.id.ambito2:
                                // Salva la classifica tesi originaria
                                TesiClassifica copia2 = dataList;
                                // Crea una lista di opzioni per il RadioGroup
                                final String[] opzioni2 = {"Ingegneria","Informatica","Economia","Medicina","Psicologia","Lettere","Architettura","Biologia","Giurisprudenza"};
                                // Crea un nuovo RadioGroup
                                RadioGroup radioGroup2 = new RadioGroup(getActivity());
                                radioGroup2.setOrientation(RadioGroup.VERTICAL);
                                // Cicla attraverso le opzioni e crea un nuovo RadioButton per ciascuna di esse
                                for (String opzione : opzioni2) {
                                    RadioButton radioButton2 = new RadioButton(getActivity());
                                    radioButton2.setText(opzione);
                                    radioGroup2.addView(radioButton2);
                                }
                                // Aggiungi il RadioGroup al LinearLayout del dialog
                                LinearLayout layout2 = new LinearLayout(getActivity());
                                layout2.setOrientation(LinearLayout.VERTICAL);
                                layout2.addView(radioGroup2);
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                                builder2.setTitle("Filtra per tempistiche");
                                builder2.setMessage("Seleziona una delle seguenti opzioni:");
                                // Aggiungi il layout personalizzato al dialog
                                builder2.setView(layout2);
                                // Aggiungi il pulsante "OK" al dialog
                                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int radioButtonID = radioGroup2.getCheckedRadioButtonId();
                                        View radioButton = radioGroup2.findViewById(radioButtonID);
                                        int index = radioGroup2.indexOfChild(radioButton);
                                        String selectedOption = opzioni2[index];
                                        // Qui devi implementare il filtro per le tempistiche utilizzando il testo inserito dall'utente
                                        // e aggiornare la lista delle tesi visualizzate di conseguenza
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        // Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di tempistiche
                                        for (Tesi t : copia2.getTesi()) {
                                            String ambito = t.getAmbito();
                                            if (selectedOption.startsWith(String.valueOf(ambito))) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        // Aggiorna la lista delle tesi visualizzate con quelle filtrate
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                // Aggiungi il pulsante "Annulla" al dialog
                                builder2.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                // Visualizza il dialog
                                builder2.show();
                                break;
                            case R.id.chiave2:
                                // Salva classifica tesi originaria //
                                TesiClassifica copia3 = dataList;
                                // Codice da inserire nel metodo onClick per l'ambito
                                final EditText input3 = new EditText(getActivity());
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                                builder3.setTitle("Filtra per chiave");
                                builder3.setMessage("Inserisci il campo di ricerca per la chiave:");
                                // Aggiungi il campo di testo personalizzato all'interno del dialog
                                builder3.setView(input3);
                                // Aggiungi il pulsante "OK" al dialog
                                builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String text3 = input3.getText().toString();
                                        // Qui devi implementare il filtro per l'ambito utilizzando il testo inserito dall'utente
                                        // e aggiornare la lista delle tesi visualizzate di conseguenza
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        // Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di ambito
                                        for (Tesi t : dataList.getTesi()) {
                                            if (t.getChiavi().contains(text3)) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        // Aggiorna la lista delle tesi visualizzate con quelle filtrate
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                // Aggiungi il pulsante "Annulla" al dialog
                                builder3.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                // Visualizza il dialog
                                builder3.show();
                                dataList.equals(copia3);
                                break;
                            case R.id.mediaVoto2:
                                // Salva la classifica tesi originaria
                                TesiClassifica copia4 = dataList;
                                // Crea il layout del dialog
                                LayoutInflater inflater4 = LayoutInflater.from(getActivity());
                                View dialogView4 = inflater4.inflate(R.layout.dialog_seekbar, null);
                                // Inizializza la seekbar
                                final TextView textView4 = dialogView4.findViewById(R.id.seekbar_value);
                                final SeekBar seekBar4 = dialogView4.findViewById(R.id.seekbar);
                                seekBar4.setMax(24);
                                seekBar4.setMin(0);
                                seekBar4.setProgress(0);
                                textView4.setText(String.valueOf((seekBar4.getProgress() * 0.5f) + 18.0f));
                                // Crea il dialog
                                AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
                                builder4.setTitle("Filtra per media voto");
                                builder4.setView(dialogView4);
                                // Aggiungi il pulsante "OK" al dialog
                                builder4.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        float voto = (seekBar4.getProgress() * 0.5f) + 18.0f;
                                        // Qui devi implementare il filtro per il media voto utilizzando la seekbar
                                        // e aggiornare la lista delle tesi visualizzate di conseguenza
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        // Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di media voto
                                        for (Tesi t : dataList.getTesi()) {
                                            if (t.getMediaVoto() >= voto) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        // Aggiorna la lista delle tesi visualizzate con quelle filtrate
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                // Aggiungi il pulsante "Annulla" al dialog
                                builder4.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                // Aggiungi un listener per aggiornare il valore della seekbar sulla textview
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
                                // Visualizza il dialog
                                builder4.show();
                                dataList.equals(copia4);
                                break;
                            case R.id.tempistiche2:
                                // Salva la classifica tesi originaria
                                TesiClassifica copia = dataList;
                                // Crea una lista di opzioni per il RadioGroup
                                final String[] opzioni = {"1 mese","2 mesi", "3 mesi", "4 mesi", "5 mesi","6 mesi"};
                                // Crea un nuovo RadioGroup
                                RadioGroup radioGroup = new RadioGroup(getActivity());
                                radioGroup.setOrientation(RadioGroup.VERTICAL);
                                // Cicla attraverso le opzioni e crea un nuovo RadioButton per ciascuna di esse
                                for (String opzione : opzioni) {
                                    RadioButton radioButton = new RadioButton(getActivity());
                                    radioButton.setText(opzione);
                                    radioGroup.addView(radioButton);
                                }
                                // Aggiungi il RadioGroup al LinearLayout del dialog
                                LinearLayout layout = new LinearLayout(getActivity());
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.addView(radioGroup);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Filtra per tempistiche");
                                builder.setMessage("Seleziona una delle seguenti opzioni:");
                                // Aggiungi il layout personalizzato al dialog
                                builder.setView(layout);
                                // Aggiungi il pulsante "OK" al dialog
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                                        View radioButton = radioGroup.findViewById(radioButtonID);
                                        int index = radioGroup.indexOfChild(radioButton);
                                        String selectedOption = opzioni[index];
                                        // Qui devi implementare il filtro per le tempistiche utilizzando il testo inserito dall'utente
                                        // e aggiornare la lista delle tesi visualizzate di conseguenza
                                        List<Tesi> tesiFiltrate = new ArrayList<>();
                                        // Cicla attraverso tutte le tesi per verificare se soddisfano il vincolo di tempistiche
                                        for (Tesi t : copia.getTesi()) {
                                            int tempistiche = t.getTempistiche();
                                            if (selectedOption.startsWith(String.valueOf(tempistiche))) {
                                                tesiFiltrate.add(t);
                                            }
                                        }
                                        // Aggiorna la lista delle tesi visualizzate con quelle filtrate
                                        adapter.updateList(tesiFiltrate);
                                    }
                                });
                                // Aggiungi il pulsante "Annulla" al dialog
                                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                // Visualizza il dialog
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
                        // aggiorna l'adapter con la nuova lista ordinata
                        adapter.addTesi(listaTesiOrdinata);
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                });
                popup.show();
            }
        });
        return view;
    }

    private void shareClassifica() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Classifica Tesi");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ecco la mia classifica di tesi: " + dataList.getTesi());
        startActivity(Intent.createChooser(shareIntent, "Condividi la classifica tramite"));
    }

}

