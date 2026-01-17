package com.example.chillbeads.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private data class Bead(val center: Offset, val radius: Float, val color: Color)

@Composable
fun SplashScreen() { // Tidak ada parameter lagi
    val beads = remember {
        List(50) { 
            Bead(
                center = Offset(x = Random.nextFloat(), y = Random.nextFloat()),
                radius = Random.nextFloat() * 20f + 10f,
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = Random.nextFloat() * 0.5f + 0.5f
                )
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bead_animation")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bead_progress"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            beads.forEach { bead ->
                val currentY = (bead.center.y * canvasHeight + (animationProgress * canvasHeight)) % canvasHeight
                drawCircle(
                    color = bead.color,
                    radius = bead.radius,
                    center = Offset(bead.center.x * canvasWidth, currentY)
                )
            }
        }
        Text(
            text = "ChillBeads",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
