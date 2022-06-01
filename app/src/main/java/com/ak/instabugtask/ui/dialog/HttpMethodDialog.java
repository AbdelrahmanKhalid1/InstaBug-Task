package com.ak.instabugtask.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ak.instabugtask.utils.HttpMethod;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HttpMethodDialog extends DialogFragment {

    private int selectedMethodCode;
    private final HttpMethodDialogListener listener;

    public HttpMethodDialog(int selectedMethodCode, HttpMethodDialogListener listener) {
        this.selectedMethodCode = selectedMethodCode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] httpMethods = {"GET", "POST"};
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Http Method")
                .setSingleChoiceItems(httpMethods, selectedMethodCode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedMethodCode = which;
                    }
                })
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String httpMethodStr = httpMethods[selectedMethodCode];
                        listener.onSelect(HttpMethod.valueOf(httpMethodStr));
                    }
                })
                .setNegativeButton("cancel", null);
        return dialog.create();
    }

    public interface HttpMethodDialogListener{
        void onSelect(HttpMethod httpMethod);
    }
}
