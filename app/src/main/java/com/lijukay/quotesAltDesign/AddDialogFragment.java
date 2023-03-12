package com.lijukay.quotesAltDesign;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.lijukay.quotesAltDesign.Database.MyDatabaseHelper;

import java.util.Objects;

public class AddDialogFragment extends DialogFragment {
    View v;

    TextInputLayout qwotable_inputLayout, author_inputLayout, foundIn_inputLayout;
    EditText qwotable_input, author_input, foundIn_input;
    Button add_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.setContentView(R.layout.fragment_dialog);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dialog, container, false);

        qwotable_inputLayout = v.findViewById(R.id.qwotable_tf);
        author_inputLayout = v.findViewById(R.id.author_tf);
        foundIn_inputLayout = v.findViewById(R.id.found_in_tf);

        qwotable_input = qwotable_inputLayout.getEditText();
        author_input = author_inputLayout.getEditText();
        foundIn_input = foundIn_inputLayout.getEditText();

        add_button = v.findViewById(R.id.button_add);

        add_button.setOnClickListener(v -> {
            MyDatabaseHelper mydb = new MyDatabaseHelper(requireContext());
            mydb.addQwotable(qwotable_input.getText().toString().trim(),
                    author_input.getText().toString().trim(),
                    foundIn_input.getText().toString().trim()); //Add data to Database

        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set DialogFragment title
        Objects.requireNonNull(getDialog()).setTitle(getString(R.string.add_own_qwotable));
        
    }
}

