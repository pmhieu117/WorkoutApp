package com.hieupm.btlandroid.custom
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants

fun Toast.showCustomToast(message: String, activity: Activity, status:String)
{

    val layout = activity.layoutInflater.inflate(
        R.layout.custom_toast_layout,
        activity.findViewById(R.id.toast_container)
    )
    val toastBackgroundCustom = layout.findViewById<RelativeLayout>(R.id.toast_background)
    val textView = layout.findViewById<TextView>(R.id.toast_text)
    textView.text = message

    if(status.equals(Constants.CUSTOM_TOAST_SUCCESS)){
        toastBackgroundCustom.setBackgroundResource(R.drawable.rounded_custom_toast_success)
        textView.setTextColor(ContextCompat.getColor(activity,R.color.toast_custom_success_text))
    }else if(status.equals(Constants.CUSTOM_TOAST_ERROR)){
        toastBackgroundCustom.setBackgroundResource(R.drawable.rounded_custom_toast_error)
        textView.setTextColor(ContextCompat.getColor(activity,R.color.toast_custom_error_text))
    }else if(status.equals(Constants.CUSTOM_TOAST_WARN)){
        toastBackgroundCustom.setBackgroundResource(R.drawable.rounded_custom_toast_warn)
        textView.setTextColor(ContextCompat.getColor(activity,R.color.toast_custom_warn_text))
    }

    // use the application extension function
    this.apply {
        setGravity(Gravity.BOTTOM, 0, 40)
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }
}