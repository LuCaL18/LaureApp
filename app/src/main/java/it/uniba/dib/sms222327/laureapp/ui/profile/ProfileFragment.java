package it.uniba.dib.sms222327.laureapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import it.uniba.dib.sms222327.laureapp.MainActivity;
import it.uniba.dib.sms222327.laureapp.MainViewModel;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.EnumScopes;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;
import it.uniba.dib.sms222327.laureapp.ui.profile.dialogs.PasswordChangeDialog;
import it.uniba.dib.sms222327.laureapp.util.ShareContent;
import it.uniba.dib.sms222327.laureapp.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Fragment che si occupa della visualizzazione e della gestione della schermata di Profilo
 */
public class ProfileFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 101;

    private ImageView profileImageView;

    private MainViewModel mainActivityViewModel;
    private TextView nameTextView;
    private TextView lastNameTextView;
    private TextView roleTextView;
    private TextView birthDateTextView;
    private TextView interestsTextView;
    private TextView emailTextView;
    private TextView bioTextView;
    private MaterialCardView bioCard;
    private MaterialCardView scopesCard;
    private Button passwordChangeButton;
    private Button updateProfileButton;
    private FloatingActionButton uploadImageButton;


    private File photoFile;
    private ActivityResultLauncher<Intent> pickPhotoLauncher;
    private ActivityResultLauncher<Intent> pickPhotoCameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileImageView = view.findViewById(R.id.profile_image_view);
        uploadImageButton = view.findViewById(R.id.upload_image_button);
        updateProfileButton = view.findViewById(R.id.edit_profile_button);

        profileImageView = view.findViewById(R.id.profile_image_view);
        nameTextView = view.findViewById(R.id.name_text_view);
        lastNameTextView = view.findViewById(R.id.last_name_text_view);
        roleTextView = view.findViewById(R.id.role_text_view);
        birthDateTextView = view.findViewById(R.id.birth_date_text_view);
        interestsTextView = view.findViewById(R.id.interests_text_view);
        emailTextView = view.findViewById(R.id.tv_email_profile);
        bioTextView = view.findViewById(R.id.bio_text_view);
        bioCard = view.findViewById(R.id.cv_bio_profile);
        scopesCard = view.findViewById(R.id.cv_scopes_profile);

        uploadImageButton.setOnClickListener(v -> openImageSelectionDialog());
        updateProfileButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_profile_to_editProfileFragment);
        });

        passwordChangeButton = view.findViewById(R.id.password_change_button);
        passwordChangeButton.setOnClickListener(v -> showPasswordChangeDialog());

        pickPhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        // Upload the file
                        uploadFile(mainActivityViewModel.getIdUser(), fileUri);
                    }
                });

        pickPhotoCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && photoFile != null) {
                uploadFile(mainActivityViewModel.getIdUser(), FileProvider.getUriForFile(requireContext(), ShareContent.AUTHORITY, photoFile));
            } else {
                showSaveToast(R.string.error_upload);
            }
        });

        observeUserData();
    }

    private void openImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.select_image))
                .setItems(new CharSequence[]{getString(R.string.menu_gallery), getString(R.string.take_picture)}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                selectImageFromGallery();
                            } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                                selectImageFromGallery();
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                            REQUEST_READ_EXTERNAL_STORAGE);
                                } else {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_READ_EXTERNAL_STORAGE);
                                }
                            }
                            break;
                        case 1:
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED) {
                                captureImageFromCamera();
                            } else {
                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                        REQUEST_CAMERA_PERMISSION);
                            }
                            break;
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoLauncher.launch(intent);
    }

    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Crea un file temporaneo per l'immagine catturata
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(requireContext(), ShareContent.AUTHORITY, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                pickPhotoCameraLauncher.launch(takePictureIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera();
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(requireContext(), getString(R.string.access_media_gallery_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayUserData(LoggedInUser user) {
        if (RoleUser.GUEST.equals(user.getRole())) {
            passwordChangeButton.setVisibility(View.GONE);
            updateProfileButton.setVisibility(View.GONE);
            uploadImageButton.setVisibility(View.GONE);
        }

        emailTextView.setText(user.getEmail());
        nameTextView.setText(user.getName());
        lastNameTextView.setText(user.getSurname());
        roleTextView.setText(user.getRole().toString());
        birthDateTextView.setText(user.getBirthDay());

        if (user.getAmbiti() == null || user.getAmbiti().isEmpty()) {
            scopesCard.setVisibility(View.GONE);
        } else {
            StringBuilder scopesString = new StringBuilder();
            List<String> scopes = user.getAmbiti();
            for (String scope : scopes) {
                if (user.getAmbiti().indexOf(scope) != scopes.size() - 1) {
                    scopesString.append(Utility.translateScopesFromEnum(getResources(), EnumScopes.valueOf(scope))).append(", ");
                } else {
                    scopesString.append(Utility.translateScopesFromEnum(getResources(), EnumScopes.valueOf(scope)));
                }
            }
            interestsTextView.setText(scopesString.toString());
        }

        if (user.getBio() == null || user.getBio().isEmpty()) {
            bioCard.setVisibility(View.GONE);
        } else {
            bioTextView.setText(user.getBio());
        }

        // Load profile image using Glide or your preferred image loading library
        Glide.with(requireContext())
                .load(user.getPhotoUrl())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_user_round)
                        .error(R.mipmap.ic_user_round)
                        .transform(new CircleCrop())
                        .skipMemoryCache(true))
                .into(profileImageView);
    }

    private void observeUserData() {
        mainActivityViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (RoleUser.GUEST.equals(user.getRole()) && getView() != null) {
                    View view = getView();
                    showGuestLayoutProfile(view);
                } else {
                    displayUserData(user);
                }
            }
        });
    }

    private File createImageFile() {
        String imageFileName = "JPEG_" + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadFile(String title, Uri fileUri) {

        LoggedInUser user = mainActivityViewModel.getUser().getValue();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference documentRef = storageRef.child("images/profile/" + title);

        // Upload the file to Firebase Storage
        documentRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        showSaveToast(R.string.image_saved_successfully);

                        documentRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            user.setPhotoUrl(uri.toString());
                            mainActivityViewModel.updateUser(user);
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

    private void showSaveToast(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showPasswordChangeDialog() {
        PasswordChangeDialog dialog = new PasswordChangeDialog();
        dialog.show(getParentFragmentManager(), "password_change_dialog");
    }

    private void showGuestLayoutProfile(View view) {
        ScrollView profileLayout = view.findViewById(R.id.profile_scroll_layout);
        profileLayout.setVisibility(View.GONE);
        LinearLayout guestLayout = view.findViewById(R.id.guest_profile_linear_layout);
        guestLayout.setVisibility(View.VISIBLE);
        Button loginButton = view.findViewById(R.id.button_go_to_login_from_profile);
        loginButton.setOnClickListener(v -> {
            if(getActivity() != null) {
                ((MainActivity)getActivity()).goToLoginActivity();
            }
        });
    }
}