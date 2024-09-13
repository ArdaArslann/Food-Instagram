package com.example.kotlininsta
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlininsta.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID


class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permisisonLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri?=null
    private lateinit var storage:FirebaseStorage
    private lateinit var fireStore:FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root


        auth = Firebase.auth
        fireStore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()

        setContentView(view)


    }

    fun openGallery(view:View){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            //ANDROID 33+
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
                //permission denied
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            //request permission
                            permisisonLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()

                }
                else{
                    //request permission
                    permisisonLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }
            }

            else{
                //permission granted
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
                binding.chooseImageTxt.text =""

            }

        }

        else{
            //android 33-
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //permission denied
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            //request permission
                            permisisonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()

                }
                else{
                    //request permission
                    permisisonLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }
            }

            else{
                //permission granted
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
                binding.chooseImageTxt.text =""

            }

        }

    }

    fun upload(view:View) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if (auth.currentUser != null) {
            if (selectedPicture != null) {
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                    val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadURL = it.toString()

                        Firebase.firestore.collection("Users").document(auth.currentUser!!.uid)
                            .get().addOnSuccessListener {
                                val userName = it.get("username")

                                val time = Calendar.getInstance().time
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                                val current = formatter.format(time)

                                var post = hashMapOf<String, Any>(
                                    "downloadURL" to downloadURL,
                                    "foodName" to binding.foodNameEditText.text.toString(),
                                    "foodDescription" to binding.foodDescpEditText.text.toString(),
                                    "date" to current.toString(),
                                    "likeCounter" to 0,
                                    "userName" to userName.toString(),
                                    "postUUID" to uuid.toString(),
                                    )

                                        fireStore.collection("Posts").document(uuid.toString())
                                            .set(post).addOnSuccessListener() {

                                                finish()
                                            }.addOnFailureListener(this) {
                                                Toast.makeText(
                                                    this,
                                                    it.localizedMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                val map = hashMapOf(
                                    "con" to "okay"
                                )
                                Firebase.firestore.collection("Posts").document(uuid.toString()).collection("likedByThem").add(map).addOnSuccessListener(){

                                }.addOnFailureListener(this){
                                    Log.wtf("errormsg",it.localizedMessage)
                                }

                                    }.addOnFailureListener() {
                                        Toast.makeText(
                                            this,
                                            it.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }


                            }


                    }
                }
            }

        }

    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->

            if(result.resultCode== RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult!=null){
                    selectedPicture = intentFromResult.data

                    selectedPicture?.let {
                        binding.pickImageView.setImageURI(selectedPicture)
                    }

                }
            }
        }

        permisisonLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
                binding.chooseImageTxt.text =""
            }
            else{
                Toast.makeText(this,"Permission needed!",Toast.LENGTH_LONG).show()
            }
        }
    }
}