package com.infinity.augmentedfacessceneform

import android.annotation.SuppressLint
import android.media.CamcorderProfile
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.infinity.augmentedfacessceneform.databinding.ActivityMainBinding
import com.infinity.augmentedfacessceneform.util.FilterFace
import com.infinity.augmentedfacessceneform.util.PhotoSaver
import com.infinity.augmentedfacessceneform.util.VideoRecorder

private const val BOTTOM_SHEET_PEEK_HEIGHT = 50f
class MainActivity : AppCompatActivity() {

    private var freckles: Texture? = null
    private var clown: Texture? = null
    private lateinit var scene: Scene
    private lateinit var sceneView: ArSceneView
    lateinit var binding : ActivityMainBinding
    lateinit var arFragment: ArFragment
    val modelArray : ArrayList<ModelRenderable> = arrayListOf()
    private var faceMeshTexture: Texture? = null
    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var regionsRenderable: ModelRenderable? = null
    private lateinit var photoSaver: PhotoSaver
    private lateinit var videoRecorder: VideoRecorder
    private var isRecording = false
    private  var directory = R.raw.sunglasses
    private  var filter : Boolean  = false
    private  var filterTexture:Texture? =null

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable
    var faceFilterNodeMap = HashMap<AugmentedFace, FilterFace>()
    private var renderableModels: ArrayList<ModelRenderable> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Texture.builder()
            .setSource(this, R.drawable.makeup)
            .build()
            .thenAccept { texture -> faceMeshTexture = texture }
        Texture.builder()
            .setSource(this, R.drawable.clown_face_mesh_texture)
            .build()
            .thenAccept { texture -> clown = texture }
        Texture.builder()
            .setSource(this, R.drawable.freckles)
            .build()
            .thenAccept { texture -> freckles = texture }
        Texture.builder()
            .setSource(this, R.drawable.makeup)
            .build()
            .thenAccept { texture -> faceMeshTexture = texture }

        ModelRenderable.builder()
            .setSource(this, directory)
            .build()
            .thenAccept { modelRenderable ->
                modelArray.add(modelRenderable)
                regionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }
        ModelRenderable.builder()
            .setSource(this, R.raw.yellow_sunglasses)
            .build()
            .thenAccept { modelRenderable ->
                modelArray.add(modelRenderable)
                renderableModels.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false

            }
        ModelRenderable.builder()
            .setSource(this, R.raw.fox_face)
            .build()
            .thenAccept { modelRenderable ->
                modelArray.add(modelRenderable)
                renderableModels.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false

            }

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
        photoSaver = PhotoSaver(this)


        setupBottomSheet()
        setupScene()
        addOnUpdateListener()
        setupFab()

        photoSaver = PhotoSaver(this)
        videoRecorder = VideoRecorder(this).apply {
            sceneView = arFragment.arSceneView
            setVideoQuality(CamcorderProfile.QUALITY_1080P, resources.configuration.orientation)
        }


        binding.blacl.setOnClickListener {
            filter = false
            regionsRenderable = modelArray[0]
        }
        binding.yellow.setOnClickListener {
            filter = false
            regionsRenderable = modelArray[1]
        }
        binding.filter.setOnClickListener {
            filter = true
            filterTexture = faceMeshTexture
        }
        binding.freckle .setOnClickListener {
            filter = true
            filterTexture = freckles
        }
        binding.clown.setOnClickListener {
            filter = true
            filterTexture = clown
        }
        binding.foxface.setOnClickListener {
            filter = false
            regionsRenderable = modelArray[2]
        }


    }
    private fun setupScene() {

        sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        scene = sceneView.scene
    }


    private fun addOnUpdateListener() {
        arFragment.arSceneView.scene.addOnUpdateListener {
            if(regionsRenderable != null ) {
                addTrackedFaces()
                removeUntrackedFaces()
            }
        }
    }


    private fun addTrackedFaces() {
        val session = arFragment.arSceneView.session ?: return
      session.getAllTrackables(AugmentedFace::class.java).let {
            for (face in it) {
                Log.e("face  found", "found face")
                if (!faceNodeMap.containsKey(face)) {
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(scene)
                    faceNode.faceRegionsRenderable = regionsRenderable
                    faceNodeMap[face] = faceNode
                       faceNodeMap.getValue(face).faceMeshTexture = null

                }else if(filter){
                    faceNodeMap.getValue(face).faceMeshTexture = filterTexture
                    faceNodeMap.getValue(face).faceRegionsRenderable = null
                } else{
                    faceNodeMap.getValue(face).faceRegionsRenderable = regionsRenderable
                        faceNodeMap.getValue(face).faceMeshTexture = null

                }

            }
        }
    }
    private fun removeUntrackedFaces() {
        val entries = faceNodeMap.entries
        for(entry in entries) {
            val face = entry.key
            if(face.trackingState == TrackingState.STOPPED) {
                val faceNode = entry.value
                faceNode.setParent(null)
                entries.remove(entry)
            }
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            if(!isRecording) {
                photoSaver.takePhoto(arFragment.arSceneView)
            }
        }
        binding.fab.setOnLongClickListener {
            isRecording = videoRecorder.toggleRecordingState()
            true
        }
        binding.fab.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP && isRecording) {
                isRecording = videoRecorder.toggleRecordingState()
                Toast.makeText(this, "Saved video to gallery!", Toast.LENGTH_LONG).show()
                true
            } else false
        }
    }
    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
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



}