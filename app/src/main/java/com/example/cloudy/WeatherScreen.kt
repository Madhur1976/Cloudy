package com.example.cloudy

import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.cloudy.api.NetworkResponse
import com.example.cloudy.api.WeatherModel
import java.nio.file.WatchEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    var city by remember { mutableStateOf("") }
    val weatherResult = viewModel.weatherResult.observeAsState()
    var currentProgress by remember { mutableFloatStateOf(0f) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cloudy", fontSize = 40.sp, fontWeight = FontWeight.Bold) })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerpadding ->
        Box(modifier = Modifier.fillMaxWidth()) {
         /*   Image(
            painter = painterResource(R.drawable.weather_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )*/

            Column(
                modifier = Modifier
                    .fillMaxSize()

                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                OutlinedTextField(
                   /* colors =    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,        // Border when focused
                        unfocusedBorderColor = Color.White,      // Border when not focused
                        cursorColor = Color.Blue,                // Blinking cursor
//                        focusedLabelColor = Color.White,         // Label when focused
//                        unfocusedLabelColor = Color.White, // Label when not focused
                        unfocusedPlaceholderColor = Color.White,
                        focusedPlaceholderColor = Color.White,
                        focusedTextColor = Color.White,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White,
                    ),*/
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    placeholder = { Text("Search for any location") },
                    modifier = Modifier
                        .padding(innerpadding)
                        .fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                viewModel.getData(city)
                            },

                            ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search for any location"
                            )

                        }
                    },
                    singleLine = true
                )

                when (val result = weatherResult.value) {
                    is NetworkResponse.Failure -> {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            result.message?.let { Text(it) }
                            Button(onClick = { viewModel.getData(city) }) {
                                Text("Retry")

                            }
                        }

                    }

                    NetworkResponse.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            LinearProgressIndicator()
                        }
                    }

                    is NetworkResponse.Success -> {
                        WeatherDisplay(result.data)
                    }

                    null -> {

                    }
                }

            }
        }
    }
}

@Composable
fun WeatherDisplay(
    weatherDetails: WeatherModel
){
    fun timeFormatter(rawTime: String): String{
        // rawTime looks like "2025-09-04 12:29"
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a")

        val dateTime = LocalDateTime.parse(rawTime, inputFormatter)
        return dateTime.format(outputFormatter)
    }

Column (
    Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
){
    Row (
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ){
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            Modifier.size(40.dp),

        )

        Text(weatherDetails.location.name, fontSize = 30.sp, fontWeight = FontWeight.Bold,)
        Spacer(Modifier.width(8.dp))


        Text(weatherDetails.location.region, fontSize = 18.sp, fontWeight = FontWeight.Light,)
        Spacer(Modifier.width(6.dp))
        Text(weatherDetails.location.country, fontSize = 18.sp, fontWeight = FontWeight.Light,)

    }
    Spacer(Modifier.height(16.dp))

    Text("${weatherDetails.current.temp_c}°C", fontSize = 48.sp, fontWeight = FontWeight.Bold,)
    Spacer(Modifier.height(8.dp))

    Image(
        painter = rememberAsyncImagePainter("https:${weatherDetails.current.condition.icon}"),
        contentDescription = "Weather condition icon",
        modifier = Modifier.size(100.dp) // instead of fillMaxSize
    )


        Card (
            colors = CardColors(containerColor = Color.White, contentColor = Color.Black, disabledContentColor = Color.Black, disabledContainerColor = Color.White)
        ){
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherConditions("Wind","${ weatherDetails.current.wind_kph } Kph")
                    WeatherConditions("Humidity","${ weatherDetails.current.humidity }%")
                }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                      WeatherConditions("Heat Index","${ weatherDetails.current.heatindex_c } °C")

                      WeatherConditions("Time",timeFormatter(weatherDetails.location.localtime))


                  }



            }
        }
    }

}


@Composable
fun WeatherConditions(
    key: String,
    value: String
){
    Column (
        Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(key, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(value)
    }
}
