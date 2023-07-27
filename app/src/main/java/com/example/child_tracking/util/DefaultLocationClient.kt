package com.example.child_tracking.util
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.coroutineContext

class DefaultLocationClient(

    private val client: FusedLocationProviderClient,
    private val context: Context,
): LocationClient {


    @SuppressLint("MissingPermission")
    override fun getLocation(inter: Long): Flow<Location> {


        return callbackFlow {
         if(!context.hasLocationPermission()) {
             throw LocationClient.LocationException("Unable to access location")
         }

         val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
         val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
         val isNetworkAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

         if (!isGpsEnabled && !isNetworkAvailable) {
             throw LocationClient.LocationException("GPS in not available")
         }

         val request = LocationRequest.create().apply {
             interval = 500
             fastestInterval = 500
             priority = LocationRequest.PRIORITY_HIGH_ACCURACY

         }
         val locationCallback = object : LocationCallback() {
         override fun onLocationResult(result: LocationResult) {
             super.onLocationResult(result)
             result.locations.lastOrNull()?.let { location ->
                 launch { send (location)}
             }
         }
     }

         client.requestLocationUpdates(
             request,
             locationCallback,
             Looper.getMainLooper()
         )

            awaitClose { client.removeLocationUpdates(locationCallback) }


     }
    }

    }


