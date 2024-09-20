package com.example.point

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChineseSlotMachineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   EnhancedAnimatedSlotMachine()
                }
            }
        }
    }
}

@Composable
fun ChineseSlotMachineTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFFE60000),
        secondary = Color(0xFFFFD700),
        tertiary = Color(0xFF000000),
        background = Color(0xFF1C1B1F)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

data class Symbol(val icon: String, val score: Int)
data class Multiplier(val text: String, val value: Int)

@Composable
fun EnhancedAnimatedSlotMachine() {
    val symbols = listOf(
        Symbol("🍒", 100),
        Symbol("🍋", 200),
        Symbol("🔔", 300),
        Symbol("🍉", 400),
        Symbol("⭐", 500),
        Symbol("7️⃣", 1000),
        Symbol("🍀", 2000),
        Symbol("福", 1500) // Adding a traditional symbol
    )

    val multipliers = listOf(
        Multiplier("X1", 1),
        Multiplier("X2", 2),
        Multiplier("X3", 3),
        Multiplier("X4", 4)
        )

    var grid by remember { mutableStateOf(List(3) { List(3) { symbols.random() } }) }
    var multiplierColumn by remember { mutableStateOf(List(3) { multipliers.random() }) }
    var score by remember { mutableStateOf(1000) }
    var message by remember { mutableStateOf("") }
    var spinning by remember { mutableStateOf(false) }

    var spinCount by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Spin 횟수 : ${spinCount}")
        // Grid display
        for (row in 0 until 3) {
            Row {
                // Multiplier column
                Box(
                    modifier = Modifier
                        .size(100.dp) // Increased size for better readability
                        .border(
                            2.dp,
                            Color.LightGray,
                            RoundedCornerShape(12.dp)
                        ) // Red border for contrast
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(multiplierColumn[row].text, fontSize = 28.sp, color = Color.Red)
                }

                // Symbol grid
                for (col in 0 until 3) {
                    Box(
                        modifier = Modifier
                            .size(100.dp) // Increased size for better readability
                            .border(
                                2.dp,
                                Color.LightGray,
                                RoundedCornerShape(12.dp)
                            ) // Red border for contrast
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(grid[row][col].icon, fontSize = 40.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Score: $score", fontSize = 28.sp, color = Color.White) // Larger text
        Text(message, fontSize = 20.sp, color = Color.Yellow) // Larger text and red color
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                spinCount = spinCount+1

                if (!spinning && score >= 10) {
                    spinning = true
                    message = "Spinning..."
                    score -= 10 // Spin cost
                    scope.launch {
                        // Animate the grid and multiplier column
                        repeat(10) {
                            grid = List(3) { List(3) { symbols.random() } }
                            multiplierColumn = List(3) { multipliers.random() }
                            delay(50)
                        }

                        // Set final results
                        val finalGrid = List(3) { List(3) { symbols.random() } }
                        val finalMultipliers = List(3) { multipliers.random() }
                        grid = finalGrid
                        multiplierColumn = finalMultipliers

                        // Check for winning combination in the second row (excluding first column)
                        val secondRow = finalGrid[1]
                        if (secondRow[0]== secondRow[1] && secondRow[1] == secondRow[2]) {
                            val winningSymbol = secondRow[1]
                            val multiplier = finalMultipliers[1].value
                            val winningScore = winningSymbol.score * multiplier
                            score += winningScore
                            message = "Jackpot! You won $winningScore points! (${winningSymbol.icon} x${multiplier})"
                        } else {
                            message = "Try again!"
                        }
                        spinning = false
                    }
                } else if (score < 10) {
                    message = "Not enough points to spin!"
                }
            },
            enabled = !spinning && score >= 10
        ) {
            Text("Spin (Cost: 10)", fontSize = 24.sp)
        }
    }
}