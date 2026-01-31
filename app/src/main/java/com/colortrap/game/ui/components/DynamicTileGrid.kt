package com.colortrap.game.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.colortrap.game.data.models.DynamicTile
import com.colortrap.game.utils.toGridColumns

/**
 * Grid các tiles để player chọn
 * Động: Spawn animation, click effect, shake khi sai
 *
 * FIXED:
 * ✅ Line 128: Added !! for bitmap (guaranteed non-null from ViewModel)
 */
@Composable
fun DynamicTileGrid(
    tiles: List<DynamicTile>,
    onTileClick: (DynamicTile) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    val columns = tiles.size.toGridColumns()

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(tiles, key = { it.id }) { tile ->
            DynamicTileItem(
                tile = tile,
                onClick = { onTileClick(tile) },
                isEnabled = isEnabled
            )
        }
    }
}

@Composable
private fun DynamicTileItem(
    tile: DynamicTile,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    // Spawn animation (fade in + scale)
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(tile.id) {
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "spawn"
    )

    // Click animation
    var isPressed by remember { mutableStateOf(false) }
    val clickScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "click"
    )

    // Wrong click shake animation
    var shouldShake by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        targetValue = if (shouldShake) 0f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(50),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale * clickScale)
            .offset(x = shakeOffset.dp)
            .clickable(
                enabled = isEnabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 2.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            // ✅ FIXED: Added !! because bitmap is guaranteed non-null
            // ViewModel loads all bitmaps before passing to UI
            Image(
                bitmap = tile.bitmap!!.asImageBitmap(),
                contentDescription = "Tile: ${tile.colorGroup}",
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}