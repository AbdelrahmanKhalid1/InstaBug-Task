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
import com.ak.instabugtask.utils.HttpMethod;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Map;

public class AddHeaderDialog extends DialogFragment {

    private final AddHeaderDialogClickListener listener;
    private EditText mEditTextHeaderName;
    private EditText mEditTextHeaderValue;

    public AddHeaderDialog(AddHeaderDialogClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_header, null);
        mEditTextHeaderName = view.findViewById(R.id.editText_header_name);
        mEditTextHeaderValue = view.findViewById(R.id.editText_header_value);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .setTitle("Add New Header")
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDoneClicked(mEditTextHeaderName.getText().toString(), mEditTextHeaderValue.getText().toString());
                    }
                })
                .setNegativeButton("cancel", null);

        return builder.create();
    }

    public interface AddHeaderDialogClickListener {
        void onDoneClicked(String headerName, String headerValue);
    }
}
