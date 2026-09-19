package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Particle(
    val initialX: Float,
    val initialY: Float,
    val speedX: Float,
    val speedY: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 45
) {
    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFF00F5FF), // Cyan
        Color(0xFFFF2A85), // Pink
        Color(0xFF10B981), // Green
        Color(0xFFA855F7), // Purple
        Color(0xFFF97316)  // Orange
    )

    val particles = remember {
        List(particleCount) {
            Particle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.3f,
                speedX = (Random.nextFloat() - 0.5f) * 0.4f,
                speedY = 0.5f + Random.nextFloat() * 0.8f,
                size = 8f + Random.nextFloat() * 12f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2500, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = progress.value

        particles.forEach { p ->
            val curX = (p.initialX + p.speedX * t) * w
            val curY = (p.initialY + p.speedY * t) * h
            val alpha = (1f - t * 0.8f).coerceIn(0f, 1f)

            drawRect(
                color = p.color.copy(alpha = alpha),
                topLeft = Offset(curX, curY),
                size = Size(p.size, p.size * 0.6f)
            )
        }
    }
}
