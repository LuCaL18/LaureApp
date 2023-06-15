package com.uniba.mobile.cddgl.laureapp.ui.calendario;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.NotificationType;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.model.Ricevimento;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentCalendarioBinding;
import com.uniba.mobile.cddgl.laureapp.util.BaseRequestNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CalendarioFragment extends Fragment {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserEmail = currentUser.getEmail();
    String currentUserUid = currentUser.getUid();
    private String guidString;
    private String receiverId=null;
    private BottomNavigationView navBar;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference ricevimentoRef = db.collection("ricevimento");
    private final CollectionReference tesiRef = db.collection("tesi");
    private final CollectionReference taskRef = db.collection("task");
    private FragmentCalendarioBinding binding;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button addR;
    private Map<String, Ricevimento> ricevimentiMap = new HashMap<>();
    public List<Tesi> tesiBackup = new ArrayList<>();
    private List<String> tesiList = new ArrayList<>();
    private Spinner tesiSpinner;
    private ListView taskListView;
    private TextView T_task;
    private EditText riepilogo;
    private EditText titolo;
    private ArrayList<String> taskList = new ArrayList<>();
    private boolean canSaveTitolo = false;
    private boolean canSaveOrario = false;
    private String tesiSelezionata;
    private long time;
    private List<String> taskRic = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private TextView taskSelezionati;
    private TextView orarioT;
    private boolean trovato;
    private int hour;
    private int minute;
    private Button orario;
    private TimePickerDialog timePickerDialog;
    private String relatoreId;
    private String studenteId;


    public CalendarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCalendarioBinding.inflate(inflater, container, false);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Seleziona le tesi in cui il campo relatore o correlatore corrisponde all'ID del relatore loggato

        CompactCalendarView calendario = binding.compactcalendarView;
        calendario.setUseThreeLetterAbbreviation(true);

        binding.mese.setText(getMese(LocalDate.now().getMonthValue()-1) + " " + LocalDate.now().getYear());
        tesiSpinner = binding.tesi;
        taskListView = binding.taskList;
        taskSelezionati = binding.taskSelected;
        riepilogo = binding.riepilogo;
        titolo = binding.titolo;
        orario = binding.orario;
        orarioT = binding.orarioT;
        T_task = binding.taskSelected;

        orario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
            }
        });

        TextWatcher textWatcher;
        {
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() < 10) {
                            canSaveTitolo = false;
                            canSave();
                            }
                    else{
                        canSaveTitolo = true;
                        canSave();
                    }
                }
            };
        }
        titolo.addTextChangedListener(textWatcher);

        binding.salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canSaveTitolo ==true){
                    caricaRicevimento();
                    Toast.makeText(getContext(), "Salvato", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Riempire i campi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addR = binding.addRicevimentoB;
        addR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (addR.getText().toString().equals("NASCONDI")) {
                    binding.addRicevimentoL.setVisibility(View.GONE);
                    addR.setText("AGGIUNGI");
                } else {
                    binding.addRicevimentoL.setVisibility(View.VISIBLE);
                    if(taskListView.getCount()>0){
                        taskListView.setVisibility(View.VISIBLE);
                        T_task.setVisibility(View.VISIBLE);
                    }
                    addR.setText("NASCONDI");
                }
            }
        });

            ricevimentoRef.whereEqualTo("relatore", currentUserUid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Ricevimento ricevimentoObj = documentSnapshot.toObject(Ricevimento.class);
                                ricevimentiMap.put(documentSnapshot.getId(),ricevimentoObj);
                                long millis = ricevimentoObj.getTime();
                                Event ev1 = new Event(Color.GREEN, millis, ricevimentoObj.getTitolo());
                                calendario.addEvent(ev1);
                            }
                        }
                    });

            ricevimentoRef.whereEqualTo("studente", currentUserUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Ricevimento ricevimentoObj = documentSnapshot.toObject(Ricevimento.class);
                            ricevimentiMap.put(documentSnapshot.getId(),ricevimentoObj);
                            long millis = ricevimentoObj.getTime();
                            Event ev1 = new Event(Color.GREEN, millis, ricevimentoObj.getTitolo());
                            calendario.addEvent(ev1);
                        }
                    }
                });

            calendario.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                final View visualizzaRicevimento = getLayoutInflater().inflate(R.layout.fragment_visualizza_ricevimento, null);
                if (dataPassata(dateClicked.getYear() + 1900, dateClicked.getMonth() + 1, dateClicked.getDate())) {
                        binding.riepilogo.setVisibility(View.VISIBLE);
                        binding.riepilogoT.setVisibility(View.VISIBLE);
                        time = dateClicked.getTime();
                    } else {
                        binding.riepilogo.setVisibility(View.GONE);
                        binding.riepilogoT.setVisibility(View.GONE);
                        time = dateClicked.getTime();
                    }
                trovato=false;
                for (Map.Entry<String, Ricevimento> entry : ricevimentiMap.entrySet()) {
                    String converted1 = convertDateClicked(dateClicked);
                    String converted2 = convertMtoD(entry.getValue().getTime(), false);
                    if (converted1.equals(converted2)) {
                        aggiungiRicevimento(entry.getValue(), visualizzaRicevimento, dateClicked);
                        trovato=true;
                    }
                    }
                if(trovato==true){
                    dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setView(visualizzaRicevimento);
                    dialog = dialogBuilder.create();
                    dialog.show();
                }
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String mese = getMese(firstDayOfNewMonth.getMonth()) + " " + (firstDayOfNewMonth.getYear() + 1900);
                binding.mese.setText(mese);
            }
        });
        popolaSpinnerTesi();

        tesiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionTesi, long idTesi) {
                T_task.setVisibility(View.VISIBLE);
                taskListView.clearChoices();
                tesiSelezionata = tesiSpinner.getSelectedItem().toString();
                for (int i=0; i<tesiBackup.size(); i++){
                    if(tesiBackup.get(i).getNomeTesi().equals(tesiSelezionata))
                        tesiSelezionata = tesiBackup.get(i).getId();
                    break;
                }
                        taskRef.whereEqualTo("tesiId", tesiSelezionata)
                        .get().addOnSuccessListener(queryDocumentSnapshots ->  {
                            taskList.clear();
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        taskList.add(documentSnapshot.toObject(Task.class).getNomeTask());
                                    }
                                    if(taskList.isEmpty()){
                                        T_task.setVisibility(View.GONE);
                                    }
                                    arrayAdapter = new ArrayAdapter<>(getContext(),
                                            android.R.layout.simple_list_item_multiple_choice, taskList);

                                    taskListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                    taskListView.setAdapter(arrayAdapter);
                                    taskListView.setOnItemClickListener((adapterView, view121, position, id) -> {
                                    taskSelezionati.setText("TASK DISCUSSI (" + taskListView.getCheckedItemCount() + ")");
                                    });
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        taskSelezionati.setText("NON CI SONO TASK PER QUESTA TESI");
                                        tesiSpinner.setVisibility(View.GONE);
                                    }
                                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void canSave() {
        if(canSaveOrario && canSaveTitolo){
            binding.salva.setBackgroundColor(getResources().getColor(R.color.primary_green));
        }
        else{
            binding.salva.setBackgroundColor(getResources().getColor(R.color.grey_app));
        }

    }

    private void caricaRicevimento() {

        for (Tesi tesi : tesiBackup) {
            if (tesi.getNomeTesi().equals(tesiSpinner.getSelectedItem().toString())) {
                if(tesi.getStudent().getEmail().equals(currentUser.getEmail())){
                    receiverId = tesi.getRelatore().getId();
                }
                else{
                    receiverId = tesi.getStudent().getId();
                }
                relatoreId = tesi.getRelatore().getId();
                studenteId = tesi.getStudent().getId();
                break;  // Interrompi il ciclo una volta che hai trovato l'oggetto desiderato
            }
        }

            hour= hour*60*60*1000;

            minute = minute*60*1000;

            for(int i=0;i<taskList.size();i++){
                if(taskListView.isItemChecked(i)){
                    taskRic.add(taskListView.getItemAtPosition(i).toString());
                }
            }

            UUID guid = UUID.randomUUID();
            guidString = guid.toString();
            DocumentReference ricevimentoref = ricevimentoRef.document(guidString);

            Map<String, Object> ricevimento = new HashMap<>();

            ricevimento.put("titolo", titolo.getText().toString());
            ricevimento.put("relatore", relatoreId);
            ricevimento.put("riepilogo", riepilogo.getText().toString());
            ricevimento.put("nomeTesi", tesiSpinner.getSelectedItem().toString());
            ricevimento.put("tesiId", tesiSelezionata);
            ricevimento.put("ricevimentoId", guidString);
            ricevimento.put("studente", studenteId);
            if(taskRic.size()==0)
            {
                taskRic.add("nessun task");
            }
            ricevimento.put("task", taskRic);
            ricevimento.put("time", time+hour+minute);
            ricevimentoref.set(ricevimento, SetOptions.merge());

            sendNotification();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_calendario_self);
    }

    private void sendNotification() {

        String title = getString(R.string.title_notification_ticket, titolo.getText().toString());

        BaseRequestNotification notification = new BaseRequestNotification(receiverId, NotificationType.MEETING);

            notification.setNotification(title, null);

        Map<String, Object> data = new HashMap<>();
        data.put("body", titolo.getText().toString() + " " + convertMtoD(time+hour+minute,true));
        data.put("receiveId", receiverId);
        data.put("type", notification.getType());
        data.put("senderName", currentUser.getEmail());
        data.put("meetingId", guidString);
        data.put("timestamp", System.currentTimeMillis());
        notification.addData(data);

        notification.sendRequest(Request.Method.POST, this.getContext());

    }



    private void popolaSpinnerTesi() {
        tesiRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DocumentSnapshot doc : value) {
                    Tesi tesi = doc.toObject(Tesi.class);
                    List<PersonaTesi> coRelatore = tesi.getCoRelatori();
                    if (!coRelatore.isEmpty()) {
                        for (PersonaTesi p : coRelatore) {
                            if (p.getId().equals(currentUserUid)) {
                                tesiList.add(tesi.getNomeTesi());
                                tesiBackup.add(tesi);
                            }
                        }
                    }
                    if (tesi.getRelatore().getId().equals(currentUserUid)) {
                        tesiList.add(tesi.getNomeTesi());
                        tesiBackup.add(tesi);
                    }
                    if(tesi.getStudent() != null) {
                        if (tesi.getStudent().getId().equals(currentUserUid)) {
                            tesiList.add(tesi.getNomeTesi());
                            tesiBackup.add(tesi);
                        }
                    }
                }
                // Creazione dell'adapter per la lista delle tesi
                ArrayAdapter<String> tesiAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tesiList);
                tesiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tesiSpinner.setAdapter(tesiAdapter);
                if(tesiList.isEmpty()){
                    addR.setVisibility(View.GONE);
                }
            }

        });

    }

    private void aggiungiRicevimento(Ricevimento value, View view, Date date) {


        LinearLayout linearLayoutT = view.findViewById(R.id.ricevimentoLayout);

        TextView spazio = new TextView(getContext());
        spazio.setTextSize(10);
        linearLayoutT.addView(spazio);

        TextView titolo = new TextView(getContext());
        titolo.setText(value.getTitolo());
        titolo.setTextSize(28);
        titolo.setTypeface(null, Typeface.BOLD_ITALIC);
        titolo.setTextColor(getResources().getColor(R.color.primary_green));
        titolo.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutT.addView(titolo);

        TextView tesi = new TextView(getContext());
        tesi.setText("TESI: "+value.getNomeTesi());
        tesi.setTextSize(25);
        tesi.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutT.addView(tesi);


        TextView orario = new TextView(getContext());
        orario.setText("ORARIO: "+convertMtoH(value.getTime()));
        orario.setTextSize(25);
        orario.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutT.addView(orario);

        if(dataPassata(date.getYear() + 1900, date.getMonth() + 1, date.getDate())) {
            TextView riepilogo = new TextView(getContext());
            riepilogo.setText("RIEPILOGO: "+value.getRiepilogo());
            riepilogo.setTextSize(20);
            riepilogo.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayoutT.addView(riepilogo);
        }

        TextView task = new TextView(getContext());
        task.setText("TASK:");
        task.setTextSize(25);
        task.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutT.addView(task);
        for (String tasks: value.getTask()) {
            TextView taskT = new TextView(view.getContext());
            taskT.setText(tasks);
            taskT.setTextSize(20);
            taskT.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayoutT.addView(taskT);
        }

        TextView spazio2 = new TextView(getContext());
        spazio.setTextSize(10);
        linearLayoutT.addView(spazio2);

    }

    private String getMese(int month) {
        switch (month) {
            case 0:
                return "GENNAIO";
            case 1:
                return "FEBBRAIO";
            case 2:
                return "MARZO";
            case 3:
                return "APRILE";
            case 4:
                return "MAGGIO";
            case 5:
                return "GIUGNO";
            case 6:
                return "LUGLIO";
            case 7:
                return "AGOSTO";
            case 8:
                return "SETTEMBRE";
            case 9:
                return "OTTOBRE";
            case 10:
                return "NOVEMBRE";
            case 11:
                return "DICEMBRE";
            default:
                return "";
        }
    }


    private boolean dataPassata(int year, int month, int dayOfMonth) {
        LocalDate todaysDate = LocalDate.now();
        LocalDate selectedDate = LocalDate.of(year, month, dayOfMonth);
        if(selectedDate.isAfter(todaysDate))
            return false;
        else return true;
    }

    public String convertMtoD(long millis, boolean orario){
        Date res = new Date(millis);
        DateFormat obj;
        if(orario){
            obj = new SimpleDateFormat("dd MM yyyy, hh:mm");
        }
        else{
            obj = new SimpleDateFormat("dd MM yyyy");
        }

        return obj.format(res);
    }

    public String convertMtoH(long millis){
        Date res = new Date(millis);
        SimpleDateFormat obj = new SimpleDateFormat("HH:mm");
        return obj.format(res);
    }

    private String convertDateClicked(Date dateClicked) {
        DateFormat obj = new SimpleDateFormat("dd MM yyyy");
        return obj.format(dateClicked);
    }

    public void popTimePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                canSaveOrario=true;
                hour=selectedHour;
                minute=selectedMinute;
                orarioT.setText(hour+":"+minute);
                canSave();
            }
        };
        timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute,true);

        timePickerDialog.setTitle("Seleziona orario");
        timePickerDialog.show();
    }

}