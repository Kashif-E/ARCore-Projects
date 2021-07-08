package com.infinity.modelviewerapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color.LTGRAY
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.assets.RenderableSource.*
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.infinity.modelviewerapp.MainActivity
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.databinding.SceneviewFragmentBinding
import java.util.concurrent.CompletionException


class ThreeDView : Fragment(R.layout.sceneview_fragment) {

    lateinit var model: ModelRenderable
    var link: String? = null
    lateinit var binding: SceneviewFragmentBinding
    private var modelNode: TransformableNode? = null
    private val viewModel: SharedViewModel by activityViewModels()
   lateinit var  transformationSystem: TransformationSystem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SceneviewFragmentBinding.bind(view)

        link = viewModel.modelUrl.value
        transformationSystem = TransformationSystem(resources.displayMetrics, FootprintSelectionVisualizer())
        renderObject()
        binding.SceneView.renderer?.setClearColor(Color(LTGRAY))
        binding.SceneView.scene
                .addOnPeekTouchListener { hitTestResult: HitTestResult?, motionEvent: MotionEvent? ->
                    transformationSystem.onTouch(
                            hitTestResult,
                            motionEvent
                    )
                }


    }

    private fun renderObject() {


        ModelRenderable.builder()
                .setSource(
                        requireContext(), builder().setSource(
                        requireContext(),
                        Uri.parse(link),
                        SourceType.GLB
                )
                        .setRecenterMode(RecenterMode.ROOT)
                        .build()

                )
                .setRegistryId(link)
                .build()
                .thenAccept { modelRenderable: ModelRenderable ->


                    model= modelRenderable
                    onRenderableLoaded(model)
                }
                .exceptionally { throwable: Throwable? ->
                    val message: String = if (throwable is CompletionException) {

                        "Internet is not working"
                    } else {

                        "Can't load Model"
                    }
                    val mainHandler = Handler(Looper.getMainLooper())
                    val finalMessage: String = message
                    val myRunnable = Runnable {
                        AlertDialog.Builder(requireContext())
                                .setTitle("Error")
                                .setMessage(finalMessage + "")
                                .setPositiveButton("Retry") { dialogInterface: DialogInterface, _: Int ->
                                    renderObject()
                                    dialogInterface.dismiss()
                                }
                                .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
                                .show()
                    }
                    mainHandler.post(myRunnable)
                    null
                }
    }

    private fun onRenderableLoaded(modelRenderable: ModelRenderable) {


        modelNode = TransformableNode(transformationSystem).apply {
            setParent(binding.SceneView.scene)
            translationController.isEnabled = true
            scaleController.isEnabled = true
            scaleController.minScale=0.01f
            scaleController.maxScale=2f
            rotationController.isEnabled = true
            localPosition = Vector3(0f,  0f, - 2.3f)
            renderable = modelRenderable
        }

        transformationSystem.selectNode(modelNode)
       // rotateTowardsUser()

    }

    private fun rotateTowardsUser() {
        if (modelNode != null) {
            modelNode?.renderable?.let {
                val cameraPos = binding.SceneView.scene.camera.worldPosition
                val viewNodePos = modelNode!!.worldPosition
                val direction = Vector3.subtract(cameraPos, viewNodePos)
                modelNode!!.worldRotation = Quaternion.lookRotation(direction, Vector3.up())

            }

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.SceneView.resume()
        }
        catch (e: CameraNotAvailableException)
        {
            e.message
        }


    }

    override fun onPause() {
        super.onPause()
        binding.SceneView.pause()
    }
    }



