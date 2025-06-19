package com.bera.josaahelpertool

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bera.josaahelpertool.navigation.Navigation
import com.bera.josaahelpertool.network.connectivity.ConnectivityObserver
import com.bera.josaahelpertool.network.connectivity.ConnectivityStatus
import com.bera.josaahelpertool.screens.home.NetworkErrorScreen
import com.bera.josaahelpertool.ui.theme.CollegeSearchTheme
import com.bera.josaahelpertool.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var connectivityObserver: ConnectivityObserver
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val darkTheme = themeViewModel.shouldUseDarkTheme()
            val uiState by themeViewModel.uiState.collectAsState()

            CollegeSearchTheme(
                darkTheme = darkTheme,
                dynamicColor = uiState.dynamicColor
            ) {

                val status by connectivityObserver.observe()
                    .collectAsState(initial = ConnectivityStatus.Unavailable)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when(status) {
                        ConnectivityStatus.Available -> Navigation()
                        else -> {
                            val cacheDir: File = applicationContext.cacheDir
                            if (cacheDir.exists()) {
                                val httpCacheDir = File(cacheDir, "http-cache")
                                if (httpCacheDir.exists()) {
                                    Navigation()
                                } else {
                                    NetworkErrorScreen(isVisible = true)
                                }
                            } else {
                                NetworkErrorScreen(isVisible = true)
                            }
                        }
                    }
                }
            }
        }
    }
}
fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}