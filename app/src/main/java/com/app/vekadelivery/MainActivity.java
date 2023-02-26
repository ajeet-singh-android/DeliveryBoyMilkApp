package com.app.vekadelivery;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.app.vekadelivery.Fragments.HomeFragment;
import com.app.vekadelivery.Fragments.NotificationFragment;
import com.app.vekadelivery.Fragments.ProfileFragment;
import com.app.vekadelivery.Network.PrefrenceManager;
import com.app.vekadelivery.Network.RetrofitInstance;
import com.app.vekadelivery.databinding.ActivityMainBinding;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;



    public static TextView notificationtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        notificationtext = findViewById(R.id.notificationcount);


        binding.username.setText(new PrefrenceManager(getApplicationContext()).getUserName() +"!");

        ExchengeFregment(new HomeFragment());





        getBoyData();

        Animation myFadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        Animation myFadeOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadeout);
        binding.belllayout.startAnimation(myFadeInAnimation);
        binding.belllayout.startAnimation(myFadeOutAnimation);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment1 = null;
            switch (item.getItemId()) {
                case R.id.home:
                    binding.logout.setVisibility(View.GONE);
                    fragment1 = new HomeFragment();
                    break;

                case R.id.changepasword:
                    binding.logout.setVisibility(View.GONE);
                    fragment1 = new ChangePasswordFragment();
                    break;

                case R.id.user:
                    binding.logout.setVisibility(View.VISIBLE);
                    fragment1 = new ProfileFragment();
                    break;


            }
            ExchengeFregment(fragment1);
            return true;
        });


        binding.notification.setOnClickListener(v ->{
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new NotificationFragment()).addToBackStack(null).commit();
        });

        binding.logout.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("LogOut")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new PrefrenceManager(getApplicationContext()).logout();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finishAffinity();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        });


    }


    private void ExchengeFregment(Fragment selectedFragment) {
        if(selectedFragment!=null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
        }
    }




    public void getBoyData(){
        RetrofitInstance.getClient().getBoyData(new PrefrenceManager(getApplicationContext()).getuserid()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response.body().toString());
                    JSONObject jsonObject = jsonObject1.getJSONObject("data");
                    String status =  jsonObject.getString("status");
                    if(!status.equals("Active")){
                        new PrefrenceManager(getApplicationContext()).logout();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finishAffinity();
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







}