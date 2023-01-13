package com.uniba.mobile.cddgl.laureapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentTesiBinding;

import java.util.HashMap;
import java.util.Map;

public class TesiFragment extends Fragment {

    private FragmentTesiBinding binding;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog.Builder dialogBuilder2;
    private AlertDialog dialog;
    private AlertDialog dialog2;
    private EditText popup_nome, popup_cognome, popup_email, popup_capacita;
    private Button cancel, save, salva;
    private ImageView B_aggiungi_relatore, B_aggiungi_vincoli, B_aggiungi_task;
    private FirebaseFirestore db;
    private BottomNavigationView navBar;
    private CheckBox permesso1, permesso2, permesso3, permesso4;
    private CheckBox vincolo1, vincolo2, vincolo3, vincolo4, vincolo5, vincolo6, vincolo7, vincolo8;

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
        NavController navController = NavHostFragment.findNavController(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        B_aggiungi_relatore = binding.aggiungiRelatore;
        B_aggiungi_relatore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder = new AlertDialog.Builder(getContext());
                final View relatorePopup = getLayoutInflater().inflate(R.layout.popup_relatore, null);
                popup_nome = relatorePopup.findViewById(R.id.nome);
                popup_cognome = relatorePopup.findViewById(R.id.cognome);
                popup_email = relatorePopup.findViewById(R.id.email);
                permesso1 = relatorePopup.findViewById(R.id.permesso1);
                permesso2 = relatorePopup.findViewById(R.id.permesso2);
                permesso3 = relatorePopup.findViewById(R.id.permesso3);
                permesso4 = relatorePopup.findViewById(R.id.permesso4);

                save = relatorePopup.findViewById(R.id.saveButton);
                cancel = relatorePopup.findViewById(R.id.cancelButton);

                dialogBuilder.setView(relatorePopup);
                dialog = dialogBuilder.create();
                dialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String permessi = "";
                        if(permesso1.isChecked()){
                            permessi += permesso1.getText() + ",";
                        }
                        if(permesso2.isChecked())
                        {
                            permessi += permesso2.getText() + ",";
                        }
                        if(permesso3.isChecked()){
                            permessi += permesso3.getText() + ", ";
                        }
                        if(permesso4.isChecked()){
                            permessi += permesso4.getText() + ", ";
                        }
                        permessi.substring(0, permessi.length()-2);
                        LinearLayout parentLayout = binding.relatoriTesi;
                        TextView nuovo_relatore = new TextView(getContext());
                        nuovo_relatore.setText(popup_nome.getText()+" "+popup_cognome.getText() + " (" + permessi + ").");
                        nuovo_relatore.setTextSize(20);
                        nuovo_relatore.setTextColor(Color.BLACK);
                        parentLayout.addView(nuovo_relatore);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

/*
 * vincoli
 */
        LinearLayout parentLayout = binding.vincoliTesi;
        TextView Vincoli = new TextView(getContext());
        Vincoli.setTextSize(20);
        Vincoli.setTextColor(Color.BLACK);
        B_aggiungi_vincoli = binding.aggiungiVincoli;
        B_aggiungi_vincoli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Vincoli.getText().toString().isEmpty())
                {
                    parentLayout.removeView(Vincoli);
                }
                dialogBuilder2 = new AlertDialog.Builder(getContext());
                final View vincoliPopup = getLayoutInflater().inflate(R.layout.popup_vincoli, null);
                popup_capacita = vincoliPopup.findViewById(R.id.CeS_edit);

                vincolo1 = vincoliPopup.findViewById(R.id.vincolo1);
                vincolo2 = vincoliPopup.findViewById(R.id.vincolo2);
                vincolo3 = vincoliPopup.findViewById(R.id.vincolo3);
                vincolo4 = vincoliPopup.findViewById(R.id.vincolo4);
                vincolo5 = vincoliPopup.findViewById(R.id.vincolo5);
                vincolo6 = vincoliPopup.findViewById(R.id.vincolo6);
                vincolo7 = vincoliPopup.findViewById(R.id.vincolo7);
                vincolo8 = vincoliPopup.findViewById(R.id.vincolo8);

                save = vincoliPopup.findViewById(R.id.saveButton);
                cancel = vincoliPopup.findViewById(R.id.cancelButton);

                dialogBuilder2.setView(vincoliPopup);
                dialog2 = dialogBuilder2.create();
                dialog2.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String vincoli = "";
                        if(vincolo1.isChecked()){
                            vincoli += vincolo1.getText();
                        }
                        if(vincolo2.isChecked())
                        {
                            vincoli += vincolo2.getText() + ", ";
                        }
                        if(vincolo3.isChecked()){
                            vincoli += vincolo3.getText() + ", ";
                        }
                        if(vincolo4.isChecked()){
                            vincoli += vincolo4.getText() + ", ";
                        }
                        if(vincolo5.isChecked()){
                            vincoli += vincolo5.getText() + ", ";
                        }
                        if(vincolo6.isChecked()){
                            vincoli += vincolo6.getText() + ", ";
                        }
                        if(vincolo7.isChecked()){
                            vincoli += vincolo7.getText() + ", ";
                        }
                        if(vincolo8.isChecked()){
                            vincoli += vincolo8.getText() + ", ";
                        }
                        vincoli.substring(0, vincoli.length()-2);
                        binding.nessunVincolo.setVisibility(View.INVISIBLE);
                        Vincoli.setText(vincoli);
                        parentLayout.addView(Vincoli);
                        dialog2.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
            }
        });

        salva = binding.salva;
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nome_tesi = binding.nomeTesi;
                String Nome_tesi = nome_tesi.getText().toString();
                TextView relatore = binding.relatorePrincipale;
                String Relatore = relatore.getText().toString();
                EditText descrizione = binding.descrizione;
                String Descrizione = descrizione.getText().toString();

                Map<String, Object> T_tesi = new HashMap<>();
                T_tesi.put("nome_tesi", Nome_tesi);
                T_tesi.put("descrizione", Descrizione);
                T_tesi.put("relatore", Relatore);

                db.collection("tesi")
                        .add(T_tesi)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                  @Override
                                                  public void onSuccess(DocumentReference documentReference) {
                                                      Toast.makeText(getContext(), "successfull", Toast.LENGTH_SHORT).show();
                                                      navController.navigate(R.id.action_tesiFragmant_to_navigation_home);
                                                  }
                                              }
                        ).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        navBar.setVisibility(View.VISIBLE);
        binding = null;
    }
}

