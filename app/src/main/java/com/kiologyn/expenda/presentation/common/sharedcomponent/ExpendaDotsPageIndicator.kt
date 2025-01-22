package com.kiologyn.expenda.presentation.common.sharedcomponent

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun ExpendaDotsPageIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
    size: Dp,
) {
    LazyRow(
        modifier = modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size/3)
    ) {
        items(totalDots) { index ->
            val isActive = index == selectedIndex
            val sizeAnimation by remember { mutableStateOf(
                Animatable(
                    if (isActive) size.value * 2
                    else size.value
                )
            ) }
            val colorAnimation by remember { mutableStateOf(
                Animatable(
                    if (isActive) selectedColor
                    else unSelectedColor
                )
            ) }

            val ANIMATION_DURATION = 25
            LaunchedEffect(selectedIndex) {
                sizeAnimation.animateTo(
                    targetValue =
                    if (isActive) size.value * 2
                    else size.value
                    ,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = LinearEasing,
                    ),
                )
                colorAnimation.animateTo(
                    targetValue =
                    if (isActive) selectedColor
                    else unSelectedColor
                    ,
                    animationSpec = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = LinearEasing,
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .height(size)
                    .width(sizeAnimation.value.dp)
                    .clip(CircleShape)
                    .background(colorAnimation.value)
                ,
            )
        }
    }
}