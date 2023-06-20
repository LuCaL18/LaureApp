package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Filtri;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentTesiBinding;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.RelatorsAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.CoRelatoreDialoog;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.ConstraintsDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreaTesiFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference filtriRef = db.collection("filtri");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<String> esami = new ArrayList<String>();
    private Tesi thesis;
    private FragmentTesiBinding binding;
    private AlertDialog.Builder dialogBuilder3;
    private RelatorsAdapter relatorsAdapter;
    private RecyclerView recyclerViewRelators;
    private AlertDialog dialog3;
    private ListView keywordListView;
    private EditText eNuovakey, eNote, eNomeTesi, eDescrizione;
    private TextView relatore;
    private Button addKey, salva, save3, cancel3;
    private BottomNavigationView navBar;
    private TextView Ambito, Tempistiche, Chiave, Skill, Media, paroleChiave;
    private String textAmbito;
    private List<String> permessiList = new ArrayList<>();
    private Spinner ambitoSpinner;
    private List<PersonaTesi> co_relatori = new ArrayList<>();
    private String skill;
    private int n_settimane = 1;
    private float textmedia;
    public List<String> keywordList = new ArrayList<>();
    public List<Filtri> filtriList = new ArrayList<>();
    public List<String> ambitiList = new ArrayList<>();
    private List<String> keywordSelezionate = new ArrayList<>();
    private PersonaTesi relatorePrincipaleObj;
    private ImageView addImage;
    private ListAdapter listAdapter;
    private static final int REQUEST_PICK_IMAGE = 1;
    private Uri selectedImageUri;

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
        recyclerViewRelators = binding.listaRelatori;
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

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        /*Imposta la sezione relativa all'aggiunta dei relatori*/
        setCardCoRelatorsCreator();

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

    private void caricaTesi() {
        List<String> documents = new ArrayList<String>();

        Tesi tesi = new Tesi(eNomeTesi.getText().toString(), co_relatori, relatorePrincipaleObj, eDescrizione.getText().toString(), textAmbito, keywordSelezionate, Skill.getText().toString(), n_settimane, esami, textmedia, documents, null, eNote.getText().toString());
        uploadImageToFirebase(tesi);

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


    private void aggiungiAmbiti(LinearLayout parent){
        parent.addView(Ambito);
        parent.addView(Chiave);
    }

    private void rimuoviAmbiti(LinearLayout parent){
        parent.removeView(Ambito);
        parent.removeView(Chiave);
    }

    private void aggiungiVincoli(LinearLayout parent){
        parent.removeAllViews();
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

    private void setCardConstraintsCreator() {
        ImageView constraintsArrowCard = binding.aggiungiVincoli;
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

    private void setCardCoRelatorsCreator() {
        ImageView relatorsArrowCard = binding.aggiungiRelatore;
        CreaTesiFragment requireFragment = this;

        relatorsArrowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View relatorePopup = getLayoutInflater().inflate(R.layout.popup_relatore, null);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setView(relatorePopup);
                AlertDialog dialog = dialogBuilder.create();

                CoRelatoreDialoog coRelatoreDialoog = new CoRelatoreDialoog(dialog, relatorePopup, thesis.getCoRelatori(), requireFragment);
                coRelatoreDialoog.show();
            }
        });
    }

    public void addCoRelator(PersonaTesi coRelator) {
        co_relatori.add(coRelator);
        relatorsAdapter = null;
        relatorsAdapter = new RelatorsAdapter(co_relatori, true, this);
        recyclerViewRelators.setAdapter(relatorsAdapter);
        // 4. Notifica all'adattatore che è stato aggiunto un nuovo elemento
        relatorsAdapter.notifyDataSetChanged();
        // Opzionalmente, scorrere la RecyclerView per visualizzare l'elemento appena aggiunto
        recyclerViewRelators.scrollToPosition(0);
        recyclerViewRelators.setVisibility(View.VISIBLE);
        recyclerViewRelators.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void removeCoRelator(PersonaTesi coRelator) {
        co_relatori.remove(coRelator);
        relatorsAdapter = new RelatorsAdapter(co_relatori, true, this);
        recyclerViewRelators.setAdapter(relatorsAdapter);
        relatorsAdapter.notifyDataSetChanged();
    }

    private void uploadImageToFirebase(Tesi tesi) {
        if (selectedImageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("tesi").child(generateUniqueFileName());

            UploadTask uploadTask = storageRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // L'immagine è stata caricata con successo su Firebase Storage
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            tesi.setImageTesi(imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Si è verificato un errore durante il caricamento dell'immagine su Firebase Storage
                }
            });
        }
    }

    // Metodo per avviare l'Intent per selezionare un'immagine dalla galleria
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    // Gestisci la risposta dell'Intent
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == MainActivity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            addImage.setImageURI(selectedImageUri);
        }
    }

    private String generateUniqueFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "image_" + timeStamp + ".jpg"; // Modifica l'estensione del file se necessario
    }

}