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

/**
 * Created by user on 3/25/2016.
 */
public class ProfileProcess {
    ProfileReq profReq;
    String data;
    String url;
    String method;

    public ProfileProcess(ProfileReq profReq, String data, String method, String url) {
        this.url = url;
        this.method=method;
        this.data = data;
        this.profReq = profReq;



        onlineReq(data,url);
    }

    public void onlineReq(String data, String url){
        if(method.equals("GET"))
            new OnlineCheck().execute(url+data);
        else if(method.equals("POST"))
            new OnlineCheck().execute(url);
    }

    //___________making online request asynchronously_________________\\
    private class OnlineCheck extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String finalUrl=params[0];

            try {

                //String url="http://41.74.172.132:8080/PetroStationManager/androiddata/login";
//_____________Opening connection and post data____________//
                URL oURL = new URL(finalUrl);
                HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
                if(method.equals("GET")){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    System.out.println("URL to Get data from:" +finalUrl);
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        //System.out.println(inputLine);
                        response.append(inputLine);
                    }
                    in.close();
                    con.disconnect();
                    System.out.println("Data from the server: " + response.toString());
                    return response.toString();
                }else if(method.equals("POST")){
                    con.setRequestMethod(method);
                    con.setRequestProperty("Content-type", "Application/json; charset=UTF-8");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(finalUrl);
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
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ObjectMapper mapper = new ObjectMapper();
            //JSON from String to Object
            System.out.print("Result from the server: "+result);
            try {
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Profile profile = mapper.readValue(result, Profile.class);

                //Calling back the usage of interface
                profReq.profData(profile);
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
