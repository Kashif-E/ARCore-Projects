package com.infinity.augmentedimages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.io.IOException

private const val REQUEST_CODE_CHOOSE_IMAGE = 0
private const val USE_DATABASE = true

class AugmentedImages : ArFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!USE_DATABASE){
            selectImage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        planeDiscoveryController.apply {
            hide()
            setInstructionView(null)
        }
        arSceneView.planeRenderer.isEnabled = false
    }

    override fun getSessionConfiguration(session: Session?): Config {
        val config = super.getSessionConfiguration(session)
        config.focusMode = Config.FocusMode.AUTO
        if(USE_DATABASE){
            config.augmentedImageDatabase = createAugmentedImageDatabase(session?: return config)
        }
        return config
    }

    private fun selectImage() {
        Intent(Intent.ACTION_GET_CONTENT).run {
            type = "image/*"
            startActivityForResult(this, REQUEST_CODE_CHOOSE_IMAGE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
            val imageUri = data?.data ?: return
            val session = arSceneView.session ?: return
            val config = getSessionConfiguration((session))
            val database = createAugmentedImageDatabase(session, imageUri)
            config.augmentedImageDatabase = database
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            session.configure(config)
        }
    }

    private fun createAugmentedImageDatabase(
        session: Session,
        imageUri: Uri
    ): AugmentedImageDatabase {

        val database = AugmentedImageDatabase(session)
        val bitmap = loadAugmentedImageBitmap(imageUri)
        database.addImage("image.jpg", bitmap)
        return database
    }

    private fun loadAugmentedImageBitmap(imageUri: Uri): Bitmap? {
        return try {
            context?.contentResolver?.openInputStream(imageUri)?.use {

                BitmapFactory.decodeStream(it)
            }

        } catch (e: IOException) {
            Log.e("an error", "occures")
            null

        }

    }

    private fun createAugmentedImageDatabase(session: Session): AugmentedImageDatabase? {
        return try {
            val inputstream = resources.openRawResource(R.raw.imagedatabase)
            AugmentedImageDatabase.deserialize(session, inputstream)

        } catch (e: IOException){
            Log.e("ian ", "erroe")
            null
        }

    }
}