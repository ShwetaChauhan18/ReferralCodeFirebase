package com.example.shweta.referralcodedemo

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.shweta.referralcodedemo.common.Utils
import com.example.shweta.referralcodedemo.databinding.ActivityRefferalCodeBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import java.lang.Exception

class RefferalCodeActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var referrerUid: String? = null
    private var TAG = "RefferalCode"
    private lateinit var binding: ActivityRefferalCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refferal_code)
        binding.refferalCode = this
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        getDeepLink()
    }

    private fun getDeepLink() {
        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, object : OnSuccessListener<PendingDynamicLinkData> {
                    override fun onSuccess(pendingDynamicLinkData: PendingDynamicLinkData?) {
                        // Get deep link from result (may be null if no link is found)
                        var deepLink: Uri? = null
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                        }

                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // [START_EXCLUDE]
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //

                        // Display deep link in the UI
                        if (deepLink != null) {
                            //referredUid you get when you pass any parameter in deeplink to identify from which user you install
                            // this app
                            //referrerUid=deepLink.getQueryParameter("invitedby")
                            //createAnonymousAccountWithReferrerInfo(referrerUid.toString())
                            Utils.showSnackBar(binding.rootView, "Found deep link!")
                            binding.linkViewReceive.text = deepLink.toString()
                            Log.e("Found deep link!", deepLink.toString())
                        } else {
                            Log.d(TAG, "getDynamicLink: no link found")
                        }
                    }

                })
                .addOnFailureListener(this, object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        //To change body of created functions use File | Settings | File Templates.
                        Log.w(TAG, "getDynamicLink:onFailure", e)
                    }
                })
    }

    private fun createAnonymousAccountWithReferrerInfo(referrerUid: String) {
        // create AnonymousAccount account first after when user signup link this anonymous account with user email and password
        /*FirebaseAuth.getInstance()
                .signInAnonymously()
                .addOnSuccessListener(OnSuccessListener<Any> {
                    // Keep track of the referrer in the RTDB. Database calls
                    // will depend on the structure of your app's RTDB.
                    val userRecord = FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getUid())
                    userRecord.child("referred_by").setValue(referrerUid)
                })*/
    }
}
