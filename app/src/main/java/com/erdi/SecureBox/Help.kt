package com.erdi.SecureBox

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import com.erdi.SecureBox.R
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import android.view.View
import com.erdi.SecureBox.About

class Help : AppCompatActivity() {
    var version: String? = null
    var ver: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
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
    }

    fun githubLink(view: View?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data =
            Uri.parse(String.format("https://github.com/%s", "erdi/SecureBox"))
        startActivity(intent)
    }

    fun whats_new(view: View?) {
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

    fun support(view: View?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(String.format("https://t.me/%s", "z3rod0t"))
        startActivity(intent)
    }
}