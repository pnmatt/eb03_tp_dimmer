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

    /**Constante de Result, pour l'activité de connexion bluetooth*/
    private final static int BT_CONNECT_CODE = 1;
    /**Constante de Result, pour la vérification des permissions bluetooth*/
    private final static int PERMISSIONS_REQUEST_CODE = 0;
    /**Constante de Result, pour    */
    private final static String[] BT_DANGEROUS_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    /**Element graphique : statut de connexion*/
    private TextView mStatus;
    /**Reference vers singleton OscilloManager, pour transmettre les commandes vers l'oscilloscope*/
    private OscilloManager mOscilloManager;
    /**Element graphique : Slider rond pour sélectionner la valeur de la Led*/
    private RoundSlider mRoundSlider;
    /**Element graphique : bouton de reconnection. Permet la déconnexion/reconnexion au périphérique */
    private Button mConnectButton;
    /**Listener pour le bouton de reconnection*/
    private View.OnClickListener mConnectButtonListener;
    /**Element graphique : */
    private String mDeviceAddress;

    /**
     * Fonction d'initialisation de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatus = findViewById(R.id.status);
        verifyBtRights();
        mOscilloManager = OscilloManager.getInstance();
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

    /**
     * Initialisation du contenu du menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    /**
     * Fonction de sélection du device bluetooth, appelle BTConnectActivity.
     * @see BTConnectActivity
     * @param item
     * @return
     */
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

    /**
     *Fonction de vérification des droits bluetooth
     */
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

    /**
     * Fonction appelée après la requête des permissions bluetooth
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * Traite le retour de l'activité de connexion bluetooth
     * @param requestCode
     * @param resultCode
     * @param data
     */
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