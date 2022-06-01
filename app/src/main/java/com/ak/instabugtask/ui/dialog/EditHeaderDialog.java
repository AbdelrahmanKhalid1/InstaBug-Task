package com.ak.instabugtask.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ak.instabugtask.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditHeaderDialog extends DialogFragment {

    private final EditHeaderClickListener listener;
    private EditText mEditTextHeaderName;
    private EditText mEditTextHeaderValue;
    private final String name;
    private final String value;

    public EditHeaderDialog(EditHeaderClickListener listener, String name, String value) {
        this.listener = listener;
        this.name = name;
        this.value = value;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_header, null);
        mEditTextHeaderName = view.findViewById(R.id.editText_header_name);
        mEditTextHeaderName.setText(name);
        mEditTextHeaderName.setEnabled(false);
        mEditTextHeaderValue = view.findViewById(R.id.editText_header_value);
        mEditTextHeaderValue.setText(value);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .setTitle("Edit Header")
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onEditDoneClick(name, mEditTextHeaderValue.getText().toString());
                    }
                })
                .setNegativeButton("cancel", null);

        return builder.create();
    }

    public interface EditHeaderClickListener{
        void onEditDoneClick(String key, String value);
    }
}
