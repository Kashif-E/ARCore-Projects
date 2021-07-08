package com.infinity.fedex


import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.widget.Toast
import com.google.ar.sceneform.ArSceneView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoSaver(
    private val activity: Activity
) {

    private fun generateFilename(): String? {
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)?.absolutePath +
                "/Fedex/${date}_screenshot.jpg"
    }

    private fun saveBitmapToGallery(bmp: Bitmap, filename: String) {
        val out = File(filename)
        if(!out.parentFile.exists()) {
            out.parentFile.mkdirs()
        }
        try {
            val outputStream = FileOutputStream(filename)
            val outputData = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputData)
            outputData.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
        } catch(e: IOException) {
            Toast.makeText(activity, "Failed to save bitmap to gallery.", Toast.LENGTH_LONG).show()
        }
    }

    fun takePhoto(arSceneView: ArSceneView) {
        val bmp = Bitmap.createBitmap(arSceneView.width, arSceneView.height, Bitmap.Config.ARGB_8888)
        val handlerThread = HandlerThread("PixelCopyThread")
        handlerThread.start()

        PixelCopy.request(arSceneView, bmp, { result ->
            if(result == PixelCopy.SUCCESS) {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    val filename = generateFilename()
                    saveBitmapToGallery(bmp, filename ?: return@request)
                }
                activity.runOnUiThread {
                    Toast.makeText(activity, "Successfully took photo!", Toast.LENGTH_LONG).show()
                }
            } else {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Failed to take photo.", Toast.LENGTH_LONG).show()
                }
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

}
