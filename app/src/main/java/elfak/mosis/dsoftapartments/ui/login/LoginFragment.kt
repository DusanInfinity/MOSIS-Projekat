package elfak.mosis.dsoftapartments.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.dsoftapartments.R
import elfak.mosis.dsoftapartments.data.User
import elfak.mosis.dsoftapartments.databinding.FragmentLoginBinding
import elfak.mosis.dsoftapartments.model.UserViewModel


class LoginFragment : Fragment() {


    private val loginViewModel: LoginViewModel by activityViewModels()

    lateinit var database: DatabaseReference
    private val userViewModel : UserViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!



    // Ovo sluzi da kad si na login fragmentu kad kliknes back dugme
    // da te ne vrati na bilo koji drugi fragment nego da izadje iz aplikacije
    private val callback = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText : EditText = binding.loginEmail
        val passwordEditText : EditText = binding.loginPassword
        val loginButton : Button = binding.buttonLogin
        val registerButton : Button = binding.buttonRegister
        val loadingProgressBar = binding.loading
        var calledLogin = false


        loginButton.setOnClickListener{
            if (emailEditText.text.isEmpty()) {
                binding.loginEmailTextLayout.error = "Email address is required"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.text).matches()) {
                binding.loginEmailTextLayout.error = "Email address has incorrect form"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (passwordEditText.text.isEmpty()) {
                binding.loginPasswordTextLayout.error = "Password is required"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(emailEditText.text.toString(),passwordEditText.text.toString())
            calledLogin=true
        }

        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.loginEmailTextLayout.error = ""
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.loginEmailTextLayout.error = ""
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        registerButton.setOnClickListener{
            findNavController().navigate(R.id.action_LogInFragment_to_RegisterFragment)
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { result ->
                if(calledLogin){
                    loadingProgressBar.visibility = View.GONE
                    if (result.isSuccess) {
                        findNavController().navigate(R.id.action_LogInFragment_to_MapFragment)
                    } else {
                        result.message?.let { Toast.makeText(view.context, it, Toast.LENGTH_LONG).show() }

                        // sakrivanje tastature !!!!!!
//                        val hide = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                        hide.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    calledLogin=false
                }
            })
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStart() {
        super.onStart()

        // vezano za back dugme, dodaje se callback, a kad stane fragment treba da se obrise
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onStop() {
        callback.remove()
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }
}

