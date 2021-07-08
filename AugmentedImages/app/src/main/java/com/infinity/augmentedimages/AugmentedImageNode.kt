package com.infinity.augmentedimages

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import kotlin.math.max

class AugmentedImageNode(private val context : Context): AnchorNode(){
    var image: AugmentedImage? = null

    private var modelCompletableFuture = ModelRenderable.builder()
        .setSource(context, R.raw.beed)
        .build()
    private lateinit var renderable: ModelRenderable

    fun setAugmentedImage(image: AugmentedImage) {
        this.image = image
        if(!modelCompletableFuture.isDone) {
            modelCompletableFuture.thenAccept {
                setAugmentedImage(image)
            }.exceptionally {
                Toast.makeText(context, "Error creating renderable", Toast.LENGTH_LONG).show()
                null
            }
            return
        }
        renderable = modelCompletableFuture.get()
        anchor = image.createAnchor(image.centerPose)

        val modelNode = Node().apply {
            setParent(this@AugmentedImageNode)
            renderable = this@AugmentedImageNode.renderable
        }

        val renderableBox = renderable.collisionShape as Box
        val maxEdgeSize = max(renderableBox.size.x, renderableBox.size.z)
        val maxImageEdge = max(image.extentX, image.extentZ)
        val modelScale = (maxImageEdge / maxEdgeSize) / 2f

        modelNode.localScale = Vector3(modelScale, modelScale, modelScale)


    }


}

