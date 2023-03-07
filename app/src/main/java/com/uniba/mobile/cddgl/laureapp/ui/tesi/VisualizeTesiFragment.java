package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import static android.app.Activity.RESULT_OK;
import static com.uniba.mobile.cddgl.laureapp.MainActivity.REQUEST_INTERNET_PERMISSION;
import static com.uniba.mobile.cddgl.laureapp.MainActivity.REQUEST_READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.DownloadedFile;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.TicketState;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.data.model.Ticket;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.DocumentAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters.RelatorsAdapter;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogueMessages.BookingDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogueMessages.QRCodeDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogueMessages.UploadFileDialogFragment;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketFragment;
import com.uniba.mobile.cddgl.laureapp.util.ShareContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualizeTesiFragment extends Fragment {

    private static final String TESI_VISUALIZE = "tesi_visualize";

    private static final int SHARE_THESIS = R.id.share_thesis;
    private static final int QR_CODE_THESIS = R.id.qr_thesis;
    private static final int FAVORITE_THESIS = R.id.favorite_thesis;
    private static final int ADD_TICKET_THESIS = R.id.add_ticket_thesis;

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

        thesis = thesisViewModel.getThesis().getValue();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_visualize_tesi, container, false);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(thesis.getNomeTesi());
        }

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
        RecyclerView recyclerViewRelators = root.findViewById(R.id.recycler_relators);
        ImageView relatorsArrowCard = root.findViewById(R.id.arrow_image_card_relators);

        RelatorsAdapter relatorsAdapter = new RelatorsAdapter(thesis.getCoRelatori());
        recyclerViewRelators.setAdapter(relatorsAdapter);
        recyclerViewRelators.setLayoutManager(new LinearLayoutManager(getContext()));

        if (thesis.getCoRelatori().isEmpty()) {
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
        timelineTextView.setText(thesis.getTempistiche());
/*
        TextView averageTextView = root.findViewById(R.id.tv_constraint_average);
        averageTextView.setText(thesis.getMediaVoto());

        TextView examTextView = root.findViewById(R.id.tv_constraint_exam);
        examTextView.setText(thesis.getEsami());
*/
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

        if (thesis.getDocuments().isEmpty()) {
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

        if (thesis.getNote() == null) {
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

                if (thesis.getRelatore().getId().equals(loggedInUser.getId())) {

                    setCardDocumentsCreator();
                    setNotesText();

                    Button editImage = root.findViewById(R.id.edit_image_tesi_button);
                    editImage.setVisibility(View.VISIBLE);
                    editImage.setOnClickListener(view1 -> {
                        checkAndRequestReadExternalStorage();
                    });


                    //TODO: gestire anche modifica vincoli e modifica relatori
                }

                if (loggedInUser.getRole().equals(RoleUser.STUDENT)) {

                    if (!thesis.getAssigned()) {
                        root.findViewById(R.id.btn_book).setVisibility(View.VISIBLE);
                    }

                    FirebaseFirestore.getInstance().collection("tesi_classifiche")
                            .document(loggedInUser.getId()).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    TesiClassifica classification = documentSnapshot.toObject(TesiClassifica.class);

                                    for (Tesi tesi : classification.getTesi()) {
                                        if (tesi.getId().equals(thesis.getId())) {
                                            menuTesi.findItem(FAVORITE_THESIS).setIcon(R.drawable.ic_favorite_24dp);
                                            isFavourite = true;
                                            break;
                                        }
                                    }
                                }
                            });
                }

                providerMenu = new MenuProvider() {
                    @Override
                    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                        menu.clear();

                        if (mainViewModel.getUser().getValue().getRole().equals(RoleUser.STUDENT)) {
                            menuInflater.inflate(R.menu.app_bar_visualize_tesi, menu);
                        } else {
                            menuInflater.inflate(R.menu.app_bar_visualize_thesis_prof, menu);
                        }

                        menuTesi = menu;
                    }

                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case VisualizeTesiFragment.FAVORITE_THESIS:
                                return switchFavouriteThesis(menuItem);
                            case VisualizeTesiFragment.ADD_TICKET_THESIS:
                                Ticket ticket = new Ticket(mainViewModel.getIdUser(), thesis.getRelatore().getId(), thesis.getId(), thesis.getNomeTesi(), TicketState.NEW);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(TicketFragment.TICKET_KEY, (Serializable) ticket);
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
                            default:
                                return false;
                        }
                    }
                };

                requireActivity().addMenuProvider(providerMenu);
            }
        });

        thesisViewModel.getThesis().observe(getViewLifecycleOwner(), tesi -> {
            if (tesi == null) {
                return;
            }

            if (!tesi.getDocuments().equals(thesis.getDocuments())) {

                thesis.setDocuments(tesi.getDocuments());
                documentAdapter.setDocuments(thesis.getDocuments());

                Map<String, Object> updates = new HashMap<>();

                updates.put("documents", tesi.getDocuments());
                updateDataThesis(updates);
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
        documentAdapter = new DocumentAdapter(getContext(), thesis.getDocuments(), thesis.getId(), thesisViewModel, true);
        recyclerViewDocuments.setAdapter(documentAdapter);
        recyclerViewDocuments.setLayoutManager(new LinearLayoutManager(getContext()));

        documentsArrowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFileDialogFragment uploadFileDialogFragment = new UploadFileDialogFragment(thesis);

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
                editTextNotes.setText(textViewNotes.getText().toString());

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
                updates.put("notes", notes);
                firestore.collection("thesis").document(thesis.getId()).update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                textViewNotes.setText(notes);
                                thesis.setNote(notes);

                                thesisViewModel.getThesis().setValue(thesis);
                                isEditing = false;
                                textViewNotes.setVisibility(View.VISIBLE);
                                editTextNotes.setVisibility(View.GONE);
                                buttonSave.setVisibility(View.GONE);
                                checkAndRequestInternetPermission();
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

                            thesisViewModel.getThesis().setValue(thesis);
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

        DocumentReference classificaDocument = FirebaseFirestore.getInstance().collection("tesi_classifiche")
                .document(mainViewModel.getIdUser());

        classificaDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                TesiClassifica classification = documentSnapshot.toObject(TesiClassifica.class);

                int icon;
                if (isFavourite) {
                    List<Tesi> newList = new ArrayList<>();

                    for (Tesi tesi : classification.getTesi()) {
                        if (!tesi.equals(thesis)) {
                            newList.add(tesi);
                        }
                    }

                    classification.setTesi(newList);
                    icon = R.drawable.ic_baseline_favorite_border_24;
                    isFavourite = false;
                } else {
                    List<Tesi> newList = classification.getTesi();
                    newList.add(thesis);

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
            }
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(TESI_VISUALIZE, thesis);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        requireActivity().removeMenuProvider(providerMenu);
        thesisViewModel.getThesis().removeObservers(getViewLifecycleOwner());
        thesisViewModel.getThesis().setValue(null);
        navBar = null;
    }
}