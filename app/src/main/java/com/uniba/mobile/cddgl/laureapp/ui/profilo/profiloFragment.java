package com.uniba.mobile.cddgl.laureapp.ui.profilo;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.Profilo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profiloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profiloFragment extends Fragment {

    private NavController navController;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference m;


// ...




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Profilo pr;

    public profiloFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profiloFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profiloFragment newInstance(String param1, String param2) {
        profiloFragment fragment = new profiloFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profilo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = NavHostFragment.findNavController(this);
        Button navigation_ModificaDati = view.findViewById(R.id.navigation_ModificaDati);
        navigation_ModificaDati.setOnClickListener(view1 -> navController.navigate(R.id.navigation_ModificaDati));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String userId = user.getUid();
        m = FirebaseDatabase.getInstance().getReference("users").child(userId);



        ValueEventListener p = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                Profilo pr = snapshot.getValue(Profilo.class);

                TextView t = view.findViewById(R.id.textNome);
                t.setText( getString(R.string.name_profile, pr.getName()) );

                TextView t2 = view.findViewById(R.id.textCognome);
                t2.setText( getString(R.string.surname_profile, pr.getSurname()) );

                TextView t3 = view.findViewById(R.id.textBio);
                t3.setText( getString(R.string.bio_profile, pr.getBio()) );

                TextView t4 = view.findViewById(R.id.textbirthDay);
                t4.setText( getString(R.string.birth_day_profile, pr.getBirthDay()) );

                TextView t5 = view.findViewById(R.id.textRole);
                String role;
                if(pr.getRole().equals(RoleUser.PROFESSOR)){
                    role = getString(R.string.professor);
                }else if(pr.getRole().equals(RoleUser.STUDENT)){
                    role = getString(R.string.student);
                }else{
                    role = getString(R.string.guest);
                }
                t5.setText(getString(R.string.role_profile , role ));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG , "errore caricamento database" , error.toException());

            }
        };
        m.addValueEventListener(p);


    }

}