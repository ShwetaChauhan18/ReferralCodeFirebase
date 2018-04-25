package com.example.shweta.referralcodedemo

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.VisibleForTesting
import android.util.Log
import android.widget.TextView
import com.example.shweta.referralcodedemo.common.Utils
import com.example.shweta.referralcodedemo.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import android.content.Intent
import android.support.v7.app.AlertDialog
import java.net.URL
import java.net.URLDecoder
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.appinvite.AppInvite
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.appinvite.FirebaseAppInvite


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val DEEP_LINK_URL = "https://myapp.com/welcome"
    private lateinit var binding: ActivityMainBinding
    private lateinit var deepLink: Uri
    private lateinit var analytics: FirebaseAnalytics
    //  private var googleApiClient: GoogleApiClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activityMain = this
        init()
        getDynamicLink()
    }

    private fun init() {
        // Validate that the developer has set the app code.
        validateAppCode()

        // Create a deep link and display it in the UI
        deepLink = buildDeepLink(Uri.parse(DEEP_LINK_URL), 0)
        binding.linkViewSend.text = deepLink.toString()

        /*googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build()*/
    }

    //onConn


    // Share button click listener
    fun clickShare() {
        shareDeepLink(deepLink.toString())
    }

    fun getDynamicLink() {
        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, object : OnSuccessListener<PendingDynamicLinkData> {
                    override fun onSuccess(pendingDynamicLinkData: PendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)

                        if (pendingDynamicLinkData != null) {
                            analytics = FirebaseAnalytics.getInstance(this@MainActivity)
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

                            val invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData)
                            if (invite != null) {
                                val invitationId = invite.invitationId
                                Utils.setLog("INVITATIONID :$invitationId")
                            }
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

    /**
     * Build a Firebase Dynamic Link.
     * https://firebase.google.com/docs/dynamic-links/android/create#create-a-dynamic-link-from-parameters
     *
     * @param deepLink the deep link your app will open. This link must be a valid URL and use the
     *                 HTTP or HTTPS scheme.
     * @param minVersion the {@code versionCode} of the minimum version of your app that can open
     *                   the deep link. If the installed app is an older version, the user is taken
     *                   to the Play store to upgrade the app. Pass 0 if you do not
     *                   require a minimum version.
     * @return a {@link Uri} representing a properly formed deep link.
     */
    @VisibleForTesting
    fun buildDeepLink(@NonNull deepLink: Uri, minVersion: Int): Uri {
        val domain = "m83rn.app.goo.gl"
        // Set dynamic link parameters:
        // * Domain (required)
        // * Android Parameters (required)
        // * Deep link
        // [START build_dynamic_link]
        val builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDynamicLinkDomain(domain)
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())
                .setLink(deepLink)
        // Build the dynamic link
        val link = builder.buildDynamicLink()
        // [END build_dynamic_link]
        // Return the dynamic link as a URI
        return link.getUri()
    }

    private fun shareDeepLink(deepLink: String) {
        try {
            val url = URL(URLDecoder.decode(deepLink.toString(),
                    "UTF-8"));
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link")
            intent.putExtra(Intent.EXTRA_TEXT, url.toString())

            startActivity(intent)
        } catch (e: Exception) {
            Log.i(TAG, "Could not decode Uri: " + e.getLocalizedMessage());
        }
    }

    private fun validateAppCode() {
        val appCode = "m83rn"
        if (appCode.contains("YOUR_APP_CODE")) {
            AlertDialog.Builder(this)
                    .setTitle("Invalid Configuration")
                    .setMessage("Please set your app code in app/build.gradle")
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show()
        }
    }
}
