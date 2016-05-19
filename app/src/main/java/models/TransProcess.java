package models;

import android.os.AsyncTask;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import beans.Transaction;
import beans.TransactionResp;

/**
 * Created by user on 3/26/2016.
 */
public class TransProcess {
    TransInterface ti;
    Transaction trans;
    String url;

    public TransProcess(TransInterface ti, String url, Transaction trans) {
        this.ti = ti;
        this.url=url;
        this.trans = trans;

        onlineDataPreparation(trans);
    }
    public void onlineDataPreparation(Transaction trans){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsnoData=mapper.writeValueAsString(trans);

            //pusshing data on online link
            new OnlinePushPull().execute(jsnoData);
        } catch (Exception e) {
            TransactionResp transactionResp=new TransactionResp();
            transactionResp.setMessage("Bad Data Structure");
            //Calling back the usage of interface
            ti.transactionData(transactionResp);
        }
    }

    //___________making online push and pull asynchronously_________________\\
    private class OnlinePushPull extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String data=params[0];

            try {


                //String url="http://41.74.172.132:8080/PetroStationManager/androiddata/login";
//_____________Opening connection and post data____________//
                URL oURL = new URL(url);
                HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-type", "Application/json; charset=UTF-8");
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(data);
                wr.flush();
                wr.close();
                System.out.println("Data to post :"+data);
                BufferedReader in1= new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in1.readLine()) != null) {
                    response.append(inputLine);
                }
                in1.close();
                con.disconnect();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("Data from Server: "+result);
            //JSON from String to Object
            try {
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                TransactionResp transactionResp = mapper.readValue(result, TransactionResp.class);

                //Calling back the usage of interface
                ti.transactionData(transactionResp);
            } catch (IOException e) {
                e.printStackTrace();
                TransactionResp transactionResp=new TransactionResp();
                transactionResp.setMessage("Bad Server Communication");
                //Calling back the usage of interface
                ti.transactionData(transactionResp);
            }catch (NullPointerException e){
                e.printStackTrace();
                TransactionResp transactionResp=new TransactionResp();
                transactionResp.setMessage("Bad Server Response");
                //Calling back the usage of interface
                ti.transactionData(transactionResp);
            }catch (Throwable t){
                t.printStackTrace();
                TransactionResp transactionResp=new TransactionResp();
                transactionResp.setMessage("Bad Server Data Exchange");
                //Calling back the usage of interface
                ti.transactionData(transactionResp);
            }
        }
    }
}
