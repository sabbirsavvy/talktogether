package com.sabbir.talktogether.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sabbir.talktogether.data.model.CaptionTurn
import com.sabbir.talktogether.domain.SpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CaptionViewModel(application: Application) : AndroidViewModel(application) {

    private val speechManager = SpeechManager(application)

    // The list of completed caption turns shown on screen
    private val _turns = MutableStateFlow<List<CaptionTurn>>(emptyList())
    val turns: StateFlow<List<CaptionTurn>> = _turns

    // The live partial text shown while someone is still speaking
    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText

    // Is the mic currently on?
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    init {
        // Wire up the SpeechManager callbacks
        speechManager.onPartialResult = { text ->
            _partialText.value = text
        }

        speechManager.onFinalResult = { text ->
            _partialText.value = "" // clear the live text
            val newTurn = CaptionTurn(
                text = text,
                isQuestion = isQuestion(text)
            )
            _turns.value = _turns.value + newTurn
        }

        speechManager.onError = { errorCode ->
            // We just let SpeechManager auto-restart, nothing to do here yet
        }
    }

    fun startListening() {
        speechManager.startListening()
        _isListening.value = true
    }

    fun stopListening() {
        speechManager.stopListening()
        _isListening.value = false
        _partialText.value = ""
    }

    // Basic question detection — we'll improve this later
    private fun isQuestion(text: String): Boolean {
        val lower = text.lowercase().trim()
        return lower.endsWith("?") ||
                lower.startsWith("what") ||
                lower.startsWith("where") ||
                lower.startsWith("when") ||
                lower.startsWith("who") ||
                lower.startsWith("why") ||
                lower.startsWith("how") ||
                lower.startsWith("do ") ||
                lower.startsWith("does ") ||
                lower.startsWith("did ") ||
                lower.startsWith("can ") ||
                lower.startsWith("could ") ||
                lower.startsWith("would ") ||
                lower.startsWith("will ") ||
                lower.startsWith("are ") ||
                lower.startsWith("is ") ||
                lower.startsWith("have ") ||
                lower.startsWith("has ")
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.stopListening()
    }
}