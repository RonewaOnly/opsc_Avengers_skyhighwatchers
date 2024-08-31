@file:Suppress("DEPRECATION", "PackageName")

package com.example.skyhigh_prototype.Model

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor

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
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    val executor = ContextCompat.getMainExecutor(context)

    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasAudioPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ))
    }

    if (hasCameraPermission && hasAudioPermission) {
        DisposableEffect(lifecycleOwner) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                imageCapture = ImageCapture.Builder().build()
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Use case binding failed", e)
            }

            onDispose {
                cameraProvider.unbindAll()
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    PreviewView(context).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    takePhoto(imageCapture, executor, context)
                }) {
                    Text("Capture Image")
                }

                Button(onClick = {
                    if (recording != null) {
                        stopRecording(recording) { recording = null }
                    } else {
                        startRecording(videoCapture, executor, context) { newRecording ->
                            recording = newRecording
                        }
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
        Text("Camera and Audio permissions are required")
    }
}

private fun takePhoto(
    imageCapture: ImageCapture?,
    executor: Executor,
    context: android.content.Context
) {
    imageCapture?.let {
        val photoFile = createFile(context, "jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        it.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Image saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Failed to save image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

private fun startRecording(
    videoCapture: VideoCapture<Recorder>?,
    executor: Executor,
    context: android.content.Context,
    onRecordingStarted: (Recording) -> Unit
) {
    val videoFile = createFile(context, "mp4")
    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    videoCapture?.let { capture ->
        // Create a PendingRecording
        val pendingRecording = capture.output.prepareRecording(context, outputOptions)
            .apply {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }

        // Start the recording
        val recording = pendingRecording.start(executor) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    Log.d("CameraXApp", "Recording started")
                }
                is VideoRecordEvent.Finalize -> {
                    if (!recordEvent.hasError()) {
                        Toast.makeText(context, "Video saved: ${videoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Video capture failed: ${recordEvent.cause?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Call the callback with the new recording
        onRecordingStarted(recording)
    }
}
private fun stopRecording(recording: Recording?, onRecordingStopped: () -> Unit) {
    recording?.stop()
    onRecordingStopped()
}

fun createFile(context: android.content.Context, extension: String): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, "CameraApp").apply { mkdirs() }
    }
    return File(mediaDir, "${System.currentTimeMillis()}.$extension")
}

@Composable
fun GalleryScreen(navController: NavHostController) {
    val context = LocalContext.current
    var mediaFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val files = context.externalMediaDirs.firstOrNull()
                ?.listFiles { file -> file.isFile && (file.extension == "jpg" || file.extension == "mp4") }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
            mediaFiles = files
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxWidth().height(2500.dp)) {
            items(mediaFiles) { file ->
                GalleryItem(file, navController) {
                    coroutineScope.launch(Dispatchers.IO) {
                        file.delete()
                        mediaFiles = mediaFiles.filter { it != file }
                    }
                    Toast.makeText(context, "Deleted: ${file.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Button(
            onClick = { navController.navigate("camera") },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Back to Camera")
        }
    }
}

@Composable
fun GalleryItem(file: File, navController: NavHostController, onDelete: () -> Unit) {
    val context = LocalContext.current
    var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(file) {
        thumbnailBitmap = if (file.extension == "mp4") {
            generateVideoThumbnail(context, file)
        } else {
            null
        }
    }

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
                painter = rememberImagePainter(file),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        } else if (file.extension == "mp4") {
            thumbnailBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            } ?: Image(
                painter = painterResource(id = android.R.drawable.ic_media_play),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(file.name)
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}

suspend fun generateVideoThumbnail(context: android.content.Context, videoFile: File): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoFile.path)
            val thumbnail = retriever.getFrameAtTime(0)
            retriever.release()
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
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer as com.google.android.exoplayer2.Player
                    useController = true
                }
            },
            modifier = Modifier.weight(1f)
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
fun ImageDetailScreen(imageUri: Uri, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.weight(1f)
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
    val scale = remember { mutableFloatStateOf(1f) }
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
    val scale by zoomState.scale
    val offset by zoomState.offset
    val endPoint = Offset.Zero
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
