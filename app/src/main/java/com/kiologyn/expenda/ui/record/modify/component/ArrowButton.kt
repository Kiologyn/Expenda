package com.kiologyn.expenda.ui.record.modify.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiologyn.expenda.R
import com.kiologyn.expenda.ui.theme.LocalExpendaColors
import kotlinx.coroutines.launch


@Composable
fun ArrowButton(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    isArrowUp: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val ARROW_SIZE = 0.50f
    val ARROWS_SIZE = 0.2f
    val ARROWS_PADDING = 0.13f
    val ROTATION_TIME = 200

    val coroutineScope = rememberCoroutineScope()
    val arrowsRotationState by remember { mutableStateOf(Animatable(0f)) }

    val arrowIcon: Painter =
        if (isArrowUp.value) painterResource(R.drawable.arrow_up)
        else painterResource(R.drawable.arrow_down)

    Box(
        modifier = modifier
            .size(size)
            .clickable {
                isArrowUp.value = !isArrowUp.value
                coroutineScope.launch {
                    arrowsRotationState.snapTo(0f)
                    arrowsRotationState.animateTo(
                        targetValue = 180f,
                        animationSpec = tween(
                            durationMillis = ROTATION_TIME,
                            easing = LinearEasing,
                        ),
                    )
                }
            }
    ) {
        Image(
            arrowIcon,
            modifier = Modifier
                .align(Alignment.Center)
                .size(size * ARROW_SIZE)
            ,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (isArrowUp.value) LocalExpendaColors.current.arrowGreen
                else LocalExpendaColors.current.arrowRed
            ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(size * ARROWS_SIZE + size * ARROWS_PADDING * 2)
            ,
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(R.drawable.arrows_reload),
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(arrowsRotationState.value)
                    .padding(size * ARROWS_PADDING)
                ,
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalExpendaColors.current.arrowsGray),
            )
        }
    }
}
