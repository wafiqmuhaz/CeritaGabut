package com.example.ceritagabut

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ceritagabut.adapter.ItemAdapters
import com.example.ceritagabut.adapter.LoadingAppStateAdapter
import com.example.ceritagabut.databinding.ActivityMainBinding
import com.example.ceritagabut.factory.AppFactory
import com.example.ceritagabut.maps.MapsActivity
import com.example.ceritagabut.models.AppMainViewModel
import com.example.ceritagabut.models.AppSigninViewModel
import com.example.ceritagabut.ui.AboutAppActivity
import com.example.ceritagabut.ui.AboutMeActivity
import com.example.ceritagabut.ui.AddItemAppActivity
import com.example.ceritagabut.ui.SigningAppActivity
import com.example.ceritagabut.ui.ThemeAppActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var signinViewModel: AppSigninViewModel
    private lateinit var mainViewModel: AppMainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBar()
        setupViewModel()
        setupRecyclerView()
        setupRecyclerViewAdapter()
    }

    private fun setupRecyclerViewAdapter() {
        val adapterItems = ItemAdapters()

        loadingApp(true)

        signinViewModel.getPerson().observe(this) { user ->
            if (user.userItemId.isEmpty()) {
                val intent = Intent(this, SigningAppActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.rvListItemsApp.adapter = adapterItems.withLoadStateFooter(
                    footer = LoadingAppStateAdapter {
                        adapterItems.retry()
                    }
                )
                mainViewModel.getListItems(user.usertoken).observe(this) {
                    adapterItems.submitData(lifecycle, it)
                    loadingApp(false)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutPosition = LinearLayoutManager(this)
        layoutPosition.orientation = LinearLayoutManager.VERTICAL
        binding.rvListItemsApp.layoutManager = layoutPosition
        val dividerDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_minimize_24)
        if (dividerDrawable != null) {
            val itemDecoration = DividerItemDecoration(this, layoutPosition.orientation)
            itemDecoration.setDrawable(dividerDrawable)
            binding.rvListItemsApp.addItemDecoration(itemDecoration)
        } else {
            Log.e(TAG, "Drawable untuk dekorasi item tidak tersedia.")
        }
    }

    private fun setupViewModel() {
        val appFactory = AppFactory.getInstance(this)
        val viewModelProviders = ViewModelProvider(this, appFactory)
        signinViewModel = viewModelProviders.get(AppSigninViewModel::class.java)
        mainViewModel = viewModelProviders.get(AppMainViewModel::class.java)
    }


    private fun appBar() {
        supportActionBar?.title = getString(R.string.app_name)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_goto_map -> {
                val i = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(i)
            }

            R.id.btn_add_items -> {
                val i = Intent(this@MainActivity, AddItemAppActivity::class.java)
                startActivity(i)
            }
            R.id.settings_language -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.setting_app -> {
                val aboutMe = Intent(this@MainActivity, ThemeAppActivity::class.java)
                startActivity(aboutMe)
            }
            R.id.aboutme -> {
                val aboutMe = Intent(this@MainActivity, AboutMeActivity::class.java)
                startActivity(aboutMe)
            }
            R.id.action_logout -> {
                signinViewModel.signoutPerson()
            }
            R.id.aboutapp -> {
                val aboutApp = Intent(this@MainActivity, AboutAppActivity::class.java)
                startActivity(aboutApp)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadingApp(isLoading: Boolean) {
        binding.progressBarApp.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}