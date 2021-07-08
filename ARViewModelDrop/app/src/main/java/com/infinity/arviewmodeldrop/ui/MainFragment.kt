package com.infinity.arviewmodeldrop.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.infinity.arviewmodeldrop.R
import com.infinity.arviewmodeldrop.util.CameraPermissionHelper
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.concurrent.Executor
class MainFragment : Fragment() {

    lateinit var fragView : View

     private val link: String =
        "https://firebasestorage.googleapis.com/v0/b/houseestimatorandmodeler.appspot.com/o/Armodels%2FWolves.glb?alt=media&token=ef5b90c0-8c04-4608-8888-a37547adbf84"
    var mSession: Session? = null
    private   var M_USER_REQUEST_INSTALL = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         fragView = inflater.inflate( R.layout.fragment_main, container, false)
        mayEnableARButton()
        val sceneViewerIntent = Intent(Intent.ACTION_VIEW)


     fragView.SceneVIewButton.setOnClickListener {
            sceneViewerIntent.data =
                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                    .appendQueryParameter(
                        "file",
                        link
                    ).appendQueryParameter("title", "Chair").build()
            sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox")
            startActivity(sceneViewerIntent)
        }



        fragView.btn3D.setOnClickListener {


            sceneViewerIntent.data =
                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                    .appendQueryParameter("file", link)
                    .appendQueryParameter("mode", "ar_only")
                    .appendQueryParameter("title", "wolf")
                    .appendQueryParameter("resizable", "true")
                    .build()

            sceneViewerIntent.setPackage("com.google.ar.core")
            startActivity(sceneViewerIntent)
        }
         fragView.btnAR.setOnClickListener {

            findNavController().navigate(R.id.action_mainFragment_to_ARFragment)


        }
        return fragView
    }


    private fun mayEnableARButton() {
        val availability = ArCoreApk.getInstance().checkAvailability(requireContext())
        if (availability.isTransient) {
            Executor { mayEnableARButton() }
        }
        if (availability.isSupported) {

            fragView.ArProgressBar.visibility= View.GONE

        } else {
            fragView.btn3D.visibility = View.INVISIBLE
             fragView.ArAvailabilityCheck.text = getString(R.string.unavaialabe)
             fragView.ArAvailabilityCheck.visibility = View.VISIBLE
             fragView.btnAR.visibility = View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            CameraPermissionHelper.requestCameraPermission(requireActivity())
            return
        }

        try {
            if (mSession == null) {

                when (ArCoreApk.getInstance()
                    .requestInstall(requireActivity(), M_USER_REQUEST_INSTALL)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        mSession = Session(requireContext())
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.ARCoreStatus),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> M_USER_REQUEST_INSTALL = false
                    else -> M_USER_REQUEST_INSTALL = false


                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            Toast.makeText(requireContext(), getString(R.string.arcore_required), Toast.LENGTH_LONG)
                .show()
        } catch (e: UnavailableArcoreNotInstalledException) {
            Toast.makeText(
                requireContext(),
                getString(R.string.arcore_not_installed),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
            Toast.makeText(
                requireContext(),
                getString(R.string.camera_permision_required),
                Toast.LENGTH_LONG
            )
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(requireActivity())) {

                CameraPermissionHelper.launchPermissionSettings(requireActivity())
            }
            //  activity?.finish()
        }

    }

}