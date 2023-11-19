package com.dicoding.musicstory.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.dicoding.musicstory.R
import com.dicoding.musicstory.constants.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.dicoding.musicstory.customview.CustNotif
import com.dicoding.musicstory.data.Result
import com.dicoding.musicstory.databinding.FragmentMapsBinding
import com.dicoding.musicstory.response.Story
import com.dicoding.musicstory.utils.FactoryVM
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: FactoryVM
    private val mapsViewModel: MapVM by viewModels { factory }
    private val boundsBuilder = LatLngBounds.Builder()
    private var googleMap: GoogleMap? = null
    private var temporaryLocation: LatLng? = null
    private val callback = OnMapReadyCallback { map ->
        googleMap = map

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isIndoorLevelPickerEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true

        setupViewModel()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
        } else {
            fetchUserLocation()
        }

        setMapStyle()
        getStoryWithLocation()
    }

    private fun fetchUserLocation() {
        googleMap?.let { map ->
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val userLocation = LatLng(location.latitude, location.longitude)
                            temporaryLocation = userLocation
                            map.addMarker(
                                MarkerOptions()
                                    .position(userLocation)
                                    .title("Your Location")
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        }
                    }
            } else {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation()
            } else {
                Toast.makeText(requireContext(), "Izin lokasi diperlukan untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    private fun setupViewModel() {
        factory = FactoryVM.getInstance(binding.root.context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getStoryWithLocation() {
        mapsViewModel.getStories().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingHandler(true)
                }
                is Result.Error -> {
                    loadingHandler(false)
                    errorHandler()
                }
                is Result.Success -> {
                    loadingHandler(false)
                        result.data.let { showMarker(it.listStory) }
                }
            }
        }
    }


    private fun showMarker(listStory: List<Story>?) {
        val padding = 50
        listStory?.forEach { story ->
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.createdAt)
                        .snippet(StringBuilder("created: ")
                            .append(story.createdAt.subSequence(11, 16).toString())
                            .toString()

                        )
                )
                boundsBuilder.include(latLng)
            }
        }

        // Setelah menambahkan semua marker, perbarui batas-batas peta untuk menampilkan semua marker.
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding))
    }



    override fun onDestroy() {
        super.onDestroy()
        temporaryLocation = null
    }

    private fun errorHandler() {
        CustNotif(binding.root.context, R.string.error_message, R.drawable.error).show()
    }

    private fun setMapStyle() {
        googleMap?.let { map ->
            try {
                val success =
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(binding.root.context, R.raw.map_style))
                if (!success) {
                    CustNotif(binding.root.context, R.string.error_message, R.drawable.error).show()
                }
            } catch (exception: Resources.NotFoundException) {
                CustNotif(binding.root.context, R.string.error_message, R.drawable.error).show()
            }
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarMap.visibility = View.VISIBLE
            binding.root.visibility = View.INVISIBLE
        } else {
            binding.progressBarMap.visibility = View.INVISIBLE
            binding.root.visibility = View.VISIBLE
        }
    }
}