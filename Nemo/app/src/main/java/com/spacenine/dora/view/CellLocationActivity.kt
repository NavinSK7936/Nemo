package com.spacenine.dora.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.spacenine.dora.model.response.CellLocation
import com.spacenine.dora.utils.getCurrentCellInfo
import com.spacenine.nemo.R
import com.spacenine.nemo.util.hide
import com.spacenine.nemo.util.show
import kotlinx.android.synthetic.main.activity_cell_location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*


val Menu.items: MutableList<MenuItem>
    get() =
        mutableListOf<MenuItem>().apply {
            forEach { add(it) }
        }

@ExperimentalCoroutinesApi
class CellLocationActivity : AppCompatActivity() {

    private lateinit var viewModel: CellLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cell_location)

        requestPermission()
        initViewModel()
        initLocationLiveData()
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        button_find.setOnClickListener(::onClickFindLocation)
        button_find_fused.setOnClickListener(fusedLocationListener)
        mapView.settings.javaScriptEnabled = true
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            CellLocationViewModelFactory()
        )[CellLocationViewModel::class.java]
    }

    private fun initLocationLiveData() {
        viewModel.locationLiveData.observe(
            this,
            Observer(::onStateChanged)
        )
    }

    private var isClicked = false

    private fun onFirstClick() {
        if (!isClicked) {

            isClicked = true

            locationTextView.hide()
            text_location.show()
            text_address.show()

        }
    }

    @SuppressLint("MissingPermission")
    val fusedLocationListener: View.OnClickListener = View.OnClickListener {

        onFirstClick()
        progressBar.show()

        LocationServices.getFusedLocationProviderClient(applicationContext)
            .getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                object : CancellationToken() {
                    override fun isCancellationRequested(): Boolean {
                        return false
                    }

                    override fun onCanceledRequested(onTokenCanceledListener: OnTokenCanceledListener): CancellationToken {
                        return this
                    }
                }).addOnSuccessListener { location ->

                progressBar.hide()
                mapView.show()

                text_location.text = getString(R.string.text_location_format, location.latitude, location.longitude)
                text_address.text = getAddressFromLocation(location.latitude, location.longitude)

                mapView.loadUrl("https://www.google.com/maps/place/${location.latitude},${location.longitude}")

            }.addOnFailureListener {
                progressBar.hide()
                Toast.makeText(applicationContext, "Unable to get Location bcz: $it", Toast.LENGTH_SHORT)
            }

    }

    private fun onClickFindLocation(view: View) {

        onFirstClick()

        val popupMenu = PopupMenu(this, view)

        val allCellInfo = getCurrentCellInfo(this)

        allCellInfo.forEachIndexed { index, cellInfo ->
            if (popupMenu.menu.isEmpty())
                popupMenu.menu.add(0, index, 0, "${cellInfo.radio}")
            else
                popupMenu.menu.forEach { menuItem ->
                    if (menuItem.title != cellInfo.radio)
                        popupMenu.menu.add(0, index, 0, "${cellInfo.radio}")
            }
        }

        popupMenu.setOnMenuItemClickListener {
            viewModel.fetchLocation(allCellInfo[it.itemId])
            true
        }

        popupMenu.show()
    }

    private fun onStateChanged(state: State) {
        when (state) {
            is State.Loading -> {
                progressBar.show()
                mapView.hide()
            }
            is State.Failed -> {
                progressBar.hide()
                mapView.hide()
                showToast("Error: ${state.message}")
            }
            is State.Success -> {
                progressBar.hide()
                mapView.show()
                showLocationInfo(state.response)
            }
        }
    }

    private fun showLocationInfo(cellLocation: CellLocation) {
        text_location.text = getString(R.string.text_location_format, cellLocation.latitude, cellLocation.longitude)
        text_address.text = cellLocation.address
        mapView.loadUrl("https://www.google.com/maps/place/${cellLocation.latitude},${cellLocation.longitude}")
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                2000
            )
        }
    }

    private fun showToast(message: String) =
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        return address //"City: $city\nState: $state\nCountry: $country\nPostalCode: $postalCode\nKnownName: $knownName"
    }
}
