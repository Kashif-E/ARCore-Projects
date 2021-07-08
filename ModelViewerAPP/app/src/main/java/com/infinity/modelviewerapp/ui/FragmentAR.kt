package com.infinity.modelviewerapp.ui

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class FragmentAR : ArFragment() {

    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.SHARED_CAMERA)

    }
    override fun getSessionConfiguration(session: Session?): Config {

        val config = Config (session)
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
        return config
    }
}