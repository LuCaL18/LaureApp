package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Filtri;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentTesiBinding;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.ConstraintsDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreaTesiFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference relatoriRef = db.collection("users");
    private final CollectionReference filtriRef = db.collection("filtri");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<String> esami = new ArrayList<String>();
    private Tesi thesis;
    private FragmentTesiBinding binding;
    private AlertDialog.Builder dialogBuilder, dialogBuilder2, dialogBuilder3;
    private AlertDialog dialog2, dialog3;
    private ListView keywordListView;
    private EditText popup_email, eNuovakey, eSkill, eNote, eNomeTesi, eDescrizione;
    private TextView popup_nome, relatore;
    private Button cancel, save, verifica, modifica, cancel2, addKey, salva, save2, save3, cancel3;
    private BottomNavigationView navBar;
    private CheckBox permesso1, permesso2, permesso3, permesso4;
    private TextView Ambito, Tempistiche, Chiave, Skill, Media, text_settimane, voto, paroleChiave;
    private String permessi, textAmbito;
    private List<String> permessiList = new ArrayList<>();
    private Spinner ambitoSpinner;
    private SeekBar mediaSeek, settimaneSeek;
    private List<PersonaTesi> co_relatori = new ArrayList<>();
    private String skill;
    private int n_settimane = 1;
    private float textmedia;
    public List<String> keywordList = new ArrayList<>();
    public List<Filtri> filtriList = new ArrayList<>();
    public List<String> ambitiList = new ArrayList<>();
    private ListView listViewRelatori;
    private ArrayList<String> listRelatori;
    private ArrayAdapter<String> adapterRelatori;
    private List<String> keywordSelezionate = new ArrayList<>();
    private PersonaTesi relatoreObj;
    private PersonaTesi relatorePrincipaleObj;
    private boolean trovato, trovato2;
    private ImageView addImage;
    private ListAdapter listAdapter;
    private static final int GALLERY_REQUEST_CODE = 100;
    private String guidString;

    public CreaTesiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTesiBinding.inflate(inflater, container, false);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thesis = new Tesi();
        impostaAmbiti();
        setCardConstraintsCreator();

        NavController navController = NavHostFragment.findNavController(this);

        relatorePrincipaleObj = new PersonaTesi(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail());
        relatore = binding.relatorePrincipale;
        relatore.setText(relatorePrincipaleObj.getDisplayName() + " ★");
        salva = binding.salva;
        eNote = binding.note;
        eDescrizione = binding.descrizione;
        eNomeTesi = binding.nomeTesi;
        addImage = binding.addImage;

        listViewRelatori = binding.listaRelatori;
        listRelatori = new ArrayList<>();
        adapterRelatori = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listRelatori);
        listViewRelatori.setAdapter(adapterRelatori);
        listViewRelatori.setOnItemLongClickListener((adapterView, view1, position, l) -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Vuoi rimuovere questo relatore?")
                    .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeItemFromList(position);
                            co_relatori.remove(position);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
            return false;
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });


        /*Imposta la sezione relativa all'aggiunta dei relatori*/
        ImageView b_aggiungi_relatore = binding.aggiungiRelatore;
        View relatorePopup = getLayoutInflater().inflate(R.layout.popup_relatore, null);
        dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(relatorePopup);
        AlertDialog dialog = dialogBuilder.create();

        /*Apertura del popup Relatore */
        b_aggiungi_relatore.setOnClickListener(view1 -> {
            viewRelatore(relatorePopup);
            save.setOnClickListener(view11 -> {
                aggiungiRelatore(checkPermessi());
                unchek();
                PersonaTesi relatore = new PersonaTesi(relatoreObj.getId(), relatoreObj.getDisplayName(), relatoreObj.getEmail(), permessiList);
                co_relatori.add(relatore);
                addItemToList(relatore.getDisplayName() + " " + relatore.getPermissions());
                resetPopupRelatore();
                dialog.dismiss();
            });
            cancel.setOnClickListener(view112 -> dialog.dismiss());
            dialog.show();
        });

        /*Imposta la sezione relativa all'aggiunta degli ambiti*/
        ImageView b_aggiungi_ambiti = binding.aggiungiAmbiti;
        final View ambitiPopup = getLayoutInflater().inflate(R.layout.popup_ambiti, null);
        dialogBuilder3 = new AlertDialog.Builder(getContext());
        dialogBuilder3.setView(ambitiPopup);
        dialog3 = dialogBuilder3.create();
        b_aggiungi_ambiti.setOnClickListener(view1 -> {
            viewAmbiti(ambitiPopup);
            LinearLayout parentLayout = binding.ambitiTesi;
            filtriRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    paroleChiave.setText("Parole chiave");
                    keywordListView.clearChoices();
                    keywordSelezionate.clear();
                    filtriList = documentSnapshots.toObjects(Filtri.class);
                    ambitiList = filtriList.get(0).getLista();
                    keywordList = filtriList.get(1).getLista();

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, ambitiList);
                    ambitoSpinner.setAdapter(adapter);

                    listAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            keywordList
                    );
                    keywordListView.setAdapter(listAdapter);
                    keywordListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    keywordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(!keywordListView.isItemChecked(position)){
                                keywordListView.getChildAt(position);
                            }
                            else{
                            }
                            paroleChiave.setText("Parole chiave (" + keywordListView.getCheckedItemCount()+")");
                        }

                    });
                }

            });

            save3.setOnClickListener(view2 -> {
                int i = 0;
                int j = 0;
                while (j < keywordListView.getCheckedItemCount() && i<keywordListView.getCount()) {
                    if (keywordListView.isItemChecked(i)) {
                        keywordSelezionate.add(keywordListView.getItemAtPosition(i).toString());
                        j++;
                    }
                    i++;
                }
                rimuoviAmbiti(parentLayout);
                textAmbito = ambitoSpinner.getSelectedItem().toString();
                Chiave.setText("("+stampaKey(keywordSelezionate)+")");
                Ambito.setText("AMBITO: " + textAmbito);
                aggiungiAmbiti(parentLayout);
                dialog3.dismiss();
            });
            cancel3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog3.dismiss();
                }
            });
            dialog3.show();
        });


        /*
        //Imposta la sezione relativa all'aggiunta dei vincoli
        ImageView b_aggiungi_vincoli = binding.aggiungiVincoli;
        final View vincoliPopup = getLayoutInflater().inflate(R.layout.popup_vincoli, null);
        dialogBuilder2 = new AlertDialog.Builder(getContext());
        dialogBuilder2.setView(vincoliPopup);
        dialog2 = dialogBuilder2.create();


        //Apertura del popup Vincoli
        b_aggiungi_vincoli.setOnClickListener(view12 -> {
            viewVincoli(vincoliPopup);
            LinearLayout parentLayout = binding.vincoliTesi;

            save2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rimuoviVincoli(parentLayout);
                    textmedia = parseInt(voto.getText().toString());
                    n_settimane = parseInt(text_settimane.getText().toString());
                    Tempistiche.setText("SETTIMANE PREVISTE: " + n_settimane);
                    Media.setText("MEDIA: " + textmedia);
                    if(eSkill.getText().toString().equals(""))
                    {
                        Skill.setText("NESSUNA SKILL RICHIESTA");
                    }
                    else{
                        Skill.setText("SKILL: " + eSkill.getText().toString());
                    }
                    aggiungiVincoli(parentLayout);
                    dialog2.dismiss();
                }
                });
            cancel2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                }
            });
            dialog2.show();
        });
        */
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eDescrizione.getText().length()<1 || eNomeTesi.getText().length()<1 || textAmbito == null){
                    Toast.makeText(getContext(), "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
                }
                else {
                    caricaTesi();
                    navController.popBackStack();
                }
            }
        });
    }

    private void resetPopupRelatore() {
        popup_email.setText(null);
        popup_nome.setText(null);
        modifica.setVisibility(View.VISIBLE);
        verifica.setVisibility(View.GONE);
        popup_email.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
    }

    private void unchek() {
        permesso1.setChecked(false);
        permesso2.setChecked(false);
        permesso3.setChecked(false);
        permesso4.setChecked(false);
    }

    private void caricaTesi() {
        List<String> documents = new ArrayList<String>();

        Tesi tesi = new Tesi(eNomeTesi.getText().toString(), co_relatori, relatorePrincipaleObj, eDescrizione.getText().toString(), textAmbito, keywordSelezionate, Skill.getText().toString(), n_settimane, esami, textmedia, documents, null, eNote.getText().toString());

            db.collection("tesi").document(tesi.getId())
                .set(tesi, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    saveKey();
                    Toast.makeText(getContext(), "tesi creata", Toast.LENGTH_SHORT).show();
                        }
                ).addOnFailureListener(e -> Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show());
    }

    private String stampaKey(List<String> keywordSelezionate) {
        String textKeySelezionate = "";
        for (String key: keywordSelezionate) {
            textKeySelezionate += key+", ";
        }
        if(textKeySelezionate.length()>2)
            textKeySelezionate= textKeySelezionate.substring(0, textKeySelezionate.length()-2);
        return textKeySelezionate;
    }

    public void viewAmbiti(View ambitiPopup){
        eNuovakey = ambitiPopup.findViewById(R.id.nuova_chiave);
        paroleChiave = ambitiPopup.findViewById(R.id.keyword);
        addKey = ambitiPopup.findViewById(R.id.aggiungi_keyword);
        save3 = ambitiPopup.findViewById(R.id.save3Button);
        cancel3 = ambitiPopup.findViewById(R.id.cancel3Button);
        keywordListView = ambitiPopup.findViewById(R.id.multiply);
        ambitoSpinner = ambitiPopup.findViewById(R.id.spinner);

        addKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean findit = false;
                if(!eNuovakey.getText().toString().equals("")){
                    for (String key: keywordList) {
                        if(key.equals(eNuovakey.getText().toString())){
                            findit=true;
                            break;
                        }
                    }
                    if (findit){
                        Toast.makeText(getContext(),"Chiave esistente", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Creare una lista per gli elementi selezionati
                        List<Integer> selectedItems = new ArrayList<>();

                        // Recuperare l'adattatore della ListView
                        ListAdapter adapter = keywordListView.getAdapter();

                        // Iterare sugli elementi della ListView
                        for (int i = 0; i < adapter.getCount(); i++) {
                            // Controllare se l'elemento è selezionato
                            if (keywordListView.isItemChecked(i)) {
                                selectedItems.add(i);
                            }
                        }
                        keywordList.add(eNuovakey.getText().toString());
                        listAdapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_list_item_multiple_choice,
                                keywordList
                        );
                        keywordListView.setAdapter(listAdapter);

                        for (Integer elemento : selectedItems) {
                            keywordListView.setItemChecked(elemento, true);
                        }
                        keywordListView.setItemChecked(keywordListView.getCount()-1, true);
                        paroleChiave.setText("Parole chiave (" + keywordListView.getCheckedItemCount()+")");
                        Toast.makeText(getContext(), "Lista aggiornata", Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(getContext(), "Inserire una chiave", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveKey() {
        DocumentReference documentRef = filtriRef.document("parole_chiave");

        Map<String, Object> mapParole = new HashMap<>();
        mapParole.put("lista", keywordList);

        // Aggiungi la lista di elementi al documento
        documentRef.update(mapParole)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La lista di elementi è stata aggiunta con successo al documento
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Si è verificato un errore durante l'aggiunta della lista di elementi al documento
                    }
                });
    }

    public void viewVincoli(View vincoliPopup){
        eSkill = vincoliPopup.findViewById(R.id.CeS_edit);
        save2 = vincoliPopup.findViewById(R.id.save2Button);
        cancel2 = vincoliPopup.findViewById(R.id.cancel2Button);
        settimaneSeek = vincoliPopup.findViewById(R.id.settimane);
        text_settimane = vincoliPopup.findViewById(R.id.n_settimane);
        settimaneSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar settimane, int progress, boolean fromUser) {
                text_settimane.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar settimane) {}

            @Override
            public void onStopTrackingTouch(SeekBar settimane) {}
        });

        voto = vincoliPopup.findViewById(R.id.voto);
        mediaSeek = vincoliPopup.findViewById(R.id.media);

        mediaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar media, int progress, boolean fromUser) {
                voto.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar media) {}

            @Override
            public void onStopTrackingTouch(SeekBar media) {}
        });
    }

    private void aggiungiAmbiti(LinearLayout parent){
        parent.addView(Ambito);
        parent.addView(Chiave);
    }

    private void rimuoviAmbiti(LinearLayout parent){
        parent.removeView(Ambito);
        parent.removeView(Chiave);
    }

    private void aggiungiVincoli(LinearLayout parent){
        parent.addView(Skill);
        parent.addView(Media);
        parent.addView(Tempistiche);
    }

    private void rimuoviVincoli(LinearLayout parent){
        parent.removeView(Skill);
        parent.removeView(Media);
        parent.removeView(Tempistiche);
    }

    private void impostaAmbiti(){
        Ambito = new TextView(getContext());
        Chiave = new TextView(getContext());
        Ambito.setTextSize(20);
        Ambito.setTextColor(Color.BLACK);
        Chiave.setTextSize(20);
        Chiave.setTextColor(Color.BLACK);
    }

    private void impostaVincoli() {
        Tempistiche = new TextView(getContext());
        Skill = new TextView(getContext());
        Media = new TextView(getContext());
        Media.setTextSize(20);
        Media.setTextColor(Color.BLACK);
        Tempistiche.setTextSize(20);
        Tempistiche.setTextColor(Color.BLACK);
        Skill.setTextSize(20);
        Skill.setTextColor(Color.BLACK);
    }

    private String checkPermessi() {
        permessiList.clear();
        permessi="";
        if(permesso1.isChecked()){
            permessi += permesso1.getText() + ", ";
            permessiList.add(permesso1.getText().toString());
        }
        if(permesso2.isChecked())
        {
            permessi += permesso2.getText() + ", ";
            permessiList.add(permesso2.getText().toString());
        }
        if(permesso3.isChecked()){
            permessi += permesso3.getText() + ", ";
            permessiList.add(permesso3.getText().toString());
        }
        if(permesso4.isChecked()){
            permessi += permesso4.getText() + ", ";
            permessiList.add(permesso4.getText().toString());
        }
        if(permessiList.isEmpty()){
            permessi += "nessun permesso";
        }
        return permessi;
    }

    private void viewRelatore(View relatorePopup) {
        popup_nome = relatorePopup.findViewById(R.id.nome);
        popup_email = relatorePopup.findViewById(R.id.email);
        permesso1 = relatorePopup.findViewById(R.id.edit_search_keys_permission);
        permesso2 = relatorePopup.findViewById(R.id.edit_documents_permission);
        permesso3 = relatorePopup.findViewById(R.id.edit_constraints_permission);
        permesso4 = relatorePopup.findViewById(R.id.edit_notes_permission);
        save = relatorePopup.findViewById(R.id.saveButton);
        cancel = relatorePopup.findViewById(R.id.cancelButton);
        verifica = (Button) impostaVerifica(relatorePopup);
    }

    private void aggiungiRelatore(String permessi) {
        TextView nuovo_relatore = new TextView(getContext());
        nuovo_relatore.setText(popup_nome.getText() + " " + " (" + permessi + ").");
        nuovo_relatore.setTextSize(20);
        nuovo_relatore.setTextColor(Color.BLACK);
    }

    private View impostaModifica(View relatorePopup) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        modifica = new Button(this.getContext());
        modifica.setLayoutParams(params);
        modifica.setTextSize(20);
        modifica.setBackgroundColor(save.getShadowColor());
        modifica.setTextColor(Color.WHITE);
        modifica.setText("modifica mail");
        LinearLayout tastiLayout = relatorePopup.findViewById(R.id.tasti);
        tastiLayout.removeView(verifica);
        tastiLayout.addView(modifica);
        return modifica;
    }

    private View impostaVerifica(View relatorePopup) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout tastiLayout = relatorePopup.findViewById(R.id.tasti);
        tastiLayout.removeView(verifica);
        verifica = new Button(this.getContext());
        verifica.setLayoutParams(params);
        verifica.setTextSize(20);
        verifica.setTextColor(Color.WHITE);
        verifica.setBackgroundColor(save.getShadowColor());
        verifica.setText("Verifica email");
        trovato = false;
        verifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trovato2= false;
                for(PersonaTesi relatore : co_relatori){
                    if(relatore.getEmail().equals(popup_email.getText().toString())){
                        trovato2=true;
                    }
                }
                if(trovato2==true){
                    Toast.makeText(getContext(), "Relatore già presente", Toast.LENGTH_SHORT).show();
                }
                else {
                    relatoriRef.whereEqualTo("email", popup_email.getText().toString())
                            .limit(1)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        trovato = true;
                                        relatoreObj = documentSnapshot.toObject(PersonaTesi.class);
                                        popup_nome.setText(relatoreObj.getDisplayName());
                                        popup_nome.setVisibility(View.VISIBLE);
                                        popup_email.setVisibility(View.INVISIBLE);
                                        modifica = (Button) impostaModifica(relatorePopup);
                                        modifica.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                popup_nome.setVisibility(View.INVISIBLE);
                                                popup_email.setVisibility(View.VISIBLE);
                                                save.setVisibility(View.GONE);
                                                impostaVerifica(relatorePopup);
                                            }
                                        });
                                        save.setVisibility(View.VISIBLE);
                                    }

                            }
                });
                }
            }
        });
        tastiLayout.removeView(modifica);
        tastiLayout.addView(verifica);
        return verifica;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            addImage.setImageURI(selectedImageUri);
            int height = 550;
            int width = 550;
            ViewGroup.LayoutParams layoutParams = addImage.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;

            addImage.setLayoutParams(layoutParams);
        }
    }

    // Metodo per aggiungere un elemento alla lista
    private void addItemToList(String item) {
        listRelatori.add(item);
        adapterRelatori.notifyDataSetChanged();

        // Ottenere i parametri di layout correnti della ListView
        ViewGroup.LayoutParams params = listViewRelatori.getLayoutParams();

        // Impostare la nuova altezza desiderata
        params.height += 125;

        // Applicare i nuovi parametri di layout alla ListView
        listViewRelatori.setLayoutParams(params);
    }

    // Metodo per rimuovere un elemento dalla lista
    private void removeItemFromList(int position) {
        listRelatori.remove(position);
        adapterRelatori.notifyDataSetChanged();

            // Ottenere i parametri di layout correnti della ListView
            ViewGroup.LayoutParams params = listViewRelatori.getLayoutParams();
            // Impostare la nuova altezza desiderata
            params.height -= 125;

            // Applicare i nuovi parametri di layout alla ListView
            listViewRelatori.setLayoutParams(params);
    }

    private void setCardConstraintsCreator() {
        ImageView constraintsArrowCard = binding.aggiungiVincoli;
        constraintsArrowCard.setImageResource(R.drawable.ic_baseline_edit_note_24);
        constraintsArrowCard.setClickable(true);

        CreaTesiFragment requireFragment = this;

        constraintsArrowCard.setOnClickListener(view -> {
            final View vincoliPopup = getLayoutInflater().inflate(R.layout.popup_edit_constraints, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setView(vincoliPopup);
            AlertDialog dialog = dialogBuilder.create();

            ConstraintsDialog constraintsDialog = new ConstraintsDialog(dialog, vincoliPopup, requireFragment);
            constraintsDialog.show();
        });
    }

    public void updateConstraints(int tempistiche, float mediaVoto, List<String> esamiNecessari, String skills) {
        impostaVincoli();
        LinearLayout parentLayout = binding.vincoliTesi;

        n_settimane = tempistiche;
        skill = skills;
        esami = esamiNecessari;
        textmedia = mediaVoto;

        rimuoviVincoli(parentLayout);

        Tempistiche.setText("SETTIMANE PREVISTE: " + n_settimane);
        Media.setText("MEDIA: " + textmedia);
        if(skill.isEmpty())
        {
            Skill.setText("NESSUNA SKILL RICHIESTA");
        }
        else{
            Skill.setText("SKILL: " + skill);
        }
        aggiungiVincoli(parentLayout);
    }

}