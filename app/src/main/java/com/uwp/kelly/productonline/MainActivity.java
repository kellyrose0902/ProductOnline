package com.uwp.kelly.productonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    private ArrayList<Product> productList= new ArrayList<>();
    private ListView list;
    private Spinner spinner;
    private EditText searchBar;
    private ArrayAdapter<String> spinnerAdapter;
    ProductListArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (ListView)findViewById(android.R.id.list);

        try{
            productList = new Networking.AsyncTaskProductList().execute().get();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(productList!=null){
            adapter = new ProductListArrayAdapter(this,productList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), ShowProduct.class);
                    intent.putExtra("isNew",false);
                    Product product = productList.get(position);

                    intent.putExtra("PID",product.id);
                    intent.putExtra("Ptitle",product.title);
                    intent.putExtra("Pprice",product.price);
                    intent.putExtra("Pquantity",product.quantity);

                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }

        searchBar = (EditText) findViewById(R.id.SearchBar);
        /*searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        // Handling spinner
        spinner = (Spinner)findViewById(R.id.spinner);
        List<String> sort_list = new ArrayList<>();
        sort_list.add("Sort by");
        sort_list.add("Price");
        sort_list.add("Quantity");
        sort_list.add("Recently Added");
        spinnerAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sort_list){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {
                    // Pass convertView as null to prevent reuse of special case views
                    v = super.getDropDownView(position, null, parent);
                }

                // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        sortPrice();
                        //.makeText(getBaseContext(),"price",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        sortQuantity();

                        // Toast.makeText(getBaseContext(),"quantity",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        sortLatest();
                        // Toast.makeText(getBaseContext(),"latest",Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }



    public void sortPrice(){
        Collections.sort(adapter.productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                if(lhs.price > rhs.price) return 1;
                else if (lhs.price < rhs.price) return -1;
                else return 0;
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void sortQuantity(){
        Collections.sort(adapter.productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                if(lhs.quantity > rhs.quantity) return 1;
                else if (lhs.quantity < rhs.quantity) return -1;
                else return 0;
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void sortLatest(){
        Collections.sort(adapter.productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                if(lhs.dateTime.compareTo(rhs.dateTime)== -1){
                    return 1;
                }
                else if(lhs.dateTime.compareTo(rhs.dateTime)== 1){
                    return -1;
                } else return 0;
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test) {
            new Networking.AsyncTaskProductList().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddNewProduct(View view) {
        Intent intent = new Intent(this,ShowProduct.class);
        intent.putExtra("isNew",true);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            try {

                productList = new Networking.AsyncTaskProductList().execute().get();
                adapter.refreshList(productList);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }



}
