package com.example.chatapp.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.chatapp.MainActivity
import com.example.chatapp.Profile.ProfileActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    lateinit var binding: ActivityOtpBinding
    private lateinit var phoneNumber: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val auth = FirebaseAuth.getInstance()
    private lateinit var progressD: AlertDialog
    val db = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)

        progressD = progressDialog("Sending Otp...")

        //AlertDialog-----
        val alert = AlertDialog.Builder(this)

        phoneNumber = intent.getStringExtra("phoneNo").toString()


        //setting message with text----
        binding.displayNumberWithMessage.text = "SMS is send to $phoneNumber \nplease verify it."

        //Enabling next button----
        binding.otpInput.addTextChangedListener{
                if(it?.length == 6)
                {
                    binding.nextButtonId.isEnabled = true
                }

        }

        //Wrong number----
           binding.wrongNumberId.setOnClickListener {
            alert
                .setPositiveButton("yes") { dialog, which ->
                    finish()
                }
                .setMessage("Your entered phone number is $phoneNumber, you really want to change number?")
                .setNegativeButton("no", null)
                .show()
        }

        progressD.show()
        //Authenticate and verify ----
        authenticateWithPhone()
        startVerification()

//        progressDialog().show()


        // resend OTP----
        binding.resendButtonId.setOnClickListener {
            resendOtp()
        }

        //next button----
        binding.nextButtonId.setOnClickListener {
            val code = binding.otpInput.text.toString()
            val credentials = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            registerAfterVerification(credentials)


        }


    }

    fun Context.progressDialog(message: String = "Loading..."): AlertDialog
    {
        val dialog = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_progress_dialog, null)
        val text = dialogView.findViewById<TextView>(R.id.textView)
        text.text = message
        dialog.setView(dialogView)
        dialog.setCancelable(false)
        return dialog.create()
    }


    private fun startVerification() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

//        progressD.show()
        // Start counter----
        countDownCounter().start()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOtp() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()


        // Start counter----
        countDownCounter().start()
        binding.resendButtonId.isEnabled = false
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun authenticateWithPhone()
    {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            override fun onVerificationCompleted(credentials: PhoneAuthCredential) {

                progressD.dismiss()
//                progressDialog().
                val smsCode = credentials.smsCode
                if(!smsCode.isNullOrEmpty())
                {
                    binding.otpInput.setText(smsCode)
                }

                registerAfterVerification(credentials)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressD.dismiss()
                Log.e("error", e.localizedMessage!!)
                Toast.makeText(applicationContext, "Somting went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("onCodeSent", "onCodeSent:$verificationId")
                progressD.dismiss()
                storedVerificationId = verificationId
                resendToken = token
            }

        }
    }

    private fun countDownCounter(timer: Long = 60000) : CountDownTimer
    {
        return object :CountDownTimer(timer, 1000)
        {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val sec: Long = (millisUntilFinished/1000) % 60
                if(sec >= 10)
                {
                    binding.timer.text = "00:$sec"
                }
                else if(sec >= 0)
                {
                    binding.timer.text = "00:0$sec"
                }

            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.timer.text = "00:00"
                binding.resendButtonId.isEnabled = true
            }

        }

    }

    override fun onDestroy() {
        countDownCounter().cancel()
        super.onDestroy()
    }

    fun registerAfterVerification(credentials: PhoneAuthCredential)
    {
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(this){ task ->

                if(task.isSuccessful)
                {
                    //Logged In successfully
                    Toast.makeText(applicationContext, "registered", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, ProfileActivity::class.java)
                        .putExtra("phoneno", phoneNumber)

                    startActivity(intent)
                    finish()
                }
                else
                {
                    if(task.exception is FirebaseAuthInvalidCredentialsException)
                    {
                        AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setTitle("Warning!")
                            .setMessage("Check OTP you have entered")
                            .setPositiveButton("ok", null)
                            .show()
                    }
                }
            }
            .addOnFailureListener{
                if(it is FirebaseAuthInvalidCredentialsException)
                {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setTitle("Warning!")
                        .setMessage("Check OTP you have entered")
                        .setPositiveButton("ok", null)
                        .show()
                }
            }
    }

}
