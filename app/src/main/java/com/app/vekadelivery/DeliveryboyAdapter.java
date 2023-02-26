package com.app.vekadelivery;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.vekadelivery.Model.Demand;
import com.app.vekadelivery.databinding.DeliveryboyItemsBinding;

import java.util.ArrayList;

public class DeliveryboyAdapter extends RecyclerView.Adapter<DeliveryboyAdapter.DeliveryBoyDataHolder> {

    ArrayList<Demand> demand;
    ClickListener listener;

    public DeliveryboyAdapter(ArrayList<Demand> demand, ClickListener listener) {
        this.demand = demand;
        this.listener = listener;
    }


    public void updateUser(ArrayList<Demand> mdemand){
        demand.addAll(mdemand);
        notifyDataSetChanged();
    }

    public void clearRecy(ArrayList<Demand> mdemand){
        demand.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryBoyDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeliveryBoyDataHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.deliveryboy_items,parent,false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DeliveryBoyDataHolder holder, int position) {
        Demand model = demand.get(position);
        holder.binding.name.setText(model.getName());
        holder.binding.quantity.setText(model.getLtr());
        holder.binding.status.setText(model.getStatus());
        holder.binding.shift.setText(model.getShift());
        if(!model.getStatus().equalsIgnoreCase("Delivered")){
            holder.binding.status.setText("UnDelivered");
        }else {
            holder.binding.status.setEnabled(false);
        }

        holder.binding.status.setOnClickListener(view -> {
            model.setStatus("Delivered");
            model.setLtr(holder.binding.quantity.getText().toString());
            notifyDataSetChanged();
            listener.clicked(model.getId(),holder.binding.quantity.getText().toString(),model.getCus_id(),model.getName());
        });


        holder.binding.upbutton1.setOnClickListener(vas ->{
            Double num1 = Double.parseDouble(holder.binding.quantity.getText().toString());
            Double addno = num1+0.5;
            holder.binding.quantity.setText(addno+"");

        });

        holder.binding.downbutton1.setOnClickListener(vsp -> {
            Double num1 = Double.parseDouble(holder.binding.quantity.getText().toString());
            if (num1 > 0) {
                Double sub = num1 - 0.5;
                holder.binding.quantity.setText(sub + "");
            }
        });

    }

    @Override
    public int getItemCount() {
        return demand.size();
    }

    public class DeliveryBoyDataHolder extends RecyclerView.ViewHolder {
        DeliveryboyItemsBinding binding;
        public DeliveryBoyDataHolder(@NonNull View itemView) {
            super(itemView);

             binding = DeliveryboyItemsBinding.bind(itemView.getRootView());
        }
    }

   public interface ClickListener{
        public void clicked(String userid,String quantity,String cusid,String name);
    }
}
