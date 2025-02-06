package com.example.locationtrackerapplication

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.Manifest
class MainActivity : AppCompatActivity() {

    var LOCPER: Int = 101
    var backlock: Int = 102
    var ispermission: Boolean = false
    var SETTINGS_PERMISSION_REQUEST_CODE: Int = 5002

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )

    private val android14Permissions = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE_LOCATION
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (arePermissionsGranted()) {
            startLocationService()
        } else {
            Toast.makeText(this, "Location permissions required!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun requestPermissionsAndStartService() {
        if (arePermissionsGranted()) {
            startLocationService()
        } else {
            val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34+
                locationPermissions + android14Permissions
            } else {
                locationPermissions
            }
            permissionLauncher.launch(requiredPermissions)
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(
            this,
            BackGroundService::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return (locationPermissions + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) android14Permissions else emptyArray())
            .all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        requestPermissionsAndStartService()

       /* if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // requestPermissionsplash(context);
            requestPermissionsplash()
        }else {

            startLocationService()
        }*/
    }



    fun requestPermissionsplash() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(permission.ACCESS_FINE_LOCATION),
            LOCPER
        )



       // showLocationPromptSplash(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("mainactivity", "onRequestPermissionsResult")
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(
                "naib activity",
                "onRequestPermissionsResult true"
            )
            if (requestCode == LOCPER) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Handle the location access here
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf<String>(permission.ACCESS_BACKGROUND_LOCATION),
                            backlock
                        )
                    } else {
                        checkUpdateApi()
                    }
                } else if ((ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permission.ACCESS_FINE_LOCATION
                    ))
                ) {
                  //  getLocationSplash(this)
                } else {

                    //getLocationSplash(this);
                    showBackgroundPermissionDialog(getString(R.string.app_name) + " Require Background Permission . Please 'Allow all the time ' from 'Permission' -> 'Location' -> 'Allow all the time'")


                    // Permission denied
                    // Prompt the user to grant permission again
                    // showPermissionExplanationDialog();
                }
            } else if (requestCode == backlock) {
                //  getLocationSplash(this);
                if (!ispermission) {
                    // ispermission = false;
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // checkUpdateApi();
                    } else {
                        checkUpdateApi()
                    }
                }
            }
        } else {
            checkUpdateApi()
        }
    }

    private fun checkUpdateApi() {
        ispermission = true
        val serviceIntent = Intent(
            this,
            BackGroundService::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun showBackgroundPermissionDialog(msg: String) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        val builder = AlertDialog.Builder(this).create()
        // Inflate the custom layout for the dialog
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_dialog_layout, null)
        val confirm_btn = dialogView.findViewById<Button>(R.id.confirm_btn_dialog)
        val text_heading_tv = dialogView.findViewById<TextView>(R.id.text_heading_tv)
        val cancel_btn = dialogView.findViewById<Button>(R.id.cancel_btn)
        builder.setView(dialogView)

        text_heading_tv.text = msg
        confirm_btn.text = "Setting"
        cancel_btn.visibility = View.GONE
        cancel_btn.setOnClickListener { // Toast.makeText(PostLoginActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            builder.dismiss()
        }

        confirm_btn.setOnClickListener {
            openAppSettings()
            //  changeDuty(b);
            builder.dismiss()
        }


        builder.show()
        builder.setCancelable(false)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.setData(uri)

        // Open the specific location permission settings
        startActivityForResult(intent, SETTINGS_PERMISSION_REQUEST_CODE)
    }

}