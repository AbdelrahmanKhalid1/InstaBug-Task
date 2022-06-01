package com.ak.instabugtask.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.instabugtask.R;

import java.util.Iterator;
import java.util.Map;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    private final Map<String, String> headers;
    private Iterator<Map.Entry<String, String>> itr;
    private final OnHeaderAdapterClickListener listener;

//    public HeaderAdapter(){
//        headers = new HashMap<>();
//    }

    public HeaderAdapter(Map<String, String> headers, OnHeaderAdapterClickListener listener) {
        this.listener = listener;
        this.headers = headers;
        itr = headers.entrySet().iterator();
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_header, parent, false);
        return new HeaderViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        Map.Entry<String, String> entry = itr.next();
        holder.bind(entry.getKey(), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Boolean addNewHeader(String name, String value){
        if(headers.containsKey(name)){
            return false;
        }
        headers.put(name, value);
        itr = headers.entrySet().iterator();
        notifyDataSetChanged();
        return true;
    }

    public void updateHeader(String key, String value) {
        headers.put(key, value);
        itr = headers.entrySet().iterator();
        notifyDataSetChanged();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerName;
        private final TextView headerValue;
        private final OnHeaderAdapterClickListener listener;

        public HeaderViewHolder(@NonNull View itemView, OnHeaderAdapterClickListener listener) {
            super(itemView);
            this.listener = listener;
            headerName = (TextView) itemView.findViewById(R.id.textView_header_name);
            headerValue = (TextView) itemView.findViewById(R.id.textView_header_value);

            ImageView deleteHeader = (ImageView) itemView.findViewById(R.id.btn_delete_header);
            deleteHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION){
                        listener.onDeleteClick(getAdapterPosition(), headerName.getText().toString());
                    }
                }
            });

            CardView cardView = (CardView) itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION){
                        listener.onUpdateClick(headerName.getText().toString());
                    }
                }
            });
        }

        public void bind(String name, String value){
            headerName.setText(name);
            headerValue.setText(value);
        }
    }

    public interface OnHeaderAdapterClickListener{
        void onDeleteClick(int position, String key);
        void onUpdateClick(String key);
    }
}
