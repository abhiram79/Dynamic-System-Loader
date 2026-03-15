package abhiram79.dsl.ui.screen.adb

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import abhiram79.dsl.model.Session

@HiltViewModel
class AdbViewModel @Inject constructor(
    private val session: Session,
) : ViewModel() {

    fun obtainScriptPath(): String = session.installationScriptPath
}
