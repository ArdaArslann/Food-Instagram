package com.example.kotlininsta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlininsta.adapter.recyclerAdapter
import com.example.kotlininsta.databinding.ActivityFeedBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBase:FirebaseFirestore
    private lateinit var hashMap: HashMap<String,Any>
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var feedrecyclerAdapter: recyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        postArrayList = ArrayList()

        firebaseAuth = Firebase.auth



        binding.toolBarReal.logOut.setOnClickListener(){
            firebaseAuth.signOut()
            val intent = Intent(this,Signin::class.java)
            startActivity(intent)
            finish()
        }



        binding.toolBarReal.addImage.setOnClickListener(){
            val intent = Intent(this,UploadActivity::class.java)
            startActivity(intent)

        }

        dataBase=Firebase.firestore
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedrecyclerAdapter = recyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedrecyclerAdapter

    }

    private fun getData() {

        dataBase.collection("Posts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        postArrayList.clear()
                        val documents = snapshot.documents
                        for(document in documents) {


                            val userName = document.get("userName") as String
                            val foodName= document.get("foodName") as String
                            val foodDescription = document.get("foodDescription") as String
                            val downloadUrl = document.get("downloadURL") as String
                            val date = document.get("date") as String
                            val likeCounter = document.getLong("likeCounter")!!.toInt()
                            val postUUID = document.get("postUUID") as String

                            val post = Post(foodName,userName,foodDescription,downloadUrl,date,likeCounter,postUUID)

                            postArrayList.add(post)


                        }
                        feedrecyclerAdapter.notifyDataSetChanged()



                        }
                    }


                }




            }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.signOut){

            firebaseAuth.signOut()
            val intent = Intent(this,Signin::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}