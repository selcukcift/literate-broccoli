package com.example.myapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object FormSubmitter {
    private const val FORM_URL = "https://example.com/form"
    private const val OPEN_RADIO_1_ID = "open_radio_1"
    private const val OPEN_RADIO_2_ID = "open_radio_2"
    private const val CLOSE_RADIO_ID = "close_radio"
    private const val TEXT_BOX_ID = "text_box"
    private const val SUBMIT_BUTTON_ID = "submit_button"

    suspend fun submitForm(isOpen: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(FORM_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
    
            val postData = buildString {
                append("${OPEN_RADIO_1_ID}=on&")
                append("${OPEN_RADIO_2_ID}=on&")
                if (isOpen) {
                    append("${OPEN_RADIO_1_ID}=on&")
                } else {
                    append("${CLOSE_RADIO_ID}=on&")
                }
                append("${TEXT_BOX_ID}=Sc06&")
                append("${SUBMIT_BUTTON_ID}=Submit")
            }
    
            println("Submitting form with data: $postData") // Debug print
    
            connection.outputStream.use { it.write(postData.toByteArray()) }
    
            val responseCode = connection.responseCode
            println("Response code: $responseCode") // Debug print
    
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("Response: $response") // Debug print
            }
    
            return@withContext responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception occurred: ${e.message}") // Debug print
            return@withContext false
        }
    }
}