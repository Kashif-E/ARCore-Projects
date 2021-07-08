package com.infinity.shapefactory
import android.Manifest
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.infinity.shapefactory.constant.Constants.Companion.BOTTOM_SHEET_PEEK_HEIGHT
import com.infinity.shapefactory.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import me.ibrahimyilmaz.kiel.adapterOf
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {

    private lateinit var node: Node
    private lateinit var parentNode: Node
    private lateinit var anchorNode: AnchorNode
    private var yellowMaterial: Material?=null
    private var anchor: Anchor?=null
    private lateinit var arFragment: ArFragment
    private var nodecount : Int = 0

    private val nodeMap: MutableMap<Int, Node> = HashMap()
    private lateinit var material : CompletableFuture<Material>
    private lateinit var binding: ActivityMainBinding
    private lateinit var modelRenderable : ModelRenderable
    private val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermission()
        setupBottomSheet()
        setRecyclerViewAdapter()

        arFragment = supportFragmentManager
            .findFragmentById(R.id.ARfragment) as ArFragment

        addTapListener()

        binding.btnCreate.setOnClickListener {
            createRenderable()
            createAndAddNodeToScene()
            if (::node.isInitialized){
                node.setParent(anchorNode)
            }
        }
        material = MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.YELLOW))
    }

    private fun toInches (value : Float): Float{
        return value/39.3701f
    }

    private fun createRenderable() {
        yellowMaterial = material.get()
        if (binding.radioCylinder.isChecked){
               modelRenderable = ShapeFactory.makeCube(Vector3(toInches(binding.CubeHeightX.text.toString().toFloat()),toInches( binding.CubeHeightY.text.toString().toFloat()),toInches( binding.CubeHeightZ.text.toString().toFloat())), Vector3(toInches(binding.CubeCenterX.text.toString().toFloat()),toInches( binding.CubeHeightY.text.toString().toFloat()),toInches(binding.CubeCenterZ.text.toString().toFloat())),yellowMaterial)
        } else if( binding.radioSphere.isChecked){
            modelRenderable = ShapeFactory.makeSphere(
                    toInches(binding.radius.text.toString().toFloat()), Vector3(toInches(binding.sphereX.text.toString().toFloat()), toInches(binding.sphereY.text.toString().toFloat()) ,toInches( binding.sphereZ.text.toString().toFloat())), yellowMaterial)
        }else{
            modelRenderable = ShapeFactory.makeCylinder(binding.cylinderHeight.text.toString().toFloat(), binding.cylinderRadius.text.toString().toFloat(), Vector3(binding.cylinderX.text.toString().toFloat(), binding.CylinderY.text.toString().toFloat(),binding.CylinderZ.text.toString().toFloat()), yellowMaterial)
        }
    }

    private fun createAndAddNodeToScene() {

        if (nodecount == 0 ){
             node = Node()
            node.apply {
                renderable = modelRenderable
                arFragment.arSceneView.scene.addChild(anchorNode)
            }
            binding.connections.visibility = View.VISIBLE
            binding.placingInstructions.visibility = View.VISIBLE
            nodeMap[nodecount]=node
            nodecount++
        }else{
            if (binding.mainX.text.toString().isNotEmpty() and binding.mainY.text.toString().isNotEmpty() and binding.mainZ.text.toString().isNotEmpty()){
           val childNode =Node().apply {

                    setParent(node)
                    renderable = modelRenderable
                    val box =  node.collisionShape as Box
                    localPosition = Vector3((box.size.x + binding.mainX.text.toString().toFloat()/39.3701f),(box.size.y + binding.mainY.text.toString().toFloat()/39.3701f),(box.size.z + binding.mainZ.text.toString().toFloat()/39.3701f))

                }
                nodecount++
                nodeMap[nodecount]=childNode

            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.cube ->
                    if (checked) {
                        Toast.makeText(this, "cube", Toast.LENGTH_SHORT).show()
                        binding.cubeCard.visibility = View.VISIBLE
                        binding.sphere.visibility = View.GONE
                        binding.cyllinderCard.visibility = View.GONE
                    }
                R.id.radio_cylinder ->
                    if (checked) {
                        Toast.makeText(this, "cylinder", Toast.LENGTH_SHORT).show()
                        binding.cubeCard.visibility = View.GONE
                        binding.sphere.visibility = View.GONE
                        binding.cyllinderCard.visibility = View.VISIBLE

                    }
                R.id.radio_sphere ->
                    if (checked) {
                        binding.cubeCard.visibility = View.GONE
                        binding.sphere.visibility = View.VISIBLE
                        binding.cyllinderCard.visibility = View.GONE
                    }
            }
        }
    }
    private fun addTapListener() {
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, _, _ ->
            if (binding.tvTap.isVisible){
                binding.tvTap.visibility = View.GONE
            }
            anchor = hitResult.createAnchor()
            anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)
            Toast.makeText(this, "Create a Node to add to scene", Toast.LENGTH_SHORT).show()
            binding.bottomSheet.visibility = View.VISIBLE
        }
    }



    private fun setRecyclerViewAdapter() {

        val recyclerViewAdapter = adapterOf<String> {
            register(
                layoutResource = R.layout.button,
                viewHolder =::NameViewHolder,
                onBindViewHolder = { vh, _, name ->
                    vh.namebtn.text = name
                }
            )
        }

        binding.rview.adapter = recyclerViewAdapter
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.peekHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                BOTTOM_SHEET_PEEK_HEIGHT, resources.displayMetrics
        ).toInt()
        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheet.bringToFront()
                bottomSheet.elevation = 10F
            }
        })

    }

    private fun getPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        permissions
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        report.let {

                            if (report.areAllPermissionsGranted()) {
                                Toast.makeText(this@MainActivity, "Permissions Granted", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Please Grant Permissions to use the app", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                }).withErrorListener {
                    Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
                }.check()

    }


}