package elfak.mosis.dsoftapartments

import android.R.attr.bitmap
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.dsoftapartments.data.User
import java.io.ByteArrayOutputStream
import java.util.*


class RegisterFragment : Fragment() {

    private val REQUEST_CODE = 101
    private var imageTaken : Bitmap? = null

    private val auth = FirebaseAuth.getInstance()
    lateinit var database: DatabaseReference

    private var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.getReference("Users")


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val regBtn: Button = requireView().findViewById(R.id.register_button_registration)

        regBtn.setOnClickListener(View.OnClickListener {
            register()
        })


        val cameraBtn: ImageButton = requireView().findViewById(R.id.image_button_add)
        cameraBtn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val takenImage: Bitmap = data?.extras?.get("data") as Bitmap
            val matrix = Matrix()
            matrix.postRotate(90.0F)
            val rotatedImage = Bitmap.createBitmap(takenImage, 0, 0, takenImage.width, takenImage.height, matrix, true)
            imageTaken = rotatedImage
            val imageView: ImageView = requireView().findViewById(R.id.registration_image_view)
            imageView.setImageBitmap(rotatedImage)




        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun register() {
        val nameEditText = requireView().findViewById(R.id.register_name) as EditText
        val emailEditText = requireView().findViewById(R.id.register_email) as EditText
        val phoneEditText = requireView().findViewById(R.id.register_phone) as EditText
        val passwordEditText = requireView().findViewById(R.id.register_password) as EditText

        val name = nameEditText.text.trim().toString()
        val email = emailEditText.text.trim().toString()
        var phone = phoneEditText.text.trim().toString()
        val password = passwordEditText.text.trim().toString()


        if (name.isEmpty()) {
            nameEditText.requestFocus()
            return
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            phoneEditText.requestFocus()
            return
        }
        if (password.isEmpty()) {
            passwordEditText.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    phone = "+381$phone"
                    if (uid != null) {
                        database.child(uid).setValue(User(uid, name, email, phone))
                    }
                    uploadImage()
                    Log.d("createUser", "Successfully signed in $user")
                } else {
                    Log.w("createUser", "Authentication failed")
                }
            }).addOnFailureListener(requireActivity(), OnFailureListener {
            Log.e("createUser", "Failed")
        })
    }

    private fun uploadImage(){
        val storageRef = storage.getReference("Images/${auth.currentUser?.uid}")
        val baos = ByteArrayOutputStream()
        imageTaken?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        storageRef.putBytes(data)
    }


}