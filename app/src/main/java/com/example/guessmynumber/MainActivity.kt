package com.example.guessmynumber

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessmynumber.ui.theme.GuessMyNumberTheme
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.models.*
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GuessMyNumberTheme {
                GuessGame()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GuessGame() {

    val context = LocalContext.current

    var randomNumber by remember { mutableStateOf((1..100).random()) }
    var userInput by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Guess a number between 1 and 100") }
    var attempts by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf<Int?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val scale = remember { Animatable(1f) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text("🎯 Guess My Number", fontSize = 26.sp)

                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Enter number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        MediaPlayer.create(context, R.raw.click).start()

                        val guess = userInput.toIntOrNull()

                        if (guess == null) {
                            resultText = "⚠️ Enter valid number"
                            return@Button
                        }

                        attempts++

                        when {
                            guess < randomNumber -> resultText = "📉 Too Low!"
                            guess > randomNumber -> resultText = "📈 Too High!"
                            else -> {
                                resultText = "🎉 Correct in $attempts attempts!"
                                MediaPlayer.create(context, R.raw.win).start()
                                showConfetti = true

                                if (highScore == null || attempts < highScore!!) {
                                    highScore = attempts
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guess")
                }

                Button(
                    onClick = {
                        randomNumber = (1..100).random()
                        attempts = 0
                        userInput = ""
                        resultText = "🔄 New Game Started"
                        showConfetti = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset")
                }

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Text(
                        resultText,
                        fontSize = 18.sp,
                        modifier = Modifier.scale(scale.value)
                    )
                }

                Text("Attempts: $attempts")

                highScore?.let {
                    Text("🏆 Best Score: $it")
                }
            }
        }

        // 🎉 Confetti
        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(2, TimeUnit.SECONDS).perSecond(100)
                    )
                )
            )
        }
    }

    // 🎬 Bounce animation (CORRECT PLACE)
    LaunchedEffect(resultText) {
        scale.animateTo(1.2f, animationSpec = tween(150))
        scale.animateTo(1f, animationSpec = tween(150))
    }
}