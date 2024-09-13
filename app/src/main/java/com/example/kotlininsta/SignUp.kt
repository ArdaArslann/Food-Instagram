package com.example.kotlininsta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlininsta.databinding.ActivityMainSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivityMainSignupBinding
    private lateinit var intent:Intent
    private lateinit var auth: FirebaseAuth
    private var email:String?=null
    private  var password:String?=null
    private  var username:String?=null
    private val dataBase = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainSignupBinding.inflate(layoutInflater)
         val view = binding.root
        setContentView(view)



        //variables


        auth=Firebase.auth


    }

    fun signUp(view: View) {
        username = binding.usernameEditText.text.toString()
        email = binding.emailEditText.text.toString()
        password = binding.passwordEditText.text.toString()

        if (email != "" && password != ""&&username!="") {
            //password must be greater than 5 digits
            if (password!!.length>5 && email!!.contains("@")) {
                auth.createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { it ->
                        if (it.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            if(auth.currentUser!=null){
                            auth.currentUser!!.sendEmailVerification().addOnCompleteListener(this) {

                                if(it.isSuccessful){
                                    Toast.makeText(this, "Check your email for the verification link!", Toast.LENGTH_LONG).show()

                                    val user = hashMapOf(
                                        "username" to username,
                                        "email" to email,

                                    )

                                    dataBase.collection("Users").document("${auth.currentUser!!.uid}").set(user).addOnSuccessListener {

                                        Log.wtf("errormsg", "successful")
                                    }
                                        .addOnFailureListener { e ->
                                            Log.wtf("errormsg", "Error adding document", e)
                                        }
                                }
                                else{
                                    Toast.makeText(this, "Verification email couldn't sent!", Toast.LENGTH_LONG).show()

                                }
                            }


                            }
                            else{
                                   Toast.makeText(this,"Current user null",Toast.LENGTH_LONG).show()
                            }

                            intent = Intent(this,Signin::class.java)
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }

                        else {
                            Log.wtf("errormsg", "createUserWithEmail:failure", it.exception)
                            Toast.makeText(this, "Sign-Up Failed!", Toast.LENGTH_LONG).show()

                        }
                    }

            } else {
                Toast.makeText(this, "Enter a longer password or check your email!", Toast.LENGTH_LONG)
                    .show()


            }
        }
    }



}