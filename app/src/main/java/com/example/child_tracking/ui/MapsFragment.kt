package com.example.child_tracking.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.child_tracking.R
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Marker

class MapsFragment : Fragment() {

    private val viewModel: ChildTrackingViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap
    private var childMarker: Marker? = null

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()
        viewModel.childLiveData.observe(viewLifecycleOwner) { child ->
            updateChildLocation(child.latitude, child.longtitude)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.childLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun updateChildLocation(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            val childLocation = LatLng(latitude, longitude)
            if (childMarker == null) {
                childMarker = googleMap.addMarker(MarkerOptions().position(childLocation).title("Child"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(childLocation))
            } else {
                childMarker?.position = childLocation
            }
        } else {
            childMarker?.remove()
            childMarker = null
        }
    }
}
