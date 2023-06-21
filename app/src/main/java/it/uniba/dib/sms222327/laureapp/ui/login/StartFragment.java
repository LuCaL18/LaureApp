package it.uniba.dib.sms222327.laureapp.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;

/**
 * Fragment che si occupa di mostrare la schermata iniziale dell'app per permettere all'utente di selezionare
 * login, registrazione o l'accesso come ospite
 */
public class StartFragment extends Fragment {
    private NavController navController;
    private LoginViewModel loginViewModel;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.navController = NavHostFragment.findNavController(this);


        Button loginButton = view.findViewById(R.id.buttonLogin);
        Button signInButton = view.findViewById(R.id.buttonSignIn);
        TextView guestText = view.findViewById(R.id.textGuest);

        loginButton.setOnClickListener(view1 -> navController.navigate(R.id.action_startFragment_to_loginFragment));

        signInButton.setOnClickListener(view1 -> navController.navigate(R.id.action_startFragment_to_signInFragment));

        guestText.setOnClickListener(view1 -> loginViewModel.setLoggedUser(new LoggedInUser("ospite", "GUEST", RoleUser.GUEST)));
    }

    @Override
    public void onStart() {
        super.onStart();
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        navController = null;
    }
}