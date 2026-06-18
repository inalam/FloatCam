package com.local.floatcam

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

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
                    color = Color.Black // AMOLED Black background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Video Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Black)
                        ) {
                            MJPEGVideo()
                            AudioPlayer()
                        }

                        // Controls Area (Hidden when in PiP)
                        if (!isPipMode) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { openSettings() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                ) {
                                    Text("Settings", color = Color.White)
                                }

                                Button(
                                    onClick = { triggerPiP() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                                ) {
                                    Text("Enter PiP", color = Color.White)
                                }
                                
                                Text(
                                    text = versionText,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
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
                    }
                    setBackgroundColor(android.graphics.Color.BLACK)
                    val html = """
                        <html>
                        <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <style>
                        body { margin: 0; padding: 0; background-color: black; display: flex; justify-content: center; align-items: center; height: 100vh; overflow: hidden; }
                        img { width: 100%; height: 100%; object-fit: contain; }
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
