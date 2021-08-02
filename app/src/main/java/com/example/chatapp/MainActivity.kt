package com.example.chatapp

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import com.example.chatapp.Profile.ProfileActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar?.hide()


        // Viewpager mediator for enabling sliding-----
        binding.viewPagerId.adapter = ScreenSliderAdapter(this)
        TabLayoutMediator(
            binding.tabLayoutId,
            binding.viewPagerId,
            TabLayoutMediator.TabConfigurationStrategy{ tab: TabLayout.Tab, position: Int ->
                when(position)
                {
                    0 -> tab.text = "CHATS"
                    else -> tab.text = "PEOPLE"
                }
        }).attach()

    }

}