package abhiram79.dsl.ui.screen.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.system.exitProcess
import kotlinx.coroutines.flow.collectLatest
import abhiram79.dsl.R
import abhiram79.dsl.ui.cards.DsuInfoCard
import abhiram79.dsl.ui.cards.ImageSizeCard
import abhiram79.dsl.ui.cards.UserdataCard
import abhiram79.dsl.ui.cards.installation.InstallationCard
import abhiram79.dsl.ui.cards.warnings.GrantingPermissionCard
import abhiram79.dsl.ui.cards.warnings.RequiresLogPermissionCard
import abhiram79.dsl.ui.cards.warnings.SetupStorage
import abhiram79.dsl.ui.cards.warnings.StorageWarningCard
import abhiram79.dsl.ui.cards.warnings.UnlockedBootloaderCard
import abhiram79.dsl.ui.cards.warnings.UnsupportedCard
import abhiram79.dsl.ui.components.ApplicationScreen
import abhiram79.dsl.ui.components.TopBar
import abhiram79.dsl.ui.screen.Destinations
import abhiram79.dsl.ui.sdialogs.CancelSheet
import abhiram79.dsl.ui.sdialogs.ConfirmInstallationSheet
import abhiram79.dsl.ui.sdialogs.DiscardDSUSheet
import abhiram79.dsl.ui.sdialogs.ImageSizeWarningSheet
import abhiram79.dsl.ui.sdialogs.ViewLogsBottomSheet
import abhiram79.dsl.ui.util.KeepScreenOn
import abhiram79.dsl.util.collectAsStateWithLifecycle

object HomeLinks {
    const val DSU_LEARN_MORE = "https://developer.android.com/topic/dsu"
    const val DSU_DOCS = "https://source.android.com/devices/tech/ota/dynamic-system-updates"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    if (uiState.shouldKeepScreenOn) {
        KeepScreenOn()
    }

    LaunchedEffect(Unit) {
        homeViewModel.setupUserPreferences()
        homeViewModel.session.operationMode.collectLatest {
            homeViewModel.initialChecks()
        }
    }

    ApplicationScreen(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.app_name),
                icon = Icons.Outlined.Settings,
                scrollBehavior = it,
                onClickIcon = { navigate(Destinations.Preferences) },
            )
        },
        content = {
            Box(modifier = Modifier.animateContentSize()) {
                when (uiState.additionalCard) {
                    AdditionalCardState.NO_DYNAMIC_PARTITIONS ->
                        UnsupportedCard(
                            onClickClose = { exitProcess(0) },
                            onClickContinueAnyway = { homeViewModel.overrideDynamicPartitionCheck() },
                        )

                    AdditionalCardState.SETUP_STORAGE ->
                        SetupStorage { homeViewModel.takeUriPermission(it) }

                    AdditionalCardState.UNAVAIABLE_STORAGE ->
                        StorageWarningCard(
                            minPercentageFreeStorage = homeViewModel.allocPercentageInt.toString(),
                            onClick = { homeViewModel.overrideUnavaiableStorage() },
                        )

                    AdditionalCardState.MISSING_READ_LOGS_PERMISSION ->
                        RequiresLogPermissionCard(
                            onClickGrant = { homeViewModel.grantReadLogs() },
                            onClickRefuse = { homeViewModel.refuseReadLogs() },
                        )

                    AdditionalCardState.GRANTING_READ_LOGS_PERMISSION ->
                        GrantingPermissionCard()

                    AdditionalCardState.BOOTLOADER_UNLOCKED_WARNING ->
                        UnlockedBootloaderCard { homeViewModel.onClickBootloaderUnlockedWarning() }

                    AdditionalCardState.NONE -> {}
                }
            }
            if (uiState.passedInitialChecks && uiState.additionalCard == AdditionalCardState.NONE) {
                InstallationCard(
                    uiState = uiState.installationCard,
                    onClickInstall = { homeViewModel.onClickInstall() },
                    onClickUnmountSdCardAndRetry = { homeViewModel.onClickUnmountSdCardAndRetry() },
                    onClickSetSeLinuxPermissive = { homeViewModel.onClickSetSeLinuxPermissive() },
                    onClickRetryInstallation = { homeViewModel.onClickRetryInstallation() },
                    onClickClear = { homeViewModel.resetInstallationCard() },
                    onSelectFileSuccess = { homeViewModel.onFileSelectionResult(it) },
                    onClickCancelInstallation = { homeViewModel.onClickCancel() },
                    onClickDiscardInstalledGsiAndInstall = { homeViewModel.onClickDiscardGsiAndStartInstallation() },
                    onClickDiscardDsu = { homeViewModel.showDiscardSheet() },
                    onClickRebootToDynOS = { homeViewModel.onClickRebootToDynOS() },
                    onClickViewLogs = { homeViewModel.showLogsWarning() },
                    onClickViewCommands = { navigate(Destinations.ADBInstallation) },
                    minPercentageOfFreeStorage = homeViewModel.allocPercentageInt.toString(),
                )
                UserdataCard(
                    isEnabled = uiState.isInstalling(),
                    uiState = uiState.userDataCard,
                    onCheckedChange = { homeViewModel.onCheckUserdataCard() },
                    onValueChange = { homeViewModel.updateUserdataSize(it) },
                )
                ImageSizeCard(
                    isEnabled = uiState.isInstalling(),
                    uiState = uiState.imageSizeCard,
                    onCheckedChange = { homeViewModel.onCheckImageSizeCard() },
                    onValueChange = { homeViewModel.updateImageSize(it) },
                )
                DsuInfoCard(
                    onClickViewDocs = { uriHandler.openUri(HomeLinks.DSU_DOCS) },
                    onClickLearnMore = { uriHandler.openUri(HomeLinks.DSU_LEARN_MORE) },
                )
            }
        },
    )

    when (uiState.sheetDisplay) {
        SheetDisplayState.CONFIRM_INSTALLATION ->
            ConfirmInstallationSheet(
                filename = homeViewModel.obtainSelectedFilename(),
                userdata = homeViewModel.session.userSelection.getUserDataSizeAsGB(),
                fileSize = homeViewModel.session.userSelection.userSelectedImageSize,
                onClickConfirm = { homeViewModel.onConfirmInstallationSheet() },
                onClickCancel = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.CANCEL_INSTALLATION ->
            CancelSheet(
                onClickConfirm = { homeViewModel.onClickCancelInstallationButton() },
                onClickCancel = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.IMAGESIZE_WARNING ->
            ImageSizeWarningSheet(
                onClickConfirm = { homeViewModel.dismissSheet() },
                onClickCancel = { homeViewModel.onCheckImageSizeCard() },
            )

        SheetDisplayState.DISCARD_DSU ->
            DiscardDSUSheet(
                onClickConfirm = { homeViewModel.onClickDiscardGsi() },
                onClickCancel = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.VIEW_LOGS ->
            ViewLogsBottomSheet(
                logs = uiState.installationLogs,
                onClickSaveLogs = { homeViewModel.saveLogs(it) },
                onDismiss = { homeViewModel.dismissSheet() },
            )

        SheetDisplayState.NONE -> {}
    }
}
