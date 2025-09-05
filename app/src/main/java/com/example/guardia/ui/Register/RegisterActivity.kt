package com.example.guardia.ui.register

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.guardia.ui.theme.GuardiaTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardiaTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen(onNavigateBack = { finish() })
                }
            }
        }
    }
}
