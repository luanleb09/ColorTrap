package com.colortrap.game.ui.screens.shop

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colortrap.game.utils.formatScore

/**
 * Shop Screen
 * - Tabs: Skins, Items
 * - Coins display at top
 * - Purchase confirmation dialog
 */
@Composable
fun ShopScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShopViewModel = viewModel()
) {
    val coins by viewModel.coins.collectAsState()
    val skins by viewModel.skins.collectAsState()
    val items by viewModel.items.collectAsState()
    val currentSkin by viewModel.currentSkin.collectAsState()

    var selectedTab by remember { mutableStateOf(ShopTab.SKINS) }
    var showPurchaseDialog by remember { mutableStateOf<PurchaseItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar with Coins
            ShopTopBar(
                coins = coins,
                onNavigateBack = onNavigateBack
            )

            // Tabs
            ShopTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Content
            when (selectedTab) {
                ShopTab.SKINS -> {
                    SkinsContent(
                        skins = skins,
                        currentSkin = currentSkin,
                        onSkinClick = { skin ->
                            if (!skin.isOwned && !skin.isFree) {
                                showPurchaseDialog = PurchaseItem.Skin(skin)
                            } else {
                                viewModel.equipSkin(skin.id)
                            }
                        }
                    )
                }
                ShopTab.ITEMS -> {
                    ItemsContent(
                        items = items,
                        onItemClick = { item ->
                            showPurchaseDialog = PurchaseItem.Item(item)
                        }
                    )
                }
            }
        }
    }

    // Purchase Dialog
    showPurchaseDialog?.let { purchaseItem ->
        PurchaseDialog(
            purchaseItem = purchaseItem,
            currentCoins = coins,
            onConfirm = {
                when (purchaseItem) {
                    is PurchaseItem.Skin -> viewModel.purchaseSkin(purchaseItem.skin.id)
                    is PurchaseItem.Item -> viewModel.purchaseItem(purchaseItem.item.type)
                }
                showPurchaseDialog = null
            },
            onDismiss = {
                showPurchaseDialog = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopTopBar(
    coins: Int,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Shop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            // Coins Display
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = coins.formatScore(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    )
}

@Composable
private fun ShopTabs(
    selectedTab: ShopTab,
    onTabSelected: (ShopTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        ShopTab.values().forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun SkinsContent(
    skins: List<ShopViewModel.SkinItem>,
    currentSkin: String,
    onSkinClick: (ShopViewModel.SkinItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(skins, key = { it.id }) { skin ->
            SkinCard(
                skin = skin,
                isEquipped = skin.id == currentSkin,
                onClick = { onSkinClick(skin) }
            )
        }
    }
}

@Composable
private fun SkinCard(
    skin: ShopViewModel.SkinItem,
    isEquipped: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .aspectRatio(0.85f)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEquipped) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Preview Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    skin.previewBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = skin.name,
                            modifier = Modifier.fillMaxSize(0.7f)
                        )
                    } ?: run {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Name
                Text(
                    text = skin.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Status/Price
                when {
                    isEquipped -> {
                        EquippedBadge()
                    }
                    skin.isOwned -> {
                        OwnedBadge()
                    }
                    skin.isFree -> {
                        FreeBadge()
                    }
                    else -> {
                        PriceBadge(price = skin.price)
                    }
                }
            }

            // Equipped Border
            if (isEquipped) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ItemsContent(
    items: List<ShopViewModel.ShopItem>,
    onItemClick: (ShopViewModel.ShopItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun ItemCard(
    item: ShopViewModel.ShopItem,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Owned: ${item.count}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Price Button
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = item.color
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.price.formatScore(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EquippedBadge() {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "EQUIPPED",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun OwnedBadge() {
    Surface(
        color = Color(0xFF4CAF50),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "OWNED",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun FreeBadge() {
    Surface(
        color = Color(0xFF2196F3),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "FREE",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun PriceBadge(price: Int) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "💰", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = price.formatScore(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun PurchaseDialog(
    purchaseItem: PurchaseItem,
    currentCoins: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val (name, price, emoji) = when (purchaseItem) {
        is PurchaseItem.Skin -> Triple(purchaseItem.skin.name, purchaseItem.skin.price, "🎨")
        is PurchaseItem.Item -> Triple(purchaseItem.item.name, purchaseItem.item.price, purchaseItem.item.emoji)
    }

    val hasEnoughCoins = currentCoins >= price

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = emoji, fontSize = 48.sp)
        },
        title = {
            Text(
                text = "Purchase $name?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Price:")
                    Text(
                        text = "💰 ${price.formatScore()}",
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Your Coins:")
                    Text(
                        text = "💰 ${currentCoins.formatScore()}",
                        fontWeight = FontWeight.Bold,
                        color = if (hasEnoughCoins) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("After Purchase:", fontWeight = FontWeight.Bold)
                    Text(
                        text = "💰 ${(currentCoins - price).coerceAtLeast(0).formatScore()}",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!hasEnoughCoins) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Not enough coins!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = hasEnoughCoins
            ) {
                Text("BUY")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

// Enums & Data Classes
enum class ShopTab(val title: String, val icon: ImageVector) {
    SKINS("Skins", Icons.Default.Palette),
    ITEMS("Items", Icons.Default.ShoppingBag)
}

sealed class PurchaseItem {
    data class Skin(val skin: ShopViewModel.SkinItem) : PurchaseItem()
    data class Item(val item: ShopViewModel.ShopItem) : PurchaseItem()
}
