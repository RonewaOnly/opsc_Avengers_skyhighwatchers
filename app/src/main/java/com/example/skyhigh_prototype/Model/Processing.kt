package com.example.skyhigh_prototype.Model

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.core.VideoCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
@Composable
fun CaptureScreen(
    onImageCaptured: (Uri) -> Unit,
    onVideoCaptured: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    // Permissions launcher to handle camera and audio permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true &&
            permissions[Manifest.permission.RECORD_AUDIO] == true &&
            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
            // Permissions granted
        } else {
            // Handle permission denied case
            onError("Camera and audio permissions are required to use this feature.")
        }
    }

    // Request the permissions at the start of this composable
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    // Now, you can include the camera and video capture Composables
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Camera Capture Section
        Text(text = "Camera Capture", style = MaterialTheme.typography.titleSmall)

        Box(
            modifier = Modifier
                .height(300.dp) // Define a fixed height for the preview area
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black) // Give the preview area a background
        ) {
            CameraCapture(
                onImageCaptured = { uri ->
                    photoUri = uri
                    onImageCaptured(uri) // Notify when an image is captured
                },
                onError = { e ->
                    Log.e("CameraCapture", "Error capturing image", e)
                    onError("Error capturing image: ${e.localizedMessage}")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Capture Photo Button
        Button(
            onClick = {
                // Code to capture photo
                // Trigger the CameraCapture composable or function
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Capture Photo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Capture Section
        Text(text = "Video Recording", style = MaterialTheme.typography.titleSmall)

        Box(
            modifier = Modifier
                .height(300.dp) // Define a fixed height for the preview area
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black)
        ) {
            CameraRecord(
                onVideoRecorded = { uri ->
                    videoUri = uri
                    onVideoCaptured(uri) // Notify when a video is captured
                },
                onError = { e ->
                    Log.e("CameraRecord", "Error recording video", e)
                    onError("Error recording video: ${e.localizedMessage}")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start/Stop Recording Button
        Button(
            onClick = {
                isRecording = !isRecording
                // Logic to start or stop recording
                if (isRecording) {
                    // Start recording
                } else {
                    // Stop recording
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
        }

        // Display Captured Image or Video Information
        Spacer(modifier = Modifier.height(16.dp))
        if (photoUri != null) {
            Text(text = "Photo saved at: ${photoUri?.path}")
        }

        if (videoUri != null) {
            Text(text = "Video saved at: ${videoUri?.path}")
        }
    }
}



@Composable
fun CameraCapture(
    onImageCaptured: (Uri) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    // Ensure camera permission is granted
    val cameraPermissionGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    // Initialize the CameraProvider asynchronously
    LaunchedEffect(cameraProviderFuture) {
        try {
            cameraProvider = cameraProviderFuture.get()  // Async fetch of camera provider
        } catch (e: Exception) {
            onError(e)  // Handle exception
        }
    }

    // ImageCapture use case setup
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    // UI for the Camera preview
    AndroidView(
        factory = { context ->
            previewView = PreviewView(context)
            previewView?.let {
                val preview = Preview.Builder().build().also { previewInstance ->
                    previewInstance.setSurfaceProvider(it.surfaceProvider)
                }

                if (cameraProvider != null && cameraPermissionGranted) {
                    try {
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider?.unbindAll()
                        cameraProvider?.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageCapture
                        )
                    } catch (e: Exception) {
                        onError(e)
                    }
                }
            }
            previewView!!
        },
        modifier = Modifier.fillMaxSize()
    )

    // Capture Button UI
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                if (cameraPermissionGranted && imageCapture != null) {
                    val photoFile = File(
                        context.externalMediaDirs.firstOrNull(),
                        "${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                                onImageCaptured(savedUri)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                onError(exception)
                            }
                        }
                    )
                } else {
                    onError(Exception("Camera permission is not granted or camera not initialized."))
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = "Capture Photo")
        }
    }
}



@Suppress("NAME_SHADOWING")
@SuppressLint("RestrictedApi", "MissingPermission")
@Composable
fun CameraRecord(
    onVideoRecorded: (Uri) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Flag to track the recording state
    var isRecording by remember { mutableStateOf(false) }

    // Create VideoCapture instance
    val videoCapture = remember {
        VideoCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, videoCapture
                )
            } catch (e: Exception) {
                onError(e)
            }

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    Box(modifier = Modifier.fillMaxSize().padding(top=90.dp)) {
        Button(
            onClick = {
                // Toggle recording state
                if (isRecording) {
                    // Stop recording
                    videoCapture.stopRecording()
                    isRecording = false
                } else {
                    // Start recording if not currently recording
                    val videoFile = File(
                        context.externalMediaDirs.firstOrNull(),
                        "${System.currentTimeMillis()}.mp4"
                    )

                    val outputOptions = VideoCapture.OutputFileOptions.Builder(videoFile).build()

                    videoCapture.startRecording(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : VideoCapture.OnVideoSavedCallback {
                            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(videoFile)
                                onVideoRecorded(savedUri)
                            }

                            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                                onError(cause ?: Exception(message))
                            }
                        }
                    )
                    isRecording = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
        }
    }
}


