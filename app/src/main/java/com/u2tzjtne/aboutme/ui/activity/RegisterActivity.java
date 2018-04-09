package com.u2tzjtne.aboutme.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.util.ValidUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {


    private EditText mEmailView;
    private EditText mNicknameView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //显示返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailView = findViewById(R.id.reg_email);
        mNicknameView = findViewById(R.id.reg_nickname);
        mPasswordView = findViewById(R.id.reg_password);
        mRePasswordView = findViewById(R.id.reg_re_password);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);


        //键盘回车键监听
        mRePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        //注册按钮
        Button mRegisterButton = findViewById(R.id.btn_register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 注册操作
                attemptRegister();
            }
        });
    }

    //尝试注册
    private void attemptRegister() {

        // 重置异常提示
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // 获取输入值
        String email = mEmailView.getText().toString();
        String nickname = mNicknameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRePasswordView.getText().toString();

        boolean cancel = false;

        //当前焦点
        View focusView = null;

        // 验证邮件地址
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!ValidUtil.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // 验证昵称
        if (!TextUtils.isEmpty(nickname) && !ValidUtil.isNicknameValid(nickname)) {
            mNicknameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNicknameView;
            cancel = true;
        }

        // 验证密码输入
        if (!TextUtils.isEmpty(password) && !ValidUtil.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // 确认密码
        if (!TextUtils.isEmpty(rePassword) && !rePassword.equals(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mRePasswordView;
            cancel = true;
        }

        if (cancel) {
            //注册异常时  重置焦点（第一个输入框）
            focusView.requestFocus();
        } else {
            //显示进度条  并尝试注册
            showProgress(true);
            //TODO 注册
            register(email, password, nickname);
        }

    }

    /**
     * 显示进度条 并隐藏注册表单
     */
    private void showProgress(final boolean show) {
        //动画
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    //开始注册
    private void register(final String email, final String password, String nickname) {
        MyUser user = new MyUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);

        user.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                showProgress(false);
                if (e == null) {
                    Toast.makeText(RegisterActivity.this, "注册成功，请前往邮箱验证！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败" + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    mPasswordView.requestFocus();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

