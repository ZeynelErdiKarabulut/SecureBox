package com.erdi.SecureBox.adapters

import com.himanshurawat.hasher.Hasher.Companion.hash
import androidx.recyclerview.widget.RecyclerView
import com.erdi.SecureBox.adapters.RecyclerViewAdapter.viewHolder
import com.erdi.SecureBox.models.ViyCred
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.erdi.SecureBox.R
import androidx.security.crypto.EncryptedSharedPreferences
import com.himanshurawat.hasher.HashType
import android.widget.TextView
import android.widget.ImageView
import com.erdi.SecureBox.Utils.AESUtils
import java.lang.Exception
import java.util.ArrayList

class RecyclerViewAdapter : RecyclerView.Adapter<viewHolder>() {
    private var credsList: List<ViyCred> = ArrayList()
    private var listener: onItemClickListener? = null
    var masterKey: MasterKey? = null
    var sharedPreferences: SharedPreferences? = null
    var sha: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_items, parent, false)
        val context = parent.context
        try {
            //x.security
            masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            //init sharedPef
            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey!!,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        sha = sharedPreferences!!.getString("HASH", "0")
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val creds = credsList[position]
        holder.provider.text = creds.providerName
        when (creds.providerName) {
            "Amazon" -> holder.providerImage.setImageResource(R.drawable.amazon)
            "Apple" -> holder.providerImage.setImageResource(R.drawable.apple)
            "Facebook" -> holder.providerImage.setImageResource(R.drawable.facebook)
            "Flickr" -> holder.providerImage.setImageResource(R.drawable.flickr)
            "Foursquare" -> holder.providerImage.setImageResource(R.drawable.foursquare)
            "Github" -> holder.providerImage.setImageResource(R.drawable.github)
            "Gmail" -> holder.providerImage.setImageResource(R.drawable.google)
            "Instagram" -> holder.providerImage.setImageResource(R.drawable.instagram)
            "Linkedin" -> holder.providerImage.setImageResource(R.drawable.linkedin)
            "Medium" -> holder.providerImage.setImageResource(R.drawable.medium)
            "Paypal" -> holder.providerImage.setImageResource(R.drawable.paypal)
            "Pinterest" -> holder.providerImage.setImageResource(R.drawable.pinterest)
            "Reddit" -> holder.providerImage.setImageResource(R.drawable.reddit)
            "Skype" -> holder.providerImage.setImageResource(R.drawable.skype)
            "Slack" -> holder.providerImage.setImageResource(R.drawable.slack)
            "Snapchat" -> holder.providerImage.setImageResource(R.drawable.snapchat)
            "Spotify" -> holder.providerImage.setImageResource(R.drawable.spotify)
            "Stackoverflow" -> holder.providerImage.setImageResource(R.drawable.stackoverflow)
            "Tinder" -> holder.providerImage.setImageResource(R.drawable.tinder)
            "Trello" -> holder.providerImage.setImageResource(R.drawable.trello)
            "Tumblr" -> holder.providerImage.setImageResource(R.drawable.tumblr)
            "Twitter" -> holder.providerImage.setImageResource(R.drawable.twitter)
            "Wordpress" -> holder.providerImage.setImageResource(R.drawable.wordpress)
            "Yahoo" -> holder.providerImage.setImageResource(R.drawable.yahoo)
            else -> holder.providerImage.setImageResource(R.drawable.google)
        }
        try {
            val keyValue = hash(sha, HashType.MD5)
            val dec = creds.email
            val decEmail = AESUtils.decrypt(dec, keyValue)
            holder.cat1.text = decEmail
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //holder.cat1.setText(creds.getEmail());
        //holder.cat2.setText(creds.getCat2());
    }

    override fun getItemCount(): Int {
        return credsList.size
    }

    fun setCreds(credsList: List<ViyCred>) {
        this.credsList = credsList
        notifyDataSetChanged()
    }

    fun getCredAt(position: Int): ViyCred {
        return credsList[position]
    }

    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val provider: TextView
        val cat1: TextView
        private val cat2: TextView? = null
        val providerImage: ImageView

        init {
            providerImage = view.findViewById(R.id.image)
            provider = view.findViewById(R.id.provider)
            //Email field
            cat1 = view.findViewById(R.id.imp_cat)
            //cat2 = view.findViewById(R.id.imp_cat2);
            view.setOnClickListener {
                val pos = adapterPosition
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(credsList[pos])
                }
            }
            view.setOnLongClickListener {
                Log.d("OnLongClick", "Long Click")
                false
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(viyCred: ViyCred?)
    }

    fun setOnItemClickListener(listener: onItemClickListener?) {
        this.listener = listener
    }

    companion object {
        private const val PREFS_NAME = "appEssentials"
    }
}