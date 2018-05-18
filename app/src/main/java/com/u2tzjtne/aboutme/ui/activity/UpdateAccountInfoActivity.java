package com.u2tzjtne.aboutme.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.constant.Constant;
import com.u2tzjtne.aboutme.model.UserModel;
import com.u2tzjtne.aboutme.ui.view.LoadDialog;
import com.u2tzjtne.aboutme.util.ToastUtil;
import com.u2tzjtne.aboutme.util.ValidUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateAccountInfoActivity extends AppCompatActivity {

    @BindView(R.id.edit_update_account_info)
    EditText editText;
    //更新类型
    private int update_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account_info);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUpdateType();
        initUI();
    }

    /*
     * 初始化更新类型
     * */
    private void initUpdateType() {
        Intent intent = getIntent();
        if (intent != null) {
            update_type = intent.getIntExtra(Constant.UPDATE_ACCOUNT_INFO_TYPE, Constant.REQUEST_CODE_NICKNAME);
        } else {
            update_type = Constant.REQUEST_CODE_NICKNAME;
        }
    }

    /*
     * 初始化界面显示
     * */
    private void initUI() {
        MyUser user = UserModel.getInstance().getCurrentUser();
        switch (update_type) {
            case Constant.REQUEST_CODE_NICKNAME:
                getSupportActionBar().setTitle("更改昵称");
                editText.setHint("输入新的昵称");
                if (!TextUtils.isEmpty(user.getNickname())) {
                    editText.setText(user.getNickname());
                    editText.setSelection(user.getNickname().length());
                }
                break;
            case Constant.REQUEST_CODE_EMAIL:
                getSupportActionBar().setTitle("更改邮箱");
                editText.setHint("输入新的邮箱");
                if (!TextUtils.isEmpty(user.getNickname())) {
                    editText.setText(user.getEmail());
                    editText.setSelection(user.getEmail().length());
                }
                break;
            case Constant.REQUEST_CODE_PASSWORD:
                getSupportActionBar().setTitle("更改密码");
                editText.setHint("输入新密码");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_update_account_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_update_account_info:
                updateInfoAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //更新信息
    private void updateInfoAction() {
        LoadDialog.show(this);
        String text = editText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            //更新信息
            switch (update_type) {
                case Constant.REQUEST_CODE_EMAIL://邮件
                    //1.验证是不是正确的邮件地址
                    //2.更新
                    if (ValidUtil.isEmailValid(text)) {
                        updateInfo(text, UpdateType.EMAIL);
                    } else {
                        LoadDialog.dismiss(this);
                        ToastUtil.s("请输入正确的邮件格式");
                    }
                    break;
                case Constant.REQUEST_CODE_NICKNAME://用户名
                    if (ValidUtil.isNicknameValid(text)) {
                        updateInfo(text, UpdateType.NICKNAME);
                    } else {
                        LoadDialog.dismiss(this);
                        ToastUtil.s("请输入正确的昵称格式");
                    }
                    break;
                case Constant.REQUEST_CODE_PASSWORD://密码
                    if (ValidUtil.isPasswordValid(text)) {
                        updateInfo(text, UpdateType.PASSWORD);
                    } else {
                        LoadDialog.dismiss(this);
                        ToastUtil.s("请输入正确的密码格式");
                    }
                    break;
            }
        } else {
            LoadDialog.dismiss(this);
            ToastUtil.s("请输入内容");
        }

    }

    private void updateInfo(String info, final UpdateType type) {
        MyUser newUser = new MyUser();
        switch (type) {
            case EMAIL:
                newUser.setEmail(info);
                break;
            case NICKNAME:
                newUser.setNickname(info);
                break;
            case PASSWORD:
                newUser.setPassword(info);
                break;
        }
        BmobUser bmobUser = UserModel.getInstance().getCurrentUser();
        newUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                LoadDialog.dismiss(UpdateAccountInfoActivity.this);
                if (e == null) {
                    if (type == UpdateType.EMAIL) {
                        ToastUtil.s("更新成功，请验证邮件后登录");
                        startActivity(new Intent(
                                UpdateAccountInfoActivity.this, LoginActivity.class));
                    } else {
                        ToastUtil.s("更新成功");
                    }
                    finish();
                } else {
                    ToastUtil.s("更新失败，请重试！");
                }
            }
        });

    }

    //更新类型
    enum UpdateType {
        EMAIL, PASSWORD, NICKNAME
    }

}
