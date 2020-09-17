package com.example.postsandroidapp.model.utils


import android.app.Activity
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.example.postsandroidapp.R
import com.tapadoo.alerter.Alerter


class Utils {
    companion object {

        fun showFailAlert(mActivity: Activity?, text: String) {
            if (mActivity == null) {
                return
            }
            try {
                Alerter.create(mActivity)
                    .setText(text)
                    .setBackgroundColorInt(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.red
                        )
                    )
                    .enableInfiniteDuration(false)
                    .setDuration(3000)
                    .setContentGravity(Gravity.END)
                    .show()
            } catch (e: Exception) {

            }
        }

        fun showWarinigAlert(mActivity: Activity?, text: String) {
            if (mActivity == null) {
                return
            }
            try {
                Alerter.create(mActivity)
                    .setText(text)
                    .setBackgroundColorInt(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.yallow
                        )
                    )
                    .enableInfiniteDuration(false)
                    .setDuration(3000)
                    .setContentGravity(Gravity.END)
                    .show()
            } catch (e: Exception) {
            }
        }

        fun showSuccessAlert(mActivity: Activity?, text: String) {
            if (mActivity == null) {
                return
            }
            try {
                Alerter.create(mActivity)
                    .setText(text)
                    .setBackgroundColorInt(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorGreen
                        )
                    )
                    .enableInfiniteDuration(false)
                    .setDuration(3000)
                    .setContentGravity(Gravity.END)
                    .show()
            } catch (e: Exception) {

            }
        }

    }

}