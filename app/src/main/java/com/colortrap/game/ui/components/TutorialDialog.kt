package com.colortrap.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Tutorial Dialog
 * Shows game instructions with "Don't show today" checkbox
 *
 * Features:
 * - Scrollable content for long instructions
 * - Checkbox to hide for current day
 * - Remembers checkbox state across sessions
 * - Resets checkbox state when day changes
 */
@Composable
fun TutorialDialog(
    initialCheckboxState: Boolean = false,
    onDismiss: (dontShowToday: Boolean) -> Unit
) {
    var dontShowToday by remember { mutableStateOf(initialCheckboxState) }

    Dialog(
        onDismissRequest = { onDismiss(dontShowToday) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎮 How to Play",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = { onDismiss(dontShowToday) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    TutorialSection(
                        title = "🎯 Objective",
                        content = "Tap on tiles that DO NOT match the forbidden colors shown at the top. Be quick and accurate!"
                    )

                    TutorialSection(
                        title = "🎨 Forbidden Colors",
                        content = "The colored tiles at the top are FORBIDDEN. Don't tap any tile with these colors!"
                    )

                    TutorialSection(
                        title = "⏱️ Timer",
                        content = "You have limited time to make your choice. The timer turns red when you're running out of time!"
                    )

                    TutorialSection(
                        title = "🎮 Game Modes",
                        content = """
                            • NORMAL: Progressive difficulty, game over on mistakes
                            • HARD: Starts harder, faster progression
                            • SUPER HARD: Extreme challenge from the start
                            • RELAX: 3 lives, more time, forgiving gameplay
                        """.trimIndent()
                    )

                    TutorialSection(
                        title = "✨ Power-ups",
                        content = """
                            • 🐌 Slow Time: Slows down the timer for 5 seconds
                            • ⏭️ Skip Level: Skip the current level
                        """.trimIndent()
                    )

                    TutorialSection(
                        title = "💰 Scoring",
                        content = """
                            • Base Score: 100 points per level
                            • Time Bonus: Extra points for remaining time
                            • Streak Bonus: Consecutive correct answers × 50
                        """.trimIndent()
                    )

                    TutorialSection(
                        title = "💡 Tips",
                        content = """
                            • Focus on color groups, not individual tiles
                            • Use power-ups strategically on harder levels
                            • In RELAX mode, take your time to learn the game
                            • Watch the timer - don't let it run out!
                        """.trimIndent()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = dontShowToday,
                        onCheckedChange = { dontShowToday = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "Don't show this today",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // OK Button
                Button(
                    onClick = { onDismiss(dontShowToday) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Got It!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialSection(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            lineHeight = 20.sp
        )
    }
}