package com.example.aplicaciones_nativas.ui.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicaciones_nativas.R
import com.example.aplicaciones_nativas.ui.screens.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val languages = listOf("en", "es", "fr")
    val currentLanguage = viewModel.getLanguage()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = stringResource(id = R.string.select_language))
        languages.forEach { language ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { scope.launch {
                        viewModel.saveLanguage(language)
                        setLocale(context, language)
                        navController.navigate("home") { popUpTo(0) }
                    } }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentLanguage == language,
                    onClick = { scope.launch {
                        viewModel.saveLanguage(language)
                        setLocale(context, language)
                        navController.navigate("home") { popUpTo(0) }
                    } }
                )
                Text(text = language, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
