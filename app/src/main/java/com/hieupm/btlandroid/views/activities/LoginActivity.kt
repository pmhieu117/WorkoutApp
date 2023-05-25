package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast

class LoginActivity : AppCompatActivity() {
    private lateinit var tvEmail : TextInputEditText
    private lateinit var tvPassword:TextInputEditText
    private lateinit var btLogin : Button
    private lateinit var tvSignUp : TextView
    private lateinit var gethelplogin : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
        setOnClick()
    }

    //fun defind
    private fun init(){
        tvEmail = findViewById(R.id.email)
        tvPassword = findViewById(R.id.password)
        btLogin = findViewById(R.id.btn_login)
        tvSignUp = findViewById(R.id.txt_signup)
        gethelplogin = findViewById(R.id.gethelplogin)
    }
    private fun setOnClick(){
        btLogin.setOnClickListener {
            if(tvEmail.text.toString()==null || tvEmail.text.toString().trim().equals("")){
                Toast(this).showCustomToast("Email is not empty !", this,Constants.CUSTOM_TOAST_WARN)
            }else if(tvPassword.text.toString()==null || tvPassword.text.toString().equals("")){
                Toast(this).showCustomToast("Password is not empty !", this,Constants.CUSTOM_TOAST_WARN)
            }else{
                loginApp();
            }
        }

        tvSignUp.setOnClickListener {
            var intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        gethelplogin.setOnClickListener {
            val emailAddress = tvEmail.text.toString().trim()
            Constants.AUTH.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast(this).showCustomToast("Send a password reset email !", this,Constants.CUSTOM_TOAST_SUCCESS)
                    }
                }
        }
    }

    //fun handler
    private fun loginApp(){
        val email:String = tvEmail.text.toString().trim()
        val password:String = tvPassword.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Hiển thị thông báo cho người dùng rằng định dạng email không hợp lệ
            Toast(this).showCustomToast("The email address is badly formatted.", this, Constants.CUSTOM_TOAST_WARN)
        }else {
            Constants.AUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast(this).showCustomToast("Authentication successful !", this,Constants.CUSTOM_TOAST_SUCCESS)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast(this).showCustomToast("Authentication failed !", this,Constants.CUSTOM_TOAST_ERROR)
                    }
                }
        }
    }
}