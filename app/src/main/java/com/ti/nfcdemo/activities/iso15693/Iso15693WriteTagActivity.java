package com.ti.nfcdemo.activities.iso15693;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.flomio.ndef.helper.utils.FlomioNdefHelper;
import com.flomio.ndef.helper.utils.ToastMaker;
import com.flomio.ndef.helper.utils.iso15693.Iso15693Utils;
import com.flomio.ndef.helper.utils.iso15693.Iso15693WriteSingleBlock;
import com.flomio.ndef.helper.utils.iso15693.OnCommandExecutedCallBack;
import com.ti.nfcdemo.R;
import com.ti.nfcdemo.activities.nfc.NfcWriteTagBaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Exchanger;

//import com.androidplot.series.XYSeries;


/**
 * Created by Udayan on 6/5/13.
 * <p>
 * add humidity ?keep thermistor
 */
//public class Iso15693WriteTagActivity extends NfcWriteTagBaseActivity implements OnCommandExecutedCallBack, SensorEventListener {
public class Iso15693WriteTagActivity extends NfcWriteTagBaseActivity implements OnCommandExecutedCallBack {
    byte Command0[] = new byte[]{
            (byte) 0x01, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x07, //-Sensor Control Register
            (byte) 0x03, //-Frequency Register
            (byte) 0x01, //-Number of Passes Register
            (byte) 0x01, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x40
    };
    byte Command1[] = new byte[]{
            (byte) 0x00, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x00, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };
    byte Command2[] = new byte[]{
            (byte) 0x19, //-General Control Register
            (byte) 0x19, //-Status Register
            (byte) 0x18, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };
    byte Command3[] = new byte[]{
            (byte) 0x00, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x00, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };
    byte Command4[] = new byte[]{
            (byte) 0x00, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x00, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };
    byte Command5[] = new byte[]{
            (byte) 0x00, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x00, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };
    byte Command6[] = new byte[]{
            (byte) 0x00, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x00, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };
    byte Command7[] = new byte[]{
            (byte) 0x00, //-General Control Register
            (byte) 0x00, //-Status Register
            (byte) 0x00, //-Sensor Control Register
            (byte) 0x00, //-Frequency Register
            (byte) 0x00, //-Number of Passes Register
            (byte) 0x00, //-Extra Sampling Register
            (byte) 0x00,  //-Power Modes
            (byte) 0x00
    };


    private static final String LOG_TAG = Iso15693WriteTagActivity.class.getSimpleName();

    private OnCommandExecutedCallBack mOnCommandExecutedCallBack;

    //private EditText mEditText;
    //private Spinner mSpinner;
    private static ProgressDialog mProgressDialog;
    //private EditText mEditTextBlockNumber;

    //   private XYPlot plot;

    private static final int HISTORY_SIZE = 10;            // number of points to plot in history
//    private SensorManager sensorMgr = null;
//    private Sensor orSensor = null;

    private XYPlot gsrHistoryPlot = null;
    private static XYPlot aprHistoryPlot = null;

    private CheckBox hwAcceleratedCb;
    private CheckBox showFpsCb;
    private SimpleXYSeries aprLevelsSeries = null;

    private static SimpleXYSeries referenceSensorSeries = null;
    private static SimpleXYSeries thermistorSensorSeries = null;
    private static SimpleXYSeries ADC0SensorSeries = null;
    private static SimpleXYSeries internalSensorSeries = null;
    private static SimpleXYSeries gsrSeries = null;

    public static boolean done = true;


    public static int domainCounter = 0;
    public static Intent myintent;
    public static Intent myintentOld;

    private static TextView debug1_val;
    private static TextView debug2_val;
    private static TextView debug3_val;
    private static TextView debug4_val;


    private static boolean unit = true; //true is degree F
    private Button save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xy_plot_layout);

        //Log.d(LOG_TAG, mContext.toString() + "ahhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

        //***************************************************************
        debug1_val = (TextView) findViewById(R.id.debug_val_1);
        debug2_val = (TextView) findViewById(R.id.debug_val_2);
        debug3_val = (TextView) findViewById(R.id.debug_val_3);
        debug4_val = (TextView) findViewById(R.id.debug_val_4);

        //*****************************************************************


        //       gsrHistoryPlot = (XYPlot) findViewById(R.id.aprLevelsPlot);

        ADC0SensorSeries = new SimpleXYSeries("ADC0 Sensor");
        ADC0SensorSeries.useImplicitXVals();

        gsrSeries = new SimpleXYSeries("GSR");
        gsrSeries.useImplicitXVals();

//        gsrHistoryPlot.setRangeBoundaries(-1000, 13000, BoundaryMode.FIXED);
//        gsrHistoryPlot.setDomainBoundaries(0, 10, BoundaryMode.FIXED);
//        gsrHistoryPlot.addSeries(ADC0SensorSeries, new LineAndPointFormatter(Color.rgb(200, 0, 0), Color.BLACK, null, null));
//        gsrHistoryPlot.setDomainStepValue(5);
//        gsrHistoryPlot.setTicksPerRangeLabel(3);
//        gsrHistoryPlot.setDomainLabel("Sample Index");
//        gsrHistoryPlot.getDomainLabelWidget().pack();
//        gsrHistoryPlot.setRangeLabel("GSR ADC value");
//        gsrHistoryPlot.getRangeLabelWidget().pack();
        domainCounter = 0;

        // setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
        aprHistoryPlot.getRangeLabelWidget().getLabelPaint().setTextSize(PixelUtils.dpToPix(20));
        aprHistoryPlot.getRangeLabelWidget().getLabelPaint().setColor(Color.CYAN);
        aprHistoryPlot.getDomainLabelWidget().getLabelPaint().setTextSize(PixelUtils.dpToPix(15));
        aprHistoryPlot.getDomainLabelWidget().getLabelPaint().setColor(Color.CYAN);

//        referenceSensorSeries = new SimpleXYSeries("Reference Sensor");
//        referenceSensorSeries.useImplicitXVals();
        thermistorSensorSeries = new SimpleXYSeries("");
        thermistorSensorSeries.useImplicitXVals();

//        internalSensorSeries = new SimpleXYSeries("Internal Sensor");
//        internalSensorSeries.useImplicitXVals();

        //aprHistoryPlot.setRangeBoundaries(-1500, 120000, BoundaryMode.FIXED);
        //aprHistoryPlot.setRangeBoundaries(62500, 69000, BoundaryMode.FIXED);   
        aprHistoryPlot.setRangeBoundaries(60, 90, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 10, BoundaryMode.FIXED);
        //     aprHistoryPlot.addSeries(referenceSensorSeries,  new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.BLACK, null, null));
        //    aprHistoryPlot.addSeries(thermistorSensorSeries, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.BLACK, null, null));
        //     aprHistoryPlot.addSeries(internalSensorSeries,   new LineAndPointFormatter(Color.rgb(200, 200, 200), Color.BLACK, null, null));
        aprHistoryPlot.setDomainStepValue(11);
        aprHistoryPlot.setTicksPerRangeLabel(1);
        //     aprHistoryPlot.setDomainLabel("Sample Index");
        aprHistoryPlot.getDomainLabelWidget().pack();
        String degreeSymbol = ("" + (char) 0x00B0);
        aprHistoryPlot.setRangeLabel("Temperature [" + degreeSymbol + "F] (red)");
        aprHistoryPlot.setDomainLabel("Sample Count");


        aprHistoryPlot.getRangeLabelWidget().pack();
        aprHistoryPlot.getGraphWidget().getRangeLabelPaint().setTextSize(PixelUtils.dpToPix(10));
        aprHistoryPlot.getGraphWidget().getDomainLabelPaint().setTextSize(PixelUtils.dpToPix(10));


        LineAndPointFormatter s1Format = new LineAndPointFormatter();
        s1Format.setPointLabelFormatter(new PointLabelFormatter());
        s1Format.configure(getApplicationContext(),
                R.xml.lpf1);
        aprHistoryPlot.addSeries(thermistorSensorSeries, s1Format);

        LineAndPointFormatter s2Format = new LineAndPointFormatter();
        s2Format.setPointLabelFormatter(new PointLabelFormatter());
        s2Format.configure(getApplicationContext(),
                R.xml.lpf2);
        aprHistoryPlot.addSeries(gsrSeries, s2Format);

        aprHistoryPlot.getGraphWidget().getBackgroundPaint().setColor(Color.DKGRAY);
        aprHistoryPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        aprHistoryPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
        aprHistoryPlot.getGraphWidget().setRangeValueFormat(new DecimalFormat("0"));
        aprHistoryPlot.getLegendWidget().setVisible(false);

        // setup checkboxes:
        // hwAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
        final PlotStatistics gsrStats = new PlotStatistics(1000, false);
        final PlotStatistics histStats = new PlotStatistics(1000, false);

        //aprLevelsPlot.addListener(levelStats);
        //    gsrHistoryPlot.addListener(gsrStats);
        aprHistoryPlot.addListener(histStats);
//        hwAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b) {                    
//          //      	gsrHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
//                	aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
//                } else {                    
//           //     	gsrHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                }
//            }
//        });

//        showFpsCb = (CheckBox) findViewById(R.id.showFpsCb);
//        showFpsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                //levelStats.setAnnotatePlotEnabled(b);
//            	gsrStats.setAnnotatePlotEnabled(b);
//                histStats.setAnnotatePlotEnabled(b);
//            }
//        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        initializeNfcAdapterAndIntentFilters();
        mOnCommandExecutedCallBack = this;
        mProgressDialog = new ProgressDialog(mContext);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date current = Calendar.getInstance().getTime();
                //String filename = getBaseContext().getFilesDir().toString()+"/"+current.toString()+".txt";
                //String filename = "/storage/emulated/0/hhhhhhhhhhhhh.txt";

                File myFilename = new File(Environment.getExternalStorageDirectory().toString()+"/nfc_temp");
                if (!myFilename.exists()) {
                    myFilename.mkdirs();
                }
                File fileTo = new File(myFilename.toString() + "/"+current.toString()+".txt");
                FileOutputStream outputStream;
                try{
                    outputStream = new FileOutputStream(fileTo);
                    for(int i = 0;i < thermistorSensorSeries.size();i++){
                        String temp_val = thermistorSensorSeries.getY(i).toString()+"\n";
                        outputStream.write(temp_val.getBytes());
                        Log.d(LOG_TAG,temp_val);
                    }
                    outputStream.close();
                    Context context = getApplicationContext();
                    String display = "file has been saved to "+fileTo.getAbsolutePath();
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,display,duration);
                    toast.show();
                    Log.d(LOG_TAG,fileTo.getAbsolutePath());
                }catch(Exception e){
                    Log.d(LOG_TAG,"Something wrong with saving the file!!");
                    e.printStackTrace();
                }
            }
        });
    }

//    private void cleanup() {
//        finish();		
//	}


    long startTime = 0;

    // runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;


            enableForegroundDispatch(mNfcPendingIntent, mWriteTagFilters, null);
            myintent = getIntent();

            String action = myintent.getAction();

            if ((myintent != myintentOld) && (action != null)) {
                done = true;
                myintentOld = myintent;
            }


            if (myintent != null) {

                mTag = myintent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            }

//          Log.d("DAMIAN", "Seconds is: "+ seconds);
            mProcessNewTags = true;


            if (mTag != null) {
                if (mProcessNewTags) {

                    if (done == true) {
                        if ((Iso15693Utils.getManufacturer(mTag.getId()) == Iso15693Utils.ISO_15693_TEXAS_INSTRUMENTS)) {

                            try {
                                Iso15693WriteSingleBlock iso15693WriteSingleBlock = new Iso15693WriteSingleBlock(mContext, mTag, 1, 1);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 1, Command1, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 2, Command2, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 3, Command3, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 4, Command4, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 5, Command5, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 6, Command6, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 7, Command7, mOnCommandExecutedCallBack);
                                iso15693WriteSingleBlock.WriteSingleBlock((byte) 0, Command0, mOnCommandExecutedCallBack);
                                done = false;
                            } catch (Exception e) {
                                ToastMaker.makeToastShort(mContext, "Failed to interact with Tag.", ToastMaker.STYLE_INFO);
                            }

                        } else {
                            //ToastMaker.makeToastShort(mContext,"Operation not supported for this tag manufacturer",ToastMaker.STYLE_INFO);
                        }
                    }
                }
                disableScanningAndDismissScanDialog();
            }

            mTag = null;
            //   myintent.setAction(null);
            action = null;
            timerHandler.postDelayed(timerRunnable, 100);

        }
    };

    protected void enableForegroundDispatch(PendingIntent intent,
                                            IntentFilter[] filters, String[][] techLists) {
        try {
            String[][] techListsArray = new String[][]{new String[]{MifareUltralight.class.getName(),
                    Ndef.class.getName(),
                    NfcA.class.getName()},
                    new String[]{MifareClassic.class.getName(),
                            Ndef.class.getName(),
                            NfcA.class.getName()}};
            mNfcAdapter.enableForegroundDispatch(this, intent, filters, techListsArray);
        } catch (IllegalStateException e) {
//       NfcDebuglog.e(LOG_TAG, "feature not supported or activity not in foreground");
        }
    }


//	    @Override
//	    public boolean onCreateOptionsMenu(Menu menu) {
//	        MenuInflater inflater = getMenuInflater();
//	        inflater.inflate(R.menu.nfc_read_operation_action_bar_menu, menu);
//	        return true;
//	    }
//	   
//	    @Override
//	    public boolean onOptionsItemSelected(MenuItem item) {
//	        switch (item.getItemId()) {
//	            case R.id.write_tag:
//
//	                return true;
//	            default:
//	                return super.onOptionsItemSelected(item);
//	        }
//	    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nfc_read_operation_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String degreeSymbol = ("" + (char) 0x00B0);
        switch (item.getItemId()) {
            case R.id.Fahrenheit:
                item.setChecked(true);
                aprHistoryPlot.setRangeLabel(" Temperature [" + degreeSymbol + "F] (red)");

                unit = true;
                return true;
            case R.id.Celsius:
                item.setChecked(true);

                aprHistoryPlot.setRangeLabel(" Temperature [" + degreeSymbol + "C] (red)");
                unit = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
//        mProcessNewTags = true;
//        if (mTag != null) {
//            if (mProcessNewTags) {
//            	              
//            	byte Command[] = new byte[] {
//            			(byte) 0x01, //-General Control Register 
//            			(byte) 0x02, //-Status Register
//            			(byte) 0xB2, //-Sensor Control Register
//            			(byte) 0x02, //-Frequency Register
//            			(byte) 0x01, //-Number of Passes Register
//            			(byte) 0x01, //-Extra Sampling Register
//            			(byte) 0x00  //-Power Modes
//            	};
//            	
//            	if((Iso15693Utils.getManufacturer(mTag.getId()) == Iso15693Utils.ISO_15693_TEXAS_INSTRUMENTS )){                            
//            		Iso15693WriteMultipleBlocks iso15693WriteMultipleBlocks = new Iso15693WriteMultipleBlocks(mContext, mTag);
//            		iso15693WriteMultipleBlocks.WriteMultipleBlock((byte) 8, Command, mOnCommandExecutedCallBack);
//            		showProgressDialog();
//            	}else{
//            		ToastMaker.makeToastShort(mContext,"Operation not supported for this tag manufacturer",ToastMaker.STYLE_INFO);
//            	}
//            }
//            disableScanningAndDismissScanDialog();
//        }
    }

    public void showProgressDialog() {
        mProgressDialog.setTitle("Please do not remove device! Reading Patch values. ");
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void onCommandExecuted(byte[] data) {
        onWriteCommandExecuted(data);
    }


    @Override
    //*************formula in data sheet*************************
    public void onWriteCommandExecuted(byte[] result) {
        Log.d(LOG_TAG, "CALLING.....");
        dismissProgressDialog();
        if (result != null) {
            String displayText = null;
            switch (result[0]) {
                case 0x00:
                    displayText = "Failed to write tag";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_FAILURE);
                    mTag = null;
                    break;
                case 0x01:
                    displayText = "GSR & temperature successfully read";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_SUCCESS);
                    mTag = null;
                    String dataFromTag = FlomioNdefHelper.mBytesToHexString(result);
                    String tempString = null;
                    //mEditTextBlockNumber.setText(Integer.parseInt(dataFromTag.substring(6)));


                    long thermValue = Long.parseLong(dataFromTag.substring(12, 14).concat(dataFromTag.substring(10, 12)), 16);
                    long refValue = Long.parseLong(dataFromTag.substring(8, 10).concat(dataFromTag.substring(6, 8)), 16);


                    long tempConv = (long) ((((((thermValue * 0.9) / 16384.0) / 2.0) / 0.0000024) * 8738.13) / refValue);


                    tempConv = (long) (((1.0 / ((((Math.log10(tempConv / 100000.0) / Math.log(2.718))) / 4250.0) + (1.0 / 298.15))) - 273.15));
                    tempConv = (long) (tempConv * 9 / 5 + 32);
                    Log.d(LOG_TAG, "The actual temperature is " + tempConv);

                    Log.d(LOG_TAG, "Reference Sensor " + dataFromTag.substring(8, 10).concat(dataFromTag.substring(6, 8)));
                    Log.d(LOG_TAG, "Thermistor Sensor " + dataFromTag.substring(12, 14).concat(dataFromTag.substring(10, 12)));
                    Log.d(LOG_TAG, "ADC0 Sensor " + dataFromTag.substring(16, 18).concat(dataFromTag.substring(14, 16)));
                    Log.d(LOG_TAG, "Internal Sensor " + dataFromTag.substring(20, 22).concat(dataFromTag.substring(18, 20)));

                    // get rid the oldest sample in history:
                    if (referenceSensorSeries.size() > HISTORY_SIZE) {
                        referenceSensorSeries.removeFirst();
                        thermistorSensorSeries.removeFirst();
                        ADC0SensorSeries.removeFirst();
                        internalSensorSeries.removeFirst();

                    }

                    // add the latest history sample:
                    referenceSensorSeries.addLast(null, Long.parseLong(dataFromTag.substring(8, 10).concat(dataFromTag.substring(6, 8)), 16));
                    //thermistorSensorSeries.addLast(null, Long.parseLong(dataFromTag.substring(12,14).concat(dataFromTag.substring(10,12)),16));
                    thermistorSensorSeries.addLast(null, tempConv);
                    ADC0SensorSeries.addLast(null, Long.parseLong(dataFromTag.substring(16, 18).concat(dataFromTag.substring(14, 16)), 16));
                    internalSensorSeries.addLast(null, Long.parseLong(dataFromTag.substring(20, 22).concat(dataFromTag.substring(18, 20)), 16));

                    // redraw the Plots:
                    //         gsrHistoryPlot.redraw();
                    aprHistoryPlot.redraw();
                    break;
                case 0x02:
                    displayText = "Text too long";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_INFO);
                    break;
                case 0x03:
                    displayText = "Failed to talk to tag, try again";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_FAILURE);
                    mTag = null;
                    break;
                case 0x04:
                    displayText = "Tag technology not supported";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_INFO);
                    mTag = null;
                    break;
            }
        }
    }


    static int Arrindex = 0;
    static long histArr[] = new long[10];
    static long histArrGSR[] = new long[10];
    static long max = -100;
    static long min = 500;

    public static void processData(byte[] result) {
//	 	 displayText = "GSR & temperature successfully read";
        mProgressDialog.dismiss();
        //     ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_SUCCESS);
        // mTag = null;
        String dataFromTag = FlomioNdefHelper.mBytesToHexString(result);
        String tempString = null;
        //mEditTextBlockNumber.setText(Integer.parseInt(dataFromTag.substring(6)));

        double B_Value = 4330.0;
        double R0_Value = 100000.0;
        double T0_Value = 298.15;
        double K0_Temp = 273.15;

        long refValue = Long.parseLong(dataFromTag.substring(6, 8).concat(dataFromTag.substring(4, 6)), 16);
        long thermValue = Long.parseLong(dataFromTag.substring(10, 12).concat(dataFromTag.substring(8, 10)), 16);
        Log.d(LOG_TAG,"MESSAGE::::"+dataFromTag);
        Log.d(LOG_TAG,"original ref::"+dataFromTag.substring(6, 8).concat(dataFromTag.substring(4, 6)));
        Log.d(LOG_TAG,"original therm:"+dataFromTag.substring(10, 12).concat(dataFromTag.substring(8, 10)));
        Log.d(LOG_TAG,"ref_value:::"+new Long(refValue).toString());
        Log.d(LOG_TAG,"therm_value:::"+new Long(thermValue).toString());
        //long adc0 = Long.parseLong(dataFromTag.substring(14,16).concat(dataFromTag.substring(12,14)),16);

        long gsrValue = Long.parseLong(dataFromTag.substring(18, 20).concat(dataFromTag.substring(16, 18)), 16);

        //gsrValue = (long)(gsrValue*200/16384.0);

        long tempConv = (long) ((((((thermValue * 0.9) / 16384.0) / 2.0) / 0.0000024) * 8738.13) / refValue);

        tempConv = (long) ((B_Value / (Math.log10(tempConv / (R0_Value * Math.exp((-B_Value) / T0_Value))) / Math.log10(2.718))) - K0_Temp);

        long gsr = (long) ((((((gsrValue * 0.9) / 16384.0) / 2.0) / 0.0000024) * 8738.13) / refValue);
        gsr = (long) ((B_Value / (Math.log10(gsr / (R0_Value * Math.exp((-B_Value) / T0_Value))) / Math.log10(2.718))) - K0_Temp);

        if (unit == true) {
            tempConv = (tempConv * 9 / 5 + 32);
        }

        //   tempConv = (long) (((1.0 / ((((Math.log10(tempConv / 100000.0) / Math.log(2.718))) / 4250.0) + (1.0 / 298.15))) - 273.15)*9/5+32);


//        Log.d(LOG_TAG, "The actual temperature is " + tempConv);
//        Log.d(LOG_TAG, "Reference Sensor " + dataFromTag.substring(8, 10).concat(dataFromTag.substring(6, 8)));
//        Log.d(LOG_TAG, "Thermistor Sensor " + dataFromTag.substring(12, 14).concat(dataFromTag.substring(10, 12)));
//        Log.d(LOG_TAG, "ADC0 Sensor " + dataFromTag.substring(16, 18).concat(dataFromTag.substring(14, 16)));
//        Log.d(LOG_TAG, "Internal Sensor " + dataFromTag.substring(20, 22).concat(dataFromTag.substring(18, 20)));
        String ref = null,therm = null,adc0 = null,internal=null;
        Long adc0Resis;
        try{
            ref = dataFromTag.substring(6, 8).concat(dataFromTag.substring(4, 6));}
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            therm = dataFromTag.substring(10, 12).concat(dataFromTag.substring(8, 10));}
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            adc0 = dataFromTag.substring(14, 16).concat(dataFromTag.substring(12, 14));
            adc0Resis = Long.parseLong(adc0,16);
            adc0Resis  = (long) ((((((adc0Resis * 0.9) / 16384.0) / 2.0) / 0.0000024) * 8738.13) / refValue);
        }
        catch(Exception e){
            e.printStackTrace();
            adc0Resis = 0L;
        }
        try{
            internal = dataFromTag.substring(4, 6).concat(dataFromTag.substring(2, 4));}
        catch(Exception e){
            e.printStackTrace();
        }

        //*************************************
//        debug1_val.setText("" + thermValue);
//        debug2_val.setText("" + refValue);
//        debug3_val.setText("" + tempConv);
//        debug4_val.setText(""+gsrValue);
        debug1_val.setText("" + ref);
        debug2_val.setText("" + therm);
        debug3_val.setText("" + adc0Resis.toString());
        debug4_val.setText(""+adc0);
        //************************************

        thermistorSensorSeries.addLast(null, tempConv);
        gsrSeries.addLast(null, thermValue / 100);
        //     ADC0SensorSeries.addLast(null, Long.parseLong(dataFromTag.substring(16,18).concat(dataFromTag.substring(14,16)),16));
        //  internalSensorSeries.addLast(null, Long.parseLong(dataFromTag.substring(20,22).concat(dataFromTag.substring(18,20)),16));

        histArrGSR[Arrindex] = thermValue / 100;
        histArr[Arrindex++] = tempConv;


        if (Arrindex == 10) {
            Arrindex = 0;
        }
//        
        domainCounter++;

        if (domainCounter < 10) {
            for (int i = 0; i < domainCounter; i++) {
                if (histArr[i] < histArrGSR[i]) {
                    if (histArr[i] <= min) {
                        min = histArr[i];
                    }
                } else {
                    if (histArrGSR[i] <= min) {
                        min = histArrGSR[i];
                    }
                }

                if (histArr[i] > histArrGSR[i]) {
                    if (histArr[i] > max) {
                        max = histArr[i];
                    }
                } else {
                    if (histArrGSR[i] > max) {
                        max = histArrGSR[i];
                    }

                }


            }
        } else {
            for (int i = 0; i < 10; i++) {
                if (i == 0) {
                    min = 500;
                    max = -100;
                }
                if (histArr[i] < histArrGSR[i]) {
                    if (histArr[i] <= min) {
                        min = histArr[i];
                    }
                } else {
                    if (histArrGSR[i] <= min) {
                        min = histArrGSR[i];
                    }
                }
                if (histArr[i] > histArrGSR[i]) {
                    if (histArr[i] > max) {
                        max = histArr[i];
                    }
                } else {
                    if (histArrGSR[i] > max) {
                        max = histArrGSR[i];
                    }

                }
            }
        }

        aprHistoryPlot.setRangeBoundaries(min - 10, max + 10, BoundaryMode.FIXED);


        if (domainCounter <= 10) {
            aprHistoryPlot.setDomainBoundaries(-1, 10, BoundaryMode.FIXED);
        } else {
            aprHistoryPlot.setDomainBoundaries(domainCounter - 10, domainCounter, BoundaryMode.FIXED);
        }
        // redraw the Plots:
//        gsrHistoryPlot.redraw();
//       aprHistoryPlot.setRangeBottomMax(tempConv+10);
//       aprHistoryPlot.setRangeBottomMin(tempConv-10);


        aprHistoryPlot.redraw();

        done = true;


    }


}