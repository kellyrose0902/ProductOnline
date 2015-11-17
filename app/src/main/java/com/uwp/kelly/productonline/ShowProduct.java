package com.uwp.kelly.productonline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.ImageUtility;

public class ShowProduct extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private Bitmap bitmapPhoto = null;
    private Point mSize;

    private Button addB;
    private EditText input_name;
    private EditText input_quantity;
    private EditText input_price;
    private ImageView productImage;

    private String oldName;
    private String oldQuantity;
    private String oldPrice;
    private String productID;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

        addB = (Button)findViewById(R.id.add_update_button);
        input_name = (EditText)findViewById(R.id.input_name);
        input_quantity = (EditText)findViewById(R.id.input_quantity);
        input_price = (EditText) findViewById(R.id.input_price);
        productImage = (ImageView)findViewById(R.id.input_image);

        Intent intent = getIntent();
        boolean mode = intent.getBooleanExtra("isNew",true);
        if(mode){
            setTitle("New Product");
            action = Intent.ACTION_INSERT;
            addB.setText("ADD");
        }
        else {
            setTitle("Edit Product");
            action = Intent.ACTION_EDIT;
            addB.setText("UPDATE");

            oldName = intent.getStringExtra("Ptitle");
            oldPrice = String.valueOf(intent.getFloatExtra("Pprice", 0f));
            oldQuantity = String.valueOf(intent.getIntExtra("Pquantity", 2));
            productID = String.valueOf(intent.getIntExtra("PID",0));
            input_name.setText(oldName);
            input_quantity.setText(oldQuantity);
            input_price.setText(oldPrice);

            input_name.requestFocus();
        }
    }

    public void AddToDatabase(View v){
        onFinishEditing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_edit, menu);}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                deleteProduct();
                finish();
        }


        return true;
    }

    private void deleteProduct() {
        try {
            new Networking.AsynTaskProductDelete().execute(productID);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Toast.makeText(this,"Product deleted",Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void onFinishEditing(){


        String name = input_name.getText().toString().trim();
        String quantity = input_quantity.getText().toString().trim();
        String price = input_price.getText().toString().trim();


        if (name.length()>0 && quantity.length()>0 && price.length()>0){
            switch (action){
                case Intent.ACTION_INSERT:
                    insertProduct(name,quantity,price,bitmapPhoto);
                    break;
                case Intent.ACTION_EDIT:

                    updateProduct(name, quantity, price, productID, bitmapPhoto);

            }
            finish();
        }
        else {
            Toast toast = Toast.makeText(this, "Please input product name", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 70);
            toast.show();
        }

    }

    private void insertProduct(String productName,String productQuantity,String productPrice, Bitmap bitmap) {
        Product product = new Product(productName,Float.parseFloat(productPrice),Integer.parseInt(productPrice));
        product.setBitmap(bitmap);
        try {
            new Networking.AsynTaskProductAdd().execute(productName,productQuantity,productPrice).get();
            //new Networking.AsynTaskProductAdd(this).execute(product).get();
        }
        catch (Exception e){

        }

        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();


        setResult(RESULT_OK);

    }
    private void updateProduct(String productName, String quantity, String price, String id, Bitmap bitmap) {
        try{

            new Networking.AsynTaskProductUpdate().execute(productName, quantity, price, id);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            // Get the bitmap in according to the width of the device
            bitmapPhoto = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), mSize.y, mSize.x);
            ((ImageView) findViewById(R.id.input_image)).setImageBitmap(bitmapPhoto);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestForCameraPermission(View view) {

        final String permission = Manifest.permission.CAMERA;

        if (ContextCompat.checkSelfPermission(ShowProduct.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShowProduct.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else {
            launch();
        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(ShowProduct.this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowProduct.this.requestForPermission(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(ShowProduct.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

    private void launch() {
        Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    launch();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
