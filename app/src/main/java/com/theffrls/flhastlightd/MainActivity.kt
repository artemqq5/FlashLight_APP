package com.theffrls.flhastlightd

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.theffrls.flhastlightd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isEnabledLight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        findCameraIdWithFlash()
        isFlashlightOn()

    }

    fun toggleFlashLight(view: View?) {
        try {
            cameraManager!!.setTorchMode(cameraId!!, !isEnabledLight)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Error: $e", Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            cameraManager!!.setTorchMode(cameraId!!, false)
        } catch (e: CameraAccessException) {
            Snackbar.make(binding.root, "Error: $e", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun isFlashlightOn() {
        cameraManager!!.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                if (this@MainActivity.cameraId == cameraId) {
                    isEnabledLight = enabled

                    if (isEnabledLight) {
                        binding.toggleLight.setImageResource(R.drawable.switch_on)
                    }else {
                        binding.toggleLight.setImageResource(R.drawable.switch_off)
                    }
                }
            }
        }, null)
    }

    private fun findCameraIdWithFlash() {
        for (id in cameraManager!!.cameraIdList) {
            val characteristics = cameraManager!!.getCameraCharacteristics(id)
            val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
            if (hasFlash == true) {
                cameraId = id
                break
            }
        }
    }
}