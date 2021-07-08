package com.infinity.fedex
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource.*
import com.google.ar.sceneform.assets.RenderableSource.RecenterMode.CENTER
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

private const val BOTTOM_SHEET_PEEK_HEIGHT = 50f
private const val DOUBLE_TAP_TOLERANCE_MS = 1000L

class MainActivity : AppCompatActivity() {

    private lateinit var anchorNode: AnchorNode

    lateinit var arFragment: ArFragment

    private val models = mutableListOf(
            Model("10 Kg ", "https://firebasestorage.googleapis.com/v0/b/modelviewerapp.appspot.com/o/10kg.glb?alt=media&token=5d0babe6-9693-4a70-85d7-245aed2ccb3b"),
            Model("25 Kg ", "https://firebasestorage.googleapis.com/v0/b/modelviewerapp.appspot.com/o/25kg.glb?alt=media&token=3af80e88-cf8e-4968-a1c0-6ec7df1d75ab"),
            Model(" Large ", "https://firebasestorage.googleapis.com/v0/b/modelviewerapp.appspot.com/o/large.glb?alt=media&token=5b3770ce-978f-4672-93b2-ba0af3e9c9fc"),
            Model("Medium", "https://firebasestorage.googleapis.com/v0/b/modelviewerapp.appspot.com/o/medium.glb?alt=media&token=0e2f092c-a004-48e2-bc33-dd8a8fb85a0a"),
            Model("Tube", "https://firebasestorage.googleapis.com/v0/b/modelviewerapp.appspot.com/o/tube.glb?alt=media&token=605f102c-dd0e-41df-9fb4-7146940ec955"),
            Model("Small", "https://firebasestorage.googleapis.com/v0/b/modelviewerapp.appspot.com/o/small.glb?alt=media&token=399bd56f-e6f0-4735-a3f3-b6964992221c")


    )

    lateinit var selectedModel: Model

    val viewNodes = mutableListOf<Node>()

    private lateinit var photoSaver: PhotoSaver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = fragment as ArFragment
        setupBottomSheet()
        setupRecyclerView()
        setupTapArPlaneListener()
        setupFab()

        photoSaver = PhotoSaver(this)

        getCurrentScene().addOnUpdateListener {
            rotateViewNodesTowardsUser()
        }

    }

    private fun setupTapArPlaneListener() {
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, _, _ ->
            anchorNode = AnchorNode(hitResult.createAnchor())
            Toast.makeText(this, "building mdel", Toast.LENGTH_LONG).show()
          buildmodel()
        }

    }





    private fun setupFab() {
        fab.setOnClickListener {
            photoSaver.takePhoto(arFragment.arSceneView)
        }
    }

    private fun setupRecyclerView() {
        rvModels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvModels.adapter = ModelAdapter(models).apply {
            selectedModel.observe(this@MainActivity, {
                this@MainActivity.selectedModel = it
                val newTitle = "Models (${it.title})"
                tvModel.text = newTitle
            })
        }
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        BOTTOM_SHEET_PEEK_HEIGHT,
                        resources.displayMetrics
                ).toInt()
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheet.bringToFront()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        })
    }

    private fun getCurrentScene() = arFragment.arSceneView.scene

    private fun createDeleteButton(): Button {
        return Button(this).apply {
            text = "Delete"
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
        }
    }

    private fun rotateViewNodesTowardsUser() {
        for(node in viewNodes) {
            node.renderable?.let {
                val camPos = getCurrentScene().camera.worldPosition
                val viewNodePos = node.worldPosition
                val dir = Vector3.subtract(camPos, viewNodePos)
                node.worldRotation = Quaternion.lookRotation(dir, Vector3.up())
            }
        }
    }

    private fun addNodeToScene(
            anchorNode: AnchorNode,
            modelRenderable: ModelRenderable,
            viewRenderable: ViewRenderable
    ) {

        val modelNode = TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelRenderable
            setParent(anchorNode)
            getCurrentScene().addChild(anchorNode)
            select()
        }
        val viewNode = Node().apply {
            renderable = null
            setParent(modelNode)
            val box = modelNode.renderable?.collisionShape as Box
            localPosition = Vector3(0f, box.size.y, 0f)
            (viewRenderable.view as Button).setOnClickListener {
                getCurrentScene().removeChild(anchorNode)
                viewNodes.remove(this)
            }
        }
        viewNodes.add(viewNode)
        modelNode.setOnTapListener { _, _ ->
            if(!modelNode.isTransforming) {
                if(viewNode.renderable == null) {
                    viewNode.renderable = viewRenderable
                } else {
                    viewNode.renderable = null
                }
            }
        }
    }

    private fun buildmodel() {

       val  modelrenderable =ModelRenderable.builder()
                .setSource(
                        this, builder().setSource(
                       this,

                        Uri.parse(selectedModel.modelResourceId),
                        SourceType.GLB
                )
                        .build()
                )
                .setRegistryId(selectedModel.modelResourceId)
                .build()


            val     viewrenderable=   ViewRenderable.builder()
                            .setView(this, createDeleteButton())
                            .build()

        CompletableFuture.allOf(modelrenderable, viewrenderable)
                .thenAccept {
                    Toast.makeText(this , "model built", Toast.LENGTH_LONG).show()
                    addNodeToScene(anchorNode,modelrenderable.get(), viewrenderable.get())
                }
                .exceptionally {
                    Toast.makeText(this, "Error loading model: $it", Toast.LENGTH_LONG).show()
                    null
                }


    }




}