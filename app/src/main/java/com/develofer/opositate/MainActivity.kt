package com.develofer.opositate

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.develofer.opositate.ui.theme.OpositateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
//            setKeepOnScreenCondition {
//                !viewModel.isReady.value
//            }
            if (Build.VERSION.SDK_INT >= 34) {
                setOnExitAnimationListener { screen ->
                    val zoomX = ObjectAnimator.ofFloat(
                        screen.iconView,
                        View.SCALE_X,
                        1.0f,
                        0.0f
                    )
                    zoomX.interpolator = OvershootInterpolator()
                    zoomX.duration = 500L
                    zoomX.doOnEnd { screen.remove() }

                    val zoomY = ObjectAnimator.ofFloat(
                        screen.iconView,
                        View.SCALE_Y,
                        1.0f,
                        0.0f
                    )
                    zoomY.interpolator = OvershootInterpolator()
                    zoomY.duration = 500L
                    zoomY.doOnEnd { screen.remove() }

                    zoomX.start()
                    zoomY.start()
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            OpositateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OpositateTheme {
        Greeting("Android")
    }
}