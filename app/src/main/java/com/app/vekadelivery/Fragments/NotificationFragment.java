package com.app.vekadelivery.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.vekadelivery.Model.NotificationModel;
import com.app.vekadelivery.Network.PrefrenceManager;
import com.app.vekadelivery.Network.RetrofitInstance;
import com.app.vekadelivery.R;
import com.app.vekadelivery.databinding.FragmentNotificationBinding;
import com.app.vekadelivery.databinding.NotificationItemsBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends Fragment {


    FragmentNotificationBinding binding;
    List<NotificationModel> notificationModelList = new ArrayList<>();
    ProgressDialog progressDialog;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getNofication();

        notificationSharePreferenceNew();
    }

    private void getNofication(){
        progreessDialog();
        RetrofitInstance.getClient().getNotfication(new PrefrenceManager(getContext()).getuserid()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getString("status").equalsIgnoreCase("success")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        notificationSharePreferenceOld(jsonArray.length());
                       for (int i=0;i<jsonArray.length();i++){
                           NotificationModel model = new Gson().fromJson(jsonArray.getJSONObject(i).toString(),NotificationModel.class);
                           notificationModelList.add(model);
                       }

                        if(notificationModelList.size()==0){
                            binding.notfound.setVisibility(View.VISIBLE);
                        }
                        binding.notificationrecyclerview.setAdapter(new NotificationAdapter(notificationModelList));



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }


    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationDataHolder> {

        List<NotificationModel> list;

        public NotificationAdapter(List<NotificationModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public NotificationDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NotificationDataHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_items,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationDataHolder holder, int position) {
            holder.binding.notificationtext.setText(list.get(position).getMsg());
            holder.binding.staus.setText(list.get(position).getStatus());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class NotificationDataHolder extends RecyclerView.ViewHolder {

            NotificationItemsBinding binding;

            public NotificationDataHolder(@NonNull View itemView) {
                super(itemView);
                binding = NotificationItemsBinding.bind(itemView.getRootView());
            }
        }
    }

    public void progreessDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressbaritems);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    public void notificationSharePreferenceNew(){
        int pp = 0;
        SharedPreferences sharedPreferences_frist;
        final String Notification_New="new_notification";
        sharedPreferences_frist = getActivity().getSharedPreferences(Notification_New, Context.MODE_PRIVATE);
        SharedPreferences.Editor new_editor = sharedPreferences_frist.edit();
        new_editor.putInt("new_data",pp);
        new_editor.apply();
        new_editor.commit();


    }


    public  void notificationSharePreferenceOld(int ii){
        SharedPreferences sharedPreferences_Second;
        final String Notification_old="old_notification";
        sharedPreferences_Second = getActivity().getSharedPreferences(Notification_old,Context.MODE_PRIVATE);
        SharedPreferences.Editor old_editot = sharedPreferences_Second.edit();
        old_editot.putInt("old",ii);
        old_editot.apply();
        old_editot.commit();
    }
}