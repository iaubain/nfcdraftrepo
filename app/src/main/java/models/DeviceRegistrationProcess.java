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

import beans.DeviceRegistration;
import beans.Record;
import beans.TransactionResp;

/**
 * Created by user on 4/11/2016.
 */
public class DeviceRegistrationProcess {
    DevInterface di;
    Record record;
    String url;

    public DeviceRegistrationProcess(DevInterface di, String url, Record record) {
        this.di = di;
        this.url=url;
        this.record = record;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsnoData=mapper.writeValueAsString(record);
            dataToSend(jsnoData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dataToSend(String data){
        //pusshing data on online link
        new OnlinePushPull().execute(data);
    }

    //___________making online push and pull asynchronously_________________\\
    private class OnlinePushPull extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String data=params[0];
            System.out.println("usage data :"+data);
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
                DeviceRegistration dr = mapper.readValue(result, DeviceRegistration.class);

                //Calling back the usage of interface
                di.what(dr);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Throwable t){
                t.printStackTrace();
            }
        }
    }
}
