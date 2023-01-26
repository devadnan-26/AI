package com.example.tictactoe

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tictactoe.ui.theme.TicTacToeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = Color(121, 107, 190)
                )
                // A surface container using the 'background' color from the them
                val navController = rememberNavController()
                val preferences = getSharedPreferences("Game", Context.MODE_PRIVATE)
                NavHost(
                    navController = navController,
                    startDestination = if (preferences.getBoolean(
                            "genderDone",
                            false
                        )
                    ) "enter" else "gender"
                ) {
                    composable("enter") { Play(this@MainActivity, navController) }
                    composable("gender") {
                        Gender(
                            navController = navController,
                            context = this@MainActivity
                        )
                    }
                    composable("choose") { PlayerChoose(navController, this@MainActivity) }
                    composable("letterChoose") { ChooseLetter(navController) }
                    composable("computerMode") { Computer(navController, this@MainActivity) }
                    composable(
                        "play/{player1}/{player2}/{mode}",
                        arguments = listOf(
                            navArgument("player1") { type = NavType.StringType },
                            navArgument("player2") { type = NavType.StringType },
                            navArgument("mode") { type = NavType.StringType })
                    ) {
                        val player1 = it.arguments?.getString("player1")
                        val player2 = it.arguments?.getString("player2")
                        val mode = it.arguments?.getString("mode")
                        PlayGame(
                            navController,
                            this@MainActivity,
                            player1 ?: "",
                            player2 ?: "",
                            mode ?: ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Play(activity: MainActivity?, navController: NavController?) {
    var dialogState by remember { mutableStateOf(false) }
    @Composable
    fun QuitDialog(
        activity: MainActivity?,
        onDismissRequest: (dialogState: Boolean) -> Unit
    ) {
        if (dialogState)
            AlertDialog(
                backgroundColor = Color.White,
                onDismissRequest = { onDismissRequest(dialogState) },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                buttons = {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.exit),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.padding(vertical = 8.dp))
                            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                OutlinedButton(
                                    onClick = { activity?.finish() },
                                    border = BorderStroke(2.dp, Color(121, 107, 190)),
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Text(
                                        text = "Quit",
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        textAlign = TextAlign.Center,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                Button(
                                    onClick = {
                                        dialogState = false
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(121, 107, 190)),
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Text(
                                        text = "Cancel",
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                },
                title = {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Are you sure you want to quit",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 28.sp,
                        )
                    }
                },
                text = null,
                shape = RoundedCornerShape(15.dp)
            )
    }
    Scaffold(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(121, 107, 190))
                .padding(it)
        ) {
            QuitDialog(activity = activity) { dialogState = !dialogState }
            BackHandler {
                dialogState = true
            }
            val (text, image, play, quit) = createRefs()
            Image(painter = painterResource(id = R.drawable.tictactoe),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(image) {
                        linkTo(parent.start, parent.end, bias = 0.8f)
                        linkTo(parent.top, parent.bottom, bias = 0.2f)
                    }
                    .size(180.dp, 180.dp),
                alignment = Alignment.Center)
            Image(painter = painterResource(id = R.drawable.font),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(text) {
                        linkTo(parent.start, image.start, bias = 1f)
                        linkTo(image.top, image.bottom)
                    }
                    .size(133.dp, 189.dp)
                    .padding(top = 16.dp))
            Button(onClick = { navController?.navigate("choose") },
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth()
                    .constrainAs(play) {
                        linkTo(parent.start, parent.end)
                        linkTo(parent.top, parent.bottom, bias = 0.55f)
                    }
                    .size(72.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(50.dp)) {
                Text(
                    "PLAY",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Button(onClick = {
                dialogState = true
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth()
                    .constrainAs(quit) {
                        linkTo(parent.start, parent.end)
                        top.linkTo(play.bottom, margin = 24.dp)
                    }
                    .size(72.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(50.dp)) {
                Text(
                    "QUIT",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun PlayerChoose(navController: NavController?, context: Context?) {
    BackHandler {
        navController?.navigate("enter")
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        ConstraintLayout(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(121, 107, 190))
        ) {
            var isComputer by remember { mutableStateOf(false) }
            var isFriend by remember { mutableStateOf(false) }
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val (ellipse, you, vs, column, player) = createRefs()

            Image(painter = painterResource(id = R.drawable.ellipse),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(ellipse) {
                        linkTo(parent.top, parent.bottom, bias = 0.03f)
                        linkTo(parent.start, parent.end)
                    }
                    .size((screenWidth - 60).dp, 175.dp))
            Image(painter = painterResource(
                id = context!!.getSharedPreferences(
                    "Game",
                    Context.MODE_PRIVATE
                ).getInt("playerImage", 0)
            ),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(player) {
                        linkTo(ellipse.top, you.top)
                        linkTo(ellipse.start, ellipse.end)
                    }
                    .size(120.dp, 120.dp),
                alignment = Alignment.TopCenter)
            Image(painter = painterResource(id = R.drawable.you),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(you) {
                        linkTo(ellipse.start, ellipse.end)
                        linkTo(ellipse.top, ellipse.bottom, bias = 1f)
                    }
                    .size(58.dp, 33.dp))
            Text(
                "VS",
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.tosca_zero)),
                fontSize = 100.sp,
                modifier = Modifier.constrainAs(vs) {
                    top.linkTo(ellipse.bottom, margin = 8.dp)
                    linkTo(parent.start, parent.end)
                })
            val contents = listOf(
                Opponent(R.drawable.computer_icon, "computer", 0),
                Opponent(R.drawable.your_friend_icon, "your friend", 1)
            )
            LazyColumn(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(column) {
                        top.linkTo(vs.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                        linkTo(parent.start, parent.end, startMargin = 8.dp, endMargin = 8.dp)
                        width = Dimension.preferredWrapContent
                        height = Dimension.preferredWrapContent
                    }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(items = contents) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .clickable {
                            if (it.index == 0) {
                                if (!isFriend) {
                                    isComputer = true
                                    navController?.popBackStack()
                                    navController?.navigate("computerMode")
                                } else {
                                    isFriend = false
                                    isComputer = true
                                    navController?.popBackStack()
                                    navController?.navigate("computerMode")
                                }
                            }
                            if (it.index == 1) {
                                if (!isComputer) {
                                    isFriend = true
                                    navController?.popBackStack()
                                    navController?.navigate("letterChoose")
                                } else {
                                    isFriend = true
                                    isComputer = false
                                    navController?.popBackStack()
                                    navController?.navigate("letterChoose")
                                }
                            }
                        }
                        .background(
                            if (it.index == 1) {
                                if (isFriend) Color(88, 73, 163, 255) else Color(
                                    88,
                                    73,
                                    163,
                                    0,
                                )
                            } else if (it.index == 0) {
                                if (isComputer) Color(88, 73, 163, 255) else Color(
                                    88,
                                    73,
                                    163,
                                    0,
                                )
                            } else {
                                Color(
                                    88,
                                    73,
                                    163,
                                    0,
                                )
                            }
                        )) {
                        Image(
                            painter = painterResource(id = it.image),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(200.dp, 200.dp)
                        )
                        Spacer(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = it.text,
                            fontSize = 30.sp,
                            fontFamily = FontFamily(Font(R.font.scac3)),
                            color = Color.Black,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
    }
}

@Composable
fun Computer(navController: NavController?, context: Context) {
    var firstNumber by remember { mutableStateOf("1") }
    var secondNumber by remember { mutableStateOf("2") }
    var first by remember { mutableStateOf("computer") }
    var second by remember { mutableStateOf("me") }
    BackHandler {
        navController?.navigate("enter")
    }
    Scaffold(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(121, 107, 190))
        ) {
            val (title, column, confirm) = createRefs()
            Text(
                "Who's First",
                fontSize = 56.sp,
                color = Color(36, 32, 65, 255),
                fontFamily = FontFamily(
                    Font(R.font.nature_beauty)
                ),
                modifier = Modifier.constrainAs(title) {
                    linkTo(parent.top, parent.bottom, bias = 0.15f)
                    linkTo(parent.start, parent.end)
                }
            )
            Column(modifier = Modifier.constrainAs(column) {
                top.linkTo(title.bottom, margin = 24.dp)
                bottom.linkTo(confirm.top, margin = 16.dp)
                linkTo(parent.start, parent.end)
                width = Dimension.preferredWrapContent
                height = Dimension.preferredWrapContent
            }) {
                Column {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Image(
                            painter = painterResource(id = R.drawable.computer_icon),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(62, 241, 105, 255))
                                .align(Alignment.BottomEnd)
                                .border(2.dp, Color.Black, RoundedCornerShape(100.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(firstNumber, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "computer ",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.scac3)),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Image(
                    painter = painterResource(id = R.drawable.exchange),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                        ) {
                            val st = firstNumber; firstNumber = secondNumber; secondNumber = st
                            val change = first; first = second; second = change
                        }
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Image(
                            painter = painterResource(
                                id = context.getSharedPreferences(
                                    "Game",
                                    Context.MODE_PRIVATE
                                ).getInt("playerImageCircled", 0)
                            ),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier.size(106.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(62, 241, 105, 255))
                                .align(Alignment.BottomEnd)
                                .border(2.dp, Color.Black, RoundedCornerShape(100.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(secondNumber, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "you",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.scac3)),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Button(
                onClick = { navController?.navigate("play/$first/$second/computer") },
                modifier = Modifier.constrainAs(confirm) {
                    linkTo(parent.start, parent.end)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                },
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Confirm",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PlayGame(
    navController: NavController?,
    context: MainActivity?,
    player1: String,
    player2: String,
    mode: String
) {
    BackHandler {
        navController?.navigate("enter")
    }
    Scaffold(modifier = Modifier.fillMaxSize()) {
        val computer = if (player1 == "computer") "X" else "O"
        val me = if (player1 == "me") "X" else "O"
        var winnerState by remember { mutableStateOf(false) }
        var winner by remember { mutableStateOf("") }
        var turn by remember { mutableStateOf(player1) }
        var blank1 by remember { mutableStateOf("") }
        var blank2 by remember { mutableStateOf("") }
        var blank3 by remember { mutableStateOf("") }
        var blank4 by remember { mutableStateOf("") }
        var blank5 by remember { mutableStateOf("") }
        var blank6 by remember { mutableStateOf("") }
        var blank7 by remember { mutableStateOf("") }
        var blank8 by remember { mutableStateOf("") }
        var blank9 by remember { mutableStateOf("") }
        fun whichBlank(blank: Int, value: String) {
            when (blank) {
                0 -> blank1 = value
                1 -> blank2 = value
                2 -> blank3 = value
                3 -> blank4 = value
                4 -> blank5 = value
                5 -> blank6 = value
                6 -> blank7 = value
                7 -> blank8 = value
                8 -> blank9 = value
            }
        }
        LaunchedEffect("computer", {}) {
            if (mode == "computer") turn = if (computer == "X") computer else me
            if (mode == "computer" && computer == "X") {
                val board = arrayListOf(
                    blank1, blank2, blank3,
                    blank4, blank5, blank6,
                    blank7, blank8, blank9
                )
                whichBlank(Minimax.computerMove(board), turn)
                turn = me
            }
        }
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(121, 107, 190))
                .verticalScroll(rememberScrollState())
        ) {
            val (turn_word, blanks, quit) = createRefs()
            Dialog(
                dialogState = winnerState,
                onDismissRequest = { dialogState -> winnerState = !dialogState },
                winner = winner,
                computer = computer,
                activity = context,
                navController = navController,
                mode = mode
            )
            Row(modifier = Modifier.constrainAs(turn_word) {
                linkTo(parent.start, parent.end)
                bottom.linkTo(blanks.top, margin = 36.dp)
            }) {
                Text(
                    text = turn,
                    fontSize = 48.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    text = "Turn",
                    fontSize = 48.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(modifier = Modifier.constrainAs(blanks) {
                bottom.linkTo(quit.top, margin = 64.dp)
                linkTo(parent.start, parent.end)
            }) {
                Row {
                    Blank(
                        index = arrayListOf(0, 0),
                        turn = blank1,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank1.isBlank() else blank1.isBlank() && turn == me
                        ) {
                            if (blank1 == "") {
                                if (mode == "friend") {
                                    blank1 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank1 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        },
                        context = context!!
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    Blank(
                        index = arrayListOf(0, 1),
                        turn = blank2,
                        context = context,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank2.isBlank() else blank2.isBlank() && turn == me
                        ) {
                            if (blank2 == "") {
                                if (mode == "friend") {
                                    blank2 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank2 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    Blank(
                        index = arrayListOf(0, 2),
                        turn = blank3,
                        context = context,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank3.isBlank() else blank3.isBlank() && turn == me
                        ) {
                            if (blank3 == "") {
                                if (mode == "friend") {
                                    blank3 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank3 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                }
                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                Row {
                    Blank(
                        index = arrayListOf(1, 0),
                        turn = blank4,
                        context = context!!,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank4.isBlank() else blank4.isBlank() && turn == me
                        ) {
                            if (blank4 == "") {
                                if (mode == "friend") {
                                    blank4 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                    blank4, blank5, blank6,
                                    blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank4 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    Blank(
                        index = arrayListOf(1, 1),
                        turn = blank5,
                        context = context,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank5.isBlank() else blank5.isBlank() && turn == me
                        ) {
                            if (blank5 == "") {
                                if (mode == "friend") {
                                    blank5 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank5 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    Blank(
                        index = arrayListOf(1, 2),
                        turn = blank6,
                        context = context,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank6.isBlank() else blank6.isBlank() && turn == me
                        ) {
                            if (blank6 == "") {
                                if (mode == "friend") {
                                    blank6 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank6 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                }
                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                Row {
                    Blank(
                        index = arrayListOf(2, 0),
                        turn = blank7,
                        context = context!!,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank7.isBlank() else blank7.isBlank() && turn == me
                        ) {
                            if (blank7 == "") {
                                if (mode == "friend") {
                                    blank7 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank7 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    Blank(
                        index = arrayListOf(2, 1),
                        turn = blank8,
                        context = context,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank8.isBlank() else blank8.isBlank() && turn == me
                        ) {
                            if (blank8 == "") {
                                if (mode == "friend") {
                                    blank8 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank8 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    Blank(
                        index = arrayListOf(2, 2),
                        turn = blank9,
                        context = context,
                        modifier = Modifier.clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            enabled = if (mode != "computer") blank9.isBlank() else blank9.isBlank() && turn == me
                        ) {
                            if (blank9 == "") {
                                if (mode == "friend") {
                                    blank9 = turn
                                    val board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    if (Minimax.isGameWon(board, player1) || Minimax.isGameWon(board, player2) || Minimax.isBoardFull(board)) {
                                        winnerState = true
                                        winner = when (Minimax.gameResult(board)) {
                                            "X" -> "X"
                                            "O" -> "O"
                                            else -> "Draw"
                                        }
                                    } else {
                                        if (turn == player1) {
                                            turn = player2
                                        } else if (turn == player2) {
                                            turn = player1
                                        }
                                    }
                                } else if (mode == "computer") {
                                    blank9 = turn
                                    turn = computer
                                    var board = arrayListOf(
                                        blank1, blank2, blank3,
                                        blank4, blank5, blank6,
                                        blank7, blank8, blank9
                                    )
                                    when {
                                        Minimax.isGameWon(board, computer) -> {
                                            winner = computer
                                            winnerState = true
                                        }
                                        Minimax.isGameWon(board, me) -> {
                                            winner = me
                                            winnerState = true
                                        }
                                        Minimax.isBoardFull(board) -> {
                                            winner = "Draw"
                                            winnerState = true
                                        }
                                        else -> {
                                            whichBlank(Minimax.computerMove(board), turn)
                                            board = arrayListOf(
                                                blank1, blank2, blank3,
                                                blank4, blank5, blank6,
                                                blank7, blank8, blank9
                                            )
                                            when {
                                                Minimax.isGameWon(board, computer) -> {
                                                    winner = computer
                                                    winnerState = true
                                                }
                                                Minimax.isGameWon(board, me) -> {
                                                    winner = me
                                                    winnerState = true
                                                }
                                                Minimax.isBoardFull(board) -> {
                                                    winner = "Draw"
                                                    winnerState = true
                                                }
                                            }
                                            turn = me
                                        }
                                    }
                                }
                            }
                        })
                }
            }
            Button(
                onClick = { navController?.navigate("enter") },
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.constrainAs(quit) {
                    bottom.linkTo(parent.bottom, margin = 36.dp)
                    linkTo(parent.start, parent.end)
                }
            ) {
                Text(
                    "Quit",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 8.dp)
                )
            }
        }
    }
}


@Composable
fun ChooseLetter(navController: NavController?) {
    var player1 by remember { mutableStateOf("as X") }
    var player2 by remember { mutableStateOf("as O") }
    BackHandler {
        navController?.navigate("enter")
    }
    Scaffold(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(121, 107, 190))
        ) {
            val (players, confirm) = createRefs()
            Column(modifier = Modifier
                .padding(vertical = 20.dp)
                .wrapContentSize()
                .constrainAs(players) {
                    linkTo(parent.top, confirm.top, bottomMargin = 24.dp)
                    linkTo(parent.start, parent.end)
                    width = Dimension.preferredWrapContent
                    height = Dimension.preferredWrapContent
                }
                .verticalScroll(rememberScrollState())) {
                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Image(
                        painter = painterResource(id = R.drawable.boy_vector),
                        contentDescription = null,
                        modifier = Modifier
                            .size((LocalConfiguration.current.screenHeightDp / 5).dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.player_1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(102.dp, 20.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 36.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(208, 198, 255, 255))
                            .padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    if (player1 == "as X") {
                                        player2 = player1
                                        player1 = "as O"
                                    } else if (player1 == "as O") {
                                        player2 = player1
                                        player1 = "as X"
                                    }
                                },
                            colorFilter = ColorFilter.tint(Color(112, 112, 112, 255))
                        )
                        Text(
                            player1,
                            color = Color(36, 32, 65, 255),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.arrow_forward),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    if (player1 == "as X") {
                                        player2 = player1
                                        player1 = "as O"
                                    } else if (player1 == "as O") {
                                        player2 = player1
                                        player1 = "as X"
                                    }
                                },
                            colorFilter = ColorFilter.tint(Color(112, 112, 112, 255))
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.girl_vector),
                        contentDescription = null,
                        modifier = Modifier
                            .size((LocalConfiguration.current.screenHeightDp / 5).dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.player_2),
                        contentDescription = null,
                        modifier = Modifier
                            .size(102.dp, 20.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 36.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(208, 198, 255, 255))
                            .padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    if (player2 == "as X") {
                                        player1 = player2
                                        player2 = "as O"
                                    } else if (player2 == "as O") {
                                        player1 = player2
                                        player2 = "as X"
                                    }
                                },
                            colorFilter = ColorFilter.tint(Color(112, 112, 112, 255))
                        )
                        Text(
                            player2,
                            color = Color(36, 32, 65, 255),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.arrow_forward),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    if (player2 == "as X") {
                                        player1 = player2
                                        player2 = "as O"
                                    } else if (player2 == "as O") {
                                        player1 = player2
                                        player2 = "as X"
                                    }
                                },
                            colorFilter = ColorFilter.tint(Color(112, 112, 112, 255))
                        )
                    }
                }
            }
            Button(
                onClick = {
                    var playerOne = ""
                    var playerTwo = ""
                    when {
                        player1.contains("X") -> {
                            playerOne = "X"; playerTwo = "O"
                        }
                        player1.contains("O") -> {
                            playerOne = "O"; playerTwo = "X"
                        }
                    }
                    navController?.navigate("play/$playerOne/$playerTwo/friend")
                },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier.constrainAs(confirm) {
                    linkTo(parent.start, parent.end)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                }
            ) {
                Text(
                    "Confirm",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DefaultPreview() {
    TicTacToeTheme {
        Play(null, null)
    }
}

@Composable
@Preview(showBackground = true)
fun ChoosePreview() {
    TicTacToeTheme {
        PlayerChoose(null, null)
    }
}

@Composable
@Preview(showBackground = true)
fun PlayPreview() {
    TicTacToeTheme {
        PlayGame(null, null, "x", "o", "friend")
    }
}

@Composable
@Preview(showBackground = true)
fun ChooseLetterPreview() {
    TicTacToeTheme {
        ChooseLetter(null)
    }
}

data class Opponent(val image: Int, val text: String, val index: Int)

@Composable
fun Blank(
    index: ArrayList<Int>,
    modifier: Modifier = Modifier,
    turn: String,
    context: Context
) {
    Box(
        modifier
            .size((LocalConfiguration.current.screenWidthDp / 4).dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(208, 198, 255, 255))
    ) {
        context.getSharedPreferences("Game", Context.MODE_PRIVATE).edit().putString("$index", turn)
            .apply()
        Text(
            text = if ((turn == "X" || turn == "O")) turn else "",
            fontSize = 48.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(
                Alignment.Center
            ),
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun friendMode(context: Context): String {
    val vertical = checkVertical(context)
    val horizontal = checkHorizontal(context)
    val diagonal = checkDiagonal(context)
    if (checkBoard(context)) {
        if (vertical.isNullOrEmpty() && horizontal.isNullOrEmpty() && diagonal.isNullOrEmpty())
            return "Draw"
    }
    return vertical ?: horizontal ?: diagonal ?: ""
}

fun updateFriend(context: Context, index: ArrayList<Int>, value: String) {
    context.getSharedPreferences("Game", Context.MODE_PRIVATE).edit().putString("$index", value)
        .apply()
}

fun checkVertical(context: Context): String? {
    val preferences = context.getSharedPreferences("Game", Context.MODE_PRIVATE)
    var winner: String? = null
    when {
        ((preferences.getString("${arrayListOf(1, 0)}", "") == "X") &&
                preferences.getString("${arrayListOf(1, 0)}", "") == "X" &&
                preferences.getString("${arrayListOf(2, 0)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(0, 1)}", "") == "X") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "X" &&
                preferences.getString("${arrayListOf(2, 1)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(0, 2)}", "") == "X") &&
                preferences.getString("${arrayListOf(1, 2)}", "") == "X" &&
                preferences.getString("${arrayListOf(2, 2)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(1, 0)}", "") == "O") &&
                preferences.getString("${arrayListOf(1, 0)}", "") == "O" &&
                preferences.getString("${arrayListOf(2, 0)}", "") == "O") -> winner = "O"
        ((preferences.getString("${arrayListOf(0, 1)}", "") == "O") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "O" &&
                preferences.getString("${arrayListOf(2, 1)}", "") == "O") -> winner = "O"
        ((preferences.getString("${arrayListOf(0, 2)}", "") == "O") &&
                preferences.getString("${arrayListOf(1, 2)}", "") == "O" &&
                preferences.getString("${arrayListOf(2, 2)}", "") == "O") -> winner = "O"
    }
    return winner
}

fun checkHorizontal(context: Context): String? {
    val preferences = context.getSharedPreferences("Game", Context.MODE_PRIVATE)
    var winner: String? = null
    when {
        ((preferences.getString("${arrayListOf(0, 0)}", "") == "X") &&
                preferences.getString("${arrayListOf(0, 1)}", "") == "X" &&
                preferences.getString("${arrayListOf(0, 2)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(1, 0)}", "") == "X") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "X" &&
                preferences.getString("${arrayListOf(1, 2)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(2, 0)}", "") == "X") &&
                preferences.getString("${arrayListOf(2, 1)}", "") == "X" &&
                preferences.getString("${arrayListOf(2, 2)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(0, 0)}", "") == "O") &&
                preferences.getString("${arrayListOf(0, 1)}", "") == "O" &&
                preferences.getString("${arrayListOf(0, 2)}", "") == "O") -> winner = "O"
        ((preferences.getString("${arrayListOf(1, 0)}", "") == "O") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "O" &&
                preferences.getString("${arrayListOf(1, 2)}", "") == "O") -> winner = "O"
        ((preferences.getString("${arrayListOf(2, 0)}", "") == "O") &&
                preferences.getString("${arrayListOf(2, 1)}", "") == "O" &&
                preferences.getString("${arrayListOf(2, 2)}", "") == "O") -> winner = "O"
    }
    return winner
}

fun checkDiagonal(context: Context): String? {
    val preferences = context.getSharedPreferences("Game", Context.MODE_PRIVATE)
    var winner: String? = null
    when {
        ((preferences.getString("${arrayListOf(0, 0)}", "") == "X") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "X" &&
                preferences.getString("${arrayListOf(2, 2)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(0, 2)}", "") == "X") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "X" &&
                preferences.getString("${arrayListOf(2, 0)}", "") == "X") -> winner = "X"
        ((preferences.getString("${arrayListOf(0, 0)}", "") == "O") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "O" &&
                preferences.getString("${arrayListOf(2, 2)}", "") == "O") -> winner = "O"
        ((preferences.getString("${arrayListOf(0, 2)}", "") == "O") &&
                preferences.getString("${arrayListOf(1, 1)}", "") == "O" &&
                preferences.getString("${arrayListOf(2, 0)}", "") == "O") -> winner = "O"
    }
    return winner
}

fun checkBoard(context: Context): Boolean {
    val preferences = context.getSharedPreferences("Game", Context.MODE_PRIVATE)
    for (column in 0..2) {
        for (row in 0..2) {
            if (preferences.getString("${arrayListOf(column, row)}", "") == "")
                return false
        }
    }
    return true
}

@Composable
fun Dialog(
    dialogState: Boolean,
    onDismissRequest: (dialogState: Boolean) -> Unit,
    winner: String,
    computer: String? = null,
    activity: MainActivity?,
    navController: NavController?,
    mode: String
) {
    if (dialogState) {
        AlertDialog(
            backgroundColor = Color.White,
            onDismissRequest = { onDismissRequest(true) },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            buttons = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        if (winner == "X" || winner == "O") {
                            if (mode == "friend") {
                                Image(
                                    painter = painterResource(id = R.drawable.winner),
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            } else if (mode == "computer") {
                                if (winner == computer) {
                                    Image(
                                        painter = painterResource(id = R.drawable.sad),
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.winner),
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        } else if (winner == "Draw") {
                            Image(
                                painter = painterResource(id = R.drawable.draw),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            OutlinedButton(
                                onClick = { activity?.finish() },
                                border = BorderStroke(2.dp, Color(121, 107, 190)),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "   Quit   ",
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                            Button(
                                onClick = {
                                    navController?.navigate("choose")
                                },
                                colors = ButtonDefaults.buttonColors(Color(121, 107, 190)),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "Play again",
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            },
            title = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    if (winner == "X" || winner == "O") {
                        if (mode == "friend") {
                            Text(
                                "$winner is winner",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontSize = 36.sp,
                            )
                        } else {
                            if (winner == computer) {
                                Text(
                                    "You lost the game",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 36.sp,
                                )
                            } else {
                                Text(
                                    "You are winner",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 36.sp,
                                )
                            }
                        }
                    } else if (winner == "Draw") {
                        Text(
                            "Tie",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 36.sp,
                        )
                    }
                }
            },
            text = null,
            shape = RoundedCornerShape(15.dp)
        )
    }
}

@Composable
fun Gender(navController: NavController?, context: Context) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(121, 107, 190))
        ) {
            var boyState by remember { mutableStateOf(true) }
            var girlState by remember { mutableStateOf(false) }
            val (gender, row, confirm) = createRefs()
            Text(
                "Gender",
                fontFamily = FontFamily(Font(R.font.nature_beauty)),
                fontSize = 56.sp,
                color = Color.White,
                modifier = Modifier.constrainAs(gender) {
                    linkTo(parent.start, parent.end)
                    linkTo(parent.top, parent.bottom, bias = 0.15f)
                }
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(row) {
                    linkTo(parent.start, parent.end)
                    linkTo(parent.top, parent.bottom)
                }) {
                Column(modifier = Modifier
                    .weight(3.0f)
                    .clickable {
                        if (!boyState) {
                            boyState = true
                            girlState = false
                        }
                    }) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.boy_circled),
                            contentDescription = null,
                            modifier = Modifier
                                .size((LocalConfiguration.current.screenWidthDp / 3).dp)
                                .align(Alignment.Center)
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = boyState,
                            enter = fadeIn(tween(250)),
                            exit = fadeOut(tween(250)),
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(62, 241, 105, 255))
                                    .align(Alignment.BottomEnd)
                                    .border(2.dp, Color.Black, RoundedCornerShape(100.dp)),
                            )
                        }
                    }
                    Text(
                        "Male",
                        fontFamily = FontFamily(Font(R.font.nature_beauty)),
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Column(modifier = Modifier
                    .weight(3.0f)
                    .clickable {
                        if (!girlState) {
                            boyState = false
                            girlState = true
                        }
                    }) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.girl_circled),
                            contentDescription = null,
                            modifier = Modifier
                                .size((LocalConfiguration.current.screenWidthDp / 3).dp)
                                .align(Alignment.Center)
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = girlState,
                            enter = fadeIn(tween(250)),
                            exit = fadeOut(tween(250)),
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(62, 241, 105, 255))
                                    .align(Alignment.BottomEnd)
                                    .border(2.dp, Color.Black, RoundedCornerShape(100.dp)),
                            )
                        }
                    }
                    Text(
                        "Female",
                        fontFamily = FontFamily(Font(R.font.nature_beauty)),
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Button(
                onClick = {
                    if (girlState && !boyState) {
                        context.getSharedPreferences("Game", Context.MODE_PRIVATE).edit()
                            .putInt("playerImageCircled", R.drawable.girl_circled)
                            .putInt("playerImage", R.drawable.girl).apply()
                    } else if (!girlState && boyState) {
                        context.getSharedPreferences("Game", Context.MODE_PRIVATE).edit()
                            .putInt("playerImageCircled", R.drawable.boy_circled)
                            .putInt("playerImage", R.drawable.boy).apply()
                    }
                    context.getSharedPreferences("Game", Context.MODE_PRIVATE).edit()
                        .putBoolean("genderDone", true).apply()
                    navController?.popBackStack()
                    navController?.navigate("enter")
                },
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.constrainAs(confirm) {
                    linkTo(row.bottom, parent.bottom)
                    linkTo(parent.start, parent.end)
                }
            ) {
                Text(
                    "Confirm",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 8.dp)
                )
            }
        }
    }
}