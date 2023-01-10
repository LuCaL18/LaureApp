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
        newtaskViewModel = new ViewModelProvider(requireActivity()).get(NewTaskViewModel.class);

        final EditText nometaskEditText = binding.nomeTask;
        final EditText statoEditText = binding.stato;
        final EditText descrizione = binding.descrizione;
        final Button addtaskEditText = binding.addtask;

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

        newtaskViewModel.getnewtaskResult().observe(getViewLifecycleOwner(), new Observer<newtaskResult>() {
            @Override
            public void onChanged(@Nullable NewTaskResult newtaskResult) {
                if (newtaskResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (newtaskResult.getError() != null) {
                    showNewTaskFailure(newtaskResult.getError());
                }
                if (newtaskResult.getSuccess() != null) {
                    NewTaskIn result = newtaskResult.getSuccess();
                    updateUiWithUser(result.getDisplayName());
                    newtaskViewModel.setLoggedUser(result);
                }
            }
        });

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
                        statoEditText.getText().toString());
            }
        };
        nometaskEditText.addTextChangedListener(afterTextChangedListener);
        statoEditText.addTextChangedListener(afterTextChangedListener);
        statoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newtaskViewModel.addTask(nometaskEditText.getText().toString(),
                            statoEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                newtaskViewModel.login(nometaskEditText.getText().toString(),
                        statoEditText.getText().toString());
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