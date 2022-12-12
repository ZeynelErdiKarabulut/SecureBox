package com.erdi.SecureBox

import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.MasterKey
import androidx.navigation.ui.AppBarConfiguration
import com.github.javiersantos.appupdater.AppUpdater
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.ImageButton
import android.widget.TextView
import androidx.security.crypto.EncryptedSharedPreferences
import android.view.WindowManager
import androidx.navigation.ui.NavigationUI
import com.github.javiersantos.appupdater.enums.UpdateFrom
import android.widget.Toast
import com.erdi.SecureBox.Home
import android.app.Activity
import android.content.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.erdi.SecureBox.models.ViyCred
import com.erdi.SecureBox.ui.password.PasswordViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.github.javiersantos.appupdater.enums.Display
import java.io.IOException
import java.lang.StringBuilder
import java.security.GeneralSecurityException
import java.util.*

class Home : AppCompatActivity() {
    val PREFS_NAME = "appEssentials"
    var sharedPreferences: SharedPreferences? = null
    var repo = "erdi"
    var pack = "SecureBox"
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    var masterKey: MasterKey? = null
    var PASSWORD = ""
    var builder: AlertDialog.Builder? = null
    var secureCodeModeState = false
    private var mAppBarConfiguration: AppBarConfiguration? = null
    var appUpdater: AppUpdater? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.action = "1"
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val view = navigationView.getHeaderView(0)
        val imageButton = view.findViewById<ImageButton>(R.id.refresh)
        val textView1 = view.findViewById<TextView>(R.id.generate_password)
        builder = AlertDialog.Builder(this)

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
        secureCodeModeState = sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)
        if (secureCodeModeState) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_password,
            R.id.nav_social,
            R.id.nav_wifi
        )
            .setDrawerLayout(drawer)
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        //Initial random password gen
        val Password = generatePassword()
        textView1.text = Password
        imageButton.setOnClickListener { nav_refresh() }
        //        App updater
        appUpdater = AppUpdater(this)
            .showEvery(3)
            .setDisplay(Display.NOTIFICATION)
            .setDisplay(Display.DIALOG)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo(repo, pack)
            .setTitleOnUpdateAvailable("Update available")
            .setContentOnUpdateAvailable("Get the latest version available for better security and fixes!")
            .setButtonUpdate("Update now?")
            .setButtonDismiss("Maybe later")
            .setButtonDoNotShowAgain("Huh, not interested")
    }

    override fun onStart() {
        super.onStart()
        if (sharedPreferences!!.getBoolean("FIRSTNOTICE", true)) {
            //AlertDialog START
            val alertDialogBuilder = AlertDialog.Builder(this)
            // Setting Alert Dialog Title
            alertDialogBuilder.setTitle("Notice")
            // Setting Alert Dialog Message
            alertDialogBuilder.setMessage("SecureBox is still in beta you may face some issues.")
            //Positive button
            alertDialogBuilder.setPositiveButton("OK") { arg0, arg1 ->
                val editor = sharedPreferences!!.edit()
                editor.putBoolean("FIRSTNOTICE", false).apply()
            }
            //Negative button
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            //AlertDialog END
            if (!secureCodeModeState) Log.d("Update", secureCodeModeState.toString())
            appUpdater!!.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(applicationContext, Settings::class.java))
                true
            }
            R.id.action_help -> {
                startActivity(Intent(applicationContext, Help::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun copy(view: View?) {
        if (sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            val copyImage = findViewById<ImageButton>(R.id.copy)
            copyImage.isEnabled = false
            Toast.makeText(
                this,
                "Secure code mode is Enabled. Copying is not allowed  ",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val textView = findViewById<TextView>(R.id.generate_password)
            val gn_password = textView.text.toString().trim { it <= ' ' }
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Password", gn_password)
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip)
                Toast.makeText(applicationContext, "Copied!", Toast.LENGTH_SHORT).show()
            }
            builder!!.setMessage("Do you want to add this password to the database?")
                .setTitle("Alert")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, which ->
                    val intent = Intent(applicationContext, Add::class.java)
                    intent.putExtra(PASSWORD, gn_password)
                    startActivityForResult(intent, ADD_RECORD)
                }
                .setNegativeButton("No") { dialog, which -> dialog.cancel() }
        }
        val alert = builder!!.create()
        alert.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_RECORD && resultCode == RESULT_OK) {
            val providerName = data!!.getStringExtra(Add.EXTRA_PROVIDER_NAME)
            val enc_passwd = data.getStringExtra(Add.EXTRA_ENCRYPT)
            val enc_email = data.getStringExtra(Add.EXTRA_EMAIL)
            val viyCred = ViyCred(PROVIDER, providerName!!, enc_email!!, enc_passwd!!)
            Log.d(
                TAG,
                "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd
            )
            val sharedPreferences =
                this.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val passwordViewModel = ViewModelProvider(this).get(
                PasswordViewModel::class.java
            )
            passwordViewModel.insert(viyCred)
            Toast.makeText(applicationContext, "Saved", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun generatePassword(): String {
        //Creating Random object
        val random = Random()
        //Limiting the length of the generated password between 8 to 14
        val limit = (Math.random() * 14 + 8).toInt()
        val password = StringBuilder()
        for (itr in 0 until limit) {
            password.append(COLLECTION[random.nextInt(COLLECTION.length)])
        }
        return password.toString()
    }

    fun nav_refresh() {
        val textView = findViewById<TextView>(R.id.generate_password)
        val Password = generatePassword()
        textView.text = Password
    }

    companion object {
        const val NO_DATA = "NO DATA"
        private const val COLLECTION =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-"

        /*private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-";*/
        private const val ADD_RECORD = 1
        private const val TAG = "HOME"
        private const val PROVIDER = "mail"
    }
}