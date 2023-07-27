package com.example.child_tracking.util

import android.location.Location
import kotlinx.coroutines.flow.Flow


interface LocationClient {

     fun getLocation(inter: Long): Flow<Location>

    class LocationException (message: String) : Exception()
}