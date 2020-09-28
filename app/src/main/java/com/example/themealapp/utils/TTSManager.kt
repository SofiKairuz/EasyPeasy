package com.example.themealapp.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TTSManager(context: Context) {

    private lateinit var mTTS: TextToSpeech
    private var isLoaded: Boolean = false

    init {
        mTTS = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                mTTS.language = Locale("es", "ES")
                isLoaded = true
            }
        })
    }

    fun addQueue(text: String) {
        if (isLoaded) {
            if (mTTS.isSpeaking) mTTS.stop()
            else mTTS.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    fun initQueue(text: String) {
        if (isLoaded) {
            if (mTTS.isSpeaking) mTTS.stop()
            else mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

}