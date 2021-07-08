package com.infinity.objectanimation

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3


class RotatingNode(
    private val degreesPerSecond: Float
) : Node() {

    private var objectAnimator: ObjectAnimator? = null

    override fun onActivate() {
        super.onActivate()
        startAnimation()
    }

    override fun onDeactivate() {
        super.onDeactivate()
        stopAnimation()
    }

    private fun startAnimation() {
        objectAnimator = objectAnimator ?: createObjectAnimator().apply {
            target = this@RotatingNode
            duration = (1000 * 360 / degreesPerSecond).toLong()
            start()
        }
    }

    private fun stopAnimation() {
        objectAnimator?.cancel()
        objectAnimator = null
    }

    private fun createObjectAnimator(): ObjectAnimator {
        val animationValues = arrayOf(
            Quaternion.axisAngle(Vector3.up(), 0f),
            Quaternion.axisAngle(Vector3.up(), 180f),
            Quaternion.axisAngle(Vector3.up(), 360f)
        )
        return ObjectAnimator().apply {
            setObjectValues(*animationValues)
            setPropertyName("localRotation")
            setEvaluator(QuaternionEvaluator())
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = LinearInterpolator()
        }
    }
}