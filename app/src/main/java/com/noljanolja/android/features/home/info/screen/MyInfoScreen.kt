package com.noljanolja.android.features.home.info.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.d2brothers.firebase_auth.model.displayIdentity
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.CommonTopAppBar
import com.noljanolja.android.common.composable.FullSizeLoading
import com.noljanolja.android.common.composable.ListTile

@Composable
fun MyInfoScreen(
    viewModel: MyInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()
    MyInfoContent(
        uiState,
        handleEvent = {
            viewModel.handleEvent(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyInfoContent(
    uiState: MyInfoUIState,
    handleEvent: (MyInfoEvent) -> Unit,
) {
    FullSizeLoading(
        uiState is MyInfoUIState.Loading,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CommonTopAppBar(
                    title = stringResource(id = R.string.my_info),
                    onBack = {
                        handleEvent(MyInfoEvent.Back)
                    },
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = (uiState as? MyInfoUIState.Loaded)?.user.displayIdentity(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground),
                )
                MyInfoItem(
                    title = stringResource(id = R.string.edit_member_infomation),
                    trailingDrawable = R.drawable.ic_forward,
                    onClick = {},
                )
                MyInfoItem(
                    title = stringResource(id = R.string.change_password),
                    trailingDrawable = R.drawable.ic_forward,
                    onClick = {},
                )
                MyInfoItem(
                    title = stringResource(id = R.string.exchange_account_management),
                    trailingDrawable = R.drawable.ic_forward,
                    onClick = {},
                )
                MyInfoItem(
                    title = stringResource(id = R.string.common_setting),
                    trailingDrawable = R.drawable.ic_forward,
                    onClick = {},
                )
                MyInfoItem(
                    title = stringResource(id = R.string.common_log_out),
                    onClick = {
                        handleEvent(MyInfoEvent.Logout)
                    },
                )
                ListTile(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .padding(top = 18.dp),
                    title = {
                        Text(
                            text = stringResource(id = R.string.common_withdrwal),
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            ),
                        )
                    },
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun MyInfoItem(
    title: String,
    @DrawableRes trailingDrawable: Int? = null,
    onClick: () -> Unit,
) {
    ListTile(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .padding(top = 18.dp),
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline,
                ),
            )
        },
        trailingDrawable = trailingDrawable,
        onClick = onClick,
    )
}