package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast
import com.hieupm.btlandroid.database.FirebaseDatabaseHelper
import com.hieupm.btlandroid.model.User
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity() {
    private lateinit var tvLogin : TextView
//    private lateinit var tvUsername : TextView
    private lateinit var tvEmail : TextView
    private lateinit var tvPassword : TextView
    private lateinit var tvFullName : TextView
    private lateinit var tvBirth : TextView
    private lateinit var tvGender : TextView
    private lateinit var btRegister : Button

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init();
        setOnClick();
    }

    // func define
    private fun init(){
        tvLogin = findViewById(R.id.txt_login)
        tvEmail = findViewById(R.id.email)
        tvPassword = findViewById(R.id.password)
        btRegister = findViewById(R.id.btn_register)
//        tvUsername = findViewById(R.id.username)
        tvFullName = findViewById(R.id.fullname)
        tvGender = findViewById(R.id.gender)
        tvBirth = findViewById(R.id.birth)
        database = Firebase.database.reference
    }
    private fun setOnClick(){
        tvLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btRegister.setOnClickListener {
            val email : String = tvEmail.text.toString().trim()
            val password : String = tvPassword.text.toString().trim()
            //val username : String = tvUsername.text.toString().trim()
            val fullname : String = tvFullName.text.toString().trim()
            val birth : String = tvBirth.text.toString().trim()
            val gender : String = tvGender.text.toString().trim()
            val role : String = "user"

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast(this).showCustomToast("The email address is badly formatted.", this, Constants.CUSTOM_TOAST_WARN)
            }else if (password.length < 6) {
                Toast(this).showCustomToast("Password should be at least 6 characters", this, Constants.CUSTOM_TOAST_WARN)
            }else {
                Constants.AUTH.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val signInMethods = task.result?.signInMethods

                            if (signInMethods.isNullOrEmpty()) {
                                // Email chưa được đăng ký
                                Constants.AUTH.createUserWithEmailAndPassword(
                                    email,
                                    password
                                )
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            Toast(this).showCustomToast(
                                                "createUserWithEmail:success",
                                                this,
                                                Constants.CUSTOM_TOAST_SUCCESS
                                            )
                                            Handler().postDelayed({
                                                // Sign in success, update UI with the signed-in user's information
                                                val new_user = User(
                                                    email,
                                                    password,
                                                    fullname,
                                                    birth,
                                                    gender,
                                                    role
                                                )
                                                val path:String = Constants.USERS + Constants.AUTH.currentUser?.uid.toString()
                                                FirebaseDatabaseHelper.addObject(path, new_user)
                                                val intent = Intent(
                                                    this@RegisterActivity,
                                                    MainActivity::class.java
                                                )
                                                startActivity(intent)
                                            }, 2000)
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast(this).showCustomToast(
                                                "createUserWithEmail:failure",
                                                this,
                                                Constants.CUSTOM_TOAST_ERROR
                                            )
                                        }
                                    }
                            } else {
                                // Email đã được đăng ký
                                Toast(this).showCustomToast(
                                    "Email already exists",
                                    this,
                                    Constants.CUSTOM_TOAST_ERROR
                                )
                            }
                        } else {
                            // Xảy ra lỗi khi kiểm tra email
                            Log.w(
                                "ERROR_CODE",
                                "Error fetching sign-in methods",
                                task.exception
                            )
                        }
                    }
            }
        }
    }
}