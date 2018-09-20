package com.cse.cou.mobarak.bookshope;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    Button reg_btn;
    String user_name;
    Boolean iserror=true;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    TextView forgot_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login Form");
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        forgot_pass= (TextView) findViewById(R.id.forgot_pass);
        forgot_pass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(LoginActivity.this,ForgotPasswordEmailSending.class);
                startActivity(intent);
                LoginActivity.this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });


        reg_btn= (Button) findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(LoginActivity.this,Register.class));
                    LoginActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


            }
        });
        progressBar= (ProgressBar) findViewById(R.id.login_progress);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attemptLogin()){
                    progressBar.setVisibility(View.VISIBLE);

                    StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.userLogin, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);

                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                iserror=jsonObject.getBoolean("error");

                                if(!iserror){
                                    finish();
                                    user_name=jsonObject.getString("name");

                                    sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putInt("user_id",jsonObject.getInt("user_id"));
                                    editor.putString("user_name",user_name);
                                    editor.commit();
                                    Intent intent=new Intent(LoginActivity.this,Profile.class);

                                    startActivity(intent);

                                    LoginActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                }
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> param=new HashMap<>();
                            param.put("email",mEmailView.getText().toString());
                            param.put("password",mPasswordView.getText().toString());
                            return param;
                        }
                    };
                    RequestHandler.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
                }


            }
        });

    }

    private boolean attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            return true;
        }
        return false;

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}

