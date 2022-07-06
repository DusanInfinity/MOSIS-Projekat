package elfak.mosis.dsoftapartments.ui.registration

import android.graphics.Bitmap
import android.text.Editable
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import elfak.mosis.dsoftapartments.data.User
import java.io.ByteArrayOutputStream

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.getReference("UserProfileImages")

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult


    fun register(user: User, password: String) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.phoneNumber = "+381${user.phoneNumber}"
                    user.uid = auth.currentUser?.uid.toString()
                    database.collection("Users")
                        .document(user.uid)
                        .set(user)
                        .addOnCompleteListener {
                            uploadImage(user)
                            _registerResult.value =
                                RegisterResult(success = "Your account has been successfully created!")
                        }.addOnFailureListener {
                            _registerResult.value =
                                RegisterResult(error = "Registration failed. Try again!")

                        }
                } else {
                    _registerResult.value = RegisterResult(error = task.exception?.message)
                }
            }.addOnFailureListener {
                Log.e("createUser", "Failed")
            }
    }

    private fun uploadImage(user: User) {
        val imageRef = storageRef.child(user.uid)
        val baos = ByteArrayOutputStream()
        user.imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        imageRef.putBytes(data).addOnCompleteListener {

        }.addOnFailureListener {
            _registerResult.value = RegisterResult(error = "Image upload failed!")
        }
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isPhoneValid(phone: String): Boolean {
        return phone.length in 7..9
    }
    fun isPasswordValid(password: String): Boolean {
        Log.d("pass", "$password, ${password.length}")
        return password.length >= 8
    }


}