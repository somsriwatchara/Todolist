package com.watchara.todolist.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.watchara.todolist.R
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {


    private var firebaseAuth :FirebaseAuth?=null

    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        callbackManager = CallbackManager.Factory.create()
        firebaseAuth = FirebaseAuth.getInstance()


        if(AccessToken.getCurrentAccessToken() != null){
            login_facebook.visibility = View.GONE
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        login_facebook.setReadPermissions("email")
        login_facebook.setOnClickListener {
            singIn()

        }


    }

    private fun debugHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                "com.watchara.todolist",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("gggggggggggggg:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }

    }

    private fun singIn() {
        login_facebook.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                login_facebook.visibility = View.GONE
                val i = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(i)
                handleFacebookAccessToken(result!!.accessToken)
                finish()

            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
            }

        })
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(accessToken!!.token)

        firebaseAuth!!.signInWithCredential(credential)
            .addOnFailureListener{ e->
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener { result ->
                val email = result.user.email
//                Toast.makeText(this,"login with Email : $email",Toast.LENGTH_LONG).show()


            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager!!.onActivityResult(requestCode,resultCode,data)
    }



}
