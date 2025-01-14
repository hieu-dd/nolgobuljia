package com.noljanolja.android.features.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.withBold
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SettingContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingContent(
    uiState: SettingUIState,
    handleEvent: (SettingEvent) -> Unit,
) {
    val user = uiState.user
    var isShowLogoutDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.common_setting),
                onBack = {
                    handleEvent(SettingEvent.Back)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PrimaryListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp)
                    .padding(top = 18.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_exchange_account_management),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                trailingDrawable = R.drawable.ic_forward,
            )
            CommonListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_name),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                trailing = {
                    Row {
                        Text(
                            user?.name.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge.withBold()
                        )
                        SizeBox(width = 24.dp)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = null
                        )
                    }
                }
            )
            CommonListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_phone_number),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                trailing = {
                    Text(
                        user?.phone?.hidePhoneNumber().orEmpty(),
                        style = MaterialTheme.typography.bodyMedium.withBold()
                    )
                }
            )
            Divider(modifier = Modifier.padding(top = 10.dp, bottom = 20.dp))
            ListTileWithToggleButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_push_notification),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                checked = uiState.allowPushNotification,
                onCheckedChange = {
                    handleEvent(SettingEvent.TogglePushNotification)
                },
            )
            Divider(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
            PrimaryListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_clear_cache_data),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                onClick = { handleEvent(SettingEvent.ClearCacheData) },
            )
            PrimaryListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_open_source_licence),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                onClick = {
                    handleEvent(SettingEvent.Licence)
                }
            )
            Divider(modifier = Modifier.padding(bottom = 10.dp))
            PrimaryListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_faq),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                onClick = {
                    handleEvent(SettingEvent.FAQ)
                }
            )
            PrimaryListTile(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.setting_current_version,
                            BuildConfig.VERSION_NAME
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
            )
            SizeBox(height = 30.dp)
            Expanded()
            PrimaryButton(
                text = stringResource(id = R.string.common_log_out).uppercase(),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    isShowLogoutDialog = true
                }
            )
            SizeBox(height = 24.dp)
        }
    }

    if (isShowLogoutDialog) {
        WarningDialog(
            title = null,
            content = stringResource(id = R.string.ask_to_logout),
            dismissText = stringResource(R.string.common_cancel),
            confirmText = stringResource(R.string.common_yes),
            isWarning = isShowLogoutDialog,
            onDismiss = {
                isShowLogoutDialog = false
            },
            onConfirm = {
                isShowLogoutDialog = false
                handleEvent(SettingEvent.Logout)
            }
        )
    }
}

private fun String.hidePhoneNumber(): String {
    return (0..this.length - 3).joinToString("") { "*" } + this.takeLast(3)
}