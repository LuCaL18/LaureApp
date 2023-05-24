package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static android.app.Activity.RESULT_OK;
import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Filtri;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentTesiBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TesiFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference relatoriRef = db.collection("users");
    private final CollectionReference filtriRef = db.collection("filtri");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FragmentTesiBinding binding;
    private AlertDialog.Builder dialogBuilder, dialogBuilder2;
    private AlertDialog dialog, dialog2;
    private ListView keywordListView;
    private EditText popup_email, eNuovakey, eSkill, eNote, eNomeTesi, eDescrizione;
    private TextView popup_nome, relatore;
    private Button cancel, save, verifica, modifica, cancel2, addKey, salva, save2;
    private BottomNavigationView navBar;
    private CheckBox permesso1, permesso2, permesso3, permesso4;
    private TextView Ambito, Tempistiche, Chiave, Skill, Media, text_settimane, voto, paroleChiave;
    private String permessi, textAmbito;
    private List<Integer> permessiList = new ArrayList<>();
    private Spinner ambitoSpinner;
    private SeekBar mediaSeek, settimaneSeek;
    private List<PersonaTesi> co_relatori = new ArrayList<>();
    private int n_settimane = 0;
    private int textmedia;
    public List<String> keywordList = new ArrayList<>();
    public List<Filtri> filtriList = new ArrayList<>();
    public List<String> ambitiList = new ArrayList<>();
    public List<String> nuove_key = new ArrayList<>();
    private List<String> keywordSelezionate = new ArrayList<>();
    private LoggedInUser relatoreObj;
    private PersonaTesi relatorePrincipaleObj;
    private boolean trovato;
    private ImageView addImage;
    private ListAdapter listAdapter;
    private static final int GALLERY_REQUEST_CODE = 100;

    public TesiFragment() {
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
        impostaVincoli();

        NavController navController = NavHostFragment.findNavController(this);

        relatorePrincipaleObj = new PersonaTesi(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail());
        relatore = binding.relatorePrincipale;
        relatore.setText(relatorePrincipaleObj.getEmail());
        salva = binding.salva;
        eNote = binding.note;
        eDescrizione = binding.descrizione;
        eNomeTesi = binding.nomeTesi;
        addImage = binding.addImage;

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });


        /*Imposta la sezione relativa all'aggiunta dei relatori*/
        ImageView b_aggiungi_relatore = binding.aggiungiRelatore;
        final View relatorePopup = getLayoutInflater().inflate(R.layout.popup_relatore, null);
        dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(relatorePopup);
        dialog = dialogBuilder.create();

        /*Apertura del popup Relatore */
        b_aggiungi_relatore.setOnClickListener(view1 -> {
            viewRelatore(relatorePopup);
            save.setOnClickListener(view11 -> {
                permessi = "";
                aggiungiRelatore(checkPermessi(permessi));
                PersonaTesi relatore = new PersonaTesi(relatoreObj.getId(), relatoreObj.getDisplayName(), relatoreObj.getEmail(), permessiList);
                co_relatori.add(relatore);
                dialog.dismiss();
            });
            cancel.setOnClickListener(view112 -> dialog.dismiss());
            dialog.show();
        });

        /*Imposta la sezione relativa all'aggiunta dei vincoli*/
        ImageView b_aggiungi_vincoli = binding.aggiungiVincoli;
        final View vincoliPopup = getLayoutInflater().inflate(R.layout.popup_vincoli, null);
        dialogBuilder2 = new AlertDialog.Builder(getContext());
        dialogBuilder2.setView(vincoliPopup);
        dialog2 = dialogBuilder2.create();

        /*Apertura del popup Vincoli*/
        b_aggiungi_vincoli.setOnClickListener(view12 -> {
            viewVincoli(vincoliPopup);
            LinearLayout parentLayout = binding.vincoliTesi;
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
            save2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rimuoviVincoli(parentLayout);
                    int i = 0;
                    int j = 0;
                    while (j < keywordListView.getCheckedItemCount() && i<keywordListView.getCount()) {
                        if (keywordListView.isItemChecked(i)) {
                            keywordSelezionate.add(keywordListView.getItemAtPosition(i).toString());
                            j++;
                        }
                        i++;
                    }
                    textmedia = parseInt(voto.getText().toString());
                    n_settimane = parseInt(text_settimane.getText().toString());
                    textAmbito = ambitoSpinner.getSelectedItem().toString();
                    Chiave.setText("("+stampaKey(keywordSelezionate)+")");
                    Tempistiche.setText("SETTIMANE PREVISTE: " + n_settimane);
                    Media.setText("MEDIA: " + textmedia);
                    Ambito.setText("AMBITO: " + textAmbito);
                    Skill.setText("SKILL: " + eSkill.getText().toString());
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
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eDescrizione.getText().length()<1 || eNomeTesi.getText().length()<1 || textAmbito == null){
                    Toast.makeText(getContext(), "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
                }
                else {
                    caricaTesi();
                    navController.navigate(R.id.action_tesiFragmant_to_navigation_home);
                }
            }
        });
    }

    private void caricaTesi() {
        Map<String, Object> T_tesi = new HashMap<>();
        long millis = System.currentTimeMillis();

        T_tesi.put("nomeTesi", eNomeTesi.getText().toString());
        T_tesi.put("descrizione", eDescrizione.getText().toString());
        T_tesi.put("relatore", relatorePrincipaleObj);
        T_tesi.put("ambito", textAmbito);
        T_tesi.put("chiavi", keywordSelezionate);
        T_tesi.put("tempistiche", n_settimane);
        T_tesi.put("skill", Skill.getText().toString());
        T_tesi.put("mediaVoto", textmedia);
        T_tesi.put("coRelatori", co_relatori);
        T_tesi.put("created_at", millis);
        T_tesi.put("note", eNote.getText().toString());
        T_tesi.put("student", null);
        T_tesi.put("isAssigned", false);
        T_tesi.put("imageTesi", null);
        T_tesi.put("documents", null);
        db.collection("tesi")
                .add(T_tesi)
                .addOnSuccessListener(documentReference -> {
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

    public void viewVincoli(View vincoliPopup){
        paroleChiave = vincoliPopup.findViewById(R.id.keyword);
        eSkill = vincoliPopup.findViewById(R.id.CeS_edit);
        eNuovakey = vincoliPopup.findViewById(R.id.nuova_chiave);
        ambitoSpinner = vincoliPopup.findViewById(R.id.spinner);
        addKey = vincoliPopup.findViewById(R.id.aggiungi_keyword);
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

        keywordListView = vincoliPopup.findViewById(R.id.multiply);
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
                        keywordList.add(eNuovakey.getText().toString());
                        listAdapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_list_item_multiple_choice,
                                keywordList
                        );
                        keywordListView.setAdapter(listAdapter);
                        keywordListView.setSelection(keywordListView.getCount());
                        paroleChiave.setText("Parole chiave");
                        Toast.makeText(getContext(), "Lista aggiornata", Toast.LENGTH_SHORT).show();
                    }
                }
                else Toast.makeText(getContext(), "Inserire una chiave", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aggiungiVincoli(LinearLayout parent){
        parent.addView(Ambito);
        parent.addView(Chiave);
        parent.addView(Skill);
        parent.addView(Media);
        parent.addView(Tempistiche);
    }

    private void rimuoviVincoli(LinearLayout parent){
        parent.removeView(Ambito);
        parent.removeView(Chiave);
        parent.removeView(Skill);
        parent.removeView(Media);
        parent.removeView(Tempistiche);
    }

    private void impostaVincoli() {
        Ambito = new TextView(getContext());
        Tempistiche = new TextView(getContext());
        Chiave = new TextView(getContext());
        Skill = new TextView(getContext());
        Media = new TextView(getContext());
        Ambito.setTextSize(20);
        Ambito.setTextColor(Color.BLACK);
        Media.setTextSize(20);
        Media.setTextColor(Color.BLACK);
        Tempistiche.setTextSize(20);
        Tempistiche.setTextColor(Color.BLACK);
        Chiave.setTextSize(20);
        Chiave.setTextColor(Color.BLACK);
        Skill.setTextSize(20);
        Skill.setTextColor(Color.BLACK);
    }

    private String checkPermessi(String permessi) {
        if(permesso1.isChecked()){
            permessi += permesso1.getText() + ", ";
            permessiList.add(1);
        }
        if(permesso2.isChecked())
        {
            permessi += permesso2.getText() + ", ";
            permessiList.add(2);
        }
        if(permesso3.isChecked()){
            permessi += permesso3.getText() + ", ";
            permessiList.add(3);
        }
        if(permesso4.isChecked()){
            permessi += permesso4.getText() + ", ";
            permessiList.add(4);
        }
        if(permessi.length()>2){
            permessi = permessi.substring(0, permessi.length()-2);
        }
        else
            permessi += "nessun permesso";

        return permessi;
    }

    private void viewRelatore(View relatorePopup) {
        popup_nome = relatorePopup.findViewById(R.id.nome);
        popup_email = relatorePopup.findViewById(R.id.email);
        permesso1 = relatorePopup.findViewById(R.id.permesso1);
        permesso2 = relatorePopup.findViewById(R.id.permesso2);
        permesso3 = relatorePopup.findViewById(R.id.permesso3);
        permesso4 = relatorePopup.findViewById(R.id.permesso4);
        save = relatorePopup.findViewById(R.id.saveButton);
        cancel = relatorePopup.findViewById(R.id.cancelButton);
        verifica = (Button) impostaVerifica(relatorePopup);
    }

    private void aggiungiRelatore(String permessi) {
        LinearLayout parentLayout = binding.relatoriTesi;
        TextView nuovo_relatore = new TextView(getContext());
        nuovo_relatore.setText(popup_nome.getText() + " " + " (" + permessi + ").");
        nuovo_relatore.setTextSize(20);
        nuovo_relatore.setTextColor(Color.BLACK);
        parentLayout.addView(nuovo_relatore);
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
                relatoriRef.whereEqualTo("email", popup_email.getText().toString())
                        .limit(1)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    trovato=true;
                                    relatoreObj = documentSnapshot.toObject(LoggedInUser.class);
                                    popup_nome.setText(relatoreObj.getDisplayName());
                                    popup_nome.setVisibility(View.VISIBLE);
                                    popup_email.setVisibility(View.INVISIBLE);
                                    modifica = (Button) impostaModifica(relatorePopup);
                                    modifica.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popup_nome.setVisibility(View.INVISIBLE);
                                            popup_email.setVisibility(View.VISIBLE);
                                            save.setVisibility(View.INVISIBLE);
                                            impostaVerifica(relatorePopup);
                                        }
                                    });
                                    save.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
        tastiLayout.removeView(modifica);
        tastiLayout.addView(verifica);
        if(trovato==false && popup_email.getText().toString().length()>0) {
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
            popup_email.startAnimation(shake);
        }
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

}