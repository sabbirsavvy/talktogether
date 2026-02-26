package com.sabbir.talktogether.domain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechManager(private val context: Context) {

    private var recognizer: SpeechRecognizer? = null

    // These are the two callbacks we care about:
    // onPartial  → called while someone is still speaking (live text)
    // onFinal    → called when there's a pause (turn is complete)
    // onError    → something went wrong, we'll auto restart
    var onPartialResult: (String) -> Unit = {}
    var onFinalResult: (String) -> Unit = {}
    var onError: (Int) -> Unit = {}

    fun startListening() {
        // Create a fresh recognizer each time
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: return
                if (text.isNotBlank()) onPartialResult(text)
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: return
                if (text.isNotBlank()) onFinalResult(text)
                // Auto restart so we keep listening continuously
                startListening()
            }

            override fun onError(error: Int) {
                onError(error)
                // Auto restart on most errors so app doesn't go silent
                startListening()
            }

            // These are required by the interface but we don't need them yet
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer?.startListening(intent)
    }

    fun stopListening() {
        recognizer?.stopListening()
        recognizer?.destroy()
        recognizer = null
    }
}