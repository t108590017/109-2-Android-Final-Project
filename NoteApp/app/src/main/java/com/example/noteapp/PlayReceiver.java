package com.example.noteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PlayReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bData = intent.getExtras();
        if(bData.get("msg").equals("play_hskay"))
        {

        }
    }
}
