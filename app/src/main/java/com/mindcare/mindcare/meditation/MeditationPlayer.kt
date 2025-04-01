package com.mindcare.mindcare.meditation

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MeditationPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition = 0
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    fun prepare(audioUrl: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            setOnPreparedListener { mp ->
                mp.start()
                _isPlaying.value = true
            }
            setOnCompletionListener {
                _isPlaying.value = false
                _progress.value = 1f
            }
            prepareAsync()
        }
    }

    fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.seekTo(currentPosition)
                it.start()
                _isPlaying.value = true
            }
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                currentPosition = it.currentPosition
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        currentPosition = position
    }

    fun updateProgress() {
        mediaPlayer?.let {
            val duration = it.duration
            if (duration > 0) {
                _progress.value = it.currentPosition.toFloat() / duration
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _progress.value = 0f
    }
} 