package com.erdi.SecureBox

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import androidx.security.crypto.MasterKey
import android.widget.TextView
import android.widget.ProgressBar
import android.os.Bundle
import androidx.security.crypto.EncryptedSharedPreferences
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.appcompat.app.AppCompatDelegate
import android.view.WindowManager
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.app.Activity
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import ir.androidexception.roomdatabasebackupandrestore.Backup
import com.erdi.SecureBox.room.CredDB
import ir.androidexception.roomdatabasebackupandrestore.Restore
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.erdi.SecureBox.ui.password.PasswordViewModel
import java.io.IOException
import java.security.GeneralSecurityException

class Settings : AppCompatActivity() {
    val PREFS_NAME = "appEssentials"
    var sharedPreferences: SharedPreferences? = null
    var UIPref: SharedPreferences? = null
    var PREF_KEY = "MASTER_PASSWORD"
    var PREF_DARK = "DARK_THEME"
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    var PREF_KEY_SCM_COPY = "SCM_COPY"
    var PREF_KEY_SCM_SCREENSHOTS = "SCM_SCREENSHOTS"
    var NO_DATA = "NO DATA"
    var TYPE_PASS_1 = "PIN"
    var TYPE_PASS_2 = "PASSWORD"
    var masterKey: MasterKey? = null
    var PACKAGE_NAME: String? = null
    var change_password: TextView? = null
    var delete_data: TextView? = null
    var about_app: TextView? = null
    var progressBar: ProgressBar? = null
    var secureCodeModeState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        PACKAGE_NAME = applicationContext.packageName
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

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
        change_password = findViewById(R.id.change_master_password)
        delete_data = findViewById(R.id.delete_all_data)
        about_app = findViewById(R.id.about_app)
        progressBar = findViewById(R.id.progress_bar)
        val askPasswordLaunchSwitch = findViewById<SwitchMaterial>(R.id.ask_password_launch)
        val secureCoreModeSwitch = findViewById<SwitchMaterial>(R.id.secure_core_mode)
        val dark_theme = findViewById<SwitchMaterial>(R.id.ask_dark_theme)
        secureCodeModeState = sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)
        val askPasswordLaunchState = sharedPreferences!!.getBoolean(PREF_KEY, true)
        secureCoreModeSwitch.isChecked = secureCodeModeState
        askPasswordLaunchSwitch.isChecked = askPasswordLaunchState
        //Set theme mode
        UIPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val onDarkTheme = UIPref!!.getBoolean(PREF_DARK, false)
        if (onDarkTheme) {
            dark_theme.isChecked = onDarkTheme
        }
        val editor = sharedPreferences!!.edit()
        askPasswordLaunchSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // ask for password
                askPassword(true)
                editor.putBoolean(PREF_KEY, true).apply()
            } else {
                // remove password
                askPassword(false)
                editor.putBoolean(PREF_KEY, false).apply()
            }
        }
        val UIEditor = UIPref!!.edit()
        dark_theme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Enable Dark theme
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                UIEditor.putBoolean(PREF_DARK, true).apply()
            } else {
                // Disable Dark theme
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                UIEditor.putBoolean(PREF_DARK, false).apply()
            }
        }
        secureCoreModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Removing
                secureCodeMode(true)
                editor.putBoolean(PREF_KEY_SECURE_CORE_MODE, true).apply()
            } else {
                secureCodeMode(false)
                editor.putBoolean(PREF_KEY_SECURE_CORE_MODE, false).apply()
            }
        }
    }

    private fun secureCodeMode(state: Boolean) {
        val editor = sharedPreferences!!.edit()
        if (state) {
            //to do False
            //remove copy to clipboard and screenshot ability
            editor.putBoolean(PREF_KEY_SCM_COPY, false).apply()
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
            Toast.makeText(
                applicationContext,
                "Success. Restart app to apply changes",
                Toast.LENGTH_LONG
            ).show()
        } else {
            //to do true
            //set copy to clipboard and screenshot ability
            editor.putBoolean(PREF_KEY_SCM_SCREENSHOTS, true).apply()
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            Toast.makeText(applicationContext, "Secure code mode is inactive", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun askPassword(state: Boolean) {
        if (state) {
            //to do False
            //remove copy to clipboard and screenshot ability
            Toast.makeText(applicationContext, "Password: ON", Toast.LENGTH_SHORT).show()
        } else {
            //to do true
            //set copy to clipboard and screenshot ability
            Toast.makeText(applicationContext, "Password: OFF", Toast.LENGTH_SHORT).show()
        }
    }

    fun changePassword(view: View?) {
        val PIN = findViewById<TextView>(R.id.change_master_password_option_1)
        //TODO Change to password disabled for now
        //TextView Password = findViewById(R.id.change_master_password_option_2);
        PIN.visibility = View.VISIBLE
        //Password.setVisibility(View.VISIBLE);
    }

    fun changePasswordToPIN(view: View?) {
        val intent = Intent(applicationContext, ChangePassword::class.java)
        intent.putExtra(ChangePassword.EXTRA_TYPE_PASS, TYPE_PASS_1)
        startActivity(intent)
    }

    fun changePasswordToPassword(view: View?) {
        val intent = Intent(applicationContext, ChangePassword::class.java)
        intent.putExtra(ChangePassword.EXTRA_TYPE_PASS, TYPE_PASS_2)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(applicationContext, "Permission required", Toast.LENGTH_LONG).show()
        }
    }

    fun check_storage_perms(activity: Activity?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            true
        }
    }

    fun export_data(view: View?) {
        check_storage_perms(this)
        Backup.Init()
            .database(CredDB.getInstance(this))
            .path("storage/emulated/0/")
            .fileName("securebox_BKP.txt") //                .secretKey("your-secret-key") //optional
            .onWorkFinishListener { success, message -> // do anything
                if (message == "success") {
                    Toast.makeText(
                        applicationContext,
                        "Restart app to sync your credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .execute()
    }

    fun restore_data(view: View?) {
        check_storage_perms(this)
        // Restore
        Restore.Init()
            .database(CredDB.getInstance(this))
            .backupFilePath("storage/emulated/0/securebox_BKP.txt") //                    .secretKey("your-secret-key") // if your backup file is encrypted, this parameter is required
            .onWorkFinishListener { success, message -> // do anything
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
            .execute()
    }

    fun deleteData(view: View?) {
        //AlertDialog START
        val alertDialogBuilder = AlertDialog.Builder(this)
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Delete Everything")
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Are you sure, You want to delete everything?")
        alertDialogBuilder.setCancelable(false)
        //Positive button
        alertDialogBuilder.setPositiveButton("yes") { arg0, arg1 ->
            val passwordViewModel = PasswordViewModel(application)
            progressBar!!.visibility = View.VISIBLE
            passwordViewModel.deleteAllNotes()
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(NO_DATA, false).apply()
            progressBar!!.visibility = View.GONE
            Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
        }
        //Negative button
        alertDialogBuilder.setNegativeButton("No") { dialog, which -> finish() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        //AlertDialog END
    }

    fun aboutApp(view: View?) {
        startActivity(Intent(this, About::class.java))
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
}