package com.ProyectoMAD

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ProyectoMAD.MainActivity.ProviderType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp

class authActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        FirebaseApp.initializeApp(this) // Inicialización de Firebase
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setup()
    }
    private fun setup() {
        title = "Authentication"

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val emailText = findViewById<EditText>(R.id.emailText)
        val passwordText = findViewById<EditText>(R.id.passwordText)
        val logInButton = findViewById<Button>(R.id.logInButton)


        signUpButton.setOnClickListener {
            if (emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailText.text.toString(),
                    passwordText.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showHome(task.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                    showAlert()
                }
            } else {
                showAlert()
            }
        }

        logInButton.setOnClickListener {
            if (emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailText.text.toString(),
                    passwordText.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showHome(task.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                    showAlert()
                }
            } else {
                showAlert()
            }
        }
    }
    private fun showAlert() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog=builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: MainActivity.ProviderType) {

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider",provider.name)
        }
        startActivity(mainIntent)
    }
}