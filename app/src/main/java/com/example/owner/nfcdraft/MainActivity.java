package com.example.owner.nfcdraft;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import beans.Transaction;
import beans.TransactionResp;
import databaseBean.DatabaseHandler;
import databaseBean.MyDevice;
import models.ConnectionCheck;
import models.Product;
import models.Profile;
import models.ProfileItems;
import models.ProfileProcess;
import models.ProfileReq;
import models.TransInterface;
import models.TransProcess;

public class MainActivity extends ActionBarActivity implements ProfileReq, TransInterface {

    Tag detectedTag;
    TextView number,amnt,msg;
    Button add,rem;
    TableRow signalBox;
    NfcAdapter nfcAdapter;
    IntentFilter[] readTagFilters;
    PendingIntent pendingIntent;
    Context context=this;
    String devId, cardId, prodId,price, token;
    String transaction_url, profile_url;
    TelephonyManager telManager;

    Profile profile;
    List pList;
    List pIdList;
    List pPrice;
    Spinner spinner;
    ArrayAdapter pListAdapter;//=new ArrayAdapter(this,android.R.layout.simple_list_item_1, pListDetails);
    //pList.setAdapter(pListAdapter);
    String amount,qty,itemId;
    int profileId,transporterId;

    ConnectionCheck cc;

    DatabaseHandler db;

    int transactionCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title and request ffull screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //_______________________Check Internet Connectivity__________________\\
        cc=new ConnectionCheck();
        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cc.isNetworkAvailable(getApplicationContext(), cm)){
            prompt("success","Welcome!","Welcome to Intercity Application. \nPowered by Oltranz Ltd.\nwww.oltranz.com");
        }else{
            prompt("failover","Attention!","This application requires internet connectivity, no internet connection detected");
        }

        //________________________Check If the device is Registered______________________________\\
        checkDevice();

        //_________________________Application url(s)_________________________\\
        profile_url="http://41.74.172.132:8080/IntercityTransport/sellingDevicesManagementServices/sellingDeviceProfile/";
        //transaction_url="http://41.74.172.132:8080/IntercityTransport/salesServices/cardPaymentOnPOSRequest/";
        transaction_url="http://41.74.172.132:8080/IntercityTransport/salesServices/cardPaymentOnPOSRequest";
        //url="http://41.74.172.132:8080/FomocoBusiness/deviceprofile/imie/";


        //__________Getting device ID_____________\\

//        String serial = null;
//
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class);
//            serial = (String) get.invoke(c, "ro.serialno");
//            if(serial.equals(null)){
//                serial = (String) get.invoke(c, "sys.serialnumber");
//                if(serial.equals(null)) {
//                    serial = (String) get.invoke(c, "ril.serialnumber");
//                    if(serial.equals(null)) {
//                            Toast.makeText(getApplicationContext(),"No device ID", Toast.LENGTH_LONG).show();
//                            this.finish();
//
//                    }
//                }
//            }
//            devId=serial;
//            System.out.println("The devices serial number is:"+devId);
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//            Toast.makeText(getApplicationContext(),"No device ID", Toast.LENGTH_LONG).show();
//            this.finish();
//        }

        //telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //devId="0123456789ABCDEF";

        transactionCount=0;

        profile=new Profile();

        //______________Refresh device profile________________\\
        //refresh url
        pList=new ArrayList<String>();
        pIdList=new ArrayList<String>();
        pPrice=new ArrayList<String>();

        refreshDevProf(devId,profile_url);


        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        detectedTag =getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

        //______________Defining UI elements_________________\\
        number=(TextView) findViewById(R.id.itemnumber);
        amnt=(TextView) findViewById(R.id.price);
        msg=(TextView) findViewById(R.id.msg);
        signalBox=(TableRow) findViewById(R.id.signalbox);

        add=(Button)findViewById(R.id.add);
        rem=(Button) findViewById(R.id.remove);
        rem.setEnabled(false);

        //__________price Initialisation___________\\
        //price="120";
        //amnt.setText(price+ " Rwf");

        spinner=(Spinner) findViewById(R.id.products);


        //Putting data in pList

        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        IntentFilter filter2     = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        readTagFilters = new IntentFilter[]{tagDetected,filter2};
    }

    @Override
    protected void onNewIntent(Intent intent) {

        setIntent(intent);
        String action = intent.getAction();
        if(getIntent().getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            readFromTag(getIntent());
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, readTagFilters, null);
    }


    public void readFromTag(Intent intent){
        //_______________detect type of NFC used__________________\\

        //if type is NDEF
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Ndef ndef = Ndef.get(detectedTag);
            try{
                ndef.connect();

                Tag ndefTag=ndef.getTag();
                cardId=bin2hex(ndefTag.getId());
               // txtType.setText("Type: " + ndef.getType().toString());
               // txtSize.setText("Size: "+String.valueOf(ndef.getMaxSize()));
                //txtWrite.setText(ndef.isWritable() ? "Write: "+"True" : "Write: "+"False");
                Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (messages != null) {
                    NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                    for (int i = 0; i < messages.length; i++) {
                        ndefMessages[i] = (NdefMessage) messages[i];
                    }
                    NdefRecord record = ndefMessages[0].getRecords()[0];

                    byte[] payload = record.getPayload();
                    String text = new String(payload);
                   // txtRead.setText(text);


                    ndef.close();

                }

                try{
                    if(transactionCount==0){
                        rem.setEnabled(false);
                        add.setEnabled(false);
                        transactionCount+=1;

                        cardId=bin2hex(detectedTag.getId());

                        qty=number.getText().toString();
                        String total=amnt.getText().toString().replace(" Rwf","");
                        Transaction transaction=new Transaction();
                        transaction.setCardId(cardId);
                        transaction.setDeviceId(devId);
                        transaction.setItemId(Integer.parseInt(itemId));
                        transaction.setQuantity(Integer.parseInt(qty));
                        transaction.setProfileId(profileId);
                        transaction.setTransporterId(transporterId);
                        transaction.setTotalAmount(Integer.parseInt(total));
                        transDataToSend(transaction);
                    }else{
                        Toast.makeText(getApplicationContext(), "Please wait the first task to finish.", Toast.LENGTH_LONG).show();
                    }

                    //  txtType.setText("Card Id: " + bin2hex(detectedTag.getId()));
                    // txtSize.setText(String.valueOf(ndef.getMaxSize()));
                    // txtWrite.setText(ndef.isWritable() ? "True" : "False");
                    //Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Problem with card.", Toast.LENGTH_LONG).show();
                }

            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Problem with card", Toast.LENGTH_LONG).show();
            }
        } else if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)|| intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED) ){
         //The card was mirfare or other

            Log.d("S/N", " Detected tag ID: " + detectedTag.getId());

            MifareUltralight mifare = MifareUltralight.get(detectedTag);
            try {
                mifare.connect();
                byte[] payload = mifare.readPages(4);
               // txtRead.setText(String.valueOf(new String(payload, Charset.forName("US-ASCII"))));
            } catch (IOException e) {
                Log.e("Notification: ", "IOException while writing MifareUltralight message...", e);
            }catch (Throwable t){
                Log.e("Notification: ", "IOException while writing MifareUltralight message...", t);
            }finally {
                if (mifare != null) {
                    try {
                        mifare.close();
                    }
                    catch (IOException e) {
                        Log.e("Notification ", "Error closing tag...", e);
                    }
                }
            }

            try{
                if(transactionCount==0){
                    rem.setEnabled(false);
                    add.setEnabled(false);
                    transactionCount+=1;

                    cardId=bin2hex(detectedTag.getId());

                    qty=number.getText().toString();
                    String total=amnt.getText().toString().replace(" Rwf","");
                    Transaction transaction=new Transaction();
                    transaction.setCardId(cardId);
                    transaction.setDeviceId(devId);
                    transaction.setItemId(Integer.parseInt(itemId));
                    transaction.setQuantity(Integer.parseInt(qty));
                    transaction.setProfileId(profileId);
                    transaction.setTransporterId(transporterId);
                    transaction.setTotalAmount(Integer.parseInt(total));
                    transDataToSend(transaction);
                }else{
                    Toast.makeText(getApplicationContext(), "Please wait the first task to finish.", Toast.LENGTH_LONG).show();
                }

              //  txtType.setText("Card Id: " + bin2hex(detectedTag.getId()));
                // txtSize.setText(String.valueOf(ndef.getMaxSize()));
                // txtWrite.setText(ndef.isWritable() ? "True" : "False");
                //Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Problem with card.", Toast.LENGTH_LONG).show();
            }
        }
    }
    //To display on the UID plain text
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
    }

    //_______________Handling add and remove button___________________\\
    public void add(View v){
        int numTickets= Integer.parseInt(number.getText().toString());
        if(numTickets==10){
            try {
                if(!price.equals("0") || price!=null) {
                    add.setEnabled(false);
                    Double amount = Double.valueOf(price) * numTickets;
                    amnt.setText(String.valueOf(Double.valueOf(price) * numTickets).split("\\.")[0] + " Rwf");
                }else{
                    //refresh device profile
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            try {
                if(!price.equals("0") || price!=null) {
                    numTickets += 1;
                    number.setText(String.valueOf(numTickets));
                    amnt.setText(String.valueOf(Double.valueOf(price) * numTickets).split("\\.")[0] + " Rwf");
                    if (!rem.isEnabled())
                        rem.setEnabled(true);
                }else{
                    //refresh device profile
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void remove(View v){
        int numTickets= Integer.parseInt(number.getText().toString());
        if(numTickets==1){
            try {
                if(!price.equals("0") || price!=null) {
                    rem.setEnabled(false);
                    amnt.setText(String.valueOf(Double.valueOf(price) * numTickets).split("\\.")[0] + " Rwf");
                }else{
                    //refresh device profile
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try {
                if(!price.equals("0") || price!=null) {
                    numTickets -= 1;
                    number.setText(String.valueOf(numTickets));
                    amnt.setText(String.valueOf(Double.valueOf(price) * numTickets).split("\\.")[0] + " Rwf");
                    if (!add.isEnabled())
                        add.setEnabled(true);
                }else{
                    //refresh device profile
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void refreshDevProf(String devId, String pUrl){
        ProfileProcess profP=new ProfileProcess(this,devId,"GET",pUrl);
    }

    @Override
    public void profData(Profile profile) {
        ProfileItems profileItems= new ProfileItems();
        try{
        this.profile=profile;
            //______________Check profile message________________\\
            /* for future use when message are standardized */
            profileItems=profile.getProfileOnSellingDevice();
            profileId=profileItems.getId();
            transporterId=profileItems.getTransporterId();
            List<String> dataToShow=new ArrayList<String>();

            List<Product> items=new ArrayList<Product>();
            items=profileItems.getItems();
            Iterator iterator=items.iterator();
            int i=0;
            while (iterator.hasNext()){
                Product product=new Product();
                product=(Product) iterator.next();
                pList.add(product.getName());
                pIdList.add(product.getId());
                pPrice.add(product.getPrice());
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //what is the selected item? and initialize values accordingly
                    selectedItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            pListAdapter=new ArrayAdapter(this,R.layout.srow,R.id.spinnerItem, pList);
            spinner.setAdapter(pListAdapter);

            //pList=dataToShow;
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    //_______________User info message_______________\\
    @Override
    public void info(String msg) {

    }

    public void selectedItem(int position){
        itemId=  String.valueOf(pIdList.get(position));
        String unitPrice=String.valueOf(pPrice.get(position));
        price=unitPrice.split("\\.")[0];
        amnt.setText(unitPrice.split("\\.")[0]+" Rwf");
    }

    public void transDataToSend(Transaction tr){
        TransProcess tp=new TransProcess(this, transaction_url, tr);
    }

    @Override
    public void transactionData(TransactionResp transactionResp) {
        String message=transactionResp.getMessage();
        message=message.trim().toUpperCase();
        if(!message.equalsIgnoreCase("success")){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetUI();
                    signalBox.setBackground(getResources().getDrawable(R.drawable.bg_notification_default));
                    msg.setText("");
                }
            }, 3000);
            signalBox.setBackground(getResources().getDrawable(R.drawable.bg_notification_failed));
            msg.setText(transactionResp.getMessage());
            try {
                AssetFileDescriptor afd = getAssets().openFd("fail_notification.mp3");
                MediaPlayer md=new MediaPlayer();
                md.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                md.prepare();
                md.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Handler handler = new Handler();
            Transaction transaction=new Transaction();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetUI();
                    signalBox.setBackground(getResources().getDrawable(R.drawable.bg_notification_default));
                    msg.setText("");
                }
            }, 3000);
            signalBox.setBackground(getResources().getDrawable(R.drawable.bg_notification_success));
            msg.setText("Deducted Amount: " + transactionResp.getCardpaymentonposrequestmodel().getTotalAmount() + "\n" + "Card ID: #######");
            try {
                AssetFileDescriptor afd = getAssets().openFd("success_notification.mp3");
                MediaPlayer md=new MediaPlayer();
                md.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                md.prepare();
                md.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resetUI(){
        if(transactionCount!=0)
            transactionCount=0;
        if(!add.isEnabled())
            add.setEnabled(true);
        if(rem.isEnabled())
            rem.setEnabled(false);
        number.setText("1");
        amnt.setText(price);
    }

    public void prompt(String what,String title,String msg){


        //alert box to show internet connection error
        final AlertDialog.Builder Internet_Alert = new AlertDialog.Builder(context);
        // set title
        Internet_Alert.setCancelable(false);
        Internet_Alert.setTitle(title);
        Internet_Alert.setMessage(msg);

        if(what.equalsIgnoreCase("failover")){
            Internet_Alert.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    onQuitPressed();
                }
            });

        }else{
            Internet_Alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Internet_Alert.create().dismiss();
                }
            });
        }
        Internet_Alert.create().show();
    }

    //to remove application from task manager
    public void onQuitPressed() {

        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    //_________________Device registaration check____________________\\
    public void checkDevice() {
        db = new DatabaseHandler(context);
        int count = db.getDeviceCount();
        System.out.print("Device count from the database: " + count);
        if (count <= 0) {
            MyDevice device = db.getAllDevice();
            devId = device.getDeviceName();
            System.out.print("Device name from the database: " + devId);

            Intent intent = new Intent(context, RegisterDevice.class);
            this.finish();
            startActivity(intent);
        } else {
            try {
                MyDevice device = db.getAllDevice();
                devId = device.getDeviceName();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getMessage());
                //Intent intent=new Intent(context,RegisterDevice.class);
                //finish();
                //startActivity(intent);}
            }
        }
    }
}
