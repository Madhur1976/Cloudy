package com.example.cloudy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.api.Constant
import com.example.cloudy.api.NetworkResponse
import com.example.cloudy.api.RetrofitInstance
import com.example.cloudy.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val _weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult
    fun getData(city: String){

        _weatherResult.value = NetworkResponse.Loading
        Log.i("City name",city)
        viewModelScope.launch {
            try{
                val response =  _weatherApi.getWeather(Constant.apiKey,city)
                if(response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                }
                else{
                    _weatherResult.value = NetworkResponse.Failure("Failed to load data")

                }

            }
            catch (e: Exception){
                _weatherResult.value = NetworkResponse.Failure("Failed to load data")

            }


        }



    }
}