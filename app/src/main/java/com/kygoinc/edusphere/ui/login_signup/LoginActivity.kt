package com.kygoinc.edusphere.ui.login_signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kygoinc.edusphere.MainActivity
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.txvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Snackbar.make(view, "Login successful", Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.btnLogin).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Snackbar.make(view, it.exception.toString(), Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.btnLogin).show()
                    }
                }
            } else {
                Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.btnLogin).show()
            }
        }
        //        Underline for txvSignUp
        binding.txvSignup.paint.isUnderlineText = true

    }
}