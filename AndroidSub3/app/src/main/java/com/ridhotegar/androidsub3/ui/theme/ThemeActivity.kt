package com.ridhotegar.androidsub3.ui.theme

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.ridhotegar.androidsub3.R
import com.ridhotegar.androidsub3.SettingPreferences
import com.ridhotegar.androidsub3.databinding.ActivityThemeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeActivity : AppCompatActivity() {

    private var _binding: ActivityThemeBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.theme)

        val pref = SettingPreferences.getInstance(dataStore)

        lifecycleScope.launchWhenCreated {
            pref.getThemeSetting().collect { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding?.switchTheme?.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding?.switchTheme?.isChecked = false
                }
            }
        }

        binding?.switchTheme?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            lifecycleScope.launch {
                pref.saveThemeSetting(isChecked)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}