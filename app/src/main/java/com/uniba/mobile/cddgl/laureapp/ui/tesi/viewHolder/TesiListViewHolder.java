package com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder;

import static com.uniba.mobile.cddgl.laureapp.ui.tesi.ClassificaTesiFragment.SHARED_PREFS_NAME;
import static com.uniba.mobile.cddgl.laureapp.ui.tesi.ClassificaTesiFragment.TESI_LIST_KEY_PREF;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.data.model.TesiClassifica;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.interfaces.FavouriteItemCallback;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.util.ShareContent;
import com.uniba.mobile.cddgl.laureapp.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewHolder che si occupa della visualizzazione di una tesi della lista tesi
 */
public class TesiListViewHolder {

    private static final String CLASS_ID = "TesiViewHolder";

    private final Context context;
    private final View view;
    private final TextView nomeTesiTextView;
    private final TextView nomeRelatoreTextView;
    private final ImageButton shareTesiButton;
    private final ImageButton preferitiTesiButton;
    private boolean isFavourite = false;
    private FavouriteItemCallback favouriteItemCallback;

    public TesiListViewHolder(View itemView, FavouriteItemCallback callback) {
        view = itemView;
        context = itemView.getContext();
        nomeTesiTextView = itemView.findViewById(R.id.nometesi);
        nomeRelatoreTextView = itemView.findViewById(R.id.nomerelatore);
        shareTesiButton = itemView.findViewById(R.id.share_tesi);
        preferitiTesiButton = itemView.findViewById(R.id.addTesi);
        favouriteItemCallback = callback;
    }

    public void bindData(Tesi tesi, VisualizeThesisViewModel thesisViewModel, LoggedInUser userLogged, boolean isFavourite) {
        this.isFavourite = isFavourite;

        if (isFavourite) {
            preferitiTesiButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24dp));
        } else {
            preferitiTesiButton.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_border_24));
        }

        nomeTesiTextView.setText(tesi.getNomeTesi());
        nomeRelatoreTextView.setText(tesi.getRelatore().getDisplayName());

        view.setOnClickListener(v -> {
            view.setSelected(true);
            thesisViewModel.getThesis().setValue(tesi);
        });

        shareTesiButton.setOnClickListener(view -> shareTesi(tesi));

        try {
            if (userLogged != null && userLogged.getRole() == RoleUser.PROFESSOR) {
                preferitiTesiButton.setVisibility(View.GONE);
            } else {
                preferitiTesiButton.setVisibility(View.VISIBLE);
                preferitiTesiButton.setOnClickListener(v -> switchFavouriteThesis(tesi, userLogged));
            }
        } catch (Exception e) {
            Log.e(CLASS_ID, "Error during add tesi to ranking --> " + e);
        }

    }

    private void shareTesi(Tesi tesi) {
        try {
            Intent intent = null;
            ShareContent shareContent = new ShareContent(context);

            try {
                intent = shareContent.shareThesisData((Tesi) tesi.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_data_with)));
        }catch (Exception e) {
            Log.e(CLASS_ID, "Error during share tesi --> " + e);
        }

    }

    private void switchFavouriteThesis(Tesi thesis, LoggedInUser user) {

        if (user.getRole().equals(RoleUser.GUEST)) {

            List<String> tesiList = Utility.getTesiList(context);
            if (tesiList.isEmpty()) {

                List<String> newTesiList = new ArrayList<>();
                newTesiList.add(thesis.getId());

                if (saveListofThesis(newTesiList)) {
                    preferitiTesiButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24dp));
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
                preferitiTesiButton.setImageDrawable(context.getDrawable(icon));
            }
            favouriteItemCallback.onFavouriteItemClicked();
            return;
        }

        DocumentReference classificaDocument = FirebaseFirestore.getInstance().collection("tesi_classifiche")
                .document(user.getId());

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
                        preferitiTesiButton.setImageDrawable(context.getDrawable(icon));
                        favouriteItemCallback.onFavouriteItemClicked();
                    } else {
                        Log.e("VisualizeTesiFragment", task.getException().getMessage());
                    }
                });
            } else {
                List<String> newTesiList = new ArrayList<>();
                newTesiList.add(thesis.getId());
                classificaDocument.set(new TesiClassifica(newTesiList, user.getId()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                preferitiTesiButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24dp));
                                isFavourite = true;
                                favouriteItemCallback.onFavouriteItemClicked();
                            } else {
                                showSaveToast(R.string.unable_add_favorite);
                            }
                        });
            }
        });
    }

    private boolean saveListofThesis(List<String> tesiList) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor mEdit1 = sp.edit();

            List<String> list = new ArrayList<>(tesiList);

            mEdit1.putString(TESI_LIST_KEY_PREF, new Gson().toJson(list));
            return mEdit1.commit();
        } catch (Exception e) {
            Log.e("ClassificaTesiFragment", e.getMessage());

            return false;
        }
    }

    private void showSaveToast(@StringRes Integer message) {
        if (context != null && context.getApplicationContext() != null) {
            Toast.makeText(
                    context.getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }
}

