package com.app.vekadelivery.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.vekadelivery.DeliveryboyAdapter;
import com.app.vekadelivery.MainActivity;
import com.app.vekadelivery.Model.DeliveryBoyUserResponse;
import com.app.vekadelivery.Model.Demand;
import com.app.vekadelivery.Model.NotificationModel;
import com.app.vekadelivery.Network.PrefrenceManager;
import com.app.vekadelivery.Network.RetrofitInstance;
import com.app.vekadelivery.R;
import com.app.vekadelivery.databinding.FilterDialogBinding;
import com.app.vekadelivery.databinding.FragmentHomeBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    List<NotificationModel> notificationMdelList = new ArrayList<>();

    Double today = 0.0;
    Double previous = 0.0;

    ///Notifiaction
    SharedPreferences sharedPreferences_frist;
    final String Notification_New = "new_notification";

    DeliveryBoyUserResponse model = null;
    FragmentHomeBinding binding;
    ProgressDialog progressDialog = null;

    ArrayList<Demand> userlist = new ArrayList<>();

    ArrayList<Demand> eveninglist = new ArrayList<>();


    int page = 0;

    DeliveryboyAdapter deliveryboyAdapter;

    public HomeFragment() {

    }


    List<String> spinerlist = new ArrayList<>();
    List<String> shiftlist = new ArrayList<>();

    String currentDateandTime = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        new PrefrenceManager(requireContext()).setShift("");
        new PrefrenceManager(requireContext()).setDeliver("");

        getNofication();

        setAdapter(eveninglist);

        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());


        spinerlist.clear();
        shiftlist.clear();

        spinerlist.add("All");
        spinerlist.add("Delivered");
        spinerlist.add("UnDelivered");


        shiftlist.add("All Shift");
        shiftlist.add("Morning");
        shiftlist.add("Evening");


        deliveryboyAdapter.clearRecy(eveninglist);
        getDeliveryboyUser(page);


        binding.line1.setOnClickListener(v -> {
            filterDialog();
        });


        binding.nestedrcyclview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page = page + 10;
                    if (binding.mprogress.getVisibility() == View.GONE)
                        getDeliveryboyUser(page);
                    binding.mprogress.setVisibility(View.VISIBLE);

                    Log.e("aas@3232", "onScrollChange: workinmg");

                }
            }
        });


    }

    private void setAdapter(ArrayList<Demand> list) {
        deliveryboyAdapter = new DeliveryboyAdapter(list, (userid, quantity, cusid, name) -> {
            progreessDialog();
            RetrofitInstance.getClient().updateStatus(userid, "Delivered", quantity).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call1, Response<JsonObject> response1) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response1.body().toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase("success")) {
                            progressDialog.dismiss();
                            addNotification(cusid, new PrefrenceManager(getContext()).getUserName() + " has Delivered " + quantity + "ltr  Milk to " + name + " Date & Time " + currentDateandTime);
//                            getDeliveryboyUser();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call1, Throwable t) {
                    progreessDialog();
                    progreessDialog();
                }
            });
        });
        binding.deliveryrecyclerview.setAdapter(deliveryboyAdapter);
    }


    public void getDeliveryboyUser(int page) {
        if (progressDialog == null)
            progreessDialog();

        RetrofitInstance.getClient().getDeliveryUserData(new PrefrenceManager(getContext()).getuserid(), String.valueOf(page), new PrefrenceManager(getContext()).getDeliver(), new PrefrenceManager(getContext()).getShift()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                userlist.clear();
                DeliveryBoyUserResponse mmodel = null;
                progressDialog.dismiss();
                binding.mprogress.setVisibility(View.GONE);
                binding.notfound.setVisibility(View.GONE);
                try {
                    Log.e("aa@323", "onResponse: " + response.body().toString());
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
//userlist.clear();
                        JSONObject jsonArray = jsonObject.getJSONObject("data");

                        model = new Gson().fromJson(jsonArray.toString(), DeliveryBoyUserResponse.class);
                        userlist.addAll(model.getDemand());


                        if (mmodel != null)
                            mmodel.getDemand().clear();
                        mmodel = new Gson().fromJson(jsonArray.toString(), DeliveryBoyUserResponse.class);


                        today = today + model.getToday_demand();
                        previous = previous + model.getPrevious_demand();

                        binding.todaydelivery.setText("Total Deliveries Today - " + today + " ltr");
                        binding.previousdelivery.setText("Previous Days Deliveries  - " + previous + " ltr");

                        deliveryboyAdapter.updateUser(mmodel.getDemand());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    /*if(mmodel.getDemand()!=null) {
                        if (mmodel.getDemand().size() == 0)
                            binding.notfound.setVisibility(View.VISIBLE);
                    }*/
                    Log.e("aad@323", "onResponse: " + e.toString());
                    progressDialog.dismiss();
                    progressDialog.dismiss();
                    binding.mprogress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                binding.notfound.setVisibility(View.VISIBLE);
                binding.mprogress.setVisibility(View.GONE);
            }
        });
    }

    public void progreessDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressbaritems);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void addNotification(String custid, String message) {
        Log.e("TAG", "custid: " + custid);
        RetrofitInstance.getClient().addNotificatin(custid, message, "Success").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.e("TAG", "onResponse: " + response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences_Second;
        final String Notification_old = "old_notification";
        sharedPreferences_Second = getActivity().getSharedPreferences(Notification_old, Context.MODE_PRIVATE);
        int second = sharedPreferences_Second.getInt("old", 00);


        sharedPreferences_frist = getActivity().getSharedPreferences(Notification_New, Context.MODE_PRIVATE);
        int frist = sharedPreferences_frist.getInt("new_data", 00);
        int original_notification;
        if (frist > 0) {
            original_notification = frist - second;
        } else {
            original_notification = frist;
        }
        MainActivity.notificationtext.setText("" + original_notification);

    }


    public void notificationSharePreferenceNew(int new_noti) {
        sharedPreferences_frist = getActivity().getSharedPreferences(Notification_New, Context.MODE_PRIVATE);
        SharedPreferences.Editor new_editor = sharedPreferences_frist.edit();
        new_editor.putInt("new_data", new_noti);
        new_editor.apply();
        new_editor.commit();
        getNewNotification();
    }


    public void getNewNotification() {
        sharedPreferences_frist = getActivity().getSharedPreferences(Notification_New, Context.MODE_PRIVATE);
        int frist = sharedPreferences_frist.getInt("new_data", 00);

        SharedPreferences sharedPreferences_Second;
        final String Notification_old = "old_notification";
        sharedPreferences_Second = getActivity().getSharedPreferences(Notification_old, Context.MODE_PRIVATE);
        int second = sharedPreferences_Second.getInt("old", 00);
        int all_notification = frist - second;

        if (all_notification == -1 || all_notification == 0) {
            MainActivity.notificationtext.setText("0");
        } else {
            MainActivity.notificationtext.setText(all_notification + "");
        }

        if (all_notification > 0) {
            notificationDialog(all_notification, notificationMdelList);
        }


    }


    private void getNofication() {
        RetrofitInstance.getClient().getNotfication(new PrefrenceManager(getContext()).getuserid()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        notificationSharePreferenceNew(jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            NotificationModel notificationMdel = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), NotificationModel.class);
                            if (notificationMdel.getMsg().contains("shift") || notificationMdel.getMsg().contains("demand") || notificationMdel.getMsg().contains("extra") || notificationMdel.getMsg().contains("Permanent") || notificationMdel.getMsg().contains("temperory")) {

                                Log.e("asss@3343444", "onResponse: working" );
                                notificationMdelList.add(notificationMdel);
                            }
                        }

                        notificationDialog(notificationMdelList.size(), notificationMdelList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    public void filterDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.filter_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.AnimationforDialog);

        ImageView crossimage = dialog.findViewById(R.id.crossimage);
        TextView clearfilter = dialog.findViewById(R.id.clearfilter);
        RadioButton deliver = dialog.findViewById(R.id.deliver);
        RadioButton undeliver = dialog.findViewById(R.id.undeliver);

        RadioButton evening = dialog.findViewById(R.id.evening);
        RadioButton morning = dialog.findViewById(R.id.morning);

        MaterialCardView applybtn = dialog.findViewById(R.id.applybtn);
        RadioGroup delivergroup = dialog.findViewById(R.id.deliverygroup);
        RadioGroup shiftgroup = dialog.findViewById(R.id.shiftgroup);
        dialog.show();

        String deliverstring = new PrefrenceManager(getContext()).getDeliver();
        if (deliverstring.equals("Delivered"))
            deliver.setChecked(true);
        else if (deliverstring.equals("pending"))
            undeliver.setChecked(true);

        String shiftstring = new PrefrenceManager(getContext()).getShift();
        if (shiftstring.equals("Morning"))
            morning.setChecked(true);
        else if (shiftstring.equals("Evening"))
            evening.setChecked(true);


        if (deliverstring.length() > 3 || shiftstring.length() > 3)
            clearfilter.setVisibility(View.VISIBLE);

        crossimage.setOnClickListener(v -> {
            dialog.dismiss();
        });

        clearfilter.setOnClickListener(v -> {
            new PrefrenceManager(requireContext()).setShift("");
            new PrefrenceManager(requireContext()).setDeliver("");
            page = 0;
            if (progressDialog == null)
                progreessDialog();
            else
                progressDialog.show();

            deliveryboyAdapter.clearRecy(eveninglist);
            getDeliveryboyUser(page);
            dialog.dismiss();
        });

        delivergroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.deliver)
                    new PrefrenceManager(requireContext()).setDeliver("Delivered");
                else if (i == R.id.undeliver)
                    new PrefrenceManager(requireContext()).setDeliver("pending");
            }
        });

        shiftgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.morning)
                    new PrefrenceManager(requireContext()).setShift("Morning");
                else if (i == R.id.evening)
                    new PrefrenceManager(requireContext()).setShift("Evening");
            }
        });
        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 0;
                if (progressDialog == null)
                    progreessDialog();
                else
                    progressDialog.show();

                deliveryboyAdapter.clearRecy(eveninglist);
                getDeliveryboyUser(page);
                dialog.dismiss();
            }
        });
    }


    public void notificationDialog(int size, List<NotificationModel> list) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.notification_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.AnimationforDialog);

        RecyclerView recyclerView = dialog.findViewById(R.id.notificationrecyclerview);
        TextView okbtn = dialog.findViewById(R.id.ok);

        DialogNotificationAdapter notificationAdapter = new DialogNotificationAdapter(size, list);
        recyclerView.setAdapter(notificationAdapter);


        okbtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}