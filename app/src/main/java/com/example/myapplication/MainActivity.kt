package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RFIDFormApp()
                }
            }
        }
    }
}

@Composable
fun RFIDFormApp() {
    var formState by remember { mutableStateOf(FormState.WAITING) }
    var message by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RFID Form Submission",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Current state: $formState",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                println("Button clicked") // Debug print
                coroutineScope.launch {
                    println("Coroutine launched") // Debug print
                    handleRFIDScan(
                        formState = { formState },
                        updateFormState = { formState = it },
                        updateMessage = { message = it }
                    )
                    println("handleRFIDScan completed") // Debug print
                }
            }
        ) {
            Text("Simulate RFID Scan")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

enum class FormState {
    WAITING, OPEN, CLOSE, SUBMITTED
}

suspend fun handleRFIDScan(
    formState: () -> FormState,
    updateFormState: (FormState) -> Unit,
    updateMessage: (String) -> Unit
) {
    println("handleRFIDScan called") // Debug print
    if (RFIDManager.isValidScanTime()) {
        val isOpen = RFIDManager.isOpenScanTime()
        println("Is open scan time: $isOpen") // Debug print
        val success = FormSubmitter.submitForm(isOpen)
        println("Form submission success: $success") // Debug print
        if (success) {
            updateFormState(FormState.SUBMITTED)
            updateMessage("Form submitted successfully (${if (isOpen) "Open" else "Close"})")
        } else {
            updateMessage("Form submission failed")
        }
    } else {
        updateMessage("Invalid scan time")
    }
    println("Final form state: ${formState()}, Final message: ${updateMessage}") // Debug print
}

suspend fun checkAutoSubmit(formState: MutableState<FormState>, message: MutableState<String>) {
    val currentTime = LocalTime.now()
    val openAutoSubmitStart = LocalTime.of(8, 0)
    val openAutoSubmitEnd = LocalTime.of(8, 30)
    val closeAutoSubmitStart = LocalTime.of(16, 0)
    val closeAutoSubmitEnd = LocalTime.of(16, 30)

    when {
        currentTime.isAfter(openAutoSubmitStart) && currentTime.isBefore(openAutoSubmitEnd) && formState.value == FormState.WAITING -> {
            delay(Random.nextLong(0, 30 * 60 * 1000)) // Random delay up to 30 minutes
            if (formState.value == FormState.WAITING) {
                submitForm(true, formState, message)
            }
        }
        currentTime.isAfter(closeAutoSubmitStart) && currentTime.isBefore(closeAutoSubmitEnd) && formState.value != FormState.SUBMITTED -> {
            delay(Random.nextLong(0, 30 * 60 * 1000)) // Random delay up to 30 minutes
            if (formState.value != FormState.SUBMITTED) {
                submitForm(false, formState, message)
            }
        }
    }
}

suspend fun submitForm(isOpen: Boolean, formState: MutableState<FormState>, message: MutableState<String>) {
    val success = FormSubmitter.submitForm(isOpen)
    if (success) {
        formState.value = FormState.SUBMITTED
        message.value = "Form auto-submitted successfully (${if (isOpen) "Open" else "Close"})"
    } else {
        message.value = "Form auto-submission failed"
    }
}