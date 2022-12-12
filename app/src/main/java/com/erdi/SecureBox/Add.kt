package com.erdi.SecureBox

import com.himanshurawat.hasher.Hasher.Companion.hash
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences
import android.os.Bundle
import com.erdi.SecureBox.R
import androidx.security.crypto.EncryptedSharedPreferences
import android.view.WindowManager
import com.himanshurawat.hasher.HashType
import android.content.Intent
import android.app.Activity
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.erdi.SecureBox.Utils.AESUtils
import java.io.IOException
import java.lang.Exception
import java.security.GeneralSecurityException
import java.util.regex.Pattern

class Add : AppCompatActivity(), View.OnClickListener {
    val PREFS_NAME = "appEssentials"
    var providersEmail = arrayOf<String?>(
        "Gmail", "Outlook", "Amazon", "Protonmail", "Yahoo",
        "Apple", "Paypal", "Github", "Spotify", "Stackoverflow",
        "Trello", "Wordpress", "Other"
    )
    var providersSocial = arrayOf<String?>(
        "Facebook", "Instagram", "Twitter", "Medium", "Flickr",
        "Foursquare", "Reddit", "Slack", "Snapchat", "Tinder",
        "Linkedin", "Pinterest", "Tumblr", "Other"
    )
    var masterKey: MasterKey? = null
    var providerNameString: String? = null
    var passwordFromCOPY: String? = null
    var add_button: Button? = null
    var providerName: Spinner? = null
    var prov_tv: TextView? = null
    var provider: String? = null
    var sharedPreferences: SharedPreferences? = null
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    private var email: EditText? = null
    private var password: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //ProgressBar progressBar = findViewById(R.id.progress_bar);
        providerName = findViewById(R.id.provider_name)
        email = findViewById(R.id.add_email)
        password = findViewById(R.id.add_password)
        val checkBox = findViewById<CheckBox>(R.id.add_show_password)
        add_button = findViewById(R.id.add_record)
        prov_tv = findViewById(R.id.prov_tv)


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
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                password!!.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
            } else {
                password!!.setInputType(129)
            }
        }
        add_button!!.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        provider = intent.getStringExtra(EXTRA_PROVIDER)
        if (provider == null) provider = "mail"
        passwordFromCOPY = intent.getStringExtra(PASSWORD)
        assert(provider != null)
        when (provider) {
            "social" -> {
                email!!.hint = "Username/Email"
                val arrayAdapterSocial: ArrayAdapter<*> =
                    ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, providersSocial)
                arrayAdapterSocial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                //Setting the ArrayAdapter data on the Spinner
                providerName!!.adapter = arrayAdapterSocial
                providerName!!.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            providerNameString = parent.getItemAtPosition(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            }
            "wifi" -> {
                prov_tv!!.visibility = View.GONE
                providerName!!.visibility = View.GONE
                email!!.hint = "SSID"
            }
            else -> {
                email!!.hint = "Email"
                password!!.setText(passwordFromCOPY)
                val arrayAdapterEmail: ArrayAdapter<*> =
                    ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, providersEmail)
                arrayAdapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                //Setting the ArrayAdapter data on the Spinner
                providerName!!.adapter = arrayAdapterEmail
                providerName!!.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            providerNameString = parent.getItemAtPosition(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            }
        }
    }

    private fun save_data() {
        val text_email: String
        val text_password: String
        text_email = email!!.text.toString()
        text_password = password!!.text.toString()
        val sha = sharedPreferences!!.getString("HASH", "0")
        val HASH = hash(sha, HashType.MD5)
        if (provider == "mail") {
            if (text_email.trim { it <= ' ' }.isEmpty()) {
                email!!.error = "Required"
                email!!.requestFocus()
                return
            }
            val regex_email =
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
            val pattern = Pattern.compile(regex_email)
            val matcher = pattern.matcher(text_email)
            //TODO Change if
            if (!matcher.matches()) {
                email!!.error = "Enter valid email"
                email!!.requestFocus()
                return
            }
        }
        if (text_password.trim { it <= ' ' }.isEmpty()) {
            password!!.error = "Required"
            password!!.requestFocus()
            return
        }
        val intent = Intent()
        intent.putExtra(EXTRA_PROVIDER_NAME, providerNameString)
        // AES UTILS ENC and DEC
        try {
            val encEmail = AESUtils.encrypt(text_email, HASH)
            val encPass = AESUtils.encrypt(text_password, HASH)
            intent.putExtra(EXTRA_EMAIL, encEmail)
            intent.putExtra(EXTRA_ENCRYPT, encPass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.add_record) {
            save_data()
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

    companion object {
        const val EXTRA_PROVIDER_NAME = "com.erdi.SecureBox.EXTRA_PROVIDER_NAME"
        const val EXTRA_PROVIDER = "com.erdi.SecureBox.EXTRA_PROVIDER"
        const val EXTRA_ENCRYPT = "com.erdi.SecureBox.EXTRA_ENCRYPT"
        const val EXTRA_EMAIL = "com.erdi.SecureBox.EXTRA_EMAIL"
        const val EXTRA_IV = "com.erdi.SecureBox.EXTRA_IV"
        const val EXTRA_SALT = "com.erdi.SecureBox.EXTRA_SALT"
        const val PASSWORD = ""
    }
}