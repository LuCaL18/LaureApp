package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static android.app.Activity.RESULT_OK;
import static com.uniba.mobile.cddgl.laureapp.MainActivity.REQUEST_INTERNET_PERMISSION;
import static com.uniba.mobile.cddgl.laureapp.MainActivity.REQUEST_READ_EXTERNAL_STORAGE;
import static com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskFragment.LIST_TASK_PERMISSION_CREATE;
import static com.uniba.mobile.cddgl.laureapp.ui.task.ListaTaskFragment.LIST_TASK_TESI_KEY;
import static com.uniba.mobile.cddgl.laureapp.ui.tesi.ClassificaTesiFragment.SHARED_PREFS_NAME;
import static com.uniba.mobile.cddgl.laureapp.ui.tesi.ClassificaTesiFragment.TESI_LIST_KEY_PREF;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.DownloadedFile;
import com.uniba.mobile.cddgl.laureapp.data.PersonaTesi;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.ChatData;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.component.RequestLoginDialog;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.DocumentAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.RelatorsAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.BookingDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.CoRelatoreDialoog;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.ConstraintsDialog;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.QRCodeDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.SearchKeyDialog;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs.UploadFileDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketFragment;
import com.uniba.mobile.cddgl.laureapp.util.ShareContent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualizeTesiFragment extends Fragment {

    public static final String TESI_VISUALIZE = "tesi_visualize";

    private static final int SHARE_THESIS = R.id.share_thesis;
    private static final int QR_CODE_THESIS = R.id.qr_thesis;
    private static final int FAVORITE_THESIS = R.id.favorite_thesis;
    private static final int ADD_TICKET_THESIS = R.id.add_ticket_thesis;
    private static final int LIST_TASK_THESIS = R.id.list_task;
    private static final int CALENDAR_THESIS = R.id.tesi_calendar;

    private BottomNavigationView navBar;
    private MenuProvider providerMenu;
    private View root;

    private VisualizeThesisViewModel thesisViewModel;
    private MainViewModel mainViewModel;
    private boolean isEditing;
    private Tesi thesis;
    private DocumentAdapter documentAdapter;
    private ActivityResultLauncher<Intent> pickFileLauncher;
    private Menu menuTesi;
    private boolean isFavourite;
    private RecyclerView recyclerViewRelators;
    private RelatorsAdapter relatorsAdapter;
    private LoggedInUser user;

    public VisualizeTesiFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireParentFragment());
        thesisViewModel = viewModelProvider.get(VisualizeThesisViewModel.class);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (savedInstanceState != null && savedInstanceState.getSerializable(TESI_VISUALIZE) != null) {
            thesis = (Tesi) savedInstanceState.getSerializable(TESI_VISUALIZE);
            return;
        }

        if (getArguments() != null && getArguments().getSerializable(TESI_VISUALIZE) != null) {
            thesis = (Tesi) getArguments().getSerializable(TESI_VISUALIZE);

            return;
        }

        thesis = thesisViewModel.getThesis().getValue();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_visualize_tesi, container, false);

        if (thesis.getImageTesi() != null) {
            ImageView imageTesi = root.findViewById(R.id.iv_thesis_image);
            Glide.with(this).load(thesis.getImageTesi()).into(imageTesi);
        }

        TextView title = root.findViewById(R.id.tv_thesis_title);
        title.setText(thesis.getNomeTesi());

        TextView description = root.findViewById(R.id.tv_thesis_desc);
        description.setText(thesis.getDescrizione());

        // SET CARD PROFESSOR
        MaterialCardView cardProfessor = root.findViewById(R.id.cv_professor);
        LinearLayout cvProfLayout = root.findViewById(R.id.ll_card_prof_info);
        ImageView cvProfArrow = root.findViewById(R.id.arrow_image_view);

        TextView tvNameProfessor = root.findViewById(R.id.tv_prof_display_name);
        tvNameProfessor.setText(thesis.getRelatore().getDisplayName());

        TextView tvEmailProfessor = root.findViewById(R.id.tv_prof_email);
        tvEmailProfessor.setText(thesis.getRelatore().getEmail());

        cardProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cvProfLayout.getVisibility() == View.GONE) {
                    cvProfLayout.setVisibility(View.VISIBLE);
                    cvProfArrow.setRotation(180);
                } else {
                    cvProfLayout.setVisibility(View.GONE);
                    cvProfArrow.setRotation(0);
                }
            }
        });

        //SET CARD RELATORS
        MaterialCardView relatorsCard = root.findViewById(R.id.card_relators);
        recyclerViewRelators = root.findViewById(R.id.recycler_relators);
        ImageView relatorsArrowCard = root.findViewById(R.id.arrow_image_card_relators);

        relatorsAdapter = new RelatorsAdapter(thesis.getCoRelatori(), false, this);
        recyclerViewRelators.setAdapter(relatorsAdapter);
        recyclerViewRelators.setLayoutManager(new LinearLayoutManager(getContext()));

        if (thesis.getCoRelatori() == null || thesis.getCoRelatori().isEmpty()) {
            relatorsCard.setVisibility(View.GONE);
        }

        relatorsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewRelators.getVisibility() == View.GONE) {
                    recyclerViewRelators.setVisibility(View.VISIBLE);

                    relatorsArrowCard.setRotation(180);
                    return;
                }

                recyclerViewRelators.setVisibility(View.GONE);
                relatorsArrowCard.setRotation(0);
            }
        });

        //SET CARD CONSTRAINTS
        MaterialCardView constraintsCard = root.findViewById(R.id.card_constraints);
        ImageView constraintsArrowCard = root.findViewById(R.id.arrow_image_card_constraints);
        LinearLayout constraintsInfoLl = root.findViewById(R.id.ll_card_constraints_info);

        TextView timelineTextView = root.findViewById(R.id.tv_constraint_timelines);
        timelineTextView.setText(thesis.getTempistiche() + " " + getString(R.string.weeks));

        TextView averageTextView = root.findViewById(R.id.tv_constraint_average);
        averageTextView.setText(String.valueOf(thesis.getMediaVoto()));

        if (thesis.getEsami() == null || thesis.getEsami().isEmpty()) {
            root.findViewById(R.id.layout_exam_n).setVisibility(View.GONE);
        } else {
            TextView examTextView = root.findViewById(R.id.tv_constraint_exam);
            String esami = String.join(", ", thesis.getEsami());
            examTextView.setText(esami);
        }

        TextView skillsTextView = root.findViewById(R.id.tv_constraint_skills);
        skillsTextView.setText(thesis.getSkill());

        constraintsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (constraintsInfoLl.getVisibility() == View.GONE) {
                    constraintsInfoLl.setVisibility(View.VISIBLE);

                    constraintsArrowCard.setRotation(180);
                    return;
                }

                constraintsInfoLl.setVisibility(View.GONE);
                constraintsArrowCard.setRotation(0);
            }
        });

        //SET CARD DOCUMENTS
        MaterialCardView documentsCard = root.findViewById(R.id.card_documents);
        RecyclerView recyclerViewDocuments = root.findViewById(R.id.recyclerView_documents);
        ImageView documentsArrowCard = root.findViewById(R.id.arrow_image_card_documents);

        documentAdapter = new DocumentAdapter(getContext(), thesis.getDocuments(), thesis.getId(), thesisViewModel);
        recyclerViewDocuments.setAdapter(documentAdapter);
        recyclerViewDocuments.setLayoutManager(new LinearLayoutManager(getContext()));

        if (thesis.getDocuments() == null || thesis.getDocuments().isEmpty()) {
            documentsCard.setVisibility(View.GONE);
        }

        documentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewDocuments.getVisibility() == View.GONE) {
                    recyclerViewDocuments.setVisibility(View.VISIBLE);

                    documentsArrowCard.setRotation(180);
                    return;
                }

                recyclerViewDocuments.setVisibility(View.GONE);
                documentsArrowCard.setRotation(0);
            }
        });

        //SET CARD NOTES
        MaterialCardView cardNotes = root.findViewById(R.id.card_notes);
        TextView textViewNotes = root.findViewById(R.id.text_view_notes);
        ImageView arrowCardNotes = root.findViewById(R.id.arrow_image_card_notes);

        if (thesis.getNote() == null || thesis.getNote().isEmpty()) {
            textViewNotes.setText(getText(R.string.no_notes));
        } else {
            textViewNotes.setText(thesis.getNote());
        }

        cardNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textViewNotes.getVisibility() == View.GONE) {

                    checkAndRequestInternetPermission();
                    textViewNotes.setVisibility(View.VISIBLE);

                    arrowCardNotes.setRotation(180);
                    return;
                }

                textViewNotes.setVisibility(View.GONE);
                arrowCardNotes.setRotation(0);
            }
        });

        //SET CARD STUDENT
        MaterialCardView cardStudent = root.findViewById(R.id.cv_student);
        if (thesis.getStudent() != null) {
            cardStudent.setVisibility(View.VISIBLE);
            LinearLayout cvStudentLayout = root.findViewById(R.id.ll_card_student_info);
            ImageView cvStudentArrow = root.findViewById(R.id.arrow_image_view_student);

            TextView tvNameStudent = root.findViewById(R.id.tv_student_display_name);
            tvNameStudent.setText(thesis.getStudent().getDisplayName());

            TextView tvEmailStudent = root.findViewById(R.id.tv_student_email);
            tvEmailStudent.setText(thesis.getStudent().getEmail());

            cardStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvStudentLayout.getVisibility() == View.GONE) {
                        cvStudentLayout.setVisibility(View.VISIBLE);
                        cvStudentArrow.setRotation(180);
                    } else {
                        cvStudentLayout.setVisibility(View.GONE);
                        cvStudentArrow.setRotation(0);
                    }
                }
            });
        } else {
            cardStudent.setVisibility(View.GONE);
        }

        //SET Card Key words
        MaterialCardView cardSearchKey = root.findViewById(R.id.card_search_keys);
        ImageView searchKeyArrowCard = root.findViewById(R.id.arrow_image_card_search_keys);
        LinearLayout searchKeyInfoLl = root.findViewById(R.id.ll_card_search_keys_info);

        TextView scopeTextView = root.findViewById(R.id.tv_search_keys_scope);
        scopeTextView.setText(thesis.getAmbito());


        if (thesis.getChiavi() == null) {
            root.findViewById(R.id.layout_search_key).setVisibility(View.GONE);
        } else {
            TextView searchWordsTextView = root.findViewById(R.id.tv_search_keys_words);
            searchWordsTextView.setText(String.join(", ", thesis.getChiavi()));
        }

        cardSearchKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchKeyInfoLl.getVisibility() == View.GONE) {
                    searchKeyInfoLl.setVisibility(View.VISIBLE);

                    searchKeyArrowCard.setRotation(180);
                    return;
                }

                searchKeyInfoLl.setVisibility(View.GONE);
                searchKeyArrowCard.setRotation(0);
            }
        });


        Button bookThesis = root.findViewById(R.id.btn_book);

        bookThesis.setOnClickListener(view -> {
            LoggedInUser user = mainViewModel.getUser().getValue();
            BookingDialogFragment bookingDialogFragment = new BookingDialogFragment(
                    mainViewModel.getIdUser(),
                    user.getEmail(), user.getName(), user.getSurname(),
                    thesis.getRelatore().getId(), thesis.getId(), thesis.getNomeTesi()
            );

            bookingDialogFragment.show(getParentFragmentManager(), "BookingDialogFragment");
        });

        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();

                        String mimeType = (getContext().getContentResolver().getType(fileUri)).split("/")[1];
                        // Upload the file
                        uploadImage(thesis.getId() + '.' + mimeType, fileUri);
                    }
                });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        ShareContent shareContent = new ShareContent(getContext());

        thesisViewModel.getRequestDocument().observe(getViewLifecycleOwner(), new Observer<DownloadedFile>() {
            @Override
            public void onChanged(DownloadedFile downloadedFile) {

                if (downloadedFile == null) {
                    return;
                }

                try {
                    DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    Long downloadReference = downloadManager.enqueue(downloadedFile.getRequest());
                    mainViewModel.setDownloadReference(downloadReference);
                    mainViewModel.setFileToOpen(downloadedFile.getFilename());

                    showSaveToast(R.string.file_saved_with_success);

                    thesisViewModel.getRequestDocument().setValue(null);
                } catch (Exception e) {
                    showSaveToast(R.string.unable_saved_file);
                }
            }

        });

        mainViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser loggedInUser) {

                if (loggedInUser == null) {
                    return;
                }
                user = loggedInUser;

                Integer menuToVisualize = null;

                if (thesis.getRelatore().getId().equals(loggedInUser.getId())) {

                    menuToVisualize = R.menu.app_bar_visualize_thesis_prof;

                    setCardDocumentsCreator();
                    setNotesText();
                    setCardCoRelatorsCreator();
                    setCardConstraintsCreator();
                    setCardStudentCreator();
                    setCardSearchKeysCreator();

                    Button editImage = root.findViewById(R.id.edit_image_tesi_button);
                    editImage.setVisibility(View.VISIBLE);
                    editImage.setOnClickListener(view1 -> {
                        checkAndRequestReadExternalStorage();
                    });
                }

                if (loggedInUser.getRole().equals(RoleUser.STUDENT) && (thesis.getStudent() == null || !thesis.getStudent().getId().equals(loggedInUser.getId()))) {
                    menuToVisualize = R.menu.app_bar_visualize_tesi;

                    if (!thesis.getIsAssigned()) {
                        root.findViewById(R.id.btn_book).setVisibility(View.VISIBLE);
                    }

                    FirebaseFirestore.getInstance().collection("tesi_classifiche")
                            .document(loggedInUser.getId()).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    TesiClassifica classification = documentSnapshot.toObject(TesiClassifica.class);

                                    if (classification.getTesi().contains(thesis.getId())) {

                                        if (menuTesi != null) {
                                            menuTesi.findItem(FAVORITE_THESIS).setIcon(R.drawable.ic_favorite_24dp);
                                        }
                                        isFavourite = true;
                                    }
                                }
                            });
                } else if (thesis.getStudent() != null && thesis.getStudent().getId().equals(loggedInUser.getId())) {
                    menuToVisualize = R.menu.app_bar_visualize_thesis_prof;
                }

                if (loggedInUser.getRole().equals(RoleUser.GUEST)) {
                    menuToVisualize = R.menu.app_bar_visualize_tesi;

                    if (getTesiList().contains(thesis.getId())) {

                        if (menuTesi != null) {
                            menuTesi.findItem(FAVORITE_THESIS).setIcon(R.drawable.ic_favorite_24dp);
                        }

                        isFavourite = true;
                    }

                    if (!thesis.getIsAssigned()) {
                        Button bookButton = root.findViewById(R.id.btn_book);
                        bookButton.setVisibility(View.VISIBLE);
                        bookButton.setEnabled(false);

                        Button buttonBookLayout = root.findViewById(R.id.button_no_book);
                        buttonBookLayout.setVisibility(View.VISIBLE);
                        buttonBookLayout.setOnClickListener(v -> Toast.makeText(getContext(), getString(R.string.registration_required_to_booking), Toast.LENGTH_SHORT).show());
                    }
                }

                Integer finalMenuToVisualize = menuToVisualize;
                providerMenu = new MenuProvider() {
                    @Override
                    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                        menu.clear();

                        if (finalMenuToVisualize == null) {
                            return;
                        }

                        menuInflater.inflate(finalMenuToVisualize, menu);
                        menuTesi = menu;

                        if (isFavourite) {
                            menuTesi.findItem(FAVORITE_THESIS).setIcon(R.drawable.ic_favorite_24dp);
                        }
                    }

                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case VisualizeTesiFragment.FAVORITE_THESIS:
                                return switchFavouriteThesis(menuItem);
                            case VisualizeTesiFragment.ADD_TICKET_THESIS:

                                String id = mainViewModel.getIdUser();

                                if (id == null) {
                                    RequestLoginDialog requestLoginDialog = new RequestLoginDialog();
                                    requestLoginDialog.show(getParentFragmentManager(), "RequestLoginDialogFragment");

                                    return true;
                                }

                                Ticket ticket = new Ticket(id, thesis.getRelatore().getId(), thesis.getId(), thesis.getNomeTesi(), TicketState.NEW);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(TicketFragment.TICKET_KEY, ticket);
                                navController.navigate(R.id.action_visualizeTesiFragment_to_ticketFragment, bundle);
                                return true;
                            case VisualizeTesiFragment.SHARE_THESIS:
                                Intent intent = null;
                                try {
                                    intent = shareContent.shareThesisData((Tesi) thesis.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                startActivity(Intent.createChooser(intent, getString(R.string.share_data_with)));
                                return true;
                            case VisualizeTesiFragment.QR_CODE_THESIS:
                                QRCodeDialogFragment dialogFragment = null;
                                try {
                                    dialogFragment = new QRCodeDialogFragment((Tesi) thesis.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                dialogFragment.show(getParentFragmentManager(), "QRCodeDialogFragment");
                                return true;
                            case VisualizeTesiFragment.LIST_TASK_THESIS:
                                Bundle bundleTask = new Bundle();
                                bundleTask.putString(LIST_TASK_TESI_KEY, thesis.getId());

                                boolean permissionCreateTask = (thesis.getRelatore().getId().equals(loggedInUser.getId()) ||
                                        thesis.getCoRelatori().contains(new PersonaTesi(loggedInUser.getId())));
                                bundleTask.putString(LIST_TASK_PERMISSION_CREATE, String.valueOf(permissionCreateTask));

                                navController.navigate(R.id.action_visualizeTesiFragment_to_nav_lista_task, bundleTask);
                                return true;
                            case VisualizeTesiFragment.CALENDAR_THESIS:
                                // TODO: collegamento con calendario
                                return true;
                            default:
                                return false;
                        }
                    }
                };

                requireActivity().addMenuProvider(providerMenu);
            }
        });

        thesisViewModel.getError().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer error) {
                if (error != null) {
                    showSaveToast(error);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(thesis.getNomeTesi());
        }
    }

    private void setCardDocumentsCreator() {
        MaterialCardView documentsCard = root.findViewById(R.id.card_documents);
        documentsCard.setVisibility(View.VISIBLE);

        RecyclerView recyclerViewDocuments = root.findViewById(R.id.recyclerView_documents);
        recyclerViewDocuments.setVisibility(View.VISIBLE);

        ImageView documentsArrowCard = root.findViewById(R.id.arrow_image_card_documents);
        documentsArrowCard.setImageResource(R.drawable.ic_baseline_add_box_24);
        documentsArrowCard.setClickable(true);

        documentAdapter = null;
        documentAdapter = new DocumentAdapter(this, thesis.getDocuments(), thesis.getId(), thesisViewModel, true);
        recyclerViewDocuments.setAdapter(documentAdapter);
        recyclerViewDocuments.setLayoutManager(new LinearLayoutManager(getContext()));

        VisualizeTesiFragment requestFragment = this;

        documentsArrowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFileDialogFragment uploadFileDialogFragment = new UploadFileDialogFragment(thesis, requestFragment);

                uploadFileDialogFragment.show(getParentFragmentManager(), "UploadFileDialogFragment");
            }
        });
    }

    private void setNotesText() {
        //In your Thesis Fragment class

        MaterialCardView card = root.findViewById(R.id.card_notes);
        card.setClickable(false);

        TextView textViewNotes;
        EditText editTextNotes;
        ImageView buttonEdit, buttonSave, arrowCardNotes;

        FirebaseFirestore firestore;

        //In the onCreateView() method
        textViewNotes = root.findViewById(R.id.text_view_notes);
        editTextNotes = root.findViewById(R.id.edit_text_notes);
        buttonEdit = root.findViewById(R.id.edit_notes_image);
        buttonSave = root.findViewById(R.id.save_notes_image);
        arrowCardNotes = root.findViewById(R.id.arrow_image_card_notes);

        textViewNotes.setVisibility(View.VISIBLE);
        arrowCardNotes.setVisibility(View.GONE);
        buttonEdit.setVisibility(View.VISIBLE);

        checkAndRequestInternetPermission();


        firestore = FirebaseFirestore.getInstance();

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = !isEditing;
                textViewNotes.setVisibility(isEditing ? View.GONE : View.VISIBLE);
                editTextNotes.setVisibility(isEditing ? View.VISIBLE : View.GONE);
                editTextNotes.setText(textViewNotes.getText().toString().equals(getText(R.string.no_notes)) ? "" : textViewNotes.getText().toString());

                if (editTextNotes.getVisibility() == View.VISIBLE) {
                    editTextNotes.requestFocus();
                } else {
                    checkAndRequestInternetPermission();
                }

                buttonSave.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = editTextNotes.getText().toString();
                Map<String, Object> updates = new HashMap<>();
                updates.put("note", notes);
                firestore.collection("tesi").document(thesis.getId()).update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                textViewNotes.setText(notes);
                                thesis.setNote(notes);
                                isEditing = false;
                                textViewNotes.setVisibility(View.VISIBLE);
                                editTextNotes.setVisibility(View.GONE);
                                buttonSave.setVisibility(View.GONE);
                                checkAndRequestInternetPermission();

                                if (thesis.getNote() == null || thesis.getNote().isEmpty()) {
                                    textViewNotes.setText(getText(R.string.no_notes));
                                } else {
                                    textViewNotes.setText(thesis.getNote());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showSaveToast(R.string.unable_to_save_changes);
                            }
                        });
            }
        });
    }

    private void checkAndRequestInternetPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        } else {
            // Permission has already been granted, continue with your code
            makeLinksClickable();
        }
    }

    private void checkAndRequestReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission has already been granted, continue with your code
            pickImageFile();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INTERNET_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, continue with your code
                makeLinksClickable();
            } else {
                // Permission has been denied, show a message to the user
                Toast.makeText(requireContext(), getString(R.string.internet_permission_required_cause), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, continue with your code
                pickImageFile();
            } else {
                // Permission has been denied, show a message to the user
                Toast.makeText(requireContext(), getString(R.string.permission_is_required_to_upload_files_from_the_device), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeLinksClickable() {
        TextView textView = root.findViewById(R.id.text_view_notes);
        Linkify.addLinks(textView, Linkify.WEB_URLS);
    }

    private void showSaveToast(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateDataThesis(Map<String, Object> updates) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("tesi");

        collectionReference.document(thesis.getId()).update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showSaveToast(R.string.changes_saved_successfully);
                return;
            }
            showSaveToast(R.string.unable_save_changes);
        });
    }

    private void pickImageFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        pickFileLauncher.launch(intent);
    }

    private void uploadImage(String title, Uri fileUri) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference documentRef = storageRef.child("images/tesi/" + title);

        // Upload the file to Firebase Storage
        documentRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        showSaveToast(R.string.image_saved_successfully);

                        documentRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            thesis.setImageTesi(uri.toString());

                            ImageView imageTesi = root.findViewById(R.id.iv_thesis_image);
                            Glide.with(getContext()).load(thesis.getImageTesi()).into(imageTesi);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("imageTesi", thesis.getImageTesi());

                            updateDataThesis(updates);
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // An error occurred during the upload
                        showSaveToast(R.string.error_upload);
                    }
                });
    }

    private boolean switchFavouriteThesis(MenuItem menuItem) {

        if (user.getRole().equals(RoleUser.GUEST)) {

            List<String> tesiList = getTesiList();
            if (tesiList.isEmpty()) {

                List<String> newTesiList = new ArrayList<>();
                newTesiList.add(thesis.getId());

                if (saveListofThesis(newTesiList)) {
                    menuItem.setIcon(R.drawable.ic_favorite_24dp);
                    isFavourite = true;
                } else {
                    Log.e("VisualizeTesiFragment", "Unable to add the thesis to the ranking");
                }
            } else {
                int icon;

                if (isFavourite) {
                    tesiList.remove(thesis.getId());

                    saveListofThesis(tesiList);
                    icon = R.drawable.ic_baseline_favorite_border_24;
                    isFavourite = false;
                } else {
                    tesiList.add(thesis.getId());
                    saveListofThesis(tesiList);
                    icon = R.drawable.ic_favorite_24dp;
                    isFavourite = true;
                }
                menuItem.setIcon(icon);
            }
            return true;
        }

        DocumentReference classificaDocument = FirebaseFirestore.getInstance().collection("tesi_classifiche")
                .document(mainViewModel.getIdUser());

        classificaDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                TesiClassifica classification = documentSnapshot.toObject(TesiClassifica.class);

                int icon;
                if (isFavourite) {
                    List<String> newList = new ArrayList<>();

                    for (String tesi : classification.getTesi()) {
                        try {
                            if (!tesi.equals(thesis.getId())) {
                                newList.add(tesi);
                            }
                        } catch (NullPointerException e) {
                            Log.e("NEW FAVOURITES LIST", "Exception during add new List: " + e.getMessage());
                        }

                    }

                    classification.setTesi(newList);
                    icon = R.drawable.ic_baseline_favorite_border_24;
                    isFavourite = false;
                } else {
                    List<String> newList = classification.getTesi();
                    newList.add(thesis.getId());

                    classification.setTesi(newList);
                    icon = R.drawable.ic_favorite_24dp;

                    isFavourite = true;
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("tesi", classification.getTesi());

                classificaDocument.update(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        menuItem.setIcon(icon);
                    } else {
                        Log.e("VisualizeTesiFragment", task.getException().getMessage());
                    }
                });
            } else {
                List<String> newTesiList = new ArrayList<>();
                newTesiList.add(thesis.getId());
                classificaDocument.set(new TesiClassifica(newTesiList, mainViewModel.getIdUser()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                menuItem.setIcon(R.drawable.ic_favorite_24dp);
                                isFavourite = true;
                            } else {
                                showSaveToast(R.string.unable_add_favorite);
                            }
                        });
            }
        });
        return true;
    }

    private void setCardCoRelatorsCreator() {
        MaterialCardView relatorsCard = root.findViewById(R.id.card_relators);
        relatorsCard.setVisibility(View.VISIBLE);

        recyclerViewRelators.setVisibility(View.VISIBLE);

        relatorsAdapter = null;
        relatorsAdapter = new RelatorsAdapter(thesis.getCoRelatori(), true, this);
        recyclerViewRelators.setAdapter(relatorsAdapter);

        ImageView relatorsArrowCard = root.findViewById(R.id.arrow_image_card_relators);
        relatorsArrowCard.setImageResource(R.drawable.ic_baseline_add_box_24);
        relatorsArrowCard.setClickable(true);

        VisualizeTesiFragment requireFragment = this;

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

    private void setCardConstraintsCreator() {
        MaterialCardView constraintsCard = root.findViewById(R.id.card_constraints);

        ImageView constraintsArrowCard = root.findViewById(R.id.arrow_image_card_constraints);
        constraintsArrowCard.setImageResource(R.drawable.ic_baseline_edit_note_24);
        constraintsArrowCard.setClickable(true);

        LinearLayout constraintsInfoLl = root.findViewById(R.id.ll_card_constraints_info);
        constraintsInfoLl.setVisibility(View.VISIBLE);

        constraintsCard.setOnClickListener(view -> constraintsInfoLl.setVisibility(constraintsInfoLl.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));

        VisualizeTesiFragment requireFragment = this;

        constraintsArrowCard.setOnClickListener(view -> {
            final View vincoliPopup = getLayoutInflater().inflate(R.layout.popup_edit_constraints, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setView(vincoliPopup);
            AlertDialog dialog = dialogBuilder.create();

            ConstraintsDialog constraintsDialog = new ConstraintsDialog(dialog, vincoliPopup, requireFragment, thesis);
            constraintsDialog.show();
        });
    }

    private void setCardStudentCreator() {
        MaterialCardView cardStudent = root.findViewById(R.id.cv_student);

        ImageView cvStudentArrow = root.findViewById(R.id.arrow_image_view_student);
        cvStudentArrow.setImageResource(R.drawable.ic_baseline_delete_24);
        cvStudentArrow.setClickable(true);

        cvStudentArrow.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.title_dialog_delete_student));
            builder.setMessage(getContext().getString(R.string.message_dialog_delete_student, thesis.getStudent().getDisplayName()));
            builder.setPositiveButton(getContext().getString(R.string.yes_text), (dialog, which) -> deleteStudent());
            builder.setNegativeButton(getContext().getString(R.string.no_text), null);
            builder.create().show();
        });

        LinearLayout cvStudentLayout = root.findViewById(R.id.ll_card_student_info);
        cvStudentLayout.setVisibility(View.VISIBLE);

        cardStudent.setOnClickListener(view -> cvStudentLayout.setVisibility(cvStudentLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
    }

    private void setCardSearchKeysCreator() {
        MaterialCardView cardSearchKey = root.findViewById(R.id.card_search_keys);
        LinearLayout searchKeyInfoLl = root.findViewById(R.id.ll_card_search_keys_info);
        searchKeyInfoLl.setVisibility(View.VISIBLE);
        cardSearchKey.setOnClickListener(view -> searchKeyInfoLl.setVisibility(searchKeyInfoLl.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));

        ImageView searchKeyArrowCard = root.findViewById(R.id.arrow_image_card_search_keys);
        searchKeyArrowCard.setImageResource(R.drawable.ic_baseline_edit_note_24);
        searchKeyArrowCard.setClickable(true);

        VisualizeTesiFragment requireFragment = this;
        searchKeyArrowCard.setOnClickListener(view -> {
            final View searchKeyPopup = getLayoutInflater().inflate(R.layout.popup_edit_search_key, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setView(searchKeyPopup);
            AlertDialog dialog = dialogBuilder.create();

            SearchKeyDialog searchKeyDialog = new SearchKeyDialog(dialog, searchKeyPopup, requireFragment, thesis.getAmbito(), thesis.getChiavi());
            searchKeyDialog.show();
        });
    }

    public void addCoRelator(PersonaTesi coRelator) {
        thesis.getCoRelatori().add(coRelator);
        relatorsAdapter.setRelators(thesis.getCoRelatori());

        Map<String, Object> updates = new HashMap<>();
        updates.put("coRelatori", thesis.getCoRelatori());
        updateDataThesis(updates);

        DocumentReference chatRef = FirebaseFirestore.getInstance().collection("chats").document(thesis.getId());

        chatRef.get().addOnCompleteListener(taskChat -> {
            if (taskChat.isSuccessful()) {
                DocumentSnapshot result = taskChat.getResult();
                ChatData chat = result.toObject(ChatData.class);

                chat.getMembers().add(coRelator.getId());

                Map<String, Object> updatesChat = new HashMap<>();
                updatesChat.put("members", chat.getMembers());

                chatRef.update(updatesChat);
            }
        });
    }

    public void removeCoRelator(PersonaTesi coRelator) {
        thesis.getCoRelatori().remove(coRelator);
        relatorsAdapter.setRelators(thesis.getCoRelatori());

        Map<String, Object> updates = new HashMap<>();
        updates.put("coRelatori", thesis.getCoRelatori());
        updateDataThesis(updates);

        DocumentReference chatRef = FirebaseFirestore.getInstance().collection("chats").document(thesis.getId());

        chatRef.get().addOnCompleteListener(taskChat -> {
            if (taskChat.isSuccessful()) {
                DocumentSnapshot result = taskChat.getResult();
                ChatData chat = result.toObject(ChatData.class);

                chat.getMembers().remove(coRelator.getId());

                Map<String, Object> updatesChat = new HashMap<>();
                updatesChat.put("members", chat.getMembers());

                chatRef.update(updatesChat);
            }
        });
    }

    public void addDocument(String document) {
        thesis.getDocuments().add(document);
        documentAdapter.setDocuments(thesis.getDocuments());

        Map<String, Object> updates = new HashMap<>();
        updates.put("documents", thesis.getDocuments());

        updateDataThesis(updates);
    }

    public void removeDocument(String document) {
        thesis.getDocuments().remove(document);
        documentAdapter.setDocuments(thesis.getDocuments());

        Map<String, Object> updates = new HashMap<>();
        updates.put("documents", thesis.getDocuments());

        updateDataThesis(updates);
    }

    public void updateConstraints(int tempistiche, float mediaVoto, List<String> esamiNecessari, String skills) {
        thesis.setTempistiche(tempistiche);
        TextView timelineTextView = root.findViewById(R.id.tv_constraint_timelines);
        timelineTextView.setText(thesis.getTempistiche() + " " + getString(R.string.weeks));

        thesis.setMediaVoto(mediaVoto);
        TextView averageTextView = root.findViewById(R.id.tv_constraint_average);
        averageTextView.setText(String.valueOf(thesis.getMediaVoto()));

        thesis.setSkill(skills);
        TextView skillsTextView = root.findViewById(R.id.tv_constraint_skills);
        skillsTextView.setText(thesis.getSkill());

        thesis.setEsami(esamiNecessari);
        if (thesis.getEsami() == null || thesis.getEsami().isEmpty()) {
            root.findViewById(R.id.layout_exam_n).setVisibility(View.GONE);
        } else {
            root.findViewById(R.id.layout_exam_n).setVisibility(View.VISIBLE);
            TextView examTextView = root.findViewById(R.id.tv_constraint_exam);
            String esami = String.join(", ", thesis.getEsami());
            examTextView.setText(esami);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("tempistiche", thesis.getTempistiche());
        updates.put("mediaVoto", thesis.getMediaVoto());
        updates.put("skill", thesis.getSkill());
        updates.put("esami", thesis.getEsami());

        updateDataThesis(updates);
    }

    public void updateSearchKey(String ambito, List<String> keyWords) {
        thesis.setAmbito(ambito);
        thesis.setChiavi(keyWords);

        TextView scopeTextView = root.findViewById(R.id.tv_search_keys_scope);
        scopeTextView.setText(thesis.getAmbito());

        if (keyWords.isEmpty()) {
            root.findViewById(R.id.layout_search_key).setVisibility(View.GONE);
        } else {
            TextView searchWordsTextView = root.findViewById(R.id.tv_search_keys_words);
            searchWordsTextView.setText(String.join(", ", thesis.getChiavi()));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("ambito", thesis.getAmbito());
        updates.put("chiavi", thesis.getChiavi());

        updateDataThesis(updates);
    }

    private void deleteStudent() {
        thesis.setIsAssigned(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tesiReference = db.collection("tesi").document(thesis.getId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("student", null);
        updates.put("isAssigned", false);

        tesiReference.update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                MaterialCardView cardStudent = root.findViewById(R.id.cv_student);
                cardStudent.setVisibility(View.GONE);

                DocumentReference chatRef = FirebaseFirestore.getInstance().collection("chats").document(thesis.getId());

                chatRef.get().addOnCompleteListener(taskChat -> {
                    if (taskChat.isSuccessful()) {
                        DocumentSnapshot result = taskChat.getResult();
                        ChatData chat = result.toObject(ChatData.class);

                        chat.getMembers().remove(thesis.getStudent().getId());

                        Map<String, Object> updatesChat = new HashMap<>();
                        updatesChat.put("members", chat.getMembers());

                        chatRef.update(updatesChat);
                    }
                });
                thesis.setStudent(null);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(TESI_VISUALIZE, thesis);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getSerializable(TESI_VISUALIZE) != null) {
            thesis = (Tesi) savedInstanceState.getSerializable(TESI_VISUALIZE);
        }
    }

    public boolean saveListofThesis(List<String> tesiList) {
        try {
            SharedPreferences sp = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor mEdit1 = sp.edit();

            List<String> list = new ArrayList<>(tesiList);

            mEdit1.putString(TESI_LIST_KEY_PREF, new Gson().toJson(list));
            return mEdit1.commit();
        } catch (Exception e) {
            Log.e("ClassificaTesiFragment", e.getMessage());

            return false;
        }
    }

    public ArrayList<String> getTesiList() {
        try {
            SharedPreferences sp = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
            String listJson = sp.getString(TESI_LIST_KEY_PREF, null);

            // Converti la stringa JSON nella mappa originale
            if (listJson != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>() {
                }.getType();

                return gson.fromJson(listJson, type);
            }
        } catch (Exception e) {
            Log.e("getTesiList", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void onStop() {
        super.onStop();
        thesisViewModel.getThesis().setValue(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        requireActivity().removeMenuProvider(providerMenu);
        thesisViewModel.getThesis().removeObservers(getViewLifecycleOwner());
        navBar = null;
    }
}