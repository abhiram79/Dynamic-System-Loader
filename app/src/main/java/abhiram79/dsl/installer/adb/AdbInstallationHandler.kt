package abhiram79.dsl.installer.adb

import abhiram79.dsl.core.StorageManager
import abhiram79.dsl.model.Session

/**
 * Generate shell script with installation
 * Used only for installing over adb commands
 */
class AdbInstallationHandler(
    private val storageManager: StorageManager,
    val session: Session,
) {
    fun generate(onGenerated: (String) -> Unit) {
        val installationScriptPath = GenerateInstallationScript(
            storageManager,
            session.getInstallationParameters(),
            session.preferences,
        ).writeToFile()
        onGenerated(installationScriptPath)
    }
}
