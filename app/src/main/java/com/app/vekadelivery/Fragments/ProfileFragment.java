package com.app.vekadelivery.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.vekadelivery.Network.PrefrenceManager;
import com.app.vekadelivery.Network.RetrofitInstance;
import com.app.vekadelivery.R;
import com.app.vekadelivery.databinding.FragmentProfileBinding;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {


    FragmentProfileBinding binding;
    ProgressDialog progressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBoyData();

    }


    public void getBoyData(){
        progreessDialog();
        RetrofitInstance.getClient().getBoyData(new PrefrenceManager(getContext()).getuserid()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = new JSONObject(response.body().toString());
                    JSONObject jsonObject = jsonObject1.getJSONObject("data");
                    binding.name.setText(jsonObject.getString("name"));
                    binding.email.setText(jsonObject.getString("email"));
                    binding.mobilenumber.setText(jsonObject.getString("mobile"));
                    binding.area.setText(jsonObject.getString("area"));
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


    public void progreessDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressbaritems);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}