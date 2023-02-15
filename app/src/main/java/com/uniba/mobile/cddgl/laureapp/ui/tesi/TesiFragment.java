package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Relatore;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentTesiBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TesiFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference relatoriRef = db.collection("relatore");
    private final CollectionReference vincoliRef = db.collection("parola_chiave");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FragmentTesiBinding binding;
    private AlertDialog.Builder dialogBuilder, dialogBuilder2;
    private AlertDialog dialog, dialog2;
    private ListView keywordListView;
    private EditText popup_email, eNuovakey, eSkill;
    private TextView popup_nome, relatore;
    private Button cancel, save, verifica, modifica, addKey;
    private BottomNavigationView navBar;
    private CheckBox permesso1, permesso2, permesso3, permesso4;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView vincoloT, Ambito, Tempistiche, Chiave, Skill, paroleChiave, voto;
    private String permessi;
    private ArrayAdapter<String> arrayAdapter2;
    private ArrayList<String> arrayChiavi = new ArrayList<>();
    private Spinner ambito;
    private SeekBar media;
    private Map<String, String> co_relatori = new HashMap<>();
    private String permessiI;


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

        String relatoreEmail = currentUser.getEmail();
        relatore = binding.relatorePrincipale;
        relatore.setText(relatoreEmail);

        ImageView b_aggiungi_relatore = binding.aggiungiRelatore;
        b_aggiungi_relatore.setOnClickListener(view1 -> {
            final View relatorePopup = getLayoutInflater().inflate(R.layout.popup_relatore, null);
            dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setView(relatorePopup);
            dialog = dialogBuilder.create();
            dialog.show();
            viewRelatore(relatorePopup);
            save.setOnClickListener(view11 -> {
                permessi = "";
                aggiungiRelatore(checkPermessi(permessi));
                co_relatori.put(popup_email.getText().toString(), permessiI);
                dialog.dismiss();
            });

            cancel.setOnClickListener(view112 -> dialog.dismiss());
        });

        /*
         * vincoli
         */

        ImageView b_aggiungi_vincoli = binding.aggiungiVincoli;
        final View vincoliPopup = getLayoutInflater().inflate(R.layout.popup_vincoli, null);
        dialogBuilder2 = new AlertDialog.Builder(getContext());
        dialogBuilder2.setView(vincoliPopup);
        dialog2 = dialogBuilder2.create();
        b_aggiungi_vincoli.setOnClickListener(view12 -> {
            viewVincoli(vincoliPopup);
            LinearLayout parentLayout = binding.vincoliTesi;
            dialog2.show();

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.ambiti,
                    android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            ambito.setAdapter(adapter);
            ambito.setOnItemSelectedListener(TesiFragment.this);



            if (!vincoliEmpty()) {
                parentLayout.removeAllViews();
            }

            ArrayList<String> keywordList = new ArrayList<>();
            addKey.setOnClickListener(view1212 -> {
                String nuovakey = String.valueOf(eNuovakey.getText());
                if(!keywordList.contains(nuovakey)) {
                    keywordList.add(nuovakey);
                    arrayAdapter2.notifyDataSetChanged();
                    keywordListView.setItemChecked(keywordList.size()-1, true);
                    paroleChiave.setText("PAROLE CHIAVE ("+keywordListView.getCheckedItemCount()+ ")");
                    Map<String, String> T_chiave = new HashMap<>();
                    T_chiave.put("chiave", nuovakey);
                    db.collection("parola_chiave")
                                .add(T_chiave)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "chiave inserita", Toast.LENGTH_SHORT).show();
                                });
                }
                    else {
                    Toast.makeText(getContext(), "Chiave esistente", Toast.LENGTH_SHORT).show();
                }
            });
            vincoliRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    vincoloT = new TextView(getContext());
                    keywordList.add(documentSnapshot.get("chiave").toString());
                }
                arrayAdapter2 = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_multiple_choice, keywordList);

                keywordListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                keywordListView.setAdapter(arrayAdapter2);
                keywordListView.setOnItemClickListener((adapterView, view121, position, id) -> {
                    paroleChiave.setText("PAROLE CHIAVE ("+keywordListView.getCheckedItemCount()+ ")");
                });
            });

            save.setOnClickListener(view1214 -> {
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = vincoliPopup.findViewById(radioId);
                String tempistiche = radioButton.getText().toString();
                Ambito.setText(ambito.getSelectedItem().toString());
                Tempistiche.setText(tempistiche);
                String chiavi="";
                for(int i=0;i<keywordList.size();i++){
                    if(keywordListView.isItemChecked(i)){
                        arrayChiavi.add(keywordListView.getItemAtPosition(i).toString());
                        chiavi += keywordListView.getItemAtPosition(i).toString()+", ";
                    }
                }
                if(chiavi.length()>2){
                    chiavi = chiavi.substring(0,chiavi.length()-2);
                }
                Chiave.setText(chiavi);
                Skill.setText(eSkill.getText().toString());
                aggiungiVincoli(parentLayout);
                dialog2.dismiss();
            });

            cancel.setOnClickListener(view1215 -> dialog2.dismiss());
        });

        Button salva = binding.salva;
        salva.setOnClickListener(view13 -> {
            EditText nome_tesi = binding.nomeTesi;
            EditText descrizione = binding.descrizione;
            String Nome_tesi = nome_tesi.getText().toString();
            String Relatore = relatore.getText().toString();
            String Descrizione = descrizione.getText().toString();
            if(TextUtils.isEmpty(Nome_tesi) || TextUtils.isEmpty(Descrizione)){
                Toast.makeText(getContext(), "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
            }
            else
            {
                caricaTesi(Nome_tesi, Descrizione, Relatore);
                navController.navigate(R.id.action_tesiFragmant_to_navigation_home);
            }
    });
    }

    private void caricaTesi(String nome_tesi, String descrizione, String relatore) {
        Map<String, Object> T_tesi = new HashMap<>();

        T_tesi.put("nome_tesi", nome_tesi);
        T_tesi.put("descrizione", descrizione);
        T_tesi.put("relatore", relatore);
        T_tesi.put("ambito", Ambito.getText().toString());
        T_tesi.put("chiave", Chiave.getText().toString());
        T_tesi.put("tempistiche", Tempistiche.getText().toString());
        T_tesi.put("skill", Skill.getText().toString());
        T_tesi.put("media", parseInt(voto.getText().toString()));
        T_tesi.put("co_relatori", co_relatori);

        db.collection("tesi")
                .add(T_tesi)
                .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "tesi creata", Toast.LENGTH_SHORT).show();
                        }
                ).addOnFailureListener(e -> Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show());
    }

    public void viewVincoli(View vincoliPopup){
        eSkill = vincoliPopup.findViewById(R.id.CeS_edit);
        eNuovakey = vincoliPopup.findViewById(R.id.nuova_chiave);
        ambito = vincoliPopup.findViewById(R.id.spinner);
        paroleChiave = vincoliPopup.findViewById(R.id.keyword);
        addKey = vincoliPopup.findViewById(R.id.aggiungi_keyword);
        save = vincoliPopup.findViewById(R.id.saveButton);
        cancel = vincoliPopup.findViewById(R.id.cancelButton);
        radioGroup = vincoliPopup.findViewById(R.id.radio);
        keywordListView = vincoliPopup.findViewById(R.id.keywordsList);
        voto = vincoliPopup.findViewById(R.id.voto);
        media = vincoliPopup.findViewById(R.id.media);

        media.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    private void aggiungiVincoli(LinearLayout parent){
        parent.addView(Ambito);
        parent.addView(Tempistiche);
        parent.addView(Chiave);
        parent.addView(Skill);
    }

    private boolean vincoliEmpty() {
        if(Ambito.getText().toString().isEmpty() ||
           Tempistiche.getText().toString().isEmpty()){
            return true;
        }else return false;
    }

    private void impostaVincoli() {
        Ambito = new TextView(getContext());
        Tempistiche = new TextView(getContext());
        Chiave = new TextView(getContext());
        Skill = new TextView(getContext());
        Ambito.setTextSize(20);
        Ambito.setTextColor(Color.BLACK);
        Tempistiche.setTextSize(20);
        Tempistiche.setTextColor(Color.BLACK);
        Chiave.setTextSize(20);
        Chiave.setTextColor(Color.BLACK);
        Skill.setTextSize(20);
        Skill.setTextColor(Color.BLACK);
    }

    private String checkPermessi(String permessi) {
        permessiI="";
        if(permesso1.isChecked()){
            permessi += permesso1.getText() + ", ";
            permessiI += "1";
        }
        if(permesso2.isChecked())
        {
            permessi += permesso2.getText() + ", ";
            permessiI += "2";
        }
        if(permesso3.isChecked()){
            permessi += permesso3.getText() + ", ";
            permessiI += "3";
        }
        if(permesso4.isChecked()){
            permessi += permesso4.getText() + ", ";
            permessiI += "4";
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
        verifica = new Button(this.getContext());
        verifica.setLayoutParams(params);
        verifica.setTextSize(20);
        verifica.setTextColor(Color.WHITE);
        verifica.setBackgroundColor(save.getShadowColor());
        verifica.setText("Verifica email");
        LinearLayout tastiLayout = relatorePopup.findViewById(R.id.tasti);
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
                                    Relatore relatoreObj = documentSnapshot.toObject(Relatore.class);
                                    popup_nome.setText(relatoreObj.getNome() + " " + relatoreObj.getCognome());
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
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "L'email non è presente nel database", Toast.LENGTH_SHORT).show();
                            }
                        });
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

}