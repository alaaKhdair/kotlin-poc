package com.example.kotlintask3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.kotlintask3.database.ItemRoomDatabase
import com.example.kotlintask3.model.ItemViewModel
import com.example.kotlintask3.repository.ItemRepository


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var viewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemRepository = ItemRepository(ItemRoomDatabase.getDatabase(this))
        val itemViewModelProviderFactory = ItemViewModelProviderFactory(application,itemRepository)
        viewModel = ViewModelProvider(this, itemViewModelProviderFactory)[ItemViewModel::class.java]

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        val actionBar=supportActionBar
        actionBar?.title="Item's"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
