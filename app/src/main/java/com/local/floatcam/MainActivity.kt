package com.local.floatcam

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

val PipIcon: ImageVector
    get() = ImageVector.Builder(
        name = "PiP",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(19f, 11f)
            horizontalLineTo(11f)
            verticalLineTo(17f)
            horizontalLineTo(19f)
            verticalLineTo(11f)
            close()
            moveTo(23f, 5f)
            curveTo(23f, 3.9f, 22.1f, 3f, 21f, 3f)
            horizontalLineTo(3f)
            curveTo(1.9f, 3f, 1f, 3.9f, 1f, 5f)
            verticalLineTo(19f)
            curveTo(1f, 20.1f, 1.9f, 21f, 3f, 21f)
            horizontalLineTo(21f)
            curveTo(22.1f, 21f, 23f, 20.1f, 23f, 19f)
            verticalLineTo(5f)
            close()
            moveTo(21f, 19f)
            horizontalLineTo(3f)
            verticalLineTo(5f)
            horizontalLineTo(21f)
            verticalLineTo(19f)
            close()
        }
    }.build()

val VolumeOnIcon: ImageVector
    get() = ImageVector.Builder(
        name = "VolumeOn", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(3f, 9f)
            verticalLineTo(15f)
            horizontalLineTo(7f)
            lineTo(12f, 20f)
            verticalLineTo(4f)
            lineTo(7f, 9f)
            horizontalLineTo(3f)
            close()
            moveTo(16.5f, 12f)
            curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
            verticalLineTo(16.02f)
            curveTo(15.48f, 15.29f, 16.5f, 13.77f, 16.5f, 12f)
            close()
            moveTo(14f, 3.23f)
            verticalLineTo(5.29f)
            curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
            curveTo(19f, 15.17f, 16.89f, 17.85f, 14f, 18.71f)
            verticalLineTo(20.77f)
            curveTo(18.01f, 19.86f, 21f, 16.28f, 21f, 12f)
            curveTo(21f, 7.72f, 18.01f, 4.14f, 14f, 3.23f)
            close()
        }
    }.build()

val VolumeOffIcon: ImageVector
    get() = ImageVector.Builder(
        name = "VolumeOff", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(16.5f, 12f)
            curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
            verticalLineTo(10.18f)
            lineTo(16.45f, 12.63f)
            curveTo(16.48f, 12.43f, 16.5f, 12.22f, 16.5f, 12f)
            close()
            moveTo(19f, 12f)
            curveTo(19f, 12.94f, 18.8f, 13.82f, 18.46f, 14.64f)
            lineTo(19.97f, 16.15f)
            curveTo(20.63f, 14.91f, 21f, 13.5f, 21f, 12f)
            curveTo(21f, 7.72f, 18.01f, 4.14f, 14f, 3.23f)
            verticalLineTo(5.29f)
            curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
            close()
            moveTo(4.27f, 3f)
            lineTo(3f, 4.27f)
            lineTo(7.73f, 9f)
            horizontalLineTo(3f)
            verticalLineTo(15f)
            horizontalLineTo(7f)
            lineTo(12f, 20f)
            verticalLineTo(13.27f)
            lineTo(16.25f, 17.53f)
            curveTo(15.58f, 18.05f, 14.83f, 18.46f, 14f, 18.7f)
            verticalLineTo(20.77f)
            curveTo(15.38f, 20.45f, 16.63f, 19.82f, 17.68f, 18.96f)
            lineTo(19.73f, 21f)
            lineTo(21f, 19.73f)
            lineTo(12f, 10.73f)
            lineTo(4.27f, 3f)
            close()
            moveTo(12f, 4f)
            lineTo(9.91f, 6.09f)
            lineTo(12f, 8.18f)
            verticalLineTo(4f)
            close()
        }
    }.build()

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MainActivity : ComponentActivity() {

    private var isPipMode by mutableStateOf(false)
    private var isMuted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val versionText = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            "v${pInfo.versionName}"
        } catch (e: Exception) {
            "v1.0"
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Video Area (Takes whole screen)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                        ) {
                            MJPEGVideo()
                            AudioPlayer(isMuted)
                        }

                        // Floating Controls Area (Upper Right, Hidden when in PiP)
                        if (!isPipMode) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Version text
                                Text(
                                    text = versionText,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                IconButton(
                                    onClick = { isMuted = !isMuted },
                                    modifier = Modifier.background(Color.DarkGray.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) VolumeOffIcon else VolumeOnIcon,
                                        contentDescription = if (isMuted) "Unmute" else "Mute",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { triggerPiP() },
                                    modifier = Modifier.background(Color.DarkGray.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = PipIcon,
                                        contentDescription = "Enter PiP",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { openSettings() },
                                    modifier = Modifier.background(Color.DarkGray.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MJPEGVideo() {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                    }
                    setBackgroundColor(android.graphics.Color.BLACK)
                    val html = """
                        <html>
                        <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes">
                        <style>
                        body { margin: 0; padding: 0; background-color: black; display: flex; justify-content: center; align-items: center; height: 100vh; }
                        img { max-width: 100%; max-height: 100vh; object-fit: contain; }
                        </style>
                        </head>
                        <body>
                        <img src="http://192.168.1.35:8080/video" />
                        </body>
                        </html>
                    """.trimIndent()
                    loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }
            }
        )
    }

    @Composable
    fun AudioPlayer(isMuted: Boolean) {
        val context = LocalContext.current
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri("http://192.168.1.35:8080/audio.wav")
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }

        LaunchedEffect(isMuted) {
            exoPlayer.volume = if (isMuted) 0f else 1f
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    }

    private fun openSettings() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.35:8080"))
        startActivity(intent)
    }

    private fun triggerPiP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        triggerPiP()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isPipMode = isInPictureInPictureMode
    }
}
