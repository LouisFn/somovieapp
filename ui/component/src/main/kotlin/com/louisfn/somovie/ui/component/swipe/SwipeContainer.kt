package com.louisfn.somovie.ui.component.swipe

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.louisfn.somovie.ui.common.extension.toIntOffset
import com.louisfn.somovie.ui.common.model.ImmutableList

private const val DefaultMaxRotationInDegrees = 15.0f
private const val DefaultFractionalThreshold = 0.25f
private val DefaultVelocityThreshold = 300.dp

private val DefaultSwipeAnimationSpec = spring<Offset>(stiffness = Spring.StiffnessMedium)
private val DefaultRewindAnimationSpec = spring<Offset>(stiffness = Spring.StiffnessMedium)

@Composable
fun <T> SwipeContainer(
    items: ImmutableList<T>,
    itemKey: (T) -> Any,
    modifier: Modifier = Modifier,
    maxRotationInDegrees: Float = DefaultMaxRotationInDegrees,
    fractionalThreshold: Float = DefaultFractionalThreshold,
    velocityThreshold: Dp = DefaultVelocityThreshold,
    swipeAnimationSpec: AnimationSpec<Offset> = DefaultSwipeAnimationSpec,
    cancelAnimationSpec: AnimationSpec<Offset> = DefaultRewindAnimationSpec,
    onDragging: (T, SwipeDirection, ratio: Float) -> Unit = { _, _, _ -> },
    onSwipe: (T, SwipeDirection) -> Unit = { _, _ -> },
    onDisappear: (T, SwipeDirection) -> Unit = { _, _ -> },
    onCancel: (T) -> Unit = {},
    itemContent: @Composable (T) -> Unit,
) {
    Box(modifier = modifier) {
        items
            .asReversed()
            .forEach { item ->
                key(itemKey(item)) {
                    SwipeableItem(
                        item = item,
                        maxRotationInDegrees = maxRotationInDegrees,
                        fractionalThreshold = fractionalThreshold,
                        velocityThreshold = velocityThreshold,
                        swipeAnimationSpec = swipeAnimationSpec,
                        cancelAnimationSpec = cancelAnimationSpec,
                        onSwipe = { direction -> onSwipe(item, direction) },
                        onDragging = { direction, ratio -> onDragging(item, direction, ratio) },
                        onCancel = { onCancel(item) },
                        onDisappear = { direction -> onDisappear(item, direction) },
                        itemContent = itemContent,
                    )
                }
            }
    }
}

@Composable
private fun <T> SwipeableItem(
    item: T,
    maxRotationInDegrees: Float,
    fractionalThreshold: Float,
    velocityThreshold: Dp,
    swipeAnimationSpec: AnimationSpec<Offset>,
    cancelAnimationSpec: AnimationSpec<Offset>,
    onDragging: (SwipeDirection, ratio: Float) -> Unit,
    onSwipe: (SwipeDirection) -> Unit,
    onDisappear: (SwipeDirection) -> Unit,
    onCancel: () -> Unit,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var itemSize by remember { mutableStateOf(IntSize.Zero) }

    val swipeController = rememberSwipeController(
        size = itemSize,
        maxRotationInDegrees = maxRotationInDegrees,
        fractionalThreshold = fractionalThreshold,
        velocityThreshold = velocityThreshold,
        swipeAnimationSpec = swipeAnimationSpec,
        cancelAnimationSpec = cancelAnimationSpec,
        onDragging = onDragging,
        onSwipe = onSwipe,
        onDisappear = onDisappear,
        onCancel = onCancel,
    )

    Box(
        modifier = modifier
            .onSizeChanged { itemSize = it }
            .swipeableItem(swipeController),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { swipeController.offset.toIntOffset() }
                .rotate(swipeController.rotation),
        ) {
            itemContent(item)
        }
    }
}
