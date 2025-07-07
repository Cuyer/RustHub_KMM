package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

@OptIn(ExperimentalAnimationApi::class)
fun defaultFadeTransition() = fadeIn(animationSpec = tween(150)) togetherWith
        fadeOut(animationSpec = tween(150))