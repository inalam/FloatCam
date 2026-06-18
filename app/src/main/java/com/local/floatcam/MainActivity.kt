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

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MainActivity : ComponentActivity() {

    private var isPipMode by mutableStateOf(false)

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
                            AudioPlayer()
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
    fun AudioPlayer() {
        val context = LocalContext.current
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri("http://192.168.1.35:8080/audio.wav")
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
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
