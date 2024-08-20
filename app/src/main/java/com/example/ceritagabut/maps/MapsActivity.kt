package com.example.ceritagabut.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.ceritagabut.R
import com.example.ceritagabut.databinding.ActivityMapsBinding
import com.example.ceritagabut.factory.AppFactory
import com.example.ceritagabut.models.AppMainViewModel
import com.example.ceritagabut.models.AppSigninViewModel
import com.example.ceritagabut.responses.ListResultItems
import com.example.ceritagabut.utils.getMapAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapsBinding: ActivityMapsBinding
    private val appFactory: AppFactory by lazy { AppFactory.getInstance(this) }
    private val signinAppViewModel: AppSigninViewModel by viewModels { appFactory }
    private val mainAppViewModel: AppMainViewModel by viewModels { appFactory }
    private lateinit var mapsLocation: FusedLocationProviderClient
    private val RETRY_LIMIT = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapsBinding.root)
        setupActionBar()
        setupMapFragment()
        setupLocationService()
    }

    private fun setupLocationService() {
        mapsLocation = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupMapFragment() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapsapp) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupActionBar() {
        supportActionBar?.run {
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapUI()
        styleMaps()
        lastLocationPerson()
        observeUserData()
    }

    private fun setupMapUI() {
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun observeUserData() {
        signinAppViewModel.getPerson().observe(this) { user ->
            if (user.userItemId.isNotEmpty()) {
                observeListItems(user.usertoken)
            }
        }
    }

    private fun observeListItems(token: String) {
        mainAppViewModel.getListMapsItems(token).observe(this) { items ->
            handleMapItems(items.listItems)
        }
    }

    private fun handleMapItems(items: List<ListResultItems>) {
        marker(items)
    }

    private fun marker(items: List<ListResultItems>) {
        val markerOptionsList = mutableListOf<MarkerOptions>()

        items.forEach { itemMap ->
            val latLng = LatLng(itemMap.latitude, itemMap.longitude)
            val addressName = getMapAddress(this@MapsActivity, itemMap.latitude, itemMap.longitude)
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(itemMap.username)
                .snippet(addressName)

            markerOptionsList.add(markerOptions)
        }

        addMarkersToMap(markerOptionsList)
        lastLocationPerson()
    }

    private fun addMarkersToMap(markerOptionsList: List<MarkerOptions>) {
        markerOptionsList.forEachIndexed { index, markerOptions ->
            Handler(Looper.getMainLooper()).postDelayed({
                mMap.addMarker(markerOptions)
            }, (index * 200).toLong())
        }
    }


    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    lastLocationPerson()
                }

                else -> {
                }
            }
        }

    private fun checkPermission(permission: String): PermissionResult {
        return when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                PermissionResult.Granted
            }

            shouldShowRequestPermissionRationale(permission) -> {
                PermissionResult.PermissionDeniedWithExplanation
            }

            else -> {
                PermissionResult.PermissionDenied
            }
        }
    }

    sealed class PermissionResult {
        object Granted : PermissionResult()
        object PermissionDenied : PermissionResult()
        object PermissionDeniedWithExplanation : PermissionResult()
    }

private fun lastLocationPerson() {
    when (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
        is PermissionResult.Granted -> {
            when (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                is PermissionResult.Granted -> {
                    mapsLocation.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            trackMarker(location)
                            Handler().postDelayed({
                            }, 200 + 1000)
                        } else {
                            Toast.makeText(this@MapsActivity, "Lokasi tidak ada", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {
                    permissions.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
                }
            }
        }
        else -> {
            permissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }
}


    private fun trackMarker(location: Location) {
        val markerOptions = buildMarkerOptions(location)
        addMarkerToMap(markerOptions)
        moveCameraToMarker(location)
    }

    private fun buildMarkerOptions(location: Location): MarkerOptions {
        val startLocation = LatLng(location.latitude, location.longitude)
        val addressName = getMapAddress(this@MapsActivity, location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
            .position(startLocation)
            .title("HERE")
            .snippet(addressName)
            .icon(createBitmapDescriptor(R.drawable.baseline_push_pin_24))
        return markerOptions
    }

    private fun createBitmapDescriptor(@DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Sumber tidak ditemukan")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun moveCameraToMarker(location: Location) {
        Handler().postDelayed({
            val startLocation = LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 8f))
        }, 1500)
    }


    private fun addMarkerToMap(markerOptions: MarkerOptions) {
        Handler().postDelayed({
            mMap.addMarker(markerOptions)
        }, 500)
    }

private fun styleMaps() {
    val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.maps)
    val styleResult = setMapStyleWithRetry(mapStyleOptions, RETRY_LIMIT)

    when (styleResult) {
        is StyleResult.Success -> {
            Log.d("MapsActivity", "Peta berhasil diterapkan.")
        }
        is StyleResult.Failure -> {
            Log.e("MapsActivity", "Gagal menerapkan peta.")
            styleResult.throwable?.let {
                Log.e("MapsActivity", "Error: ", it)
            }
        }
    }
}

    private fun setMapStyleWithRetry(mapStyleOptions: MapStyleOptions, maxRetries: Int): StyleResult {
        var attempt = 0
        var success = false
        var throwable: Throwable? = null

        while (attempt < maxRetries && !success) {
            try {
                success = mMap.setMapStyle(mapStyleOptions)
            } catch (e: Resources.NotFoundException) {
                throwable = e
                Log.e("MapsActivity", "Gagal menerapkan gaya peta pada percobaan ke-$attempt.")
            }
            attempt++
        }

        return if (success) {
            StyleResult.Success
        } else {
            StyleResult.Failure(throwable)
        }
    }

    sealed class StyleResult {
        object Success : StyleResult()
        data class Failure(val throwable: Throwable?) : StyleResult()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
