package com.ti.nfcdemo.activities.iso15693;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by hanfei on 10/16/17.
 */

public class Iso15693WriteTagHumidityActivity extends Iso15693WriteTagActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showProgressDialog() {
        super.showProgressDialog();
    }

    @Override
    public void dismissProgressDialog() {
        super.showProgressDialog();
    }

    @Override
    public void onCommandExecuted(byte[] data) {
        super.onWriteCommandExecuted(data);
    }

    @Override
    public void onWriteCommandExecuted(byte[] result){
        super.onWriteCommandExecuted(result);
    }

    public static void processData(byte[] result){
        Iso15693WriteTagActivity.processData(result);
    }
}
