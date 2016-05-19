package models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Owner on 4/8/2016.
 */
public class ConnectionCheck {
    Context context;
    ConnectivityManager cm;
//    public ConnectionCheck(Context context, ConnectivityManager cm){
//
//    }

    //check connection
    public boolean isNetworkAvailable(Context context, ConnectivityManager cm) {
        this.context=context;
        this.cm=cm;

        try{
            //ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null && netInfo.isFailover()) {
                //prompt("failover","Attention!","This application requires internet connectivity, no internet connection detected");
                return false;
            }else{
                //prompt("success","Welcome!","Welcome to Intercity Application. Powered by Oltranz Ltd.\nwww.oltranz.com");
                return true;
            }
            //return;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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
            public void onClick(DialogInterface arg0, int arg1)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                onQuitPressed();
            }
        });

        }else{
            Internet_Alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1)
                {
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
