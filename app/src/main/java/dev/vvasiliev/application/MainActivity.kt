package dev.vvasiliev.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import dev.vvasiliev.application.core.app.MyPlayerApp
import dev.vvasiliev.application.core.config.AppConfigurator
import dev.vvasiliev.application.screen.navigation.Navigator
import dev.vvasiliev.structures.android.launcher.contract.ShareFileContract
import dev.vvasiliev.structures.android.operation.ContentDeletionLauncher
import dev.vvasiliev.structures.android.operation.ContentEditionRequestLauncher
import dev.vvasiliev.structures.android.operation.ShareContentLauncher
import dev.vvasiliev.structures.android.permission.NotificationPermissionLauncher
import dev.vvasiliev.structures.android.permission.ReadStoragePermissionLauncher
import dev.vvasiliev.structures.android.permission.WriteStoragePermission
import dev.vvasiliev.view.composable.splash.screen.SplashScreen
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    init {
        ReadStoragePermissionLauncher.create(this)
        NotificationPermissionLauncher.create(this)
        WriteStoragePermission.create(this)
        ContentEditionRequestLauncher.create(
            this,
            ActivityResultContracts.StartIntentSenderForResult()
        )
        ContentDeletionLauncher.create(this, ActivityResultContracts.StartIntentSenderForResult())
        ShareContentLauncher.create(this, ShareFileContract())
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var configurator: AppConfigurator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    //Inject This activity into dependency graph
                    MyPlayerApp.dagger.mainActivityModule.bindNavController(rememberNavController())
                        .build().injectMainActivity(this)
                    configurator.configure()
                    navigator.Navigation()
                }
            }
        }
    }

    override fun onDestroy() {
        configurator.stopMusicService()
        super.onDestroy()
    }
}

@Composable
fun Greeting() {
    SplashScreen()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting()
    }
}