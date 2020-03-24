package com.example.ratingbarviewcontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ScrollView
import android.view.View
import android.util.Log
import com.example.ratingbarviewcontroller.services.MyDownloadService
import com.example.ratingbarviewcontroller.services.PlayerList
import kotlinx.android.synthetic.main.activity_service_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MyTag"
    public val MESSAGE_KEY = "message_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_main)

    }

    public fun runCode(v:View) {
        log("Running code")
        displayProgressBar(true)

        for (song in PlayerList.songs){
            val intent = Intent(this@MainActivity, MyDownloadService::class.java)
            intent.putExtra(MESSAGE_KEY,song)

            startService(intent);
        }
    }

    fun clearOutput(v: View) {
        tvLog.setText("")
        scrollTextToEnd()
    }

    fun log(message: String) {
        Log.i(TAG, message)
        tvLog.append(message + "\n")
        scrollTextToEnd()
    }

    private fun scrollTextToEnd() {
        scrollLog.post(Runnable { scrollLog.fullScroll(ScrollView.FOCUS_DOWN) })
    }

    fun displayProgressBar(display: Boolean) {
        if (display) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }
}
