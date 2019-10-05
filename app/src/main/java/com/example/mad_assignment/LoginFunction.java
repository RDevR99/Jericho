package com.example.mad_assignment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import com.example.mad_assignment.SharedPreferencesUtils;


public class LoginFunction extends Activity
{
    private EditText E_account;
    private EditText E_password;
    private Button Login_Btn;
    private CheckBox C_remember;
    private CheckBox auto_Login; // Keep me logged in
    private ImageView see_password;
    private SharedPreferencesUtils sharedPreference;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        sharedPreference = new SharedPreferencesUtils(this,"setting");
        initViews(); // Initialize the view components.
        setupEvents(); // Set up the on click listeners.
        initData();
    }

    private void initData(){

        if(firstLogin()){
            // These are false by default...
            C_remember.setChecked(false);
            auto_Login.setChecked(false);
        }

        if(rememberPassword())
        {
            C_remember.setChecked(true);
            setTextAccountAndPassword();
        }

        if(autoLogin()){
            auto_Login.setChecked(true);
            login();
        }
    }

    public void setTextAccountAndPassword(){

        // Are we supposed to store account as shared preference values?
        //sharedPreference.setString("account", E_account.getText());
        //sharedPreference.setString("password", E_password.getText());
        E_account.setText("" + getLocalAccount());
        E_password.setText(""+ getLocalPassword());
    }

    /*public void setTextAccount(){
        E_account.setText("" + getLocalAccount());
    }*/

    public String getLocalAccount(){
        String account = sharedPreference.getString("account");
        return account;
    }

    public String getLocalPassword() {
        String password = sharedPreference.getString("password");
        return password;
    }

     public boolean autoLogin(){
        boolean autoLogin = sharedPreference.getBoolean("autoLogin", false);
        return autoLogin;
    }

    private boolean rememberPassword(){
        boolean rememberPassword = sharedPreference.getBoolean("rememberPassword", false);
        return rememberPassword;
    }

    private void initViews(){
        Login_Btn = (Button)findViewById(R.id.loginBtn);
        E_account = (EditText)findViewById(R.id.account);
        E_password = (EditText)findViewById(R.id.password);
        C_remember = (CheckBox)findViewById(R.id.remember_password);
        auto_Login = (CheckBox)findViewById(R.id.autologin);
        see_password = (ImageView)findViewById(R.id.seepassword);
    }

    private void setupEvents(){
        Login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Loads the shared preference key value pair with the account information
                loadAccount();
                // Authenticates user and changes view to the home fragment.
                login();
            }
        });

       // C_remember.setOnCheckedChangeListener(this);
       // auto_Login.setOnCheckedChangeListener(this);

        see_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasswordVisibility();
            }
        });
    }

    private boolean firstLogin(){
        boolean first = sharedPreference.getBoolean("first", true);
        if(first){
            sharedPreference.putValues(new SharedPreferencesUtils.ContentValue("first", false),
                    new SharedPreferencesUtils.ContentValue("rememberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("name", ""),
                    new SharedPreferencesUtils.ContentValue("password", ""));
                    return true;
        }
        return false;
    }

    private void login(){
        if(getAccount().isEmpty()){
            showToast("Account required!");
            return;
        }
        if(getPassword().isEmpty()){
            showToast("Password required!");
            return;
        }
        Thread loginRunnable = new Thread() {
            @Override
            public void run() {
                super.run();
                setLoginBtnClickable(false);

                // This condition will change to pass HTTP requests
                // if(request.getString("response").equalsIgnoreCase("Success"))
                if (getAccount().equals("admin") && getPassword().equals("admin"))
                {
                    loadCheckBoxState();
                    startActivity(new Intent(LoginFunction.this, HomeFragment.class));
                   // finish();
                }
                else {
                    showToast("Invalid account or password");
                }

                // Not sure if the below line is required.
                //setLoginBtnClickable(true);
            }
        };

        loginRunnable.start();
    }
    public void loadAccount()
    {
        // getAccount gives you the username.
        if(!getAccount().equals("") || !getAccount().equals("Account required!"))
        {
            // Shared preferences are used to store the key value pairs at an application level.
            sharedPreference.putValues(new SharedPreferencesUtils.ContentValue("account", getAccount()));
        }
    }
    private void setPasswordVisibility(){
        if(see_password.isSelected()){
            //see_password.setSelected(false);
            E_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
           // see_password.setSelected(true);
            E_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }
    public String getAccount()
    {
        return E_account.getText().toString().trim();
    }

    public String getPassword(){
        return E_password.getText().toString().trim();
    }
    private void loadCheckBoxState(){
        loadCheckBoxState(C_remember, auto_Login);
    }
    public void loadCheckBoxState(CheckBox C_remember, CheckBox auto_Login){

        if(auto_Login.isSelected()){
            sharedPreference.putValues(
                    new SharedPreferencesUtils.ContentValue("rememberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", true),
                    new SharedPreferencesUtils.ContentValue("account",getAccount()),
                    new SharedPreferencesUtils.ContentValue("password",getPassword()));
        }
        else if(!C_remember.isSelected()) {
            sharedPreference.putValues(
                    new SharedPreferencesUtils.ContentValue("rememberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", ""));
        }
        else if(C_remember.isSelected()){
            sharedPreference.putValues(
                    new SharedPreferencesUtils.ContentValue("rememberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("account",getAccount()),
                    new SharedPreferencesUtils.ContentValue("password", getPassword()));
        }
    }
    public void setLoginBtnClickable(boolean clickable){
        Login_Btn.setClickable(clickable);
    }

    public void onCheckChanged(CompoundButton buttonView, boolean isChecked){
        if(buttonView ==C_remember){
            if(!isChecked){
                auto_Login.setChecked(false);
            }
        }else if(buttonView == auto_Login){
            if(isChecked){
                C_remember.setChecked(true);
            }
        }
    }
    public void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginFunction.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}