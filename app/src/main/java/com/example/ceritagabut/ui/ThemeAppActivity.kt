package com.example.ceritagabut.ui

import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ceritagabut.R
import com.example.ceritagabut.databinding.ActivityThemeAppBinding
import com.example.ceritagabut.theme.ThemeFactory
import com.example.ceritagabut.theme.ThemeModel
import com.example.ceritagabut.theme.ThemePreferences
import com.example.ceritagabut.theme.ThemeSave
import kotlinx.coroutines.launch

class ThemeAppActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private var _bindingApps: ActivityThemeAppBinding? = null
    private val bindingApps get() = _bindingApps!!

    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

    private lateinit var themePreferences: ThemePreferences
    private lateinit var themeSave: ThemeSave
    private lateinit var settingAppViewModel: ThemeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _bindingApps = ActivityThemeAppBinding.inflate(layoutInflater)
        setContentView(bindingApps.root)

        themePreferences = ThemePreferences.getAppInstances(dataStore)
        themeSave = ThemeSave.getUserInstances(themePreferences)
        settingAppViewModel = ViewModelProvider(this, ThemeFactory.getInstance(this, themeSave)).get(
            ThemeModel::class.java)

        bindingApps.switchDarkMode.setOnCheckedChangeListener(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingAppViewModel.getAppTheme.collect { state ->
                        if (state) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        bindingApps.switchDarkMode.isChecked = state
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.themeapp)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.switch_dark_mode -> settingAppViewModel.saveThemeSetting(isChecked)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        _bindingApps = null
        super.onDestroy()
    }
}