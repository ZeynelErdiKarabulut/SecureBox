package com.erdi.SecureBox

import com.himanshurawat.hasher.Hasher.Companion.hash
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences
import android.os.Bundle
import com.erdi.SecureBox.R
import androidx.security.crypto.EncryptedSharedPreferences
import com.erdi.SecureBox.Modify
import android.view.WindowManager
import com.himanshurawat.hasher.HashType
import android.content.Intent
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.erdi.SecureBox.Utils.AESUtils
import java.io.IOException
import java.lang.Exception
import java.security.GeneralSecurityException

class Modify : AppCompatActivity(), View.OnClickListener {
    var newPassword: EditText? = null
    var emailText: TextView? = null
    var oldPassword: TextView? = null
    var provName: String? = null
    var email: String? = null
    var passwd: String? = null
    var decPass: String? = null
    var show_change_password: CheckBox? = null
    var show_password: CheckBox? = null
    var masterKey: MasterKey? = null
    var changePasswordButton: Button? = null
    var updateBtn: Button? = null
    var deleteBtn: Button? = null
    var sharedPreferences: SharedPreferences? = null
    var PREF_NAME = "appEssentials"
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    var newPasswordLayout: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        emailText = findViewById(R.id.modify_email)
        oldPassword = findViewById(R.id.modify_old_password)
        show_password = findViewById(R.id.show_password)
        changePasswordButton = findViewById(R.id.change_password_button)
        newPassword = findViewById(R.id.modify_new_password)
        show_change_password = findViewById(R.id.modify_show_password)
        updateBtn = findViewById(R.id.modify_update)
        deleteBtn = findViewById(R.id.modify_delete)
        updateBtn!!.setEnabled(false)

        // Encrypted SharedPrefs
        try {
            //x.security
            masterKey = MasterKey.Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
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
        if (sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        val sha = sharedPreferences!!.getString("HASH", "0")
        val HASH = hash(sha, HashType.MD5)
        //DECRYPT
        val intent = intent
        provName = intent.getStringExtra(EXTRA_PROVIDER_NAME)
        email = intent.getStringExtra(EXTRA_EMAIL)
        passwd = intent.getStringExtra(EXTRA_ENCRYPT)
        try {
            val decEmail = AESUtils.decrypt(email, HASH)
            decPass = AESUtils.decrypt(passwd, HASH)
            emailText!!.setText(decEmail)
            oldPassword!!.setText(decPass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        show_change_password!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                newPassword!!.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
            } else {
                newPassword!!.setInputType(129)
            }
        })
        show_password!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                oldPassword!!.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
            } else {
                oldPassword!!.setInputType(129)
            }
        })
        updateBtn!!.setOnClickListener(this)
        deleteBtn!!.setOnClickListener(this)
        changePasswordButton!!.setOnClickListener(this)
    }

    private fun changePassword() {
        updateBtn!!.isEnabled = true
        findViewById<View>(R.id.show_password).visibility = View.GONE
        changePasswordButton!!.visibility = View.GONE
        newPasswordLayout = findViewById(R.id.change_password)
        newPasswordLayout!!.setVisibility(View.VISIBLE)
    }

    private fun delete_data() {
        val intent = Intent()
        intent.putExtra(EXTRA_DELETE, true)
        intent.putExtra(EXTRA_EMAIL, email)
        intent.putExtra(EXTRA_ENCRYPT, passwd)
        val id = getIntent().getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun modify_data() {
        val text_old_password: String
        val text_new_password: String
        text_old_password = oldPassword!!.text.toString()
        text_new_password = newPassword!!.text.toString()
        val sha = sharedPreferences!!.getString("HASH", "0")
        val HASH = hash(sha, HashType.MD5)
        if (text_old_password.trim { it <= ' ' }.isEmpty()) {
            oldPassword!!.error = "Required"
            oldPassword!!.requestFocus()
            return
        }
        if (text_new_password.trim { it <= ' ' }.isEmpty()) {
            newPassword!!.error = "Required"
            newPassword!!.requestFocus()
            return
        }
        val intent = Intent()
        intent.putExtra(EXTRA_PROVIDER_NAME, provName)
        intent.putExtra(EXTRA_EMAIL, email)
        try {
            val encPass = AESUtils.encrypt(text_new_password, HASH)
            intent.putExtra(EXTRA_ENCRYPT, encPass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val id = getIntent().getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.modify_update && updateBtn!!.isEnabled) {
            modify_data()
        } else if (v.id == R.id.modify_delete) {
            delete_data()
        } else if (v.id == R.id.change_password_button) {
            changePassword()
        }
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

    fun copy_email(view: View?) {
        if (sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            Toast.makeText(
                this,
                "Secure code mode is Enabled. Copying is not allowed  ",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val textView = findViewById<TextView>(R.id.modify_email)
            val gn_email = textView.text.toString().trim { it <= ' ' }
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Email", gn_email)
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip)
                Toast.makeText(applicationContext, "Email Copied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun copy_password(view: View?) {
        if (sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            Toast.makeText(
                this,
                "Secure code mode is Enabled. Copying is not allowed  ",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val textView = findViewById<TextView>(R.id.modify_old_password)
            val gn_password = textView.text.toString().trim { it <= ' ' }
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Password", gn_password)
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip)
                Toast.makeText(applicationContext, "Password Copied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "MODIFY"
        const val EXTRA_DELETE = "DELETE"
        const val EXTRA_PROVIDER_NAME = "com.erdi.SecureBox.EXTRA_PROVIDER_NAME"
        const val EXTRA_ID = "com.erdi.SecureBox.EXTRA_ID"
        const val EXTRA_ENCRYPT = "com.erdi.SecureBox.EXTRA_ENCRYPT"
        const val EXTRA_EMAIL = "com.erdi.SecureBox.EXTRA_EMAIL"
        const val EXTRA_IV = "com.erdi.SecureBox.EXTRA_IV"
        const val EXTRA_SALT = "com.erdi.SecureBox.EXTRA_SALT"
        private const val PREFS_NAME = "appEssentials"
    }
}