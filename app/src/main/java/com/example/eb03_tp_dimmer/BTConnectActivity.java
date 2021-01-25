  package com.example.eb03_tp_dimmer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Set;

  public class BTConnectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int BT_ACTIVATION_REQUEST_CODE = 0;
    private static final int RN42_COD = 0x1F00;
    private Toolbar mToolbar;
    private Button mScann;
    private ListView mPairedList;
    private ListView mDiscoveredList;
    private BroadcastReceiver mBroadcastReceiver;

    private ArrayAdapter<String> mPairedAdapter;
    private ArrayAdapter<String> mDiscoveredAdapter;
    private ProgressBar mProgressBar;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mBroadcastRegistered;

    private enum Action {START, STOP};

      /**
       *
       * @param savedInstanceState
       */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_connect);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mProgressBar = findViewById(R.id.progress);
        mScann = findViewById(R.id.scan_button);
        mPairedList = findViewById(R.id.paired);
        mDiscoveredList = findViewById(R.id.discovered);

        mPairedList.setOnItemClickListener(this);
        mDiscoveredList.setOnItemClickListener(this);

        mPairedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDiscoveredAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        mPairedList.setAdapter(mPairedAdapter);
        mDiscoveredList.setAdapter(mDiscoveredAdapter);

        mScann.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice pairedDevice : pairedDevices) {
                mPairedAdapter.add(pairedDevice.getName() + "\n" + pairedDevice.getAddress());
            }
        } else {
            mPairedAdapter.add("pas de périphérique appairé");
        }

        mBroadcastRegistered = false;
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (mDiscoveredAdapter.getCount() == 0) {
                            mDiscoveredAdapter.add("aucun périphérique trouvé");
                        }
                        mScann.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;

                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) { // &&(device.getBluetoothClass().getDeviceClass() == RN42_COD)
                            mDiscoveredAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                        break;
                }

            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
        /*if(mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }*/
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        String info = ((TextView)view).getText().toString();
        if(info.equals("aucun périphérique trouvé")||info.equals("pas de périphérique appairé")){
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // lecture de l'adresse du device : récupération des 17 derniers caractères
        if(info.length()>17) {
            info = info.substring(info.length()-17);
            intent.putExtra("device",info);
            setResult(RESULT_OK,intent);
            finish();
            return;
        }

        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void finish() throws Error {
        super.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan_button:
                if(!mBluetoothAdapter.isEnabled()){
                    Intent BTActivation;
                    BTActivation = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(BTActivation,BT_ACTIVATION_REQUEST_CODE);
                    return;
                }
                toggleBtScan();
                break;


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_ACTIVATION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    toggleBtScan();
                } else {
                    Toast.makeText(this, "Le BT doit être activé", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void toggleBtScan(){
        if(mScann.getText().equals("Scanner")){
            btScan(Action.START);
            mProgressBar.setVisibility(View.VISIBLE);
            mScann.setText("Annuler");
        }else{
            btScan(Action.STOP);
            mProgressBar.setVisibility(View.INVISIBLE);
            mScann.setText("Scanner");
        }

    }

    private void btScan(Action startstop){
        if(startstop == Action.START){
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver,filter);
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver,filter);
            mBroadcastRegistered = true;
            mBluetoothAdapter.startDiscovery();
        }else{
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastRegistered = false;
            mBluetoothAdapter.cancelDiscovery();
        }
    }

}
