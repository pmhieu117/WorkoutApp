package com.hieupm.btlandroid.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.custom.showCustomToast

class LoginActivity : AppCompatActivity() {
    private lateinit var tvEmail : TextInputEditText
    private lateinit var tvPassword:TextInputEditText
    private lateinit var btLogin : Button
    private lateinit var tvSignUp : TextView
    private lateinit var gethelplogin : TextView

    //GOOGLE
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnGg : SignInButton

    //Facebook
    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton
    private  val PERMISSIONS = listOf("email", "public_profile")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
        setOnClick()

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                //Toast(this).showCustomToast("Google sign in success", this,Constants.CUSTOM_TOAST_SUCCESS)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast(this).showCustomToast("Google sign in failed", this,Constants.CUSTOM_TOAST_ERROR)

            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    //fun defind
    private fun init(){
        tvEmail = findViewById(R.id.email)
        tvPassword = findViewById(R.id.password)
        btLogin = findViewById(R.id.btn_login)
        tvSignUp = findViewById(R.id.txt_signup)
        gethelplogin = findViewById(R.id.gethelplogin)
        btnGg = findViewById(R.id.btnGg)
        setGooglePlusButtonText(btnGg,"Continue with Google")

        buttonFacebookLogin = findViewById(R.id.btnFb)
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin.setReadPermissions(PERMISSIONS)
        buttonFacebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG_FB, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG_FB, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG_FB, "facebook:onError", error)
                }
            },
        )
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
            if(emailAddress==null || emailAddress.equals("")){
                Toast(this).showCustomToast("Please fill out the email !", this,Constants.CUSTOM_TOAST_WARN)
            }else {
                Constants.AUTH.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast(this).showCustomToast(
                                "Send a password reset email !",
                                this,
                                Constants.CUSTOM_TOAST_SUCCESS
                            )
                        }
                    }
            }
        }

        btnGg.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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

    // GOOGLE FUNC
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Constants.AUTH.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = Constants.AUTH.currentUser
                    Toast(this).showCustomToast("Authentication successful !", this,Constants.CUSTOM_TOAST_SUCCESS)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("GOOGLE","true")
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //Toast(this).showCustomToast("Hello "+user!!.displayName, this,Constants.CUSTOM_TOAST_SUCCESS)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    companion object {
        private const val TAG = "TestGoogleActivity"
        private const val RC_SIGN_IN = 9001
        private const val TAG_FB = "FacebookLogin"
    }

    private fun setGooglePlusButtonText(signInButton : SignInButton, buttonText:String) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (i in 0 until signInButton.childCount) {
            var v:View = signInButton.getChildAt(i);

            if (v is TextView) {
                val tv :TextView = v as TextView
                tv.setText(buttonText);
                return;
            }
        }
    }

    // Facebook Fun
    // [START auth_with_facebook]
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG_FB, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        Constants.AUTH.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = Constants.AUTH.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG_FB, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
    // [END auth_with_facebook]
}