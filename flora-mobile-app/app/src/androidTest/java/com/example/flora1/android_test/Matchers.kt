package com.example.flora1.android_test

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider

fun SemanticsNodeInteractionsProvider.onUnmergedNode(
    matcher: SemanticsMatcher,
) = onNode(matcher, useUnmergedTree = true)

fun SemanticsNodeInteractionsProvider.onAllUnmergedNodes(
    matcher: SemanticsMatcher,
) = onAllNodes(matcher, useUnmergedTree = true)
