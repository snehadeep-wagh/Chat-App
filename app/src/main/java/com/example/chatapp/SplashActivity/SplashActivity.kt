package com.example.chatapp.SplashActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.chatapp.MainActivity
import com.example.chatapp.Profile.ProfileActivity
import com.example.chatapp.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = auth.currentUser

        if(currentUser == null)
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else {
            val phoneNumber: String = auth.currentUser!!.phoneNumber.toString()
            db.collection("Users").document(phoneNumber!!)
                .get()
                .addOnSuccessListener {
                    val name: String = it.data?.get("name").toString()
                    if(name == "")
                    {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener{
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }
}