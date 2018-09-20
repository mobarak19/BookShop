package com.cse.cou.mobarak.bookshope;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class ShowBookDetails extends AppCompatActivity {

    Toolbar toolbar;

    TextView book_name,book_writer,book_edition,book_price,book_contact_number,department;
    ImageView imageView;
    Bitmap bitmap;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book_details);
     setTitle("Book Details");
        sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);



        imageView= (ImageView) findViewById(R.id.book_image);
        book_name= (TextView) findViewById(R.id.book_name);
        book_writer= (TextView) findViewById(R.id.book_writer);
        book_edition= (TextView) findViewById(R.id.book_edition);
        book_price= (TextView) findViewById(R.id.book_price );
        book_contact_number= (TextView) findViewById(R.id.contact_number);
        department= (TextView) findViewById(R.id.deptment);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(ShowBookDetails.this);
                builder.setTitle("  ");
                LayoutInflater layoutInflater=ShowBookDetails.this.getLayoutInflater();

                View view1= layoutInflater.inflate(R.layout.show_image,null);
                ImageView im= (ImageView) view1.findViewById(R.id.show_imageview);
                im.setImageBitmap(bitmap);
                builder.setNegativeButton("Close",null);

                builder.create();
                builder.setView(view1);
                builder.show();
            }
        });


        final int i=getIntent().getIntExtra("book_id",-1);


        ImageRequest imageRequest=new ImageRequest(getIntent().getStringExtra("img_link"), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                bitmap=response;
                imageView.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestHandler.getInstance(ShowBookDetails.this).addToRequestQueue(imageRequest);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.getBookDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject=new JSONObject(response);

                    JSONArray jsonArray=jsonObject.getJSONArray("server_response");

                    JSONObject object=jsonArray.getJSONObject(0);
                    book_name.setText("Book name: "+object.getString("book_name"));
                    book_writer.setText("Writer: "+object.getString("writer_name"));
                    book_edition.setText("Edition: "+object.getString("edition"));
                    book_price.setText("Price: "+object.getString("price"));
                    book_contact_number.setText("Contact number: "+object.getString("phone"));
                    department.setText("Department: "+object.getString("dept"));







                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //book details

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("book_id",i+"");
                return param;
            }
        };

        RequestHandler.getInstance(ShowBookDetails.this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowBookDetails.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            ShowBookDetails.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        }
        return super.onOptionsItemSelected(item);

    }
}
