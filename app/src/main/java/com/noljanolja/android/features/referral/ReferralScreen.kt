package com.noljanolja.android.features.referral

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.secondaryTextColor
import com.noljanolja.android.util.shareText
import com.noljanolja.android.util.showToast
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.getViewModel

@Composable
fun ReferralScreen(
    viewModel: ReferralViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ReferralContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun ReferralContent(
    uiState: UiState<User>,
    handleEvent: (ReferralEvent) -> Unit,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val code = uiState.data?.referralCode.orEmpty()
    ScaffoldWithUiState(
        uiState = uiState,
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.join_play),
                onBack = {
                    handleEvent(ReferralEvent.Back)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.benefits_bg),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 36.dp)
                )
                Row(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        code,
                        style = MaterialTheme.typography.bodyLarge.withBold(),
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 30.dp).fillMaxHeight()
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Text(
                        stringResource(id = R.string.common_copy),
                        style = MaterialTheme.typography.bodySmall.withBold(),
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(code))
                                context.showToast(context.getString(R.string.common_copy_success))
                            }
                            .padding(horizontal = 22.dp).fillMaxHeight()
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            PrimaryButton(
                text = stringResource(id = R.string.send_invite_link),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp).height(52.dp)
            ) {
                context.shareText(code)
            }
            Text(
                stringResource(id = R.string.how_to_refer),
                style = MaterialTheme.typography.labelLarge.withMedium(),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            SizeBox(height = 18.dp)
            Row() {
                ReferralDirectionItem(
                    image = R.drawable.referral1,
                    step = "01",
                    description = stringResource(id = R.string.referral_step_1),
                )
                ReferralDirectionItem(
                    image = R.drawable.referral2,
                    step = "02",
                    description = stringResource(id = R.string.referral_step_2)
                )
            }
            SizeBox(height = 27.dp)
            Row() {
                ReferralDirectionItem(
                    image = R.drawable.referral3,
                    step = "03",
                    description = stringResource(id = R.string.referral_step_3)
                )
                ReferralDirectionItem(
                    image = R.drawable.referral4,
                    step = "04",
                    description = stringResource(id = R.string.referral_step_4)
                )
            }
            Text(
                stringResource(id = R.string.referral_description),
                modifier = Modifier.padding(vertical = 25.dp, horizontal = 20.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.secondaryTextColor()
            )
        }
    }
}

@Composable
private fun RowScope.ReferralDirectionItem(
    @DrawableRes image: Int,
    step: String,
    description: String,
) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(98.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            step,
            style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 20.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.secondaryTextColor(),
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = TextAlign.Center
        )
    }
}