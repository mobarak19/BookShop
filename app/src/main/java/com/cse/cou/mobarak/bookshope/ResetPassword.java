package com.cse.cou.mobarak.bookshope;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    Button reset_btn;
    EditText password,confirm_password;
    ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("Reset Password");
        password= (EditText) findViewById(R.id.reset_pass);

        progressbar= (ProgressBar) findViewById(R.id.resetprogress);

        confirm_password= (EditText) findViewById(R.id.confirm_reset_pass);
        reset_btn= (Button) findViewById(R.id.reset_passord_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(confirm_password.getText().toString())){

                    if(isPasswordValid(password.getText().toString())){
                        updatePassword();

                    }else {
                        Toast.makeText(ResetPassword.this,"Password must be more than 5 characters",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }

    private void updatePassword() {
        final String email=getIntent().getStringExtra("email");

        progressbar.setVisibility(View.VISIBLE);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.registerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressbar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(!jsonObject.getBoolean("error")){
                        startActivity(new Intent(ResetPassword.this,LoginActivity.class));
                    }
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressbar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("email",email);
                param.put("password",password.getText().toString());
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
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
