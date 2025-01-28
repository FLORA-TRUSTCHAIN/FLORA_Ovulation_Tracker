package com.example.flora1.features.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import com.example.flora1.core.presentation.designsystem.getPrimaryHorizontalBrush
import com.example.flora1.core.presentation.ui.observers.ObserveAsEvents
import com.example.flora1.core.presentation.ui.toast.showSingleToast
import com.example.flora1.core.presentation.ui.uikit.buttons.CloseButton
import com.example.flora1.features.profile.consent.ProfileEvent

@Composable
fun ProfileRoot(
    onBack: () -> Unit,
    onNavigateToManageConsent: () -> Unit,
    onNavigateToMyDoctors: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    ObserveAsEvents(flow = viewModel.events) {
        when (it) {
            ProfileEvent.NavigateBack -> onBack()
            ProfileEvent.NavigateToManageConsent -> onNavigateToManageConsent()
            ProfileEvent.NavigateToMyDoctors -> onNavigateToMyDoctors()
            is ProfileEvent.ShowMessage -> context.showSingleToast(it.message)
        }
    }

    ProfileRoot(
        state = state,
        onAction = viewModel::onAction,
    )

}

@Composable
fun ProfileRoot(
    state: ProfileState = ProfileState(),
    onAction: (ProfileAction) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .statusBarsPadding()
    ) {
        CloseButton(
            modifier = Modifier.align(Alignment.End),
            onClick = { onAction(ProfileAction.OnBackClicked) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.profile),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        state.username?.let { username ->
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.welcome_christos, username),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        PrimaryInfoRow(
            primaryText = stringResource(R.string.language),
            secondaryText = "Choose your preferred language",
            leadingIconRes = R.drawable.ic_language
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryInfoRow(
            primaryText = "Dark Mode",
            secondaryText = "Tap to toggle",
            leadingIconRes = R.drawable.ic_dark_mode,
        )

        Spacer(modifier = Modifier.height(20.dp))


        PrimaryInfoRowWithSwitch(
            primaryText = "Enable DidRoom",
            secondaryText = "Enabling this option triggers machine learning using DidRoom instead.",
            leadingIconRes = R.drawable.ic_didroom,
            enabled = state.isDidRoomEnabled,
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryInfoRow(
            primaryText = "Manage Data Consent",
            leadingIconRes = null,
            trailingIconRes = R.drawable.ic_onboarding_next_arrow,
            onClick = { onAction(ProfileAction.OnManageConsentClicked) },
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryInfoRow(
            primaryText = "My Doctors",
            leadingIconRes = R.drawable.ic_doctor,
            trailingIconRes = R.drawable.ic_onboarding_next_arrow,
            onClick = { onAction(ProfileAction.OnMyDoctorsClicked) },
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoRow(enabled = false) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                OnBoardingItem(text = "Age")
                OnBoardingItem(text = "Pregnancy Stats")
                OnBoardingItem(text = "Race")
                OnBoardingItem(text = "Weight")
                OnBoardingItem(text = "Vitamins")
                OnBoardingItem(text = "Contraceptives")
                OnBoardingItem(text = "Average Cycle")
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        PrimaryInfoButton(
            brush = getPrimaryHorizontalBrush(),
            text = if (state.isLoggedIn) "Login" else "Logout",
            onClick = {},
            color = MaterialTheme.colorScheme.onPrimary,
        )

        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))

    }
}

@Composable
fun PrimaryInfoRowWithSwitch(
    onClick: () -> Unit = {},
    brush: Brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
        )
    ),
    enabled: Boolean = true,
    primaryText: String = "Dark Mode",
    secondaryText: String? = null,
    @DrawableRes leadingIconRes: Int? = R.drawable.eye_closed,
    checked: Boolean = false,
    onCheckedChanged: ((Boolean) -> Unit)? = null,
) {
    PrimaryInfoRow(
        onClick = onClick,
        brush = brush,
        enabled = enabled,
        primaryText = primaryText,
        secondaryText = secondaryText,
        leadingIconRes = leadingIconRes,
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChanged)
        }
    )
}

@Composable
private fun OnBoardingItem(
    modifier: Modifier = Modifier,
    text: String = "Item",
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_onboarding_next_arrow),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun PrimaryInfoRow(
    onClick: () -> Unit = {},
    brush: Brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
        )
    ),
    enabled: Boolean = true,
    primaryText: String = "Dark Mode",
    primaryTextAlignment: Alignment.Horizontal = Alignment.Start,
    secondaryText: String? = null,
    @DrawableRes leadingIconRes: Int? = R.drawable.eye_closed,
    @DrawableRes trailingIconRes: Int,
) {
    PrimaryInfoRow(
        onClick = onClick,
        brush = brush,
        leadingIconRes = leadingIconRes,
        enabled = enabled,
        primaryText = primaryText,
        primaryTextAlignment = primaryTextAlignment,
        secondaryText = secondaryText,
        trailingContent = {
            Icon(
                painter = painterResource(id = trailingIconRes),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    )
}

@Composable
fun PrimaryInfoRow(
    onClick: () -> Unit = {},
    brush: Brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
        )
    ),
    enabled: Boolean = true,
    primaryText: String = "Dark Mode",
    primaryTextAlignment: Alignment.Horizontal = Alignment.Start,
    secondaryText: String? = null,
    @DrawableRes leadingIconRes: Int? = R.drawable.eye_closed,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    InfoRow(
        onClick = onClick,
        brush = brush,
        enabled = enabled,
    ) {
        leadingIconRes?.let {
            Icon(
                painter = painterResource(id = leadingIconRes),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }


        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                modifier = Modifier.align(primaryTextAlignment),
                text = primaryText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,

                )

            Spacer(modifier = Modifier.height(5.dp))

            secondaryText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        trailingContent?.invoke()
    }
}

@Composable
private fun PrimaryInfoButton(
    text: String = "Login",
    onClick: () -> Unit,
    brush: Brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
        )
    ),
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    InfoRow(
        onClick = onClick,
        brush = brush,
        enabled = true,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
}

@Composable
fun InfoRow(
    onClick: () -> Unit = {},
    brush: Brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
        )
    ),
    enabled: Boolean,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .background(
                brush = brush
            )
            .padding(horizontal = 15.dp, vertical = 25.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
@PreviewLightDark
fun Preview(modifier: Modifier = Modifier) {
    Flora1Theme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ProfileRoot(

            )
        }
    }

}
