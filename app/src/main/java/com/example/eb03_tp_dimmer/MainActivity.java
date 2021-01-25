package com.example.eb03_tp_dimmer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_eb03_customview.RoundSlider;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    private final static int BT_CONNECT_CODE = 1;
    private final static int PERMISSIONS_REQUEST_CODE = 0;
    private final static String[] BT_DANGEROUS_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private TextView mStatus;
    private AppCompatSeekBar mSeekbar;
    private OscilloManager mOscilloManager;
    private RoundSlider mRoundSlider;
    private Button mConnectButton;
    private View.OnClickListener mConnectButtonListener;
    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatus = findViewById(R.id.status);
        verifyBtRights();
        mOscilloManager = OscilloManager.getInstance();
        /*mSeekbar = findViewById(R.id.seekBar);
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int nb_commands = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mOscilloManager.getStatus() == Transceiver.STATE_CONNECTED){
                    mOscilloManager.setCalibrationDutyCycle(progress);
                }else if(mOscilloManager.getStatus() == Transceiver.STATE_CONNECTING){
                    if(nb_commands%100 == 0){
                        Toast.makeText(MainActivity.this,"L'application se connecte...",Toast.LENGTH_LONG).show();
                        nb_commands = 0;
                    }
                    nb_commands ++;
                }else if(mOscilloManager.getStatus() == Transceiver.STATE_NOT_CONNECTED){
                    if(nb_commands%100 == 0){
                        Toast.makeText(MainActivity.this,String.format("%d",nb_commands),Toast.LENGTH_LONG).show();
                        nb_commands = 0;
                    }
                    //"L'application n'est pas connectée"
                    nb_commands ++;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
        mRoundSlider = findViewById(R.id.roundslider);
        mRoundSlider.setRoundSliderListener(new RoundSlider.RoundSliderListener() {
            private int nb_commands = 0;

            @Override
            public void onChange(float v) {
                if(mOscilloManager.getStatus() == Transceiver.STATE_CONNECTED){
                    mOscilloManager.setCalibrationDutyCycle((int)v);
                }else if(mOscilloManager.getStatus() == Transceiver.STATE_CONNECTING){
                    if(nb_commands%100 == 0) {
                        Toast.makeText(MainActivity.this, "L'application se connecte...", Toast.LENGTH_LONG).show();
                        nb_commands = 0;
                    }
                    nb_commands ++;
                }else if(mOscilloManager.getStatus() == Transceiver.STATE_NOT_CONNECTED){
                    if(nb_commands%100 == 0) {
                        Toast.makeText(MainActivity.this,"L'application n'est pas connectée",Toast.LENGTH_LONG).show();
                    nb_commands = 0;
                }
                nb_commands ++;
                }
            }

            @Override
            public void onDoubleClick(float v) {
                if(mOscilloManager.getStatus() == Transceiver.STATE_CONNECTED){
                    mOscilloManager.setCalibrationDutyCycle(0);
                }else if(mOscilloManager.getStatus() == Transceiver.STATE_CONNECTING){
                    if(nb_commands%100 == 0) {
                        Toast.makeText(MainActivity.this, "L'application se connecte...", Toast.LENGTH_LONG).show();
                        nb_commands = 0;
                    }
                    nb_commands ++;
                }else if(mOscilloManager.getStatus() == Transceiver.STATE_NOT_CONNECTED){
                    if(nb_commands%100 == 0) {
                        Toast.makeText(MainActivity.this,"L'application n'est pas connectée",Toast.LENGTH_LONG).show();
                        nb_commands = 0;
                    }
                    nb_commands ++;
                }
            }
        });

        mConnectButton = findViewById(R.id.connect_button);
        mConnectButtonListener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mConnectButton.getText() == "disconnect"){
                    mOscilloManager.detachTransceiver();
                    mConnectButton.setText("connect");
                }else{
                    Transceiver tr = new BTManager();
                    mOscilloManager.attachTransceiver(tr);
                    tr.connect(mDeviceAddress);
                    mConnectButton.setText("disconnect");
                }

            }
        };
        mConnectButton.setOnClickListener(mConnectButtonListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItem = item.getItemId();
        switch(menuItem){
            case R.id.connect:
                Intent BTConnect;
                BTConnect = new Intent(this,BTConnectActivity.class);
                startActivityForResult(BTConnect,BT_CONNECT_CODE);
        }
        return true;
    }



    private void verifyBtRights(){
        if(BluetoothAdapter.getDefaultAdapter() == null){
            Toast.makeText(this,"Cette application nécessite un adaptateur BT",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
                requestPermissions(BT_DANGEROUS_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this,"Les autorisations BT sont requises pour utiliser l'application",Toast.LENGTH_LONG).show();
                finish();
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_CONNECT_CODE:
                if (resultCode == RESULT_OK) {
                    String address = data.getStringExtra("device");
                    mDeviceAddress = address;
                    mStatus.setText(address);
                    Transceiver tr = new BTManager();
                    mOscilloManager.attachTransceiver(tr);
                    tr.connect(address);
                    mConnectButton.setEnabled(true);
                    mConnectButton.setText("disconnect");
                }
                break;
            default:
        }
    }
}