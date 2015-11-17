package com.uwp.kelly.productonline;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kelly on 11/13/2015.
 */
public class Networking {

    //define URL for remote server
    static final String baseURL = "http://dev.mateo.io/mobiledev/hanh/";
    static final String getProductsURL = baseURL+"get_products.php";
    static final String addProductURL = baseURL+"add_product.php";
    //static final String addProductURL = baseURL+"add_product_test.php";
    static final String updateProductURL = baseURL+"update_product.php";
    static final String deleteProductURL = baseURL+"delete_product.php";

    //JSON nodes
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_QUANTITY = "quantity";
    private static final String TAG_DATE = "dateAdded";





    public static class AsyncTaskProductList extends AsyncTask<Void,Void,ArrayList> {


        @Override
        protected ArrayList<Product> doInBackground(Void... params) {
            ArrayList<Product> productArrayList = null;
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;




            String productJsonStr = null;

            try {
                URL url = new URL(getProductsURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read input stream

                InputStream inputStream = urlConnection.getInputStream();


                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    productJsonStr = null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line=bufferedReader.readLine())!=null){
                    buffer.append(line+"\n");
                }
                if(buffer.length()==0){
                    productJsonStr = null;
                }
                else productJsonStr = buffer.toString();

                Log.d("MainActivity", productJsonStr);

                productArrayList = new ArrayList<>();
                productArrayList = getProductFromJSON(productJsonStr);


            }
            catch (IOException e){
                productJsonStr = null;
                Log.e("MainActivity", "Parsing error", e);
            }
            finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if (bufferedReader!=null){
                    try {
                        bufferedReader.close();
                    }
                    catch (final IOException e){
                        Log.e("MainActivity","Error clossing Stream",e);
                    }
                }
            }
            return productArrayList;
        }

        private ArrayList<Product> getProductFromJSON(String productJsonStr){
            ArrayList<Product> productList = new ArrayList<>();
            JSONArray products = null;
            try{
                JSONObject productJson = new JSONObject(productJsonStr);
                int success = productJson.getInt(TAG_SUCCESS);
                if(success==1){
                    products = productJson.getJSONArray(TAG_PRODUCTS);
                    for(int i = 0; i < products.length();i++){
                        JSONObject product = products.getJSONObject(i);
                        int id = product.getInt(TAG_PID);
                        String title = product.getString(TAG_NAME);
                        Float price = BigDecimal.valueOf(product.getDouble(TAG_PRICE)).floatValue();
                        int quantity = product.getInt(TAG_QUANTITY);
                        String dateString = product.getString(TAG_DATE);
                        Product productItem = new Product(id,title,price,quantity,dateString);
                        productList.add(productItem);
                    }
                }
            }
            catch (Exception e){
                Log.e("Networking","Failed to create JSONObject");
            }

            return  productList;
        }
    }

   public static class AsynTaskProductAdd extends AsyncTask<String,Void,Void>{




        @Override
        protected Void doInBackground(String... params) {
            String title = params[0];
            String quantity = params[1];
            String price = params[2];
            String urlParameters = "Name="+title+"&Quantity="+quantity+"&Price="+price;

            URL url;
            HttpURLConnection connection = null;
            try {
                //Create connection
                url = new URL(addProductURL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                Log.e("Networking","flag");
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }

                rd.close();
                Log.e("Networking",response.toString());


            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }
            return null;

        }
    }


    /*public static class AsynTaskProductAdd extends AsyncTask<Product,Void,Void>{
        private Context mcontext;
        public AsynTaskProductAdd(Context context){
            context = mcontext;
        }



        @Override
        protected Void doInBackground(Product... params) {
            Product product;
            product = params[0];
            String title = product.title;
            String quantity = String.valueOf(product.quantity);
            String price = String.valueOf(product.price);
            Bitmap bitmap = product.bitmap;
            FileInputStream fileInputStream =null;
            URL url;
            HttpURLConnection connection = null;
            String filename = title+".PNG";

            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String urlParameters = "Name="+title+"&Quantity="+quantity+"&Price="+price;
            if (bitmap!= null){
                File imageFile = getImageFile(bitmap,filename);

                try {
                    fileInputStream = new FileInputStream(imageFile);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }



            try {
                //Create connection


                url = new URL(addProductURL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                //connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("image_uploaded", filename);

                dos = new DataOutputStream(connection.getOutputStream());

                //Send request

                dos.writeBytes(twoHyphens+boundary+lineEnd);

                //adding parameter Name

                dos.writeBytes("Content-Disposition: form-data; name = \"Name\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(title);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens+boundary+lineEnd);

                //adding parameter Quantity

                dos.writeBytes("Content-Disposition: form-data; name = \"Quantiy\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(quantity);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens+boundary+lineEnd);

                //adding parameter Price

                dos.writeBytes("Content-Disposition: form-data; name = \"Price\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(price);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens+boundary+lineEnd);

                //adding parameter File

                if(fileInputStream!=null){



                }
            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }
            return null;

        }

        public File getImageFile(Bitmap bitmap, String filename){

            File f = new File(mcontext.getCacheDir(), filename);
            try{
                f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return f;
        }
    }*/
    public static class AsynTaskProductUpdate extends AsyncTask<String,Void,Void>{




        @Override
        protected Void doInBackground(String... params) {
            String title = params[0];
            String quantity = params[1];
            String price = params[2];
            String PID = params[3];
            String urlParameters = "PID="+PID+"&Name="+title+"&Quantity="+quantity+"&Price="+price;
            Log.e("Networking","Quantity" + quantity);
            URL url;
            HttpURLConnection connection = null;
            try {
                //Create connection
                url = new URL(updateProductURL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                Log.e("Networking","flagupdate"+PID);
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }

                rd.close();
                Log.e("Networking",response.toString());


            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }
            return null;

        }
    }

    public static class AsynTaskProductDelete extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... params) {
            String PID = params[0];

            String urlParameters = "PID="+PID;

            URL url;
            HttpURLConnection connection = null;
            try {
                //Create connection
                url = new URL(deleteProductURL);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                Log.e("Networking","flagupdate"+PID);
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }

                rd.close();
                Log.e("Networking",response.toString());


            } catch (Exception e) {

                e.printStackTrace();
                return null;

            } finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }
            return null;

        }
    }

}
