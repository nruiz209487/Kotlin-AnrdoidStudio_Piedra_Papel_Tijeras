package com.example.piedrapapeltijeras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piedrapapeltijeras.ui.theme.PiedraPapelTijeras

/**
 *MainActivity que implementa  un gameViewModel y un navController con 3 composables GamePage ,PantallaResultado ,PantallaResultadoPartida
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val gameViewModel: GameViewModel = viewModel()  // Inicializar ViewModel aquí
            val navController = rememberNavController()

            PiedraPapelTijeras {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "GamePage",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("GamePage") {
                            GamePage(navController = navController)
                        }
                        composable("PantallaResultado/{eleccionJugador}") { backStackEntry ->
                            val eleccionJugador =
                                backStackEntry.arguments?.getString("eleccionJugador") ?: ""
                            PantallaResultado(eleccionJugador, navController, gameViewModel)
                        }
                        composable("PantallaResultadoPartida/{ganador}") { backStackEntry ->
                            val ganador = backStackEntry.arguments?.getString("ganador") ?: ""
                            PantallaResultadoPartida(ganador, navController, gameViewModel)
                        }
                    }
                }
            }
        }
    }
}

/**
 *Pantalla GamePage la cual se centtra en la escoger piedra papel o tijera a travez de una imagen sy navega a PantallaResultado pasando por parametro eleccionJugador
 */
@Composable
fun GamePage(navController: NavController) {
    var eleccionJugador by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = "Escoge una opción")
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            //papel
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.clickable {
                    eleccionJugador = "papel"
                    navController.navigate("PantallaResultado/$eleccionJugador")
                })
            Spacer(modifier = Modifier.width(40.dp))
            //piedra
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.clickable {
                    eleccionJugador = "piedra"
                    navController.navigate("PantallaResultado/$eleccionJugador")
                })
            Spacer(modifier = Modifier.width(40.dp))
            //tijeras
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.clickable {
                    eleccionJugador = "tijeras"
                    navController.navigate("PantallaResultado/$eleccionJugador")
                })
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 *PantallaResultado recibe eleccionJugador y navega a PantallaResultadoPartida interactua con el  gameViewModel aumenta el num de victorias y al llegar a 5 se navega a la siguiente pagina
 */
@Composable
fun PantallaResultado(
    eleccionJugador: String,
    navController: NavController,
    gameViewModel: GameViewModel
) {
    val numeroAleatorio = remember { (1..3).random() }
    var eleccionMaquina by remember { mutableStateOf("") }
    var resultadoRonda by remember { mutableStateOf("") }


    eleccionMaquina = when (numeroAleatorio) {
        1 -> "piedra"
        2 -> "papel"
        else -> "tijeras"
    }


    resultadoRonda = when {
        eleccionMaquina == eleccionJugador -> "empate"
        eleccionMaquina == "tijeras" && eleccionJugador == "piedra" -> "Jugador"
        eleccionMaquina == "piedra" && eleccionJugador == "tijeras" -> "Maquina"
        eleccionMaquina == "papel" && eleccionJugador == "tijeras" -> "Jugador"
        eleccionMaquina == "tijeras" && eleccionJugador == "papel" -> "Maquina"
        eleccionMaquina == "piedra" && eleccionJugador == "papel" -> "Jugador"
        eleccionMaquina == "papel" && eleccionJugador == "piedra" -> "Maquina"
        else -> "empate"
    }


    LaunchedEffect(resultadoRonda) {
        if (resultadoRonda == "Jugador") {
            gameViewModel.incrementarVictoriasJugador()
        } else if (resultadoRonda == "Maquina") {
            gameViewModel.incrementarVictoriasMaquina()
        }
    }
    LaunchedEffect(gameViewModel.victoriasJugador, gameViewModel.victoriasMaquina) {
        if (gameViewModel.victoriasJugador == 5) {

            navController.navigate("PantallaResultadoPartida/Jugador")
        } else if (gameViewModel.victoriasMaquina == 5) {
            navController.navigate("PantallaResultadoPartida/Maquina")
        }
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "El jugador ha escogido: $eleccionJugador")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Su oponente ha escogido: $eleccionMaquina")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Ha ganado: $resultadoRonda")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Victorias Máquina: ${gameViewModel.victoriasMaquina}")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Victorias Jugador: ${gameViewModel.victoriasJugador}")
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { navController.navigate("GamePage") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Volver")
        }
    }
}

/**
 *Recibe como parametro el ganador y lo muestra al pulsar en el botoon llaama al gameViewModel y setea las variables a 0
 */
@Composable
fun PantallaResultadoPartida(
    ganador: String, navController: NavController, gameViewModel: GameViewModel
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (ganador == "Jugador") {
            Text(text = "Ha ganado el $ganador ")
        } else if (ganador == "Maquina") {
            Text(text = "Ha ganado la $ganador ")
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                gameViewModel.victoriasMaquina = 0
                gameViewModel.victoriasJugador = 0
                navController.navigate("GamePage")
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Volver a jugar.")
        }
    }
}

/**
 *
 */
class GameViewModel : ViewModel() {
    var victoriasMaquina by mutableIntStateOf(0)

    var victoriasJugador by mutableIntStateOf(0)

    fun incrementarVictoriasMaquina() {
        victoriasMaquina++
    }

    fun incrementarVictoriasJugador() {
        victoriasJugador++
    }
}
