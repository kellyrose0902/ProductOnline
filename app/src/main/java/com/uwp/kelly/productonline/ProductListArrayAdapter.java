package com.uwp.kelly.productonline;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelly on 11/14/2015.
 */
public class ProductListArrayAdapter extends ArrayAdapter<Product> {

    private final Activity context;
    public List<Product> productList;


    public ProductListArrayAdapter(Activity context,List<Product> productList) {
        super(context, R.layout.product_list_item, productList);
        this.context = context;
        this.productList = productList;
    }

    static class ViewHolder{
        protected TextView id;
        protected TextView title;
        protected TextView price;
        protected TextView quantity;
        protected ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View view = null;

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.product_list_item,null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.id = (TextView)view.findViewById(R.id.id);
            viewHolder.title = (TextView)view.findViewById(R.id.product);
            viewHolder.price = (TextView)view.findViewById(R.id.price);
            viewHolder.quantity = (TextView)view.findViewById(R.id.quantity);
            viewHolder.icon = (ImageView)view.findViewById(R.id.icon1);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
        }


        ViewHolder holder = (ViewHolder)view.getTag();
        holder.id.setText(String.valueOf(productList.get(position).id));
        holder.title.setText(productList.get(position).title);
        holder.price.setText("$ "+String.valueOf(productList.get(position).price));
        holder.quantity.setText("Quantity: " +String.valueOf(productList.get(position).quantity));
        holder.icon.setImageResource(R.drawable.ic_package);
        return  view;

    }


    public void refreshList(ArrayList<Product> newList){
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub

                return null;
            }

            /* (non-Javadoc)
             * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
             */
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                // TODO Auto-generated method stub

            }

        };
    }

}
