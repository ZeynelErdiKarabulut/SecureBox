package com.erdi.SecureBox

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.content.pm.PackageManager
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView

class About : AppCompatActivity() {
    var ver: TextView? = null
    var version: String? = null
    var whatsnew = arrayOf<String?>(
        "Dark mode",
        "Fingerprint Auth",
        "Backup and restore credentials to storage",
        "Minor UI changes",
        "Minor Bug fixes and Improvements"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        try {
            val pInfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
            version = "Version " + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        ver = findViewById(R.id.version)
        ver!!.text = version
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.list_whats_new, whatsnew)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
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