package elfak.mosis.dsoftapartments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.dsoftapartments.data.User
import elfak.mosis.dsoftapartments.databinding.LoginFragmentBinding
import elfak.mosis.dsoftapartments.model.UserViewModel


class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding? = null


    lateinit var database: DatabaseReference
    private val userViewModel : UserViewModel by activityViewModels()
    private lateinit var auth : FirebaseAuth


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton : Button = requireView().findViewById(R.id.button_login)
        loginButton.setOnClickListener{
            performLogin()
        }

        val registerButton : Button = requireView().findViewById(R.id.button_register)
        registerButton.setOnClickListener{
            view.findNavController().navigate(R.id.action_LogInFragment_to_RegisterFragment)
        }
    }

    private fun performLogin() {
        if (auth.currentUser == null){
            Log.d("firebase", "prazno")
        }
        else{
            Log.d("firebase", auth.currentUser!!.uid)
        }

        val emailEditText : EditText = requireView().findViewById(R.id.login_email)
        val passwordEditText : EditText = requireView().findViewById(R.id.login_password)

        val email = emailEditText.text.trim().toString()
        val password = passwordEditText.text.trim().toString()

        if (email.isEmpty()  || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.requestFocus()
            return
        }
        if (password.isEmpty()){
            passwordEditText.requestFocus()
            return
        }


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if (user != null) {
                        database = Firebase.database.getReference("Users")
                        database.child(user.uid).get().addOnCompleteListener {
                            val result = it.result.getValue(User::class.java)
                            userViewModel.currentUser = result
                            // tek da ode u mapFragment kada su podaci pribavljeni
                            findNavController().navigate(R.id.action_LogInFragment_to_MapFragment)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("firebase", "signIn:failure", task.exception)
                }
            }
            .addOnFailureListener {
                Log.d("firebase", "Failure")
            }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }
}

