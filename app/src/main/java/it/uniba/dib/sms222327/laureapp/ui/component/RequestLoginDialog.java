package it.uniba.dib.sms222327.laureapp.ui.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms222327.laureapp.MainActivity;
import it.uniba.dib.sms222327.laureapp.R;

/**
 * Classe che estende DialogFragment
 * utilizzata per mostrare il dialog per la richiesta di login
 */
public class RequestLoginDialog extends DialogFragment {

    public RequestLoginDialog() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.request_login_dialog, container, false);

        Button loginButton = view.findViewById(R.id.button_go_to_login);
        ImageView closeButton = view.findViewById(R.id.image_close_dialog);

        loginButton.setOnClickListener(v -> {
            if(getActivity() != null) {
                //richiama metodo dell'activity
                ((MainActivity)getActivity()).goToLoginActivity();
                dismiss();
            }
        });

        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
