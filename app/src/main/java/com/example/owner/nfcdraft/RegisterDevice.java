package com.example.owner.nfcdraft;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import beans.DeviceRegistration;
import beans.Record;
import databaseBean.DatabaseHandler;
import databaseBean.MyDevice;
import models.ConnectionCheck;
import models.DevInterface;
import models.DeviceRegistrationProcess;

public class RegisterDevice extends ActionBarActivity implements DevInterface {

    TextView monitor;
    EditText tel,pw,devName;
    Button reg;

    Context context=this;

    DeviceRegistrationProcess dp;

    String devRegUrl, dName;

    ConnectionCheck cc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);

        //_______________________Check Internet Connectivity__________________\\
        cc=new ConnectionCheck();
        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cc.isNetworkAvailable(getApplicationContext(), cm)){
            internetPrompt("success","Welcome!","Welcome to Intercity Application. \nPowered by Oltranz Ltd.\nwww.oltranz.com");
        }else{
            internetPrompt("failover","Attention!","This application requires internet connectivity, no internet connection detected");
        }

        devRegUrl="http://41.74.172.132:8080/IntercityTransport/sellingDevicesManagementServices/registerDevice";

        monitor=(TextView) findViewById(R.id.register);
        tel=(EditText) findViewById(R.id.tel);
        pw=(EditText) findViewById(R.id.password);
        devName=(EditText) findViewById(R.id.devicename);

        reg=(Button) findViewById(R.id.register);
    }

    @Override
    public void what(DeviceRegistration dr) {

        if(!dr.getMessage().equals("New sellingDevice successfully registered")){
            prompt("Attention!",dr.getMessage());
        }else{
            //Record device into Database and go to main activity.




//            //__________Getting device ID_____________\\
//
//            String serial = null, devId= null;
//
//            try {
//                Class<?> c = Class.forName("android.os.SystemProperties");
//                Method get = c.getMethod("get", String.class);
//                serial = (String) get.invoke(c, "ro.serialno");
//                if(serial.equals(null)) {
//                    serial = (String) get.invoke(c, "sys.serialnumber");
//                    if(serial.equals(null)) {
//                        serial = (String) get.invoke(c, "ril.serialnumber");
//                        if(serial.equals(null)) {
//                            Toast.makeText(getApplicationContext(), "No device ID", Toast.LENGTH_LONG).show();
//                            devId="default";
//                            //this.finish();
//
//                        }
//                    }
//                }
//                devId=serial;
//                System.out.println("The devices serial number is:"+devId);
//            } catch (Exception ignored) {
//                ignored.printStackTrace();
//                Toast.makeText(getApplicationContext(),"No device ID", Toast.LENGTH_LONG).show();
//                //this.finish();
//            }
            MyDevice md=new MyDevice();
            md.setDeviceName(dName);
            DatabaseHandler db=new DatabaseHandler(context);
            int truncate=db.truncate();
            System.out.println("Truncate Status is: " + truncate);

            //Adding new Device
            long insert=db.addDevice(md);
            System.out.println("Inserting new device Status: " + insert);

            Intent intent=new Intent(context,MainActivity.class);
            this.finish();
            startActivity(intent);
        }
    }

    public void regClicked(View view){
        if(tel.getText().toString().length()!=0 && pw.getText().toString().length()!=0 && devName.getText().toString().length()!=0){
            Record rec=new Record();
            rec.setDeviceName(devName.getText().toString());
            rec.setPassword(pw.getText().toString());
            rec.setPhoneNumber(tel.getText().toString());
            dName=devName.getText().toString();
            dp=new DeviceRegistrationProcess(this, devRegUrl,rec);
        }else{
            prompt("Attention!","Consider revising your field's data.");
        }
    }

    public void prompt(String title,String msg){
        //alert box
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // set title
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.create().dismiss();
            }
            });

        dialog.create().show();
    }

    public void internetPrompt(String what,String title,String msg){


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
}
