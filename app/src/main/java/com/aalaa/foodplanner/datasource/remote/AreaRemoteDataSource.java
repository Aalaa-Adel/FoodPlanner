package com.aalaa.foodplanner.datasource.remote;

import com.aalaa.foodplanner.domain.auth.model.model.Area;
import com.aalaa.foodplanner.domain.auth.model.model.AreaResponse;
import com.aalaa.foodplanner.data.network.Network;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AreaRemoteDataSource {


    private MealServices mealServices;
    public AreaRemoteDataSource() {
        this.mealServices = new Network().getMealServices();
    }
    public void getArea(AreaNetworkResponse callback) {
        mealServices.getAreasList().enqueue(
                new Callback<AreaResponse>() {
                    @Override
                    public void onResponse(Call<AreaResponse> call, Response<AreaResponse> response) {
                        if(response.code()==200)
                        {
                            AreaResponse areaResponse = response.body();
                            List<Area> areas = areaResponse.getAreas();
                            callback.onAreasSuccess(areas);
                        }
                        else {
                            callback.onFailure("Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<AreaResponse> call, Throwable t) {
                        if(t instanceof IOException){
                            callback.noInternet();
                        }
                        else {
                            callback.onFailure("Something went wrong");
                        }
                    }

                });
    }

}
