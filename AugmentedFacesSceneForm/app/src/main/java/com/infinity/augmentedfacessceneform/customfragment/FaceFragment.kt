package com.infinity.augmentedfacessceneform.customfragment

import com.google.ar.sceneform.ux.ArFragment


import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.Config
import com.google.ar.core.Session
import java.util.*

private const val LOG_TAG = "AudioRecordTest"


 class FaceFragment : ArFragment() {
    override fun getSessionConfiguration(session: Session?): Config {
        val config = Config(session)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        return config
    }

    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.FRONT_CAMERA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
    }
     override fun getAdditionalPermissions(): Array<String> {
         val additionalPermissions = super.getAdditionalPermissions()
         val permissionsLength = additionalPermissions.size
         val permissions = Array(permissionsLength + 1) { Manifest.permission.WRITE_EXTERNAL_STORAGE }
         if(permissionsLength > 0) {
             System.arraycopy(additionalPermissions, 0, permissions, 1, permissionsLength)
         }
         return permissions
     }
}