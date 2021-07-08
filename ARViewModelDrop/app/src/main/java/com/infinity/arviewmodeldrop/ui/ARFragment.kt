package com.infinity.arviewmodeldrop.ui

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource.*
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.infinity.arviewmodeldrop.R
import com.infinity.arviewmodeldrop.ui.ARFragment
import kotlinx.android.synthetic.main.fragment_a_r.*


class ARFragment : Fragment() {


    lateinit var chair: ModelRenderable
    private val link: String =
        "https://firebasestorage.googleapis.com/v0/b/houseestimatorandmodeler.appspot.com/o/Armodels%2FWolves.glb?alt=media&token=ef5b90c0-8c04-4608-8888-a37547adbf84"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_a_r, container, false)
        val arFragment = childFragmentManager
            .findFragmentById(R.id.ARFragment) as ArFragment
        ModelRenderable.builder()
            .setSource(
                requireContext(), builder().setSource(
                    requireContext(),
                    Uri.parse(link),
                    SourceType.GLB
                )
                    .setScale(0.5f)
                    .setRecenterMode(RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId(link)
            .build()
            .thenAccept { renderable: ModelRenderable ->
                chair = renderable
                Toast.makeText(
                    requireContext(), "Model built", Toast.LENGTH_LONG
                ).show()
            }
            .exceptionally {
               Toast.makeText(
                    requireContext(), "Unable to load renderable $link", Toast.LENGTH_LONG
                ).show()
                null
            }



            if (::chair.isInitialized)
            {
                arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
                    val anchorNode = AnchorNode(hitResult.createAnchor())
                    anchorNode.renderable = chair
                    arFragment.arSceneView.scene.addChild(anchorNode)
            }

            }
        else
        {
            Toast.makeText(
                requireContext(), "Please wait and till the model is built", Toast.LENGTH_LONG
            ).show()
        }


        return view

    }


}