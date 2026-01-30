package com.example.workhive.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // animation controllers
    val pulse = rememberInfiniteTransition()
    val pulseScale by pulse.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // shimmer across the logo
    val shimmerTransition = rememberInfiniteTransition()
    val shimmerX by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing)
        )
    )

    // entrance fade for text/logo
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 700)
    )

    LaunchedEffect(Unit) {
        // small staged entrance then hold then navigate
        visible = true
        delay(2200)
        onTimeout()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF07060A), Color(0xFF0D0E14))
                )
            )
            .statusBarsPadding(), // pad top to avoid statusbar overlap
        contentAlignment = Alignment.Center
    ) {
        // stylized container for logo + glow
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {

            // soft neon backdrop circle (pulsing)
            Canvas(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val baseRadius = size.minDimension * 0.38f * pulseScale
                // radial neon glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8E57FF).copy(alpha = pulseAlpha), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = baseRadius * 1.6f
                    ),
                    radius = baseRadius * 1.6f,
                    center = Offset(cx, cy)
                )
                // inner glossy ring
                drawCircle(
                    color = Color(0xFF8E57FF).copy(alpha = 0.18f),
                    radius = baseRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = baseRadius * 0.08f)
                )
            }

            // logo circle (solid)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(Color(0xFF1A1033), Color(0xFF240E3A))))
                    .alpha(alpha),
                contentAlignment = Alignment.Center
            ) {
                // shimmer over the logo text
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val shimmerWidth = w * 0.28f
                    val startX = shimmerX * w
                    val brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.36f), Color.Transparent),
                        start = Offset(startX - shimmerWidth, h * 0.15f),
                        end = Offset(startX + shimmerWidth, h * 0.85f)
                    )
                    // dark base circle
                    drawCircle(color = Color.Transparent)
                    // apply shimmer by overlaying rect
                    drawRect(brush = brush, size = size)
                }

                Text(
                    text = "WorkHive",
                    color = Color(0xFFF5F7FA),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // small hive emoji badge bottom-right
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8E57FF))
                    .alpha(alpha),
                contentAlignment = Alignment.Center
            ) {
                Text("🐝", fontSize = 20.sp)
            }
        }

        // subtitle
        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp).alpha(alpha)) {
            Text(
                "Collaborate · Manage · Ship",
                color = Color(0xFFBFC6D1),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
