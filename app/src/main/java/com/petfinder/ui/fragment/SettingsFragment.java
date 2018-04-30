package com.petfinder.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.petfinder.R;
import com.petfinder.util.Util;

public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final String IP_PATTERN = "^\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[:]\\d{4,10}$";

    private EditText etIP;
    private Button btnSaveChanges;

    private View layout;

    //@SuppressLint("CommitPrefEdits")
    private View.OnClickListener btnSaveChangesOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(!etIP.getText().toString().trim().isEmpty()) {
                if (etIP.getText().toString().matches(IP_PATTERN)) {
                    Util.SharedPreferencesHelper.setValue(getString(R.string.ip_key),
                            String.format("http://%s", etIP.getText().toString()),
                            getContext());
                    Util.showAlert("IP guardada correctamente", getContext());
                    etIP.setError(null);
                }
                else
                    etIP.setError("La IP esta malformada");
            } else
                etIP.setError("Debe proporcionar una IP");
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_settings, container, false);

        etIP = layout.findViewById(R.id.etIP);
        btnSaveChanges = layout.findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(btnSaveChangesOnClickListener);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Object ip = Util.SharedPreferencesHelper.getValue(getString(R.string.ip_key), getContext());
        String currentIP = ip != null ? ip.toString() : "";
        if(currentIP != null)
            etIP.setText(currentIP);
    }
}
