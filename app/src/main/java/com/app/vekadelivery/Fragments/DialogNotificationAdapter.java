package com.app.vekadelivery.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.vekadelivery.Model.NotificationModel;
import com.app.vekadelivery.R;
import com.app.vekadelivery.databinding.NotificationItems1Binding;

import java.util.List;

public class DialogNotificationAdapter extends RecyclerView.Adapter<DialogNotificationAdapter.NotificationDataHolder> {

    List<NotificationModel> list;
    int size;

    public DialogNotificationAdapter(int size, List<NotificationModel> list) {
        this.size = size;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationDataHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_items1,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationDataHolder holder, int position) {
        holder.binding.notificationtext.setText(list.get(position).getMsg());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationDataHolder extends RecyclerView.ViewHolder {

        NotificationItems1Binding binding;

        public NotificationDataHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationItems1Binding.bind(itemView.getRootView());
        }
    }
}
