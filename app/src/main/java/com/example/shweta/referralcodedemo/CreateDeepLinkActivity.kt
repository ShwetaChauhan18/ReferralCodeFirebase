package com.example.shweta.referralcodedemo

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.example.shweta.referralcodedemo.databinding.ActivityCreateDeepLinkBinding
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class CreateDeepLinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateDeepLinkBinding
    private var mContext: Context? = null
    private val DEEP_LINK_URL = "https://myapp.com/welcome"
    private var deepLink: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_deep_link)
        init()
    }

    private fun init() {
        binding.shareDeeplink = this
    }

    fun shareDeepLink() {
        deepLink = buildDeepLink(Uri.parse(DEEP_LINK_URL), 0)
        binding.txtDeeplinkValue.text = deepLink.toString()
        val invitationLink = deepLink.toString()
        val msg = "Let's play MyExampleGame together! Use my referrer link: $invitationLink"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, msg)
        intent.putExtra(Intent.EXTRA_TEXT, deepLink)

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        }
    }

    @VisibleForTesting
    fun buildDeepLink(deepLink: Uri, minVersion: Int): Uri {
        val domain = "ktnk3.app.goo.gl"
        // Set dynamic link parameters:
        //  * Domain (required)
        //  * Android Parameters (required)
        //  * Deep link
        // [START build_dynamic_link]

        /*
        * FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String link = "https://www.idi.com/welcome?invitedby=" + uid;
        */
        val builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(DEEP_LINK_URL))
                .setDynamicLinkDomain(domain)
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())

        // Build the dynamic link
        val mDynamiclink = builder.buildDynamicLink()
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
        return mDynamiclink.uri
    }

    fun openNewActivity() {
        startActivity(Intent(this, RefferalCodeActivity::class.java))
    }
}
