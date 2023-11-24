package com.kygoinc.edusphere.ui.login_signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.databinding.ActivitySignupBinding
import java.util.Locale

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private lateinit var firebaseUserId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val username = binding.edtSUsername.text.toString()
            val email = binding.edtSEmail.text.toString()
            val password = binding.edtSPassword.text.toString()
            val confirmPass = binding.edtCSPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (password == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseUserId = firebaseAuth.currentUser!!.uid
                                refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId)

                                val userHashMap = HashMap<String, Any>()
                                userHashMap["uid"] = firebaseUserId
                                userHashMap["username"] = username
                                userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/symphony-job-pin.appspot.com/o/user%20(1).png?alt=media&token=32a980a6-1bd0-43b6-aa25-55b57ec527f6"
                                userHashMap["status"] = "offline"
                                userHashMap["about"] = "Hey! I'm new to EduSphere"
                                userHashMap["interests"] = ""
                                userHashMap["phone"] = ""
                                userHashMap["search"] = username.lowercase(java.util.Locale.getDefault())


                                refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        Toast.makeText(this, "There was an error in creating your account. PLease try again", Toast.LENGTH_SHORT ).show()
                                    }

                                }
                                Snackbar.make(
                                    view,
                                    "Registration successful. Please log in",
                                    Snackbar.LENGTH_SHORT
                                ).setAnchorView(R.id.btnSignup).show()


                            } else {
                                Snackbar.make(view, it.exception.toString(), Snackbar.LENGTH_SHORT)
                                    .setAnchorView(R.id.btnSignup).show()
                            }
                        }
                } else {
                    Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.btnSignup).show()
                }
            } else {
                Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.btnSignup).show()
            }

        }
        //        Underline for txvSignUp
        binding.txvSignIn.paint.isUnderlineText = true
    }
}