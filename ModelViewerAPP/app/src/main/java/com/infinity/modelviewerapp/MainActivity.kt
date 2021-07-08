package com.infinity.modelviewerapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.infinity.modelviewerapp.repository.repository
import com.infinity.modelviewerapp.ui.ViewModelProviderFactory
import com.infinity.modelviewerapp.ui.SharedViewModel
import com.infinity.modelviewerapp.util.CameraPermissionHelper


class MainActivity : AppCompatActivity() {

    lateinit var activitySharedViewModel: SharedViewModel
    lateinit var viewModelProviderFactory : ViewModelProviderFactory
    lateinit var repository: repository
    var mSession: Session? = null
    private   var M_USER_REQUEST_INSTALL = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         repository = repository()
        viewModelProviderFactory = ViewModelProviderFactory(repository)
         activitySharedViewModel =  ViewModelProvider(this, viewModelProviderFactory).get(SharedViewModel::class.java)
    }
    override fun onResume() {
        super.onResume()

        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission( this )
            return
        }

        try {
            if (mSession == null) {

                when (ArCoreApk.getInstance()
                        .requestInstall( this , M_USER_REQUEST_INSTALL)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        mSession = Session( this )

                        Toast.makeText( this , "Arcore Session Started", Toast.LENGTH_LONG).show()
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> M_USER_REQUEST_INSTALL = false
                    else -> M_USER_REQUEST_INSTALL = false


                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            Toast.makeText( this , "Please Allow ARCore installation to use AR Content", Toast.LENGTH_LONG).show()
        } catch (e: UnavailableArcoreNotInstalledException) {
            Toast.makeText( this , "You need to Install ARCore to contniue", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission( this )) {

            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale( this )) {

                CameraPermissionHelper.launchPermissionSettings( this )
            }
        }

    }


}