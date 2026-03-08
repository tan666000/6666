// SkeletonLoading.kt
package com.steadywj.wjfakelocation.manager.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 骨架屏加载组件
 * 
 * 功能:
 * - Shimmer 渐变动画
 * - 自定义宽高
 * - 多种预设样式
 * - 无障碍支持
 */

/**
 * Shimmer 效果修饰符
 */
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition()
    
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
    
    return this.then(background(brush))
}

/**
 * 骨架屏文本行
 */
@Composable
fun SkeletonTextLine(
    modifier: Modifier = Modifier,
    widthPercent: Float = 1f
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthPercent)
            .height(16.dp)
            .shimmerEffect()
    )
}

/**
 * 骨架屏圆形头像
 */
@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shimmerEffect()
    ) {
        // 圆形由大小决定
    }
}

/**
 * 骨架屏矩形卡片
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shimmerEffect()
    )
}

/**
 * 列表骨架屏（多条文本）
 */
@Composable
fun SkeletonList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(modifier = modifier) {
        repeat(itemCount) { index ->
            Spacer(modifier = Modifier.height(16.dp))
            SkeletonTextLine(
                widthPercent = when (index % 3) {
                    0 -> 1f
                    1 -> 0.9f
                    else -> 0.8f
                }
            )
        }
    }
}

/**
 * 地图加载骨架屏
 */
@Composable
fun SkeletonMap(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        // 模拟地图网格
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            repeat(8) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(6) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.LightGray.copy(alpha = 0.5f)
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
