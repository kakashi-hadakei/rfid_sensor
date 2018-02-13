package com.ti.nfcdemo.activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import android.util.Log;

import com.ti.nfcdemo.R;
import com.ti.nfcdemo.activities.iso15693.Iso15693WriteTagActivity;
import com.ti.nfcdemo.activities.iso15693.Iso15693WriteTagHumidityActivity;
import com.ti.nfcdemo.activities.nfc.NfcBaseActivity;
//import com.ti.nfcdemo.activities.nfc.NfcReadTagActivity;
//import com.ti.nfcdemo.activities.nfc.NfcWriteTagWithTextActivity;
//import com.ti.nfcdemo.activities.nfc.NfcWriteTagWithUriActivity;
//import com.ti.nfcdemo.activities.nfc.NfcWriteTagWithWifiActivity;
//import com.ti.nfcdemo.activities.p2p.P2pReceiveTextOrWifiActivity;
//import com.ti.nfcdemo.activities.p2p.P2pSendTextActivity;
//import com.ti.nfcdemo.activities.p2p.P2pSendWifiActivity;


public class MainActivity extends NfcBaseActivity {

    private static String TAG = "MainActivity";
	
  //  ImageButton mButtonRead;
 //   ImageButton mButtonWriteText;
    ImageButton mButtonWriteIso15693Tag;
    ImageButton mButtonReadIso15693Tag;
    ImageButton mButtonWriteTempHumidityTag;
    AlertDialog about_app;
  //  ImageButton mButtonPeerToPeerReceive;
  //  ImageButton mButtonPeerToPeerTransferText;
 //   ImageButton mButtonPeerToPeerTransferWifiConfig;
//    ImageButton mButtonTiCom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_grid);
        initializeNfcAdapterAndIntentFilters();

        mButtonWriteIso15693Tag = (ImageButton) findViewById(R.id.button_write_15693_tag);
        mButtonWriteIso15693Tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newintent = new Intent(getApplicationContext(), Iso15693WriteTagActivity.class);
                newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(newintent);                                                
            }
        });

        //****************************edited Oct8th ************************************
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(mContext);
        View view = getLayoutInflater().inflate(R.layout.about_app_layout, null);
        myBuilder.setView(view);
        about_app = myBuilder.create();

        mButtonWriteTempHumidityTag = (ImageButton)findViewById(R.id.button_write_gsr_tag);
        mButtonWriteTempHumidityTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent newintent = new Intent(getApplicationContext(), Iso15693WriteTagHumidityActivity.class);
                newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(newintent);
                */

            }
        });
        //******************************************************************************

//        mButtonReadIso15693Tag = (ImageButton) findViewById(R.id.button_read_15693_tag);
//        mButtonReadIso15693Tag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent newintent = new Intent(getApplicationContext(), NfcReadTagActivity.class);
////                newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                getApplicationContext().startActivity(newintent);
//            }
//        });    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nfc_write_operation_action_bar_menu, menu);
        inflater.inflate(R.menu.main_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.about_app:
                about_app.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
        //Log.d(TAG,"On resume haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        about_app.cancel();

        Intent myintent = getIntent();
        if (myintent != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(myintent.getAction())
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(myintent.getAction())
                    || NfcAdapter.ACTION_TAG_DISCOVERED.equals(myintent.getAction())) {
                setIntent(null);
                myintent.setClass(getApplicationContext(), Iso15693WriteTagActivity.class);
                myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myintent);
            }
        }
    }
}