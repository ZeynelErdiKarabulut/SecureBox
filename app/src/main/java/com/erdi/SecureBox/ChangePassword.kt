package com.erdi.SecureBox

import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.MasterKey
import android.widget.EditText
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import com.erdi.SecureBox.R
import androidx.security.crypto.EncryptedSharedPreferences
import android.widget.ImageButton
import android.view.WindowManager
import com.erdi.SecureBox.ChangePassword
import android.widget.Toast
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import java.io.IOException
import java.lang.Exception
import java.security.GeneralSecurityException

class ChangePassword : AppCompatActivity() {
    val PREFS_NAME = "appEssentials"
    var TYPE_PASS_1 = "PIN"
    var TYPE_PASS_2 = "PASSWORD"
    var PREF = "HASH"
    var masterKey: MasterKey? = null

    //  String PREF_VAL;
    var old_password_et: EditText? = null
    var new_password_1_et: EditText? = null
    var new_password_2_et: EditText? = null
    var submit: Button? = null
    var old_password: String? = null
    var new_password_1: String? = null
    var new_password_2: String? = null
    var TYPE_PASSWORD: String? = null
    var sharedPreferences: SharedPreferences? = null
    var PREF_NAME = "Settings"
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        old_password_et = findViewById(R.id.old_password)
        new_password_1_et = findViewById(R.id.change_password_1)
        new_password_2_et = findViewById(R.id.change_password_2)
        submit = findViewById(R.id.submit)

        // Encrypted SharedPrefs
        try {
            //x.security
            masterKey = MasterKey.Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            //init sharedpPef
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
        if (sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            try {
                val copyImage = findViewById<ImageButton>(R.id.copy)
                copyImage.isEnabled = false
            } catch (e: Exception) {
                e.stackTrace
            }
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        TYPE_PASSWORD = intent.getStringExtra(EXTRA_TYPE_PASS)
        preBuiltFormalities(TYPE_PASSWORD)
        submit!!.setOnClickListener(View.OnClickListener {
            if (validate()) {
                savePassword(new_password_1)
                Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun preBuiltFormalities(TYPE: String?) {
        if (TYPE == TYPE_PASS_1) {
            new_password_1_et!!.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            new_password_2_et!!.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            new_password_1_et!!.filters = arrayOf<InputFilter>(LengthFilter(4))
            new_password_2_et!!.filters = arrayOf<InputFilter>(LengthFilter(4))
        } else if (TYPE == TYPE_PASS_2) {
            new_password_1_et!!.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            new_password_2_et!!.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            new_password_1_et!!.filters = arrayOf<InputFilter>(LengthFilter(16))
            new_password_2_et!!.filters = arrayOf<InputFilter>(LengthFilter(16))
        }
    }

    private fun validate(): Boolean {

        //Taking input to string
        old_password = old_password_et!!.text.toString()
        new_password_1 = new_password_1_et!!.text.toString()
        new_password_2 = new_password_2_et!!.text.toString()

        //Fetching hash from sharedPref
        val PREF_VAL = sharedPreferences!!.getString(PREF, "0")
        Log.d(PREF, PREF_VAL!!)
        if (PREF_VAL != old_password) {
            old_password_et!!.requestFocus()
            old_password_et!!.error = "Wrong Password"
            Log.d(PREF, "Previous: " + PREF_VAL + "Previous password: " + old_password)
            return false
        }
        if (new_password_1 != new_password_2) {
            new_password_2_et!!.requestFocus()
            new_password_2_et!!.error = "Password mismatch"
            return false
        }
        return true
    }

    private fun savePassword(password: String?) {
        val editor = sharedPreferences!!.edit()
        editor.putString(PREF, password).apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // app icon in action bar clicked; goto parent activity.
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_TYPE_PASS = "com.erdi.SecureBox.EXTRA_TYPE_PASS"
    }
}