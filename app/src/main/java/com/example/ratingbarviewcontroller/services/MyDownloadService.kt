package com.example.ratingbarviewcontroller.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.ratingbarviewcontroller.MainActivity


class MyDownloadService : Service() {

    private val TAG = "MyTag"

    //1)after after application cloases service stop but after some time it will restart running.
    //2)as it run on main thread it's execution stop ui thread.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val songName = intent!!.getStringExtra("message_key")
        downloadSong(songName)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun downloadSong(songName: String) {
        Log.d(TAG, "run: staring download")
        try {
            Thread.sleep(4000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d(TAG, "downloadSong: $songName Downloaded...")
    }

}
