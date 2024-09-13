package com.example.kotlininsta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlininsta.databinding.ActivitySigninBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Signin : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var intentt: Intent
    private lateinit var auth: FirebaseAuth
    private var email: String? = null
    private var password: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var isChecked:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySigninBinding.inflate(layoutInflater)
        val intent = binding.root
        setContentView(intent)


        //variables

        val dataBase = Firebase.firestore
        auth = Firebase.auth

        sharedPreferences = this.getSharedPreferences("com.example.kotlininsta", Context.MODE_PRIVATE)
        isChecked = sharedPreferences.getString("isChecked","false").toString()


        intentt = Intent(this,FeedActivity::class.java)

        if(isChecked == "true" && auth.currentUser!=null) {
            if (auth.currentUser!!.isEmailVerified) {
                startActivity(intentt)
                finish()
            }
        }


    }

    fun register(view: View) {
        intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        intent = Intent(this,FeedActivity::class.java)
        val intentToUpload = Intent(this,UploadActivity::class.java)
        email = binding.emailEditText.text.toString()
        password = binding.passwordEditText.text.toString()

        if (email!="" && password != "") {
            auth.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { it ->
                if (auth.currentUser != null) {
                        if (it.isSuccessful) {
                            if (auth.currentUser!!.isEmailVerified){

                                if(binding.rememberMeCheckBox.isChecked){
                                       sharedPreferences.edit().putString("isChecked","true").apply()
                                }

                                else{
                                    //do nothing
                                }
                                Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show()

                                startActivity(intent)

                                finish()
                            }
                            else{
                                Toast.makeText(this, "Verify your account first!", Toast.LENGTH_LONG).show()

                            }

                        } else {
                            Log.w("errormsg", "signInWithEmail:failure", it.exception)

                            Toast.makeText(
                                this,
                                "Authentication failed!",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                }
            }
        }

        }

    fun forgotPassword(view: View){
        if(binding.emailEditText.text.isEmpty()){
            Snackbar.make(view,"Enter your email!",Snackbar.LENGTH_LONG).show()
        }
        else {
            Firebase.auth.sendPasswordResetEmail(binding.emailEditText.text.toString())
                .addOnCompleteListener(this) { it ->
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to your email!", Toast.LENGTH_LONG)
                            .show()
                    }
                    else {
                        Toast.makeText(this,"Reset link couldn't sent!",Toast.LENGTH_LONG).show()
                    }
                }
        }



        }

    }

