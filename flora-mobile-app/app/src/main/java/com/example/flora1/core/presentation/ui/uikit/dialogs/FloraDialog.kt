package com.example.flora1.core.presentation.ui.uikit.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.flora1.R
import com.example.flora1.core.presentation.ui.uikit.buttons.PrimaryButton

@Composable
fun FloraDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    desc: String? = null,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    headerImage: (@Composable BoxScope.() -> Unit)? = null,
    @StringRes testTag : Int = R.string.flora_dialog_test_tag,
) {
    FloraDialog(
        modifier = modifier,
        title = title,
        desc = buildAnnotatedString { append(desc) },
        onAccept = onAccept,
        onDismiss = onDismiss,
        headerImage = headerImage,
        testTag = testTag,
    )
}

@Composable
fun FloraDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    desc: AnnotatedString? = null,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
    headerImage: (@Composable BoxScope.() -> Unit)? = null,
    @StringRes testTag : Int = R.string.flora_dialog_test_tag,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = dismissOnClickOutside,
            dismissOnBackPress = dismissOnBackPress,
        )
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = 15.dp)
        ) {
            Column(
                modifier = modifier.testTag(stringResource(id = testTag)),
            ) {
                Spacer(modifier = Modifier.height(130.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(24.dp))
                        title?.let {
                            Text(
                                text = it,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth(),
                                fontFamily = FontFamily(Font(R.font.raleway_bold)),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        desc?.let {
                            Text(
                                text = it,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }


                        PrimaryButton(
                            text = "Yes",
                            onClick = onAccept,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        PrimaryButton(
                            text = "No",
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5.dp))
                        )


                    }
                }
            }
            headerImage?.invoke(this)
        }
    }
}
