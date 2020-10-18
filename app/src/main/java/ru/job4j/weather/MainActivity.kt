package ru.job4j.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.job4j.weather.fragments.CallbackToActivity
import ru.job4j.weather.fragments.FragmentDaysRV
import ru.job4j.weather.fragments.FragmentHoursRV
import ru.job4j.weather.fragments.FragmentMainInfo
import ru.job4j.weather.retrofit.RetrofitForJSON
import ru.job4j.weather.store.*
import java.util.*

class MainActivity : AppCompatActivity(), RetrofitForJSON.GetAnswerFromAPI, CallbackToActivity {
    private var mLatLng: LatLng? = null
    private val mMemStore: MemStore = MemStore.getMemStore()
    private val db = RoomDB.getDatabase(applicationContext)
    private var mSavedInRoom = false
    private var mDayPosition = 0
    private var mHourPosition = 0
    private val GEO = 0
    private val PLACES = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlacesAPI()
        if (isLocationPermissionGranted()) initLocListener()
        initSwipeToRefresh()
        mSavedInRoom = savedInstanceState?.getBoolean("savedInRoom") ?: false
        mDayPosition = savedInstanceState?.getInt("day") ?: 0
        mHourPosition = savedInstanceState?.getInt("hour") ?: 0
        mMemStore.answer?.city?.let { city -> setTitle(city) }
        if (!mSavedInRoom && mMemStore.answer == null) {
            GlobalScope.launch(Dispatchers.Main) {
                val answer = db.getAnswerDao().getAnswer(GEO)
                answer?.let {
                    mSavedInRoom = true
                    mMemStore.saveAnswer(it)
                    fillingTheUI()
                    setTitle(it.city)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("savedInRoom", mSavedInRoom)
        outState.putInt("day", mDayPosition)
        outState.putInt("hour", mHourPosition)
    }

    private fun initSwipeToRefresh() {
        swipeRefreshLayout.apply {
            setOnRefreshListener { callApi(mLatLng) }
            setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            )
        }
    }

    private fun callApi(coordinates: LatLng?, type: Int = GEO) {
        if (coordinates != null) {
            RetrofitForJSON(this, coordinates, type).callForAnswer()
        } else {
            swipeRefreshLayout.isRefreshing = false
            if (isLocationPermissionGranted()) initLocListener()
            Toast.makeText(this, "Проверьте что геолокация включена или повторите позже", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initLocListener()
        }
    }

    private fun initPlacesAPI() {
        Places.initialize(this, getString(R.string.google_maps_key))
        (autocomplete_fragment as AutocompleteSupportFragment).apply {
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    callApi(place.latLng, PLACES)
                }

                override fun onError(status: Status) {}
            })
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocListener() {
        val loc: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mLatLng = LatLng(location.latitude, location.longitude)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        Objects.requireNonNull(locationManager).requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, loc)
    }

    private fun isLocationPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return false
            }
        }
        return true
    }

    private fun fillingTheUI() {
        (days_fragment as FragmentDaysRV).updateUI(mMemStore.days, mDayPosition)
        (hours_fragment as FragmentHoursRV).updateUI(mMemStore.days[mDayPosition].hours, mHourPosition)
        var position = mHourPosition
        mMemStore.days.forEachIndexed { index, day -> if (index < mDayPosition) position+=day.hours.size }
        mMemStore.answer?.list?.get(position)?.let { (main_info_fragment as FragmentMainInfo).updateUI(it) }
    }

    override fun successAnswer(response: Boolean, body: Answer?, code: Int) {
        swipeRefreshLayout.isRefreshing = false
        body?.let {
            mMemStore.saveAnswer(it)
            fillingTheUI()
            setTitle(it.city)
            GlobalScope.launch(Dispatchers.Main) {
                db.getAnswerDao().insertAnswer(it)
            }
        }
    }

    private fun setTitle(city: Answer.City) {
        title = "${city.name}, ${city.country} "
    }

    override fun failedAnswer(response: String) {
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(this, response, Toast.LENGTH_LONG).show()
    }

    override fun updatePositions(day: Int, hour: Int) {
        if(day != -1) mDayPosition = day
        mHourPosition = hour
        fillingTheUI()
    }
}

