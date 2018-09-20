package com.cse.cou.mobarak.bookshope;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView department_name;
     ArrayList arrayList;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        department_name= (RecyclerView) findViewById(R.id.depatrmet_name_list_view);
      final   GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);

        progressBar= (ProgressBar) findViewById(R.id.progress);

         arrayList=new ArrayList();
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest=new StringRequest(Request.Method.GET, Constant.getDept, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {



                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    JSONArray jsonArray=jsonObject.getJSONArray("server_response");

                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object=jsonArray.getJSONObject(i);
                        ShowDept dept=new ShowDept(object.getInt("id"),object.getInt("book_amount"),object.getString("dept"));
                        arrayList.add(dept);
                    }

                    department_name.setLayoutManager(gridLayoutManager);
                    department_name.setAdapter(new RecycleviewAdapter(MainActivity.this,arrayList));

                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                TextView t= (TextView) findViewById(R.id.netError);
                t.setVisibility(View.VISIBLE);


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

    }

    private class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.MyViewHolder> {


        Context context;
        private List<ShowDept> modelClassList;
        private LayoutInflater layoutInflater;
        public RecycleviewAdapter(Context context,List<ShowDept> modelClasses){
            layoutInflater=LayoutInflater.from(context);
            modelClassList=modelClasses;

            this.context=context;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=layoutInflater.inflate(R.layout.dept_list,parent,false);
            MyViewHolder holder=new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecycleviewAdapter.MyViewHolder holder, final int position) {
            final ShowDept current=modelClassList.get(position);
            holder.setData(current,position);
            holder.myveiw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(view.getContext(),BookList.class);
                    intent.putExtra("id",current.getId());
                    intent.putExtra("dept_name",current.dept_name);

                    context.startActivity(intent);
                    MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
            });
        }


        @Override
        public int getItemCount() {
            return modelClassList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView text_name,text_amount;
            ShowDept model;
            View myveiw;

            public MyViewHolder(View itemView) {
                super(itemView);
                text_name= (TextView) itemView.findViewById(R.id.depatrmet_name);
                text_amount= (TextView) itemView.findViewById(R.id.amount);
                myveiw=itemView;
            }

            public void setData(ShowDept current, int position) {
                this.text_name.setText(current.getDept_name()+"");
                this.text_amount.setText(current.getAmount()+"");
                this.model=current;

            }
        }
    }

}
