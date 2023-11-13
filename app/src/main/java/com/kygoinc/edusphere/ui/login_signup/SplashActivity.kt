package com.kygoinc.edusphere.ui.login_signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kygoinc.edusphere.MainActivity
import com.kygoinc.edusphere.R
import java.util.logging.Handler

class SplashActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    private val SPLASH_DELAY_TIME = 7000L
    private val handler = android.os.Handler()
    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        handler.postDelayed({
            if (firebaseUser != null) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_DELAY_TIME)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

    }
}