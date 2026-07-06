package com.aitor.trivia

import android.app.Application
import com.aitor.trivia.util.ThemeManager

/**
 * TriviaApp.kt
 *
 * Clase Application necesaria para aplicar el tema guardado
 * ANTES de que se cree cualquier Activity.
 *
 * ⚠️ Añadir en AndroidManifest.xml dentro de <application>:
 *      android:name=".TriviaApp"
 */
class TriviaApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
