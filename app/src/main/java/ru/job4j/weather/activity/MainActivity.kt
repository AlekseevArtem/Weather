package ru.job4j.weather.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.job4j.weather.R
import ru.job4j.weather.fragments.CallbackToActivity
import ru.job4j.weather.fragments.FragmentDaysRV
import ru.job4j.weather.fragments.FragmentHoursRV
import ru.job4j.weather.fragments.FragmentMainInfo
import ru.job4j.weather.presenter.MainActivityPresenter
import ru.job4j.weather.store.*
import ru.job4j.weather.view.MainActivityView

class MainActivity : MvpAppCompatActivity(), MainActivityView, CallbackToActivity {
    private var coordinates: LatLng? = null
    private lateinit var details: Answer.Details
    private lateinit var days: List<Day>
    private lateinit var currentLocation: Answer.City

    @InjectPresenter
    lateinit var activityPresenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlacesAPI()
        if (isLocationPermissionGranted()) initLocListener()
        initSwipeToRefresh()
        activityPresenter.getAnswerFromDB()
    }

    private fun initSwipeToRefresh() {
        swipeRefreshLayout.apply {
            setOnRefreshListener { callApi(coordinates) }
            setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            )
        }
    }

    private fun callApi(coordinates: LatLng?, type: Int = Answer.GEO) {
        if (coordinates != null) {
            activityPresenter.callApi(coordinates, type, getString(R.string.geo_api_key))
        } else {
            swipeRefreshLayout.isRefreshing = false
            if (isLocationPermissionGranted()) initLocListener()
            Toast.makeText(this, getString(R.string.error_check_geolocation), Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initLocListener()
            }
        }
    }


    private fun initPlacesAPI() {
        Places.initialize(this, getString(R.string.google_maps_key))
        (autocomplete_fragment as AutocompleteSupportFragment).apply {
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) = callApi(place.latLng, Answer.PLACES)
                override fun onError(status: Status) {}
            })
        }
    }


    @SuppressLint("MissingPermission")
    private fun initLocListener() {
        val loc: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                coordinates = LatLng(location.latitude, location.longitude)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, loc)
    }

    private fun isLocationPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), 1)
                return false
            }
        }
        return true
    }

    private fun fillingTheUI(day: Int, hour: Int) {
        (days_fragment as FragmentDaysRV).updateUI(days, day)
        (hours_fragment as FragmentHoursRV).updateUI(days[day].hours, hour)
        (main_info_fragment as FragmentMainInfo).updateUI(details)
        title = "${currentLocation.name}, ${currentLocation.country} "
    }

    override fun successAnswer(days: List<Day>, details: Answer.Details, city: Answer.City, day: Int, hour: Int) {
        swipeRefreshLayout.isRefreshing = false
        this.days = days
        this.details = details
        this.currentLocation = city
        fillingTheUI(day, hour)
    }

    override fun successWithError(code: Int) {
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(this, code, Toast.LENGTH_LONG).show()
    }

    override fun failedAnswer(response: String) {
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(this, response, Toast.LENGTH_LONG).show()
    }

    override fun updatePositions(day: Int, hour: Int, details: Answer.Details) {
        this.details = details
        fillingTheUI(day, hour)
    }

    override fun updatePositionsFromFragment(day: Int, hour: Int) =
            activityPresenter.changeCurrentDayAndHour(day, hour)
}

