package abhiram79.dsl.ui.sdialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import abhiram79.dsl.R
import abhiram79.dsl.ui.components.DialogLikeBottomSheet

@Composable
fun CancelSheet(
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {
    DialogLikeBottomSheet(
        title = stringResource(id = R.string.cancel_installation_question),
        icon = Icons.Outlined.Cancel,
        text = stringResource(id = R.string.cancel_installation_description),
        confirmText = stringResource(id = R.string.yes),
        cancelText = stringResource(id = R.string.no),
        onClickConfirm = onClickConfirm,
        onClickCancel = onClickCancel,
    )
}
