package com.erdi.SecureBox

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.andrognito.pinlockview.PinLockView
import com.andrognito.pinlockview.IndicatorDots
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences
import android.os.Bundle
import com.erdi.SecureBox.MLock
import com.erdi.SecureBox.R
import androidx.security.crypto.EncryptedSharedPreferences
import com.andrognito.pinlockview.PinLockListener
import android.widget.Toast
import android.content.Intent
import com.erdi.SecureBox.Home
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt.PromptInfo
import android.annotation.TargetApi
import android.os.Build
import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

//MLock Master Lock
class MLock : AppCompatActivity() {
    val PREFS_NAME = "appEssentials"
    val PREF_KEY = "firstRun"
    val HASH = "HASH"
    val DOESNT_EXIST = -1
    var mlock_tv_greet: TextView? = null
    var mlock_tv_pp: TextView? = null
    var mPinLockView: PinLockView? = null
    var mIndicatorDots: IndicatorDots? = null
    var masterKey: MasterKey? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarGradiant(this)
        setContentView(R.layout.activity_mlock)
        bioAuth()
        mlock_tv_greet = findViewById(R.id.mlock_l_tv_greet)
        /*mlock_et_mp = findViewById(R.id.mlock_l_et_mpass);
        mlock_b_mp = findViewById(R.id.mlock_l_b_setmp);*/mIndicatorDots =
            findViewById(R.id.indicator_dots)
        mPinLockView = findViewById(R.id.pin_lock_view)


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

        //First time then change Text
        if (sharedPreferences!!.getBoolean(PREF_KEY, true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            mlock_tv_greet!!.setText(R.string.mlock_st_create_password)
            //Setting BIOAUTH button to GONE
            findViewById<View>(R.id.launchAuthentication).visibility = View.GONE
        }
        val mPinLockListener: PinLockListener = object : PinLockListener {
            override fun onComplete(pin: String) {
                if (sharedPreferences!!.getBoolean(PREF_KEY, true)) {
                    sharedPreferences!!.edit().putString(HASH, pin).apply()
                    //                  String HASH = new String(Hex.encodeHex(DigestUtils.sha(pin)));
                    sharedPreferences!!.edit().putBoolean(PREF_KEY, false).apply()
                    Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, Home::class.java))
                    finish()
                } else {
                    val sp = sharedPreferences!!.getString(HASH, "0")
                    if (sp == pin) {
//                        Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_SHORT).show();
                        startActivity(Intent(applicationContext, Home::class.java))
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Wrong password", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onEmpty() {
//                Log.d(TAG, "Pin empty");
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String) {
                //Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        }
        mPinLockView!!.setPinLockListener(mPinLockListener)
        mPinLockView!!.attachIndicatorDots(mIndicatorDots)
    }

    fun bioAuth() {
        //Create a thread pool with a single thread//
        val newExecutor: Executor = Executors.newSingleThreadExecutor()
        val activity: FragmentActivity = this
        //Start listening for authentication events//
        val myBiometricPrompt = BiometricPrompt(
            activity,
            newExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                //onAuthenticationError is called when a fatal error occurrs//
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    } else {
                        //Print a message to Logcat//
//                    Log.d(TAG, "An unrecoverable error occurred");
                    }
                }

                //onAuthenticationSucceeded is called when a fingerprint is matched successfully//
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    //Print a message to Logcat//
                    startActivity(Intent(this@MLock, Home::class.java))
                    finish()
                    //                Log.d(TAG, "Fingerprint recognised successfully");
                }

                //onAuthenticationFailed is called when the fingerprint doesn\’t match//
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    //Print a message to Logcat//
//                Log.d(TAG, "Fingerprint not recognised");
                }
            })
        //Create the BiometricPrompt instance//
        val promptInfo = PromptInfo.Builder() //Add some text to the dialog//
            .setTitle("Fingerprint Authentication")
            .setDescription("Place your finger on the sensor to authenticate")
            .setNegativeButtonText("Cancel") //Build the dialog//
            .build()
        findViewById<View>(R.id.launchAuthentication).setOnClickListener {
            myBiometricPrompt.authenticate(
                promptInfo
            )
        }
    }

    companion object {
        // Gradient on statusbar
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun setStatusBarGradiant(activity: Activity) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            //window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            //window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }
}