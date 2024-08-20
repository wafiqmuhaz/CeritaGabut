package com.example.ceritagabut.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ceritagabut.MainActivity
import com.example.ceritagabut.R
import com.example.ceritagabut.databinding.ActivityAddItemAppBinding
import com.example.ceritagabut.factory.AppFactory
import com.example.ceritagabut.models.AppMainViewModel
import com.example.ceritagabut.models.AppSigninViewModel
import com.example.ceritagabut.utils.appReduceImage
import com.example.ceritagabut.utils.appUriFile
import com.example.ceritagabut.utils.getMapAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddItemAppActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivityAddItemAppBinding
    private val factory: AppFactory by lazy { AppFactory.getInstance(this) }
    private val signinModelApp: AppSigninViewModel by viewModels { factory }
    private val mainViewModel: AppMainViewModel by viewModels { factory }
    private lateinit var locationPerson: FusedLocationProviderClient

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private var lonLoc = 0.0
    private var latLoc = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityAddItemAppBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        appBar()
        permissionAllowed()
        galery()
        setLocation()
        buttonAdd()

        locationPerson = LocationServices.getFusedLocationProviderClient(this)
        currentLocation()
    }

    private fun buttonAdd() {
        appBinding.buttonAdd.setOnClickListener {
            checkFileAndDescription()
        }
    }

    private fun checkFileAndDescription() {
        if (isFileSelected() && isDescriptionNotEmpty()) {
            uploadItem()
        } else {
            showErrorMessage()
        }
    }

    private fun isFileSelected(): Boolean {
        return getFile != null
    }

    private fun isDescriptionNotEmpty(): Boolean {
        return appBinding.edAddDescription.text?.isNotEmpty() == true
    }

    private fun uploadItem() {
        val file = getFile as File
        signinModelApp.getPerson().observe(this) { user ->
            mainViewModel.postNewItems(
                user.usertoken,
                appReduceImage(file),
                appBinding.edAddDescription.text.toString(),
                lonLoc.toString(),
                latLoc.toString()
            ).observe(this) { result ->
                showLoading(result.message.isEmpty())
            }
        }
    }

    private fun showErrorMessage() {
        val errorMessage = if (!isFileSelected()) {
            createErrorMessage(R.string.item_image_mandatory)
        } else {
            createErrorMessage(R.string.item_description_mandatory)
        }
        showToast(errorMessage)
    }

    private fun createErrorMessage(messageResId: Int): String {
        val messagePrefix = "Peringatan: "
        val errorMessage = getString(messageResId)
        return "$messagePrefix$errorMessage"
    }

    private fun setLocation() {
        val clickListener = View.OnClickListener {
            lonLoc = mainViewModel.Long.value ?: 0.0
            latLoc = mainViewModel.Lat.value ?: 0.0
            val address = getMapAddress(this@AddItemAppActivity, latLoc, lonLoc)
            if (address != null) {
                displayLocation(address)
            }
        }

        appBinding.buttonSetLocation.setOnClickListener(clickListener)
    }

    private fun displayLocation(location: String) {
        appBinding.tvLocation.text = location
        appBinding.tvLocation.text = getMapAddress(this@AddItemAppActivity, latLoc, lonLoc)
    }

    private fun galery() {
        appBinding.btnAddGaleryApp.setOnClickListener { openGallery() }
    }

    private fun permissionAllowed() {
        val permissionsGranted = allPermissionsGranted()

        if (!permissionsGranted) {
            requestPermissions()
        } else {
            handlePermissionsGranted()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun handlePermissionsGranted() {
        Log.e("Permissions", "PermissionsGranted")
    }

    private fun appBar() {
        supportActionBar?.title = getString(R.string.item_add_items)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showLoading(isLoading: Boolean) {
        appBinding.progressBarAddApp.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (!isLoading) {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
        }
    }

    private var getFile: File? = null
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data ?: Uri.EMPTY
            getFile = appUriFile(selectedImg, this@AddItemAppActivity)
            appBinding.tvAddImgApp.setImageURI(selectedImg)
        }
    }

    private fun openGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.item_choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (!(permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false) ||
                !(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false)
            ) {
                showToast("Perizinan")
                finish()
            } else {
                currentLocation()
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun currentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            locationPerson.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    coordinateItems(location.latitude, location.longitude)
                } else {
                    coordinateItems(0.0, 0.0)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun coordinateItems(latitude: Double, longitude: Double) {
        mainViewModel.Lat.postValue(latitude)
        mainViewModel.Long.postValue(longitude)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showToast("Perizinan")
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AddItemAppActivity, message, Toast.LENGTH_SHORT).show()
    }
}