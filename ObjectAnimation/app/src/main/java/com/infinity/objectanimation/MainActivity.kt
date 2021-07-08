package com.infinity.objectanimation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource.*
import com.google.ar.sceneform.assets.RenderableSource.RecenterMode.CENTER
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import java.util.*
import java.util.concurrent.CompletionException


class MainActivity : AppCompatActivity() {
    private var building: Boolean = false
    private  lateinit var arFragment : ArFragment
    private val linksage= "${R.raw.pumpkin}"
    private  val arrayModel = arrayOf("https://firebasestorage.googleapis.com/v0/b/houseestimatorandmodeler.appspot.com/o/pumpkin.glb?alt=media&token=6514c672-9b70-4351-8688-337c8c1d7fa7",
        "https://firebasestorage.googleapis.com/v0/b/houseestimatorandmodeler.appspot.com/o/google.glb?alt=media&token=f3b4ec60-5559-461d-98d5-b099e35fee85",
        "https://firebasestorage.googleapis.com/v0/b/houseestimatorandmodeler.appspot.com/o/ghost.glb?alt=media&token=6bd9ef3f-69bc-43cc-89af-c96c4d130741")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment =supportFragmentManager.findFragmentById(R.id.ArFragment) as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
           if(!building){
               val rnds = (0..2).random()
               LoadModel(hitResult.createAnchor(),arrayModel[rnds],rnds)
           }else{
               Toast.makeText(this, "Pleasse while previous model is being build", Toast.LENGTH_SHORT).show()
           }

        }

    }
    private fun LoadModel(anchor: Anchor, link: String, rnds: Int){
        Toast.makeText(this, "BuildingModel", Toast.LENGTH_SHORT).show()
        building = true
        ModelRenderable.builder()
            .setSource(
                this, builder().setSource(
                    this,
                    Uri.parse(link),
                    SourceType.GLB
                )

                    .setRecenterMode(CENTER)
                    .build()
            )
            .setRegistryId(link)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->

                Toast.makeText(this, "ModelBuilt", Toast.LENGTH_SHORT).show()
                val ghost = when(rnds){
                    0 -> Ghosts.ghost
                    1 -> Ghosts.googlehost
                    2 -> Ghosts.pumpkin
                    else -> Ghosts.ghost
                }

                addNodeToScene(anchor ,modelRenderable ,ghost)
            }
            .exceptionally { throwable: Throwable? ->
               if (throwable is CompletionException) {
                   Log.e("Error","Internet is not working")
                } else {

                  Log.e("Error",  "Can't load Model")
                }
                null
            }


    }

    private fun addNodeToScene(anchor: Anchor, modelRenderable: ModelRenderable, ghosts: Ghosts) {
        building = false
        val anchorNode = AnchorNode(anchor)
        val rotatingNode = RotatingNode(ghosts.degreesPerSecond).apply {
            setParent(anchorNode)
        }
        Node().apply {
            renderable = modelRenderable
            setParent(rotatingNode)
            localPosition = Vector3(ghosts.radius, ghosts.height, 0f)
            localRotation = Quaternion.eulerAngles(Vector3(0f, ghosts.rotationDegrees, 0f))
        }
        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}