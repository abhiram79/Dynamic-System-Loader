package abhiram79.dsl.ui.sdialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import abhiram79.dsl.R
import abhiram79.dsl.model.DSUConstants
import abhiram79.dsl.ui.components.DialogItem
import abhiram79.dsl.ui.components.DialogLikeBottomSheet

@Composable
fun ConfirmInstallationSheet(
    filename: String,
    userdata: String,
    fileSize: Long,
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
) {
    DialogLikeBottomSheet(
        title = stringResource(id = R.string.proceed_installation),
        icon = Icons.Outlined.InstallMobile,
        text = stringResource(id = R.string.proceed_installation_description),
        content = {
            Spacer(modifier = Modifier.padding(4.dp))
            DialogItem(
                icon = Icons.Outlined.InsertDriveFile,
                title = "${stringResource(id = R.string.selected_file)}:",
                text = filename,
                textColor = MaterialTheme.colorScheme.onBackground,
            )
            DialogItem(
                icon = Icons.Outlined.Storage,
                title = "${stringResource(id = R.string.userdata_size)}:",
                text = "${userdata}GB",
                textColor = MaterialTheme.colorScheme.onBackground,
            )
            if (fileSize != DSUConstants.DEFAULT_IMAGE_SIZE) {
                DialogItem(
                    icon = Icons.Outlined.Article,
                    title = "${stringResource(id = R.string.image_size)}:",
                    text = "${fileSize}b",
                    textColor = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        confirmText = stringResource(id = R.string.proceed),
        cancelText = stringResource(id = R.string.cancel),
        onClickConfirm = { onClickConfirm() },
        onClickCancel = { onClickCancel() },
    )
}
