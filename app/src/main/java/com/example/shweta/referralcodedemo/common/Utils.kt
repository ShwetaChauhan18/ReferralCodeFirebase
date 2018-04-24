package com.example.shweta.referralcodedemo.common

import android.content.ContentValues.TAG
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.example.shweta.referralcodedemo.BuildConfig

/**
 * Created by Shweta on 24-04-2018.
 */
open class Utils {

    companion object {
        fun setLog(log: String) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, log)
        }

        /**
         *  Show snackbar
         *  @param view - Main layout
         *  @param message - message which show
         */
        fun showSnackBar(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }
    }
}