package com.infinity.modelviewerapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.filament.Box
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.assets.RenderableSource.*
import com.google.ar.sceneform.assets.RenderableSource.RecenterMode.CENTER
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.infinity.modelviewerapp.MainActivity
import com.infinity.modelviewerapp.R
import com.infinity.modelviewerapp.databinding.FragmentARBinding
import java.util.*
import java.util.concurrent.CompletionException


class ARFragment : Fragment(R.layout.fragment_a_r) {
    private var modelNode: TransformableNode? = null
    private lateinit var transformationSystem: TransformationSystem
    lateinit var model: ModelRenderable
    lateinit var link: String
    val viewModel: SharedViewModel by activityViewModels()
    lateinit var binding: FragmentARBinding
    lateinit var arFragment: ArFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentARBinding.bind(view)
        link = viewModel.modelUrl.value.toString()
        arFragment = childFragmentManager
            .findFragmentById(R.id.ARFragment) as ArFragment
        transformationSystem =
            TransformationSystem(resources.displayMetrics, FootprintSelectionVisualizer())
        buildmodel()
        addTapListener()

    }



    private fun addTapListener() {
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, _, _ ->


            if (::model.isInitialized) {

                val anchorNode = AnchorNode(hitResult.createAnchor())

                addNodeToScene(anchorNode)
            } else {
                Toast.makeText(
                    requireContext(), "Please wait till the model is built", Toast.LENGTH_LONG
                ).show()
            }

        }
    }


    private fun addNodeToScene(anchorNode: AnchorNode) {

        anchorNode.setParent(arFragment.arSceneView.scene)
         modelNode = TransformableNode(arFragment.transformationSystem).apply {
            renderable = model
            setParent(anchorNode)
            scaleController.minScale = 0.01f
            scaleController.maxScale = 2f
            rotationController.isEnabled = true
             select()
        }
        transformationSystem.selectNode(modelNode)


    }

    private fun buildmodel() {
        ModelRenderable.builder()
            .setSource(
                requireContext(), builder().setSource(
                    requireContext(),
                    Uri.parse(link),
                    SourceType.GLB
                )

                    .setRecenterMode(CENTER)
                    .build()
            )
            .setRegistryId(link)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->


                model = modelRenderable

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
                            buildmodel()
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
                        .show()
                }
                mainHandler.post(myRunnable)
                null
            }
    }


    override fun onResume() {
        super.onResume()
        if (::arFragment.isInitialized) {
            arFragment.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::arFragment.isInitialized) {

            arFragment.onPause()
        }
    }

}
