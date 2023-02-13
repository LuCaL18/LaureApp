package com.uniba.mobile.cddgl.laureapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniba.mobile.cddgl.laureapp.MainActivity;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.Tesi;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tesiRef = db.collection("tesi");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FragmentHomeBinding binding;
    private MenuProvider provider;
    private final int NOTIFICATION_APP_BAR = R.id.notification_app_bar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        loadTesi();
           return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        NavController navController = NavHostFragment.findNavController(this);

        if(actionBar != null) {
            actionBar.setTitle(R.string.app_name_upperCase);
        }

        TextView tesi = binding.mostraTesi;
        tesi.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        navController.navigate(R.id.action_navigation_home_to_lista_tesi);
                                    }
                                });

                provider = new MenuProvider() {
                    @Override
                    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                        menu.clear();
                        menuInflater.inflate(R.menu.app_bar_home, menu);
                    }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //TODO: gestire barra per fragment come fatto qui
                    case NOTIFICATION_APP_BAR: {
                        navController.navigate(R.id.action_navigation_home_to_chatFragment);
                        return true;
                    }
                    case R.id.crea: {
                        navController.navigate(R.id.action_navigation_home_to_tesiFragmant);
                    }
                    default:
                        return false;
                }
            }

        };

        requireActivity().addMenuProvider(provider);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(provider);
        binding = null;
        provider = null;
    }

    private void loadTesi() {
            tesiRef.whereEqualTo("relatore", currentUser.getDisplayName())
                    .limit(3)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int counter=0;
                            CardView cardView;
                            ImageView img;
                            TextView Titolo;
                            TextView Descrizione;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                switch (counter) {
                                    case 0:
                                        cardView = binding.card1;
                                        cardView.setVisibility(View.VISIBLE);
                                        img = binding.img1;
                                        Titolo = binding.NomeTesi1;
                                        Descrizione = binding.DescrizioneTesi1;
                                        break;
                                    case 1:
                                        cardView = binding.card2;
                                        cardView.setVisibility(View.VISIBLE);
                                        img = binding.img2;
                                        Titolo = binding.NomeTesi2;
                                        Descrizione = binding.DescrizioneTesi2;
                                        break;
                                    case 2:
                                        cardView = binding.card3;
                                        cardView.setVisibility(View.VISIBLE);
                                        img = binding.img3;
                                        Titolo = binding.NomeTesi3;
                                        Descrizione = binding.DescrizioneTesi3;
                                        break;
                                    default:
                                        img = binding.img1;
                                        Titolo = binding.NomeTesi1;
                                        Descrizione = binding.DescrizioneTesi1;
                                        break;
                                }
                                Tesi tesi = documentSnapshot.toObject(Tesi.class);
                                tesi.setId_tesi(documentSnapshot.getId());

                                String nome = tesi.getNome_tesi();
                                String descrizione = tesi.getDescrizione();

                                counter++;
                                img.setImageResource(R.drawable.add);
                                Titolo.setText(nome);
                                Descrizione.setText(descrizione);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"ERROR",Toast.LENGTH_SHORT);
                        }
                    });
        }
    }
