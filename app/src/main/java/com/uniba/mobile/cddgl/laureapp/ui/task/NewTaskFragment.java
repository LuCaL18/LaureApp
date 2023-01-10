package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.NewTaskIn;
import com.uniba.mobile.cddgl.laureapp.databinding.FragmentNewTaskBinding;

public class NewTaskFragment extends Fragment {

    private FragmentNewTaskBinding binding;
    private BottomNavigationView navBar;
    private MenuProvider provider;
    private Toolbar toolbar;
    private Drawable iconPre;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewTaskViewModel newtaskViewModel = new ViewModelProvider(requireActivity()).get(NewTaskViewModel.class);

        final EditText nometaskEditText = binding.nometask;
        final EditText statoEditText = binding.stato;
        final EditText descrizioneEditText = binding.descrizione;
        final Button addtaskButton = binding.button;

        newtaskViewModel.getnewtaskFormState().observe(getViewLifecycleOwner(), new Observer<NewTaskFormState>() {
            @Override
            public void onChanged(@Nullable NewTaskFormState newTaskFormState) {
                if (newTaskFormState == null) {
                    return;
                }
                addtaskButton.setEnabled(newTaskFormState.isDataValid());
                if (newTaskFormState.getNometaskError() != null) {
                    nometaskEditText.setError(getString(newTaskFormState.getNometaskError()));
                }
                if (newTaskFormState.getStatoError() != null) {
                    statoEditText.setError(getString(newTaskFormState.getStatoError()));
                }
            }
        });

        /* newtaskViewModel.getnewtaskResult().observe(getViewLifecycleOwner(), new Observer<newtaskResult>() {
            @Override
            public void onChanged(@Nullable NewTaskResult newtaskResult) {
                if (newtaskResult == null) {
                    return;
                }
                if (newtaskResult.getError() != null) {
                    showNewTaskFailure(newtaskResult.getError());
                }
                if (newtaskResult.getSuccess() != null) {
                    NewTaskIn result = newtaskResult.getSuccess();
                    updateUIwithUser(result.getNomeTask());
                    newtaskViewModel.setLoggedUser(result);
                }
            }
        }); */

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                newtaskViewModel.newtaskDataChanged(nometaskEditText.getText().toString(),
                        statoEditText.getText().toString(),descrizioneEditText.getText().toString());
            }
        };
        nometaskEditText.addTextChangedListener(afterTextChangedListener);
        statoEditText.addTextChangedListener(afterTextChangedListener);
        statoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newtaskViewModel.insertNewTask(nometaskEditText.getText().toString(),
                            statoEditText.getText().toString(),descrizioneEditText.getText().toString());
                }
                return false;
            }
        });

        addtaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newtaskViewModel.insertNewTask(nometaskEditText.getText().toString(),
                        statoEditText.getText().toString(),descrizioneEditText.getText().toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        requireActivity().removeMenuProvider(provider);
        toolbar.setNavigationIcon(iconPre);
        navBar = null;
        binding = null;
        provider = null;
        toolbar = null;
    }
}