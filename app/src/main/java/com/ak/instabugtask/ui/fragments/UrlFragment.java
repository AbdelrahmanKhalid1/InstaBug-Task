package com.ak.instabugtask.ui.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.ak.instabugtask.ui.adapter.HeaderAdapter;
import com.ak.instabugtask.ui.dialog.AddHeaderDialog;
import com.ak.instabugtask.ui.dialog.EditHeaderDialog;
import com.ak.instabugtask.ui.dialog.HttpMethodDialog;
import com.ak.instabugtask.ui.presnter.UrlFragmentPresenter;
import com.ak.instabugtask.utils.HttpMethod;
import com.ak.instabugtask.R;
import com.ak.instabugtask.model.Request;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UrlFragment extends Fragment implements HttpMethodDialog.HttpMethodDialogListener, UrlFragmentPresenter.UrlFragmentPresenterListener,
        AddHeaderDialog.AddHeaderDialogClickListener, HeaderAdapter.OnHeaderAdapterClickListener, EditHeaderDialog.EditHeaderClickListener {

    private static final String KEY_URL = "com.ak.instabugtask.ui.fragments.url";
    private static final String KEY_BODY = "com.ak.instabugtask.ui.fragments.body";
    private static final String KEY_METHOD_NAME = "com.ak.instabugtask.ui.fragments.method";
    private static final String KEY_HEADERS = "com.ak.instabugtask.ui.fragments.headers";

    private Button mBtnRequest;
    private Button mBtnHttpMethod;
    private Button mBtnAddHeader;
    private TextInputEditText mEditTextUrl;
    private TextInputEditText mEditTextBody;
    private TextInputLayout mTextInputLayoutBody;
    private TextView mTextViewRes;
    private RecyclerView mRecyclerView;
    private HeaderAdapter mHeaderAdapter;

    private UrlFragmentPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_url, container, false);
        mBtnRequest = (Button) view.findViewById(R.id.btn_request);
        mBtnHttpMethod = (Button) view.findViewById(R.id.btn_method);
        mEditTextUrl = (TextInputEditText) view.findViewById(R.id.editText_url);
        mEditTextBody = (TextInputEditText) view.findViewById(R.id.editText_body);
        mTextInputLayoutBody = (TextInputLayout) view.findViewById(R.id.textInputLayout_body);
        mTextViewRes = (TextView) view.findViewById(R.id.textView_res);
        mBtnAddHeader = (Button) view.findViewById(R.id.btn_add_header);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        Request req = extractRequestFromBundle(savedInstanceState);
        presenter = new UrlFragmentPresenter(this, req);

        mHeaderAdapter = new HeaderAdapter(presenter.getReq().getHeaders(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mHeaderAdapter);
        return view;
    }

    private Request extractRequestFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return new Request(HttpMethod.GET, "", "", new HashMap<>()); //default

        String url = savedInstanceState.getString(KEY_URL, "");
        String body = savedInstanceState.getString(KEY_BODY, "");
        String httpMethod = savedInstanceState.getString(KEY_METHOD_NAME, HttpMethod.GET.name());
        Map<String, String> headers = (Map<String, String>) savedInstanceState.getSerializable(KEY_HEADERS);
        return new Request(HttpMethod.valueOf(httpMethod), url, body, headers);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mEditTextUrl.setText(presenter.getReq().getUrlStr());
        mEditTextBody.setText(presenter.getReq().getBody());
        mBtnHttpMethod.setText(presenter.getReq().getHttpMethod().name());

        setEditTextBodyVisibility(presenter.getReq().getHttpMethod());
        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDeviceOnline()) {
                    Snackbar.make(requireContext(), v, "Device is not connected! Try again later.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mEditTextUrl.onEditorAction(EditorInfo.IME_ACTION_DONE);
                try {
                    testUrl();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mBtnHttpMethod.setText(presenter.getReq().getHttpMethod().name());
        mBtnHttpMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpMethodDialog dialog = new HttpMethodDialog(presenter.getReq().getHttpMethod().getMethodCode(), UrlFragment.this);
                dialog.show(getParentFragmentManager(), "Http method dialog");
            }
        });

        mBtnAddHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHeaderDialog dialog = new AddHeaderDialog(UrlFragment.this);
                dialog.show(getParentFragmentManager(), "add header dialog");
            }
        });
    }

    private void testUrl() throws IOException {
        if (mEditTextUrl.getText() == null || mEditTextUrl.getText().length() == 0) {
            Snackbar.make(requireContext(), requireView(), "Url can't be empty", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String body = mEditTextBody.getText() == null ? "" : mEditTextBody.getText().toString();
        presenter.execute(mEditTextUrl.getText().toString(), body);
        mHeaderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelect(HttpMethod httpMethod) {
        presenter.getReq().setHttpMethod(httpMethod);
        mBtnHttpMethod.setText(httpMethod.name());
        setEditTextBodyVisibility(httpMethod);
    }

    private void setEditTextBodyVisibility(HttpMethod httpMethod) {
        int editTextBodyVisibility = httpMethod == HttpMethod.POST ? View.VISIBLE : View.GONE;
        mTextInputLayoutBody.setVisibility(editTextBodyVisibility);
    }

    @Override
    public void onResponse(String result) {
        mTextViewRes.setText(result);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    @Override
    public void onDoneClicked(String headerName, String headerValue) {
        if (!mHeaderAdapter.addNewHeader(headerName, headerValue)) {
            Snackbar.make(requireContext(), mBtnAddHeader, "Header already exists", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position, String key) {
        mHeaderAdapter.getHeaders().remove(key);
        mHeaderAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onUpdateClick(String key) {
        EditHeaderDialog dialog = new EditHeaderDialog(this, key, mHeaderAdapter.getHeaders().get(key));
        dialog.show(getParentFragmentManager(), "edit header dialog");
    }

    @Override
    public void onEditDoneClick(String key, String value) {
        mHeaderAdapter.updateHeader(key, value);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_URL, mEditTextUrl.getText().toString());
        outState.putString(KEY_BODY, mEditTextBody.getText().toString());
        outState.putString(KEY_METHOD_NAME, presenter.getReq().getHttpMethod().name());
        outState.putSerializable(KEY_HEADERS, (Serializable) presenter.getReq().getHeaders());
    }
}