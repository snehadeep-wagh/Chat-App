package com.example.chatapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val et: EditText = binding.phoneId

        et.addTextChangedListener {
            binding.sendOtpButtonId.isEnabled = !(it.isNullOrBlank() || it.length < 10)
        }

        //registering editText with ccp
        binding.ccp.registerCarrierNumberEditText(et)

        //Intent to otpActivity
        binding.sendOtpButtonId.setOnClickListener {
            val phoneNumber: String = binding.ccp.fullNumberWithPlus.toString()

            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra("phoneNo", phoneNumber)
            startActivity(intent)
        }


    }

//    override fun onStart() {
//        val currentUser = auth.currentUser
//        if(currentUser != null)
//        {
//            Toast.makeText(applicationContext, currentUser.phoneNumber, Toast.LENGTH_SHORT).show()
//            Intent(this, MainActivity::class.java).also {
//                startActivity(it)
//            }
//        }
//        super.onStart()
//
//    }

}

