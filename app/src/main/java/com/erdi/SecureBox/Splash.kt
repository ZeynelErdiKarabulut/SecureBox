package com.erdi.SecureBox

import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.erdi.SecureBox.Splash
import com.erdi.SecureBox.R
import android.widget.TextView
import androidx.security.crypto.EncryptedSharedPreferences
import android.content.Intent
import com.erdi.SecureBox.Welcome
import com.erdi.SecureBox.MLock
import com.erdi.SecureBox.Home
import android.widget.Toast
import android.annotation.TargetApi
import android.os.Build
import android.app.Activity
import android.os.Handler
import android.view.WindowManager
import androidx.core.content.ContextCompat
import java.io.IOException
import java.security.GeneralSecurityException

class Splash : AppCompatActivity() {
    val PREFS_NAME = "appEssentials"
    var PREF_KEY = "MASTER_PASSWORD"
    var PREF_DARK = "DARK_THEME"
    var PREF_KEY_FRUN = "FIRST RUN"
    var masterKey: MasterKey? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val UIPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val UI = UIPref.getBoolean(PREF_DARK, false)
        if (UIPref.getBoolean(PREF_DARK, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setStatusBarGradiant(this)
        setContentView(R.layout.activity_splash)
        val password_manager = findViewById<TextView>(R.id.password_manager)
        password_manager.text = "Password Manager"
        Handler().postDelayed({ // Encrypted SharedPrefs
            try {
                //x.security
                masterKey =
                    MasterKey.Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()
                //init sharedPef
                sharedPreferences = EncryptedSharedPreferences.create(
                    applicationContext,
                    PREFS_NAME,
                    masterKey!!,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val askPasswordLaunchState = sharedPreferences!!.getBoolean(PREF_KEY, true)
            val firstRun = sharedPreferences!!.getBoolean(PREF_KEY_FRUN, true)
            if (firstRun) {
                startActivity(Intent(this@Splash, Welcome::class.java))
            } else {
                if (askPasswordLaunchState) {
                    startActivity(Intent(this@Splash, MLock::class.java))
                } else {
                    startActivity(Intent(this@Splash, Home::class.java))
                    Toast.makeText(
                        applicationContext,
                        "Consider using password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        private const val SPLASH_TIME_OUT = 3000

        // Gradient on statusbar
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun setStatusBarGradiant(activity: Activity) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(activity, R.color.bg_color_splash)
            //window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            //window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }
}