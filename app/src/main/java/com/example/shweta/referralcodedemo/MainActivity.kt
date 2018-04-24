package com.example.shweta.referralcodedemo

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.util.Log
import android.widget.TextView
import com.example.shweta.referralcodedemo.common.Utils
import com.example.shweta.referralcodedemo.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val DEEP_LINK_URL = "https://myapp.com/welcome"
    private lateinit var binding: ActivityMainBinding
    private lateinit var deepLink: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activityMain = this
    }

    fun getDynamicLink() {
        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, object : OnSuccessListener<PendingDynamicLinkData> {
                    override fun onSuccess(pendingDynamicLinkData: PendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)

                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink()
                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...
                        // [START_EXCLUDE]
                        // Display deep link in the UI
                        if (deepLink != null) {
                            Utils.showSnackBar(binding.rootView, "Found deep link!")
                            binding.linkViewReceive.text = deepLink.toString()
                        } else {
                            Utils.setLog("getDynamicLink: no link found")
                        }
                        // [END_EXCLUDE]
                    }
                }).addOnFailureListener(this, object : OnFailureListener {
                    override fun onFailure(@NonNull e: Exception) {
                        Utils.setLog("getDynamicLink:onFailure" + e)
                    }
                })

    }
}
