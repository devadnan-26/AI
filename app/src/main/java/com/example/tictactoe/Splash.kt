package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.tictactoe.ui.theme.TicTacToeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class Splash: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = Color(121, 107, 190)
                )
                var visible by remember { mutableStateOf(false) }
                BackHandler {

                }
                Handler(Looper.getMainLooper()).postDelayed({
                    visible = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this ,MainActivity::class.java))
                        finish()
                    }, 2500)
                }, 2500)
                Scaffold {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(121, 107, 190))
                            .padding(it)
                    ) {
                        val (text, image) = createRefs()
                        Image(painter = painterResource(id = R.drawable.tictactoe),
                            contentDescription = null,
                            modifier = Modifier
                                .constrainAs(image) {
                                    linkTo(parent.start, parent.end, bias = 0.8f)
                                    linkTo(parent.top, parent.bottom)
                                }
                                .size(180.dp, 180.dp),
                            alignment = Alignment.Center)
                        AnimatedVisibility(
                            modifier = Modifier
                                .wrapContentSize()
                                .constrainAs(text) {
                                    linkTo(parent.start, image.start, bias = 1f)
                                    linkTo(image.top, image.bottom)
                                    width = Dimension.preferredWrapContent
                                    height = Dimension.preferredWrapContent
                                },
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(700))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.font),
                                contentDescription = null,
                                modifier = Modifier.size(133.dp, 189.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}