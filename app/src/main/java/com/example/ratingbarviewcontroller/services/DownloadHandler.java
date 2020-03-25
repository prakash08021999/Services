package com.example.ratingbarviewcontroller.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.ratingbarviewcontroller.MainActivity;

public class DownloadHandler extends Handler {
    private static final String TAG = "MyTag";
    private MyDownloadService myDownloadService;
    private MainActivity.MyResultReciever myResultReciever;

    public DownloadHandler() {
    }

    @Override
    public void handleMessage(Message msg) {
        downloadSong(msg.obj.toString());
        myDownloadService.stopSelf(msg.arg1);
        Log.d(TAG, "handleMessage: Song Downloaded: "+msg.obj.toString() + " Intent Id: "+msg.arg1);

        Bundle bundle = new Bundle();
        bundle.putString("message_key",msg.obj.toString());
        myResultReciever.send(MainActivity.RESULT_OK,bundle);
    }

    private void downloadSong(String songName) {
        Log.d(TAG, "run: staring download");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "downloadSong: "+songName+" Downloaded...");
    }

    public void setMyDownloadService(MyDownloadService myDownloadService) {
        this.myDownloadService = myDownloadService;
    }

    public void setMyResultReciever(MainActivity.MyResultReciever myResultReciever) {
        this.myResultReciever = myResultReciever;
    }
}
