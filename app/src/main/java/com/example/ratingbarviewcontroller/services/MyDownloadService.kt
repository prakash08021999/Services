package com.example.ratingbarviewcontroller.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.os.Message

class MyDownloadService : Service() {

    private val TAG = "MyTag"
    var mDownlaodThread : DownloadThread? = null

    override fun onCreate() {
        super.onCreate()
        //this will be called once how many time you call start service.
        Log.d(TAG, "onCreate: called")
        mDownlaodThread = DownloadThread()
        mDownlaodThread!!.start()

        while (mDownlaodThread!!.mHandler == null) {

        }
    }

    //1)after after application cloases service stop but after some time it will restart running.
    //2)as it run on main thread it's execution stop ui thread.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //this will be called when ever you call start service
        Log.d(TAG,"Thread id"+Thread.currentThread().name)//you will see that thread run on same main thread
        Log.d(TAG,"onStartCommand Called")
        val songName = intent!!.getStringExtra("message_key")
        val message = Message.obtain();
        message.obj = songName
        mDownlaodThread!!.mHandler.sendMessage(message)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG,"onBind Called")
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onBind Called")
        // if you destroy any service  and close it will not restart.
    }

}
