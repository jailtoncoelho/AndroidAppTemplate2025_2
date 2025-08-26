package com.ifpr.androidapptemplate.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ifpr.androidapptemplate.MainActivity
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.ui.usuario.CadastroUsuarioActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "signInWithEmail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)

        // Inicializa o Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        loginButton = findViewById(R.id.button_login)
        registerLink = findViewById(R.id.registerLink)

        val registerLink: TextView = findViewById(R.id.registerLink)
        registerLink.setOnClickListener {
            val intent: Intent = Intent(
                applicationContext,
                CadastroUsuarioActivity::class.java
            )
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            signIn(email, password)
        }
    }

    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    updateUI(firebaseAuth.currentUser)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Navegue para a proxima atividade
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                applicationContext,
                "Email ou senha incorretos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}