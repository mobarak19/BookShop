package com.cse.cou.mobarak.bookshope;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
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

public class BookList extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    ArrayList arrayList;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    int  dept_id;
    String dept_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        arrayList=new ArrayList<>();

        dept_id=getIntent().getIntExtra("id",-1);
        dept_name=getIntent().getStringExtra("dept_name");

        sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);

        sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("dept_id",dept_id);
        editor.putString("dept_name",dept_name);
        editor.commit();

        setTitle("Books of "+dept_name);


        recyclerView= (RecyclerView) findViewById(R.id.dept_books);
        final GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);

        Toast.makeText(getApplicationContext(),String.valueOf(getIntent().getIntExtra("id",0)), Toast.LENGTH_LONG).show();

        progressBar= (ProgressBar) findViewById(R.id.book_list_progress);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.getdeptBookList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("TAG",response);
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
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(new RecycleviewAdapter(getApplicationContext(),arrayList));




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(BookList.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> param=new HashMap<>();
                param.put("dept",dept_name+"");
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
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestHandler.getInstance(BookList.this).addToRequestQueue(stringRequest);


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        BookList.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


    private class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.MyViewHolder> {


        Context context;
        private List<BookInfo> modelClassList;
        private LayoutInflater layoutInflater;
        public RecycleviewAdapter(Context context,List<BookInfo> modelClasses){
            layoutInflater=LayoutInflater.from(context);
            modelClassList=modelClasses;

            this.context=context;
        }

        @Override
        public RecycleviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=layoutInflater.inflate(R.layout.book_list,parent,false);
            RecycleviewAdapter.MyViewHolder holder=new RecycleviewAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecycleviewAdapter.MyViewHolder holder, final int position) {
            final BookInfo current=modelClassList.get(position);
            holder.setData(current,position);
            holder.myveiw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(BookList.this,ShowBookDetails.class);
                    intent.putExtra("book_id",current.book_id);
                    intent.putExtra("img_link",current.getImg_link());
                    intent.putExtra("dept name",dept_name);

                    startActivity(intent);
                    BookList.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

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
                this.text_name.setText("Book:"+current.getBook_name()+"(edition:"+current.getEdition()+")");
                this.werter.setText("Writer:"+current.getWriter());

                String s=current.getImg_link();

                ImageRequest imageRequest=new ImageRequest(s, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        imageView.setImageBitmap(response);

                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BookList.this,error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
                RequestHandler.getInstance(BookList.this).addToRequestQueue(imageRequest);
                this.model=current;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);

            BookList.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


        }
        if(item.getItemId()==R.id.menu_add_book){
            finish();
            startActivity(new Intent(BookList.this,LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
