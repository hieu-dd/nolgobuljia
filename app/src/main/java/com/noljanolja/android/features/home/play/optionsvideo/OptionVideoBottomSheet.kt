package com.noljanolja.android.features.home.play.optionsvideo

import android.view.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.lifecycle.compose.*
import coil.compose.*
import coil.request.*
import com.noljanolja.android.R
import com.noljanolja.android.common.*
import com.noljanolja.android.features.common.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.*
import com.noljanolja.core.video.domain.model.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.compose.*

@Composable
fun OptionVideoBottomBottomSheet(
    visible: Boolean = false,
    video: Video,
    onDismissRequest: () -> Unit,
    videoViewModel: OptionsVideoViewModel = getViewModel(),
    optionsVideoViewModel: OptionsVideoViewModel = getViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(videoViewModel.shareSuccessEvent) {
        videoViewModel.shareSuccessEvent.collectLatest {
            context.showToast(it ?: context.getString(R.string.common_share_success))
            onDismissRequest()
        }
    }
    LaunchedEffect(videoViewModel.errorFlow) {
        videoViewModel.errorFlow.collectLatest {
            context.showError(it)
        }
    }
    val contacts by videoViewModel.contactsFlow.collectAsStateWithLifecycle()
    val showConfirmDialog by optionsVideoViewModel.showConfirmDialog.collectAsStateWithLifecycle()
    var selectContact by remember {
        mutableStateOf<ShareContact?>(null)
    }
    var isSelectConversation by remember {
        mutableStateOf(false)
    }
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
            dialogWindowProvider.window.setGravity(Gravity.BOTTOM)
            dialogWindowProvider.window.setBackgroundDrawableResource(R.color.black30)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismissRequest() },
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = false) { }
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .height(IntrinsicSize.Min)
                            .background(MaterialTheme.colorScheme.background)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val (x, y) = dragAmount
                                    if (y > 50) onDismissRequest()

                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                            .padding(top = 10.dp, bottom = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when {
                            selectContact != null -> {
                                ShareVideoContent(
                                    shareContact = selectContact,
                                    video = video,
                                    onShareVideo = { video, contact ->
                                        videoViewModel.handleEvent(
                                            contact?.let {
                                                OptionsVideoEvent.ShareVideo(
                                                    video,
                                                    contact
                                                )
                                            }
                                        )
                                    }
                                )
                            }

                            isSelectConversation -> {
                                SelectConversation(
                                    contacts = contacts,
                                    onSelectContact = {
                                        selectContact = it
                                    },
                                    onShareClick = {
                                        optionsVideoViewModel.changeDialogState(it)
                                    }
                                )
                            }

                            else -> {
                                OptionsContent(
                                    onShare = {
                                        when (it) {
                                            R.string.common_share -> {
                                                isSelectConversation = true
                                            }

                                            R.string.common_copy -> {
                                                context.run {
                                                    copyToClipboard(video.url)
                                                    videoViewModel.copySuccess(getString(R.string.common_copy_success))
                                                }
                                            }

                                            R.string.ignore_video -> {

                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    showConfirmDialog?.run {
        ConfirmDialog(
            appName = stringResource(
                id = R.string.common_open_app,
                appName
            ),
            onConfirmClick = {
                context.shareToAnotherApp(
                    videoUrl = video.url,
                    shareToAppData = this
                )
                optionsVideoViewModel.changeDialogState(null)
            }
        ) {
            optionsVideoViewModel.changeDialogState(null)
        }
    }
}

@Composable
private fun ShareVideoContent(
    shareContact: ShareContact?,
    video: Video,
    onShareVideo: (Video, ShareContact?) -> Unit,
) {
    val context = LocalContext.current
    SizeBox(height = 24.dp)
    Text(
        "Send to",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    SizeBox(height = 10.dp)
    OvalAvatar(avatar = shareContact?.avatar)
    SizeBox(height = 10.dp)
    Text(
        shareContact?.title ?: "",
        maxLines = 2,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground
    )
    SizeBox(height = 10.dp)
    SubcomposeAsyncImage(
        ImageRequest.Builder(context = LocalContext.current)
            .data(video.thumbnail)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(Const.VIDEO_IMAGE_RATIO)
    )
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        SizeBox(height = 2.dp)
        Text(
            text = video.category.title,
            style = MaterialTheme.typography.labelMedium,
            color = Blue00
        )
        SizeBox(height = 2.dp)
        Text(
            text = video.getShortDescription(context),
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.secondaryTextColor()
            ),
        )
        SizeBox(height = 44.dp)
        PrimaryButton(text = stringResource(id = R.string.common_share).uppercase()) {
            onShareVideo(video, shareContact)
        }

        SizeBox(height = 24.dp)
    }
}

@Composable
private fun SelectConversation(
    contacts: List<ShareContact>,
    onSelectContact: (ShareContact) -> Unit,
    onShareClick: (String) -> Unit
) {
    SizeBox(height = 10.dp)
    Text(
        stringResource(id = R.string.common_share),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    SizeBox(height = 20.dp)
    if (contacts.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            items(contacts.size) {
                val contact = contacts[it]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(56.dp)
                        .clickable {
                            onSelectContact(contact)
                        }
                ) {
                    OvalAvatar(avatar = contact.avatar, size = 40.dp)
                    SizeBox(height = 10.dp)
                    Text(
                        contact.title,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
    Divider()
    SizeBox(height = 20.dp)
    LazyRow(
        modifier = Modifier
            .padding(12.dp)
            .height(48.dp)
    ) {
        item {
            Image(
                ImageVector.vectorResource(R.drawable.facebook),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        onShareClick(AppNameShareToApp.FACEBOOK)
                    }
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.twiter),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        onShareClick(AppNameShareToApp.TWITTER)
                    }
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.ic_whats_app),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        onShareClick(AppNameShareToApp.WHATS_APP)
                    }
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.telegram),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        onShareClick(AppNameShareToApp.TELEGRAM)
                    }
            )
            SizeBox(width = 30.dp)
        }
        item {
            Image(
                ImageVector.vectorResource(R.drawable.ic_messenger),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        onShareClick(AppNameShareToApp.MESSENGER)
                    }
            )
            SizeBox(width = 30.dp)
        }
    }
    SizeBox(height = 30.dp)
}

@Composable
private fun OptionsContent(
    onShare: (Int) -> Unit,
) {
    OptionVideoRow(
        icon = ImageVector.vectorResource(id = R.drawable.ic_share),
        text = stringResource(R.string.common_share),
        onClick = {
            onShare(R.string.common_share)
        }
    )
    OptionVideoRow(
        icon = Icons.Default.Link,
        text = stringResource(R.string.common_copy),
        onClick = {
            onShare(R.string.common_copy)
        }
    )
//    OptionVideoRow(
//        icon = Icons.Default.Block,
//        text = stringResource(R.string.ignore_video),
//        onClick = {
//            onShare(R.string.ignore_video)
//        }
//    )
}

@Composable
private fun OptionVideoRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        SizeBox(width = 5.dp)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ConfirmDialog(
    appName: String,
    onConfirmClick: () -> Unit,
    onNegativeClick: () -> Unit
) {
    androidx.compose.material.AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DefaultValue.ROUND_DIALOG.dp))
            .background(Color.White),
        title = {
            androidx.compose.material.Text(
                text = appName,
                style = Typography.titleMedium
            )
        },
        text = null,
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultValue.ROUND_DIALOG.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(R.string.common_no).uppercase(),
                    style = Typography.titleSmall,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(10.dp)
                        .clickable {
                            onNegativeClick()
                        },
                    color = MaterialTheme.primaryColor(),
                    textAlign = TextAlign.Center
                )
                MarginHorizontal(DefaultValue.PADDING_HORIZONTAL_SCREEN)
                Text(
                    text = stringResource(R.string.common_yes).uppercase(),
                    style = Typography.titleSmall,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(10.dp)
                        .clickable {
                            onConfirmClick()
                        },
                    color = MaterialTheme.primaryColor(),
                    textAlign = TextAlign.Center
                )
            }
        },
        properties = DialogProperties(),
        backgroundColor = Color.White,
        onDismissRequest = onNegativeClick
    )
}
