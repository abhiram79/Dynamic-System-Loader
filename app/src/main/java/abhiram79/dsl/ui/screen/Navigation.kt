package abhiram79.dsl.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import abhiram79.dsl.ui.screen.about.AboutScreen
import abhiram79.dsl.ui.screen.adb.AdbScreen
import abhiram79.dsl.ui.screen.home.Home
import abhiram79.dsl.ui.screen.libraries.LibrariesScreen
import abhiram79.dsl.ui.screen.settings.Settings

object Destinations {
    const val Homepage = "home"
    const val Preferences = "preferences"
    const val ADBInstallation = "adb_installation"
    const val About = "about"
    const val Libraries = "libraries"
    const val Up = "up"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.Homepage) {
        fun navigate(destination: String) {
            if (destination == Destinations.Up) {
                navController.navigateUp()
            } else {
                navController.navigate(destination)
            }
        }

        composable(Destinations.Homepage) { Home(navigate = { navigate(it) }) }
        composable(Destinations.Preferences) { Settings(navigate = { navigate(it) }) }
        composable(Destinations.ADBInstallation) { AdbScreen(navigate = { navigate(it) }) }
        composable(Destinations.About) { AboutScreen(navigate = { navigate(it) }) }
        composable(Destinations.Libraries) { LibrariesScreen(navigate = { navigate(it) }) }
    }
}
