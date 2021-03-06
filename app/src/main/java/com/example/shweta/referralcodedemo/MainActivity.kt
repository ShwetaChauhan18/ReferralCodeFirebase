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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.appinvite.FirebaseAppInvite
import com.google.firebase.dynamiclinks.ShortDynamicLink


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    private val TAG = "MainActivity"
    private val DEEP_LINK_URL = "https://com.example.shweta.referralcodedemo"
    private lateinit var binding: ActivityMainBinding
    private lateinit var deepLink: Uri
    private lateinit var analytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activityMain = this

        init()
        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)

                    /* if (pendingDynamicLinkData != null) {
                                            analytics = FirebaseAnalytics.getInstance(this@MainActivity)
                                            deepLink = pendingDynamicLinkData.getLink()
                                        }
                                        // Handle the deep link. For example, open the linked
                                        // content, or apply promotional credit to the user's
                                        // account.
                                        // ...
                                        // [START_EXCLUDE]
                                        // Display deep link in the UI
                                        Utils.setLog("DEEPLINK ${deepLink.toString()}")
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
                                        }*/

                    if (pendingDynamicLinkData == null) {
                        Log.d(TAG, "getInvitation: no data");
                    }

                    // Get the deep link
                    val deepLink = pendingDynamicLinkData.getLink()

                    // Extract invite
                    val invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData)
                    if (invite != null) {
                        val invitationId = invite.getInvitationId()
                        Utils.setLog("INVITATIONID :$invitationId")
                    }

                    // Handle the deep link
                    // [START_EXCLUDE]
                    Log.d(TAG, "deepLink:" + deepLink);
                    if (deepLink != null) {
                        val intent = Intent(Intent.ACTION_VIEW);
                        intent.setPackage(getPackageName());
                        intent.setData(deepLink);

                        startActivity(intent);
                    }
                    // [END_EXCLUDE]
                }.addOnFailureListener(this, object : OnFailureListener {
                    override fun onFailure(@NonNull e: Exception) {
                        Utils.setLog("getDynamicLink:onFailure" + e)
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun init() {
        // Create a deep link and display it in the UI
        deepLink = buildDeepLink(Uri.parse(DEEP_LINK_URL), 0)
        binding.linkViewSend.text = deepLink.toString()

    }

    //onConn


    // Share button click listener
    fun clickShare() {
        shareDeepLink(deepLink.toString())
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

    fun buildShortLink(): Task<ShortDynamicLink> {
        val domain = "m83rn.app.goo.gl"
        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(DEEP_LINK_URL))
                .setDynamicLinkDomain(domain)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, object : OnCompleteListener<ShortDynamicLink> {
                    override fun onComplete(task: Task<ShortDynamicLink>) {
                        if (task.isSuccessful()) {
                            // Short link created
                            val shortLink = task.getResult().getShortLink();
                            val flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            // Error
                            // ...
                        }
                    }

                })

        return shortLinkTask;
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

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
