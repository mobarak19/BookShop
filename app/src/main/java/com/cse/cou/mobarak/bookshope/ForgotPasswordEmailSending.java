package com.cse.cou.mobarak.bookshope;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordEmailSending extends AppCompatActivity {

    Button send_code,submit;
    AutoCompleteTextView code,email;
    String verication_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_email_sending);
        send_code= (Button) findViewById(R.id.send_retrive_code_btn);

        setTitle("Forgot Password");
        submit= (Button) findViewById(R.id.submit_retrive_code_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!code.getText().toString().isEmpty()){
                    if(verication_code.equals(code.getText().toString().trim())){
                        Intent intent=new Intent(ForgotPasswordEmailSending.this,ResetPassword.class);
                        intent.putExtra("email",email.getText().toString());
                        startActivity(intent);
                        ForgotPasswordEmailSending.this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                    }else {
                        Toast.makeText(ForgotPasswordEmailSending.this,"You entered wrong code",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(ForgotPasswordEmailSending.this,"You must enter verification code",Toast.LENGTH_LONG).show();

                }

            }
        });
        code= (AutoCompleteTextView) findViewById(R.id.send_code);
        email= (AutoCompleteTextView) findViewById(R.id.retrive_email);
        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!email.getText().toString().isEmpty()){
                    if(isEmailValid(email.getText().toString())){
                        sendEmail();
                        code.setVisibility(View.VISIBLE);
                        send_code.setText("Resend verification code");

                    }else {
                        Toast.makeText(ForgotPasswordEmailSending.this,"Enter a valid email",Toast.LENGTH_LONG).show();

                    }

                }else {
                    Toast.makeText(ForgotPasswordEmailSending.this,"Enter email first",Toast.LENGTH_LONG).show();
                }


            }
        });



    }

    private void sendEmail(){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.forgot_password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    verication_code=jsonObject.getString("code");
                    Toast.makeText(ForgotPasswordEmailSending.this,"Email is sent",Toast.LENGTH_LONG).show();

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
                param.put("email",email.getText().toString());
                return param;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

}
