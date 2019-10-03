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
    private CheckBox auto_Login;
    private ImageView see_password;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        initViews();
        setupEvents();
        initData();
    }

    private void initData(){
        if(firstLogin()){
            C_remember.setChecked(false);
            auto_Login.setChecked(false);
        }
        if(rememberPassword()) {
            C_remember.setChecked(true);
            setTextAccountAndPassword();
        }else{
            setTextAccount();
        }
        if(autoLogin()){
            auto_Login.setChecked(true);
            login();
        }
    }
    public void setTextAccountAndPassword(){
        E_account.setText("" + getLocalAccount());
        E_password.setText(""+ getLocalPassword());
    }
    public void setTextAccount(){
        E_account.setText("" + getLocalAccount());
    }
    public String getLocalAccount(){
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        String account = helper.getString("account");
        return account;
    }
    public String getLocalPassword() {
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        String password = helper.getString("password");
        return password;
    }
     public boolean autoLogin(){
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean autoLogin = helper.getBoolean("autoLogin", false);
        return autoLogin;
    }
    private boolean rememberPassword(){
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean rememberPassword = helper.getBoolean("rememberPassword", false);
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
                loadAccount();
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
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean first = helper.getBoolean("first", true);
        if(first){
            helper.putValues(new SharedPreferencesUtils.ContentValue("first", false),
                    new SharedPreferencesUtils.ContentValue("remenberPassword", false),
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
                if (getAccount().equals("admin") && getPassword().equals("admin")) {
                    loadCheckBoxState();
                    startActivity(new Intent(LoginFunction.this, HomeFragment.class));
                   // finish();
                } else {
                    showToast("Invalid account or password");
                }
                setLoginBtnClickable(true);
            }
        };

        loginRunnable.start();
    }
    public void loadAccount(){
        if(!getAccount().equals("") || !getAccount().equals("Account required!")){
            SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
            helper.putValues(new SharedPreferencesUtils.ContentValue("account", getAccount()));
        }
    }
    private void setPasswordVisibility(){
        if(see_password.isSelected()){
            see_password.setSelected(false);
            E_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            see_password.setSelected(true);
            E_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }
    public String getAccount(){
        return E_account.getText().toString().trim();
    }
    public String getPassword(){
        return E_password.getText().toString().trim();
    }
    private void loadCheckBoxState(){
        loadCheckBoxState(C_remember, auto_Login);
    }
    public void loadCheckBoxState(CheckBox C_remember, CheckBox auto_Login){
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this,"setting");
        if(auto_Login.isSelected()){
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("rememberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", true),
                    new SharedPreferencesUtils.ContentValue("password",getPassword()));
        }else if(!C_remember.isSelected()) {
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("rememberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", ""));
        }else if(C_remember.isSelected()){
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("rememberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
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