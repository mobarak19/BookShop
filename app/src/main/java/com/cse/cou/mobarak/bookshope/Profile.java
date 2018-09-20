package com.cse.cou.mobarak.bookshope;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

public class Profile extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList arrayList;
    Button add_book_btn;

    int user_id;
    SharedPreferences sharedPreferences;
    TextView user_name;
    ProgressBar progressBar;
    GridLayoutManager gridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        recyclerView= (RecyclerView) findViewById(R.id.user_book_list);
        arrayList=new ArrayList<>();
        user_name= (TextView) findViewById(R.id.user_name);
        add_book_btn= (Button) findViewById(R.id.addbook_button);
        add_book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,AddBook.class));
            }
        });

        setTitle("My account");

        sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);

        user_id=sharedPreferences.getInt("user_id",-1);
        user_name.setText(sharedPreferences.getString("user_name",null));

        progressBar= (ProgressBar) findViewById(R.id.profile_progress);
        getAllBookOfUser();


    }

    public void getAllBookOfUser(){
       gridLayoutManager=new GridLayoutManager(this,2);

        recyclerView.setAdapter(null);
        progressBar.setVisibility(View.VISIBLE);




        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.all_user_book, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray jsonArray=object.getJSONArray("server_response");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject obj=jsonArray.getJSONObject(i);
                        BookInfo dept=new BookInfo(obj.getInt("book_id"),
                                obj.getString("book_name"),
                                obj.getString("edition"),
                                obj.getString("writer_name"),
                                obj.getString("dept"),
                                obj.getString("image_link"),

                                obj.getString("price"),
                                obj.getString("phone"),
                                obj.getInt("user_id"));
                        arrayList.add(dept);
                    }
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(new Profile.RecycleviewAdapter(getApplicationContext(),arrayList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(Profile.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> param=new HashMap<>();
                param.put("user_id",user_id+"");
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
        RequestHandler.getInstance(Profile.this).addToRequestQueue(stringRequest);

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Profile.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    private class RecycleviewAdapter extends RecyclerView.Adapter<Profile.RecycleviewAdapter.MyViewHolder> {


        Context context;
        private List<BookInfo> modelClassList;
        private LayoutInflater layoutInflater;
        public RecycleviewAdapter(Context context,List<BookInfo> modelClasses){
            layoutInflater=LayoutInflater.from(context);
            modelClassList=modelClasses;

            this.context=context;
        }

        @Override
        public Profile.RecycleviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=layoutInflater.inflate(R.layout.book_list,parent,false);
            Profile.RecycleviewAdapter.MyViewHolder holder=new Profile.RecycleviewAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Profile.RecycleviewAdapter.MyViewHolder holder, final int position) {
            final BookInfo current=modelClassList.get(position);
            holder.setData(current,position);
            holder.myveiw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(Profile.this,ShowBookDetails.class);
                    intent.putExtra("book_id",current.book_id);
                    intent.putExtra("img_link",current.img_link);

                    startActivity(intent);
                    Profile.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                }
            });
            holder.myveiw.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(Profile.this);
                    alertDialog.setTitle("Confirmation message");
                    alertDialog.setMessage("Are you wanted to delete this infomation?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteBookInfo(current.book_id,current.dept,current.img_link);


                        }
                    });
                    arrayList.clear();
                    getAllBookOfUser();

                    alertDialog.setNegativeButton("NO",null);
                    alertDialog.show();


                    return true;
                }
            });
        }
        @Override
        public int getItemCount() {
            return modelClassList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView text_name,werter;
            BookInfo model;
            ImageView imageView;
            View myveiw;

            public MyViewHolder(View itemView) {
                super(itemView);
                text_name= (TextView) itemView.findViewById(R.id.text_book_name_edition);
                werter= (TextView) itemView.findViewById(R.id.text_writer);
                imageView= (ImageView) itemView.findViewById(R.id.book_img);
                myveiw=itemView;
            }

            public void setData(BookInfo current, int position) {
                this.text_name.setText(current.getBook_name()+"(edition:"+current.getEdition()+")");
                this.werter.setText(current.getWriter());

                String s=current.getImg_link();

                ImageRequest imageRequest=new ImageRequest(s, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                RequestHandler.getInstance(Profile.this).addToRequestQueue(imageRequest);
                this.model=current;
            }
        }
    }

    private void deleteBookInfo(final int book_id,final String dept,final String img_link) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.delete_book_info, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("server_response");

                    JSONObject object=jsonArray.getJSONObject(0);
                    if(!object.getBoolean("error")){

                        Toast.makeText(Profile.this,object.getString("message"),Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Profile.this,object.getString("message"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> param=new HashMap<>();
                param.put("book_id",book_id+"");
                param.put("dept",dept);
                param.put("img_link",img_link);
                param.put("user_id",user_id+"");

                return param;
            }
        };

        RequestHandler.getInstance(Profile.this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu01,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_log_out){
            finish();
            sharedPreferences.edit().clear();
            startActivity(new Intent(Profile.this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
