package com.example.whatsappclone.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.example.whatsappclone.R
import com.example.whatsappclone.util.DATA_USERS
import com.example.whatsappclone.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        if (user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_sign_up)

        setTextChangeListener(nameET, nameTil)
        setTextChangeListener(phoneET, phoneTil)
        setTextChangeListener(emailET, emailTil)
        setTextChangeListener(passwordET, passwordTil)
        progressLayout.setOnTouchListener { v, event -> true }
    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }

        })
    }

    fun onSignup(view: View) {
        var proceed = true
        if (nameET.text.isNullOrEmpty()) {
            nameTil.error = "Name is required"
            nameTil.isErrorEnabled = true
            proceed = false
        }

        if (phoneET.text.isNullOrEmpty()) {
            phoneTil.error = "Phone is required"
            phoneTil.isErrorEnabled = true
            proceed = false
        }

        if (emailET.text.isNullOrEmpty()) {
            emailTil.error = "Email is required"
            emailTil.isErrorEnabled = true
            proceed = false
        }

        if (passwordET.text.isNullOrEmpty()) {
            passwordTil.error = "Password is required"
            passwordTil.isErrorEnabled = true
            proceed = false
        }
        if (proceed) {
            progressLayout.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        progressLayout.visibility = View.GONE
                        Toast.makeText(this@SignUpActivity, "Signup error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    } else if (firebaseAuth.uid != null) {
                        val email: String = emailET.text.toString()
                        val phone: String = phoneET.text.toString()
                        val name: String = nameET.text.toString()
                        val user = User(email, phone, name, "Hello, I'm new", "", "")
                        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)
                    }
                    progressLayout.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    progressLayout.visibility = View.GONE
                    e.printStackTrace()
                }

        }

        startActivity(MainActivity.newIntent(this))
        finish()
    }

    fun onLogin(view: View) {
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
