package nmhu.edu.sd_bssd4250_permission

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() , SensorEventListener {
    lateinit var infoText: TextView
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            infoText.text = "Granted"
            openCamera()
        } else {
            infoText.text = "Denied"
        }
    }

    val requestPermissionLauncher1 = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            infoText.text = "Granted"
            openCamera()
        } else {
            infoText.text = "Cancel"
        }
    }

    private val requestMultiplePermissionLauncher1 = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() ) { permissions ->
        permissions.entries.forEach {
            var currText1 = infoText.text
            var isCam = false
            if (it.key == android.Manifest.permission.CAMERA) {
                currText1 = "$currText1 Camera = "
                isCam = true
            } else {
                currText1 = "$currText1 Motion = "
            }

            if (it.value) {
                currText1 = "$currText1 Accept."
                if (isCam) {
                    openCamera()
                } else {
                    readMotion()
                }
            } else {
                currText1 = "$currText1 Cancel."
            }
            infoText.text
        }
    }


    private val requestMultiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() ) { permissions ->
        permissions.entries.forEach {
            var currText = infoText.text
            var isCam = false
            if (it.key == android.Manifest.permission.CAMERA) {
                currText = "$currText Camera = "
                isCam = true
            } else {
                currText = "$currText Motion = "
            }

            if (it.value) {
                currText = "$currText Granted."
                if (isCam) {
                    openCamera()
                } else {
                    readMotion()
                }
            } else {
                currText = "$currText Denied."
            }
            infoText.text
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readMotion() //delete this after proving steps works
        infoText = TextView(this).apply {
            hint = "Click a buttton"
        }


        val cameraButton = Button(this).apply {
            text = "Camera"
            setOnClickListener {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                when {
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openCamera()
                        //You can use the API that requires the permission
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                        AlertDialog.Builder(context).apply {
                            setTitle("Camera Features")
                            setMessage("You must allow Camera permissions to use this feature. Ask again?")
                            setPositiveButton("Yes") { _, _ ->
                                //requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                requestMultiplePermissionLauncher.launch(
                                    arrayOf(
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.ACTIVITY_RECOGNITION
                                    )
                                )
                            }
                            create()
                            show()
                        }

                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestMultiplePermissionLauncher.launch(
                            arrayOf(
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.ACTIVITY_RECOGNITION))
                    }

                    when (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    } ->
                    {
                        shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
                    }

                    else -> {
                        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            }
        }


        val mainLayout = LinearLayoutCompat(this).apply {
            orientation = LinearLayoutCompat.VERTICAL
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT)
            addView(infoText)
            addView(cameraButton)
        }
        setContentView(mainLayout)
    }

    private fun readMotion(){
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val mSensor: Sensor? = sensorManager. getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL)

    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(cameraIntent)
    }
   fun onScreenChanged(p0: SensorEvent) {
        Log.d("MOTION", p0!!.values[0].toString())
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        Log.d("MOTION", p0!!.values[0].toString())
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int){
        //not neeeded to use, but required to have
    }
}