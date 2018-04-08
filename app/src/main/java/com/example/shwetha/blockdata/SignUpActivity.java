package com.example.shwetha.blockdata;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SignUpActivity extends AppCompatActivity {
    private Button submit;
    private EditText user,pass,email,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        submit= (Button) findViewById(R.id.submit);
        user= (EditText) findViewById(R.id.suser);
        pass= (EditText) findViewById(R.id.spassword);
       email= (EditText) findViewById(R.id.semail);
       contact= (EditText) findViewById(R.id.phone);

       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
               String url = "http://10.0.0.3:8081/Blockchain/SignUp?user=" + user.getText() + "&pass=" + pass.getText() + "&email=" + email.getText() + "&contact=" + contact.getText();

                // Request a string response from the provided URL.
               StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                       new Response.Listener<String>() {
                           @Override
                           public void onResponse(String response) {
                               // Display the first 500 characters of the response string.
                               UserKey.Appid=response;
                               Toast.makeText(getApplicationContext(),"Successfull:"+UserKey.Appid,Toast.LENGTH_LONG).show();

                           }
                       }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                   }
               });
                // Add the request to the RequestQueue.
               queue.add(stringRequest);
           }
       });




    }

}
