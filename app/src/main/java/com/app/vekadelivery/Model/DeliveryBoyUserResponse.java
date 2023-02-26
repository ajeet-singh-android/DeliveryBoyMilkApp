package com.app.vekadelivery.Model;

import java.util.ArrayList;

public class DeliveryBoyUserResponse {

    public ArrayList<Demand> demand;
    public double today_demand;
    public double previous_demand;


    public ArrayList<Demand> getDemand() {
        return demand;
    }

    public void setDemand(ArrayList<Demand> demand) {
        this.demand = demand;
    }

    public double getToday_demand() {
        return today_demand;
    }

    public void setToday_demand(double today_demand) {
        this.today_demand = today_demand;
    }

    public double getPrevious_demand() {
        return previous_demand;
    }

    public void setPrevious_demand(double previous_demand) {
        this.previous_demand = previous_demand;
    }
}