package app.televault.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.televault.MainViewModel
import app.televault.data.AuthState

/** Log in with your own Telegram account – that is what gives the app access to Saved Messages. */
@Composable
fun SetupScreen(vm: MainViewModel) {
    var phone by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val auth = vm.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("☁️✈️", fontSize = 48.sp)
        Text("TeleVault", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Your own cloud drive inside your Telegram Saved Messages. Log in with your Telegram account and your files are stored in your private Saved Messages chat.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when (auth) {
            AuthState.Starting -> {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                Text("Starting Telegram…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            AuthState.WaitPhone -> {
                Text("Log in", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.trim() },
                    label = { Text("Phone number with country code") },
                    placeholder = { Text("+98 912 345 6789") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
                StepButtons(vm, "Send code") { vm.submitPhone(phone) }
            }

            AuthState.WaitCode -> {
                Text("Enter code", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Use the login code Telegram sent to your Telegram app (or by SMS). The code comes from Telegram, not from this app.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.filter(Char::isDigit) },
                    label = { Text("Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                StepButtons(vm, "Log in") { vm.submitCode(code) }
                TextButton(onClick = { vm.restartLogin() }) { Text("Wrong number? Start over") }
            }

            is AuthState.WaitPassword -> {
                Text("Two-step verification", style = MaterialTheme.typography.titleMedium)
                if (auth.hint.isNotBlank()) Text("Hint: ${auth.hint}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Cloud password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                StepButtons(vm, "Continue") { vm.submitPassword(password) }
            }

            is AuthState.Unsupported -> {
                Text(auth.text, color = MaterialTheme.colorScheme.error)
                Button(onClick = { vm.restartLogin() }) { Text("Try again") }
            }

            is AuthState.Ready -> {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
        }

        val err = vm.setupError
        if (err != null) Text(err, color = MaterialTheme.colorScheme.error)

        Text(
            "Logging in gives this app access to your Telegram account, like any Telegram app. The session stays on this phone, inside the app's private storage, and the app only talks to Telegram's servers. You can end it any time under Settings → Disconnect, or from Telegram → Settings → Devices.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StepButtons(vm: MainViewModel, label: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = onClick, enabled = !vm.busy) { Text(label) }
        if (vm.busy) CircularProgressIndicator(modifier = Modifier.padding(4.dp))
    }
}
