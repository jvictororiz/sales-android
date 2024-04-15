package br.com.joaov.sales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.com.joaov.designsystem.theme.SalesAndroidTheme
import br.com.joaov.home.navigation.addHomeFeatureGraph
import br.com.joaov.navigation.Feature
import br.com.joaov.navigation.FeatureNavigation
import br.com.joaov.order.navigation.addOrderFeatureGraph
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()
            SalesAndroidTheme {
                val navigation = get<FeatureNavigation>()
                LaunchedEffect("navigation") {
                    navigation.sharedFlow.onEach { feature ->
                        navController.navigate(feature.route) {
                            popUpTo(feature.route)
                        }
                    }.launchIn(this)
                }

                NavHost(
                    navController = navController,
                    startDestination = Feature.Home().route
                ) {
                    addHomeFeatureGraph { navController.popBackStack() }
                    addOrderFeatureGraph { navController.popBackStack() }
                }
            }
        }
    }
}