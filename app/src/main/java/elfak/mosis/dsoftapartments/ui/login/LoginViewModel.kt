package elfak.mosis.dsoftapartments.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.dsoftapartments.R
import elfak.mosis.dsoftapartments.data.User

class LoginViewModel() : ViewModel() {

    private val auth : FirebaseAuth = Firebase.auth

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   _loginResult.value = LoginResult(isSuccess = true)
                } else {
                   _loginResult.value = LoginResult(isSuccess = false, message = "Incorrect email or password!")
                }
            }
            .addOnFailureListener {
                Log.d("Auth", "Failure")
            }


    }
}