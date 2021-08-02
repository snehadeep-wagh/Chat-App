package com.example.chatapp.Profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.user_data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

const val REQUEST_CODE = 1000

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    lateinit var name: String
    lateinit var info: String
    val auth = FirebaseAuth.getInstance()
    val storageRef = FirebaseStorage.getInstance()
    var downloadUri: String = ""
    lateinit var thumbUri: String
    val db = FirebaseFirestore.getInstance()
    lateinit var phoneNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

//        phoneNumber = intent.getStringExtra("phoneno").toString()

        phoneNumber = auth.currentUser!!.phoneNumber.toString()
        //Adding Image----
        binding.avatarId.setOnClickListener {

            //Checking if permission is granted----
            checkPermission()
        }


        //listner to button----
        binding.profileSaveButton.setOnClickListener {
            //Getting name, and info
            name = binding.profileName.text.toString()!!
            info = binding.infoid.text.toString()

            if(name.isBlank())
            {
                Toast.makeText(applicationContext, "Name field cannot be kept empty", Toast.LENGTH_LONG).show()
            }
            else
            {
                //Upload user data----
                uploadUserData(name, phoneNumber,info, downloadUri, downloadUri)
            }

        }
    }

    private fun checkPermission() {

        //check if permission is already granted----
        fun readExternalStorage() = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        //check if permission is already granted----
        fun writeExternalStorage() = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        //If permission is not granted then add permissions to array----
        val listOfPermissions = arrayListOf<String>()

        if (!readExternalStorage()) {
            listOfPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!writeExternalStorage()) {
            listOfPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        //Check if list is not empty, if yes then ask for permissions----
        if (listOfPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listOfPermissions.toTypedArray(), 1001)
        }
        else{
            //Choose image from phone----
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(applicationContext, "DONE IMAGE", Toast.LENGTH_LONG).show()
            data?.data?.let {

                binding.avatarId.setImageURI(it)

                //Upload Image to firebase----
                uploadImage(it)


            }
        }
    }

    //function to upload user data to firebase
    private fun uploadUserData(name: String, toString: String, info: String, downloadUri: String, downloadUri1: String) {
        val ref = db.collection("Users").document(phoneNumber)
            .set(User(name, phoneNumber, downloadUri, downloadUri, info))
            .addOnCompleteListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
    }

    // function to upload image to firebase and get link----
    private fun uploadImage(it: Uri) {
        binding.profileSaveButton.isEnabled = false
        val ref = storageRef.reference.child("uploads/" +auth.uid.toString()+"profileImage")
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask { task ->
            if(!task.isSuccessful)
            {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    downloadUri = task.result.toString()
                    Log.i("url", downloadUri)
                }
                else
                {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        binding.profileSaveButton.isEnabled = true
    }


}
