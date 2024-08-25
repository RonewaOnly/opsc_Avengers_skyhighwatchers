@file:Suppress("PackageName")

package com.example.skyhigh_prototype.Model

import android.Manifest
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoOutput
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@Composable
fun CameraApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "camera") {
        composable("camera") { CameraScreen(navController) }
        composable("gallery") { GalleryScreen(navController) }
        composable("video_player/{videoUri}") { backStackEntry ->
            val videoUri = backStackEntry.arguments?.getString("videoUri")?.toUri()
            if (videoUri != null) {
                VideoPlayerScreen(videoUri, navController)
            }
        }
        composable("image_detail/{imageUri}") { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")?.toUri()
            if (imageUri != null) {
                ImageDetailScreen(imageUri, navController)
            }
        }
    }
}

@Composable
fun CameraScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
    }

    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        onDispose {
            // Properly unbind all use cases and clean up resources when this composable leaves the composition
            cameraProvider.unbindAll()
        }
    }

    if (hasCameraPermission) {
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val preview = Preview.Builder().build()
        val recorder = Recorder.Builder().build()

        cameraProvider.unbindAll()  // Ensure no conflicting bindings
        imageCapture = ImageCapture.Builder().build()
        videoCapture = VideoCapture.withOutput(recorder)


            cameraProvider.bindToLifecycle(
                LocalContext.current as ComponentActivity,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            )


        val previewView = PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            preview.setSurfaceProvider(surfaceProvider)
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView({ previewView }, modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val photoFile = createFile(context, "jpg")
                    if (photoFile != null) {
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                        imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    Toast.makeText(context, "Image saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Failed to save image: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Failed to create file for image.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Capture Image")
                }

                Button(onClick = {
                    if (recording == null) {
                        val videoFile = createFile(context, "mp4")
                        if (videoFile != null) {
                            val outputOptions = FileOutputOptions.Builder(videoFile).build()
                            recording = videoCapture?.output?.prepareRecording(context, outputOptions)
                                ?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                    when (recordEvent) {
                                        is VideoRecordEvent.Start -> { /* Handle start */ }
                                        is VideoRecordEvent.Finalize -> {
                                            if (!recordEvent.hasError()) {
                                                Toast.makeText(context, "Video saved: ${videoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Failed to record video: ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                                            }
                                            recording = null
                                        }
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Failed to create file for video.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        recording?.stop()
                        recording = null
                    }
                }) {
                    Text(if (recording == null) "Start Video" else "Stop Video")
                }

                Button(onClick = { navController.navigate("gallery") }) {
                    Text("Gallery")
                }
            }
        }
    } else {
        Text("Camera permission required")
    }
}


fun createFile(context: android.content.Context, extension: String): File? {
    return try {
        File(context.externalMediaDirs.first(), "${System.currentTimeMillis()}.$extension")
    } catch (e: IOException) {
        Log.e("CameraApp", "Failed to create file: ${e.message}")
        null
    }
}

@Composable
fun GalleryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val mediaFiles = remember { mutableStateListOf<File>() }

    LaunchedEffect(Unit) {
        context.externalMediaDirs.firstOrNull()?.listFiles()?.let { files ->
            mediaFiles.clear()
            mediaFiles.addAll(files.filter { it.isFile && (it.extension == "jpg" || it.extension == "mp4") })
        }
    }

    // Ensure LazyColumn has a constrained height
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(mediaFiles) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (file.extension == "mp4") {
                                navController.navigate("video_player/${Uri.encode(file.toUri().toString())}")
                            } else if (file.extension == "jpg") {
                                navController.navigate("image_detail/${Uri.encode(file.toUri().toString())}")
                            }
                        }
                        .padding(8.dp)
                ) {
                    if (file.extension == "jpg") {
                        Image(
                            painter = rememberImagePainter(file.toUri()),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    } else if (file.extension == "mp4") {
                        val thumbnailBitmap = remember { mutableStateOf<Bitmap?>(null) }

                        LaunchedEffect(file) {
                            thumbnailBitmap.value = generateVideoThumbnail(context, file)
                        }

                        thumbnailBitmap.value?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                        } ?: run {
                            Image(
                                painter = painterResource(id = android.R.drawable.ic_media_play),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(file.name)
                        Button(
                            onClick = {
                                mediaFiles.remove(file)
                                file.delete()
                                Toast.makeText(context, "Deleted: ${file.name}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Delete", color = Color.White)
                        }
                    }
                }
            }
        }

        // Back to camera button
        Button(
            onClick = { navController.navigate("camera") },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Back to Camera")
        }
    }
}


@OptIn(UnstableApi::class)
suspend fun generateVideoThumbnail(context: android.content.Context, videoFile: File): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoFile.path)
            val thumbnail = retriever.getFrameAtTime(0)
            thumbnail
        } catch (e: Exception) {
            Log.e("ThumbnailError", "Failed to generate thumbnail: ${e.message}")
            null
        }
    }
}

@Composable
fun VideoPlayerScreen(videoUri: Uri, navController: NavHostController) {
    val context = LocalContext.current
    val exoPlayer: ExoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    DisposableEffect(
        kotlin.Unit
    ) {
        onDispose { exoPlayer.release() }
    }

    Button(
        onClick = { navController.navigateUp() },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Back to Gallery")
    }
}

@Composable
fun ImageDetailScreen(imageUri: Uri, navController: NavHostController) {
    val zoomState = rememberZoomState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZoomableImage(
            painter = rememberImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            zoomState = zoomState,
            contentScale = ContentScale.Fit
        )

        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Back to Gallery")
        }
    }
}

@Composable
fun rememberZoomState(): ZoomState {
    val scale = remember { mutableStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }
    return ZoomState(scale, offset)
}


@Composable
fun ZoomableImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    zoomState: ZoomState,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center
) {
    var scale by zoomState.scale
    var offset by zoomState.offset
    var endPoint = Offset.Zero
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { zoomState.onTap() }
                )
                detectDragGestures(
                    onDragStart = { change ->
                        zoomState.onDragStart(change)
                    },
                    onDragEnd = {
                        zoomState.onDragEnd(endPoint)

                    },
                    onDragCancel = { zoomState.onDragCancel() },
                    onDrag = { change, dragAmount ->
                        zoomState.onDrag(change, dragAmount)
                    }
                )
            },
        contentScale = contentScale,
        alignment = alignment
    )
}

class ZoomState(
    var scale: MutableState<Float>,
    var offset: MutableState<Offset>
) {

    private var lastDragOffset = Offset.Zero

    fun onTap() {
        scale.value = 1f
        offset.value = Offset.Zero
    }

    fun onDragStart(start: Offset) {
        lastDragOffset = offset.value
    }

    fun onDragEnd(end: Offset) {
        val dragDistance = end - lastDragOffset
        offset.value += dragDistance
    }

    fun onDragCancel() {
        lastDragOffset = Offset.Zero
    }

    fun onDrag(change: PointerInputChange, dragAmount: Offset) {
        offset.value += dragAmount
    }
}
