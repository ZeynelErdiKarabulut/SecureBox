package com.erdi.SecureBox.ui.password

import android.widget.TextView
import com.erdi.SecureBox.repos.CredsRepository
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.erdi.SecureBox.R
import android.widget.ProgressBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.ImageButton
import android.view.WindowManager
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
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.erdi.SecureBox.Add
import java.lang.Exception

// Mails
class PasswordFragment : Fragment() {
    var status = false
    var PREF_NAME = "Settings"
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    private var empty: TextView? = null
    private var passwordViewModel: PasswordViewModel? = null
    private val repository: CredsRepository? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_password, container, false)
        val progressBar = root.findViewById<ProgressBar>(R.id.progress_bar)
        val fab = root.findViewById<FloatingActionButton>(R.id.fab)
        empty = root.findViewById(R.id.empty)
        val sharedPreferences = this.requireActivity()
            .getSharedPreferences(PROVIDER, Context.MODE_PRIVATE)
        val sp = this.requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (sp.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            try {
                val copyImage = root.findViewById<ImageButton>(R.id.copy)
                copyImage.isEnabled = false
            } catch (e: Exception) {
                e.stackTrace
            }
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
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
        passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
        //passwordViewModel = ViewModelProviders.of(this).get(PasswordViewModel.class);
        passwordViewModel!!.allMails.observe(viewLifecycleOwner) { viyCreds ->
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
                    passwordViewModel!!.delete(viewAdapter.getCredAt(viewHolder.adapterPosition))
                    Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        viewAdapter.setOnItemClickListener(object : onItemClickListener {
            override fun onItemClick(viyCred: ViyCred?) {
                val intent = Intent(activity, Modify::class.java)
                intent.putExtra(Modify.EXTRA_ID, viyCred!!.id)
                intent.putExtra(Modify.EXTRA_PROVIDER_NAME, viyCred.providerName)
                intent.putExtra(Modify.EXTRA_EMAIL, viyCred.email)
                intent.putExtra(Modify.EXTRA_ENCRYPT, viyCred.cat)
                startActivityForResult(intent, MODIFY_RECORD)
            }
        })
        // viewAdapter.SetOnLongClickListener(onL)
        fab.setOnClickListener {
            val intent = Intent(context, Add::class.java)
            intent.putExtra(Add.EXTRA_PROVIDER, PROVIDER)
            startActivityForResult(intent, ADD_RECORD)
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_RECORD && resultCode == Activity.RESULT_OK) {
            val providerName = data!!.getStringExtra(Add.EXTRA_PROVIDER_NAME)
            val enc_passwd = data.getStringExtra(Add.EXTRA_ENCRYPT)
            val enc_email = data.getStringExtra(Add.EXTRA_EMAIL)
            val viyCred = ViyCred(PROVIDER, providerName!!, enc_email!!, enc_passwd!!)
            //For showing "No data" or not on activity if the list is empty
            val sharedPreferences = this.requireActivity()
                .getSharedPreferences(PROVIDER, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(NO_DATA, true).apply()
            empty!!.visibility = View.GONE
            passwordViewModel!!.insert(viyCred)
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        } else if (requestCode == MODIFY_RECORD && resultCode == Activity.RESULT_OK) {
            val id = data!!.getIntExtra(Modify.EXTRA_ID, -1)
            if (id == -1) {
                Toast.makeText(context, "Cannot be updated!", Toast.LENGTH_LONG).show()
                return
            }
            val providerName = data.getStringExtra(Modify.EXTRA_PROVIDER_NAME)
            val enc_passwd = data.getStringExtra(Modify.EXTRA_ENCRYPT)
            val enc_email = data.getStringExtra(Modify.EXTRA_EMAIL)
            val viyCred = ViyCred(PROVIDER, providerName!!, enc_email!!, enc_passwd!!)
            //IMP
            viyCred.id = id
            if (!data.getBooleanExtra(Modify.EXTRA_DELETE, false)) {
                // Log.d(TAG, "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd);
                passwordViewModel!!.update(viyCred)
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            } else {
                passwordViewModel!!.delete(viyCred)
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val NO_DATA = "NO DATA"
        private const val ADD_RECORD = 1
        private const val MODIFY_RECORD = 2
        private const val DELETE_RECORD = 3
        private const val PROVIDER = "mail"
        private const val TAG = "P FRAG "
    }
}