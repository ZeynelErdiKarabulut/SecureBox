package com.erdi.SecureBox.ui.wifi

import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.erdi.SecureBox.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.erdi.SecureBox.adapters.RecyclerViewAdapter
import androidx.lifecycle.ViewModelProvider
import com.erdi.SecureBox.models.ViyCred
import androidx.recyclerview.widget.ItemTouchHelper
import android.widget.Toast
import com.erdi.SecureBox.adapters.RecyclerViewAdapter.onItemClickListener
import android.content.Intent
import com.erdi.SecureBox.Modify
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.erdi.SecureBox.Add

// Mails
class WifiFragment : Fragment() {
    var status = false
    private var wifiViewModel: WifiViewModel? = null
    private var empty: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.wifi_fragment, container, false)


        //ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        val fab = root.findViewById<FloatingActionButton>(R.id.fab)
        empty = root.findViewById(R.id.empty)
        val sharedPreferences = this.requireActivity()
            .getSharedPreferences(PROVIDER, Context.MODE_PRIVATE)
        status = sharedPreferences.getBoolean(NO_DATA, false)
        if (status) {
            empty!!.setVisibility(View.GONE)
        } else {
            empty!!.setText(NO_DATA)
        }
        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        val viewAdapter = RecyclerViewAdapter()
        recyclerView.adapter = viewAdapter
        wifiViewModel = ViewModelProvider(this).get(WifiViewModel::class.java)
        wifiViewModel!!.allWifi.observe(viewLifecycleOwner) { viyCreds ->
            viewAdapter.setCreds(
                viyCreds
            )
        }
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    wifiViewModel!!.delete(viewAdapter.getCredAt(viewHolder.adapterPosition))
                    Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        viewAdapter.setOnItemClickListener(object : onItemClickListener {
            override fun onItemClick(viyCred: ViyCred?) {
                Log.d(TAG, "Onclick")
                val intent = Intent(activity, Modify::class.java)
                intent.putExtra(Modify.EXTRA_ID, viyCred!!.id)
                intent.putExtra(Modify.EXTRA_PROVIDER_NAME, viyCred.providerName)
                intent.putExtra(Modify.EXTRA_EMAIL, viyCred.email)
                intent.putExtra(Modify.EXTRA_ENCRYPT, viyCred.cat)
                startActivityForResult(intent, MODIFY_RECORD)
            }
        })
        fab.setOnClickListener {
            val intent = Intent(context, Add::class.java)
            intent.putExtra(Add.EXTRA_PROVIDER, PROVIDER)
            startActivityForResult(intent, ADD_RECORD)
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_RECORD && resultCode == Activity.RESULT_OK) {
            val enc_passwd = data!!.getStringExtra(Add.EXTRA_ENCRYPT)
            val enc_email = data.getStringExtra(Add.EXTRA_EMAIL)
            val viyCred = ViyCred(PROVIDER, PROVIDER, enc_email!!, enc_passwd!!)
            Log.d(
                TAG,
                "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd
            )
            //For showing "No data" or not on activity if the list is empty
            val sharedPreferences = this.requireActivity()
                .getSharedPreferences(PROVIDER, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(NO_DATA, true).apply()
            empty!!.visibility = View.GONE
            wifiViewModel!!.insert(viyCred)
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        } else if (requestCode == MODIFY_RECORD && resultCode == Activity.RESULT_OK) {
            val id = data!!.getIntExtra(Modify.EXTRA_ID, -1)
            if (id == -1) {
                Toast.makeText(context, "Cannot be updated!", Toast.LENGTH_LONG).show()
                return
            }
            val enc_passwd = data.getStringExtra(Modify.EXTRA_ENCRYPT)
            val enc_email = data.getStringExtra(Modify.EXTRA_EMAIL)
            val viyCred = ViyCred(PROVIDER, PROVIDER, enc_email!!, enc_passwd!!)
            //IMP
            viyCred.id = id
            if (!data.getBooleanExtra(Modify.EXTRA_DELETE, false)) {
                Log.d(
                    TAG,
                    "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd
                )
                wifiViewModel!!.update(viyCred)
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            } else {
                wifiViewModel!!.delete(viyCred)
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
            //
        } else if (requestCode == DELETE_RECORD && resultCode == Activity.RESULT_OK) {
//            int id = data.getIntExtra(Modify.EXTRA_ID, -1);
//            if (id == -1) {
//                Toast.makeText(getContext(), "Cannot be deleted!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            String enc_passwd = data.getStringExtra(Modify.EXTRA_ENCRYPT);
//            String enc_email = data.getStringExtra(Modify.EXTRA_EMAIL);
//            ViyCred viyCred = new ViyCred(PROVIDER, enc_email, enc_passwd);
//            viyCred.setId(id);
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val NO_DATA = "NO DATA"
        private val application: Application? = null
        private const val ADD_RECORD = 1
        private const val MODIFY_RECORD = 2
        private const val DELETE_RECORD = 3
        const val PROVIDER = "wifi"
        const val TAG = "W_FRAG"
    }
}