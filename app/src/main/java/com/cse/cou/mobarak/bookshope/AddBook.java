package com.cse.cou.mobarak.bookshope;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBook extends AppCompatActivity {

    EditText book_name,writer,edition,phone_number,price;
    ImageView imageView;
    Button add_book_btn;
    Bitmap bitmap;
    Spinner department;
    SharedPreferences sharedPreferences;
    boolean result ;
    List<String> dept_list;
    int user_id,department_id;
    String department_str;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);
        department_id=sharedPreferences.getInt("dept_id",-1);
        user_id=sharedPreferences.getInt("user_id",-1);

        book_name= (EditText) findViewById(R.id.add_bookname);
        writer= (EditText) findViewById(R.id.add_writer);
        edition= (EditText) findViewById(R.id.add_book_edition);
        department= (Spinner) findViewById(R.id.add_dept);

        phone_number= (EditText) findViewById(R.id.add_phone_number);
        price= (EditText) findViewById(R.id.add_book_price);
        imageView= (ImageView) findViewById(R.id.add_book_img);

        progressBar= (ProgressBar) findViewById(R.id.add_book_progress);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }
        });


        add_book_btn= (Button) findViewById(R.id.add_book_btn);
        add_book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                addBook();



            }
        });






//dept spinner
        dept_list=new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constant.getDept, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {



                try {
                    JSONObject jsonObject=new JSONObject(response);

                    JSONArray jsonArray=jsonObject.getJSONArray("server_response");

                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object=jsonArray.getJSONObject(i);
                        String dept=object.getString("dept");
                        dept_list.add(dept);
                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(AddBook.this,android.R.layout.simple_list_item_1,dept_list);

                    progressBar.setVisibility(View.INVISIBLE);
                    department.setAdapter(adapter);

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddBook.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


        //end of dept spinner


        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department_str=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    private void addBook(){


        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.addBook, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.INVISIBLE);
                Log.v("mmmm",response+"");

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.v("mmmm",jsonObject.getBoolean("error")+"");
                    if(!jsonObject.getBoolean("error")){

                        startActivity(new Intent(AddBook.this,Profile.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> param=new HashMap<>();
                param.put("book_name",book_name.getText().toString());
                param.put("writer",writer.getText().toString());
                param.put("edition",edition.getText().toString());
                param.put("department",department_str);
                param.put("user_id",user_id+"");
                param.put("price",price.getText().toString());
                param.put("phone_number",phone_number.getText().toString());
                param.put("image",imageToString(bitmap));
                return param;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestHandler.getInstance(AddBook.this).addToRequestQueue(stringRequest);

    }



    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imgbyte=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgbyte,Base64.DEFAULT);
    }
    private void selectImage(){

     Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK&& data!=null){
            Uri path=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);

///TODO alertdailoge to upload image less then 100kb
long img_size=(bitmap.getByteCount()/(8*1024));
                if(img_size<204800){
                    imageView.setImageBitmap(bitmap);
                }else {

                    AlertDialog.Builder builder=new AlertDialog.Builder(AddBook.this)
                            .setTitle("Image size alart")
                            .setMessage("Image size must be less then 100kb! Your image size is "+img_size )
                            .setPositiveButton("Ok",null);
                    builder.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
