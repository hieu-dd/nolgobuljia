package com.noljanolja.android

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.ui.theme.NoljanoljaTheme
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: NavigationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO : remove after
        Log.e("HASHHHH", printKeyHash(this).orEmpty())

        setContent {
            NoljanoljaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(navigationManager)
                }
            }
        }
    }
}

fun printKeyHash(context: Activity): String? {
    val packageInfo: PackageInfo
    var key: String? = null
    try {
        // getting application package name, as defined in manifest
        val packageName = context.applicationContext.packageName

        // Retriving package info
        packageInfo = context.packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNATURES
        )
        for (signature in packageInfo.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            key = String(Base64.encode(md.digest(), 0))

            // String key = new String(Base64.encodeBytes(md.digest()));
        }
    } catch (e: Exception) {
        Log.e("Exception", e.toString())
    }
    return key
}
