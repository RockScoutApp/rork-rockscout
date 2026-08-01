package com.rork.rockscout.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private const val TAG = "Pronunciation"

/**
 * Small wrapper around the platform text-to-speech engine used to read
 * dinosaur names out loud for kids.
 *
 * Created lazily by [rememberSpeaker] and shut down when the composable that
 * owns it leaves the tree, so no engine connection leaks.
 */
class Speaker(context: Context) {

    private var isReady: Boolean = false
    private var pending: String? = null
    var isSpeaking: Boolean by mutableStateOf(false)
        private set

    private val engine: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            runCatching {
                engine.language = Locale.US
                // Slightly slower than normal so young readers can follow along.
                engine.setSpeechRate(0.82f)
                engine.setPitch(1.02f)
            }.onFailure { Log.w(TAG, "TTS voice setup failed: ${it.message}") }
            pending?.let { queued ->
                pending = null
                speak(queued)
            }
        } else {
            Log.w(TAG, "TTS unavailable (status $status)")
        }
    }

    init {
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
            }

            @Deprecated("Required override on older API levels")
            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                isSpeaking = false
            }
        })
    }

    /** Speak [text], interrupting anything currently playing. */
    fun speak(text: String) {
        if (text.isBlank()) return
        if (!isReady) {
            pending = text
            return
        }
        runCatching {
            engine.stop()
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dino-name")
        }.onFailure {
            isSpeaking = false
            Log.w(TAG, "Speak failed: ${it.message}")
        }
    }

    /** Release the engine. Safe to call more than once. */
    fun release() {
        runCatching {
            engine.stop()
            engine.shutdown()
        }
        isSpeaking = false
    }
}

/** Creates a [Speaker] tied to the current composition lifetime. */
@Composable
fun rememberSpeaker(): Speaker {
    val context = LocalContext.current
    val speaker = remember { Speaker(context) }
    DisposableEffect(speaker) {
        onDispose { speaker.release() }
    }
    return speaker
}

/**
 * Turns a phonetic respelling such as "BRAK-ee-oh-sore-us" into syllables the
 * speech engine reads smoothly, and keeps the natural genus name as a fallback
 * when no respelling exists.
 */
fun pronunciationSpeechText(name: String, pronunciation: String): String {
    val cleanName = name.substringBefore(" (").trim()
    if (pronunciation.isBlank()) return cleanName
    val syllables = pronunciation
        .replace('\u2019', '\'')
        .replace(Regex("""[^A-Za-z'\- ]"""), "")
        .split('-', ' ')
        .map { it.trim().lowercase(Locale.US) }
        .filter { it.isNotEmpty() }
    if (syllables.isEmpty()) return cleanName
    return syllables.joinToString("")
}

/**
 * "Say it" pill: shows the phonetic respelling with a speaker button so kids
 * can hear the species name spoken aloud.
 *
 * @param name Display name of the species, used as the spoken fallback
 * @param pronunciation Phonetic respelling, may be blank
 * @param accent Era accent color for the text and icon
 */
@Composable
fun PronunciationRow(
    name: String,
    pronunciation: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val speaker = rememberSpeaker()
    val haptics = LocalHapticFeedback.current
    val speechText = remember(name, pronunciation) {
        pronunciationSpeechText(name, pronunciation)
    }
    val pulse = rememberInfiniteTransition(label = "sayItPulse")
    val ringScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ringScale",
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = 0.14f))
            .clickable(role = Role.Button) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                speaker.speak(speechText)
            }
            .padding(start = 6.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (speaker.isSpeaking) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .scale(ringScale)
                        .alpha(0.35f)
                        .clip(RoundedCornerShape(50))
                        .background(accent),
                )
            }
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = "Hear how to say $name",
                    tint = Color(0xFF0C0F0B),
                    modifier = Modifier.size(15.dp),
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (pronunciation.isNotBlank()) "say it: $pronunciation" else "hear its name",
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}
