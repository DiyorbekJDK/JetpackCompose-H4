package org.diyorbek.jetpackcompose_h4


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.KeyboardShortcutGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.ui.StyledPlayerView
import org.diyorbek.jetpackcompose_h4.ui.theme.JetpackComposeH4Theme
import org.diyorbek.jetpackcompose_h4.ui.theme.Shapes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeH4Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen(getVideoUri())
                }
            }
        }
    }

    private fun getVideoUri(): Uri {
        val rawId = resources.getIdentifier("ground", "raw", packageName)
        val videoUri = "android.resource://$packageName/$rawId"
        return Uri.parse(videoUri)
    }


    @Composable
    fun LoginScreen(videoUri: Uri) {
        val context = LocalContext.current
        val passwordFocusRequester = FocusRequester()
        val focusManager = LocalFocusManager.current
        val exoPlayer = remember { context.buildExoPlayer(videoUri) }



        DisposableEffect(
            AndroidView(
                factory = { it.buildPlayerView(exoPlayer) },
                modifier = Modifier.fillMaxSize()
            )
        ) {
            onDispose {
                exoPlayer.release()
            }
        }



        ProvideWindowInsets {


            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = null,
                    Modifier.size(80.dp),
                    tint = Color.White
                )
                TextInput(InputType.Name, keyboardActions = KeyboardActions(onNext = {
                    passwordFocusRequester.requestFocus()
                }))
                TextInput(InputType.Password, keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }), focusRequester = passwordFocusRequester)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Sing In", Modifier.padding(vertical = 8.dp))
                }
                Divider(
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 48.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Don't have an account?", color = Color.White)
                    TextButton(onClick = {}) {
                        Text(text = "Sign Up")
                    }

                }
            }
        }

    }

    @Composable
    fun TextInput(
        inputType: InputType,
        focusRequester: FocusRequester? = null,
        keyboardActions: KeyboardActions
    ) {

        var value by remember { mutableStateOf("") }

        TextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusOrder(focusRequester ?: FocusRequester()),
            leadingIcon = {
                Icon(
                    imageVector = inputType.icon,
                    contentDescription = null,

                    )
            },
            label = { Text(text = inputType.label) },
            shape = Shapes.small,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = inputType.keyboardOptions,
            visualTransformation = inputType.visualTransformation,
            keyboardActions = keyboardActions
        )

    }

    sealed class InputType(
        val label: String,
        val icon: ImageVector,
        val keyboardOptions: KeyboardOptions,
        val visualTransformation: VisualTransformation
    ) {
        object Name : InputType(
            label = "Username",
            icon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            visualTransformation = VisualTransformation.None
        )

        object Password : InputType(
            label = "Password",
            icon = Icons.Default.Lock,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation()

        )
    }

    private fun Context.buildExoPlayer(uri: Uri) =
        ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
        }

    fun Context.buildPlayerView(exoPlayer: ExoPlayer) =
        StyledPlayerView(this).apply {
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            useController = false
            resizeMode = RESIZE_MODE_ZOOM
        }
}
