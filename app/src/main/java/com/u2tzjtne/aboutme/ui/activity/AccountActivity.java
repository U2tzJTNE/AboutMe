package com.u2tzjtne.aboutme.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.model.UserModel;
import com.u2tzjtne.aboutme.ui.view.LoadDialog;
import com.u2tzjtne.aboutme.util.PhotoUtils;
import com.u2tzjtne.aboutme.util.ValidUtil;


import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView avatar;
    private Button edit;
    private EditText editNickname;
    private EditText editEmail;
    private RelativeLayout changePass;
    private Button logout;
    private PhotoUtils photoUtils;
    private Uri selectUri;
    private Context mContext;


    private boolean isEdit = false;
    public static final int REQUEST_PERMISSIONS_CAMERA = 101;
    public static final int REQUEST_PERMISSIONS_GALLERY = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        initView();
        setPortraitChangeListener();
    }

    private void initView() {
        avatar = findViewById(R.id.avatar);
        edit = findViewById(R.id.btn_edit);
        editNickname = findViewById(R.id.edit_nickname);
        editEmail = findViewById(R.id.edit_email);
        changePass = findViewById(R.id.change_password);
        logout = findViewById(R.id.btn_logout);

        avatar.setOnClickListener(this);
        edit.setOnClickListener(this);
        changePass.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(AccountActivity.this, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUser user = UserModel.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String nickname = user.getNickname();
            String avatarURL = user.getAvatarURL();
            if (email != null) {
                editEmail.setText(email);
            }
            if (nickname != null) {
                editNickname.setText(nickname);
            }
            if (avatarURL != null) {
//                ImageOptions.Builder builder = new ImageOptions.Builder();
//                builder.setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP);
//                ImageOptions options = builder.build();
//                x.image().bind(avatar, avatarURL, options);
                Glide.with(AccountActivity.this).load(avatarURL).into(avatar);
            } else {
                //TODO 默认头像
                avatar.setImageResource(R.drawable.splash);
            }
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar: //修改头像
                showDialog();
                break;
            case R.id.btn_edit://修改资料
                if (isEdit) {
                    //判断是否修改
                    //获取输入框的值
                    String email = editEmail.getText().toString().trim();
                    String nickname = editNickname.getText().toString().trim();

                    MyUser user = UserModel.getInstance().getCurrentUser();
                    String oldNickname = user.getNickname();
                    String oldEmail = user.getEmail();
                    boolean isSubmit = false;
                    //待提交的信息
                    String[] infos = new String[]{null, null, null};
                    //邮件相等
                    if (!email.equals(oldEmail) && ValidUtil.isEmailValid(email)) {
                        infos[1] = email;
                        isSubmit = true;
                    }
                    //昵称相等
                    if (!nickname.equals(oldNickname) && ValidUtil.isNicknameValid(nickname)) {
                        infos[0] = nickname;
                        isSubmit = true;
                    }

                    //当前状态为可提交
                    if (isSubmit) {
                        boolean succeed = updateAccountInfo(infos);
                        if (!succeed) {
                            editEmail.setText(oldEmail);
                            editNickname.setText(oldNickname);
                        }
                    }
                }
                isEdit = !isEdit;
                editEmail.setEnabled(isEdit);
                editNickname.setEnabled(isEdit);
                edit.setText(isEdit ? "保存修改" : "编辑资料");
                break;
            case R.id.change_password://修改密码
                startActivity(new Intent(AccountActivity.this, UpdatePasswordActivity.class));
                break;
            case R.id.btn_logout://退出登录
                UserModel.getInstance().logout();
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
                finish();
                break;
        }
    }


    private void showDialog() {
        String[] items = new String[]{"相册", "拍照"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0://相册
                        int storage1 = ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (storage1 != PackageManager.PERMISSION_GRANTED) {
                            //获取权限
                            if (Build.VERSION.SDK_INT >= 23) {
                                getPermission(REQUEST_PERMISSIONS_GALLERY);
                            } else {
                                Toast.makeText(mContext, "请去设置里面打开存储权限", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            photoUtils.selectPicture(AccountActivity.this);
                        }
                        break;
                    case 1://拍照
                        int camera = ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.CAMERA);
                        int storage = ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (camera != PackageManager.PERMISSION_GRANTED || storage != PackageManager.PERMISSION_GRANTED) {
                            //获取权限
                            if (Build.VERSION.SDK_INT >= 23) {
                                getPermission(REQUEST_PERMISSIONS_CAMERA);
                            } else {
                                Toast.makeText(mContext, "请去设置里面打开相机和存储权限", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            photoUtils.takePicture(AccountActivity.this);
                        }
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermission(final int requestCode) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CAMERA:
                //之前拒绝过
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //向用户解释
                    new AlertDialog.Builder(mContext)
                            .setMessage("您需要在设置里打开相机和存储权限")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;
            case REQUEST_PERMISSIONS_GALLERY://只获取存储权限
                //之前拒绝过
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //向用户解释
                    new AlertDialog.Builder(mContext)
                            .setMessage("您需要在设置里打开外置存储权限")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();

                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //允许  开始调用相机
                    photoUtils.takePicture(AccountActivity.this);
                } else {
                    // 拒绝
                    Toast.makeText(AccountActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSIONS_GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //允许  开始调用相册
                    photoUtils.selectPicture(AccountActivity.this);
                } else {
                    // 拒绝
                    Toast.makeText(AccountActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //裁剪后返回
    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                System.out.println("-----------onPhotoResult--------------" + uri);
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    //显示加载
                    LoadDialog.show(mContext);
                    System.out.println("-----------开始创建BmobFile--------------");
                    final BmobFile bmobFile = new BmobFile(new File(uri.getPath()));
                    System.out.println("-----------创建完成--------------");

                    //TODO 上传出错
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            //上传失败
                            LoadDialog.dismiss(mContext);
                            if (e == null) {
                                System.out.println("-----------上传成功--------------");
                                //上传成功后更新头像链接
                                updateAccountInfo(new String[]{null, null, bmobFile.getFileUrl()});
                            } else {
                                Toast.makeText(mContext, "上传失败:" + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    //更新资料
    private boolean updateAccountInfo(String[] items) {
        System.out.println("---------------------------进入更新信息---------------");
        final boolean[] succeed = {false};
        String nickname = items[0];
        final String email = items[1];
        String avatarURL = items[2];
        LoadDialog.show(AccountActivity.this);
        BmobUser user = UserModel.getInstance().getCurrentUser();
        MyUser myUser = new MyUser();
        if (nickname != null) {
            myUser.setNickname(nickname);
        }
        if (email != null) {
            myUser.setEmail(email);
        }
        if (avatarURL != null) {
            myUser.setAvatarURL(avatarURL);
        }
        myUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                System.out.println("----------------------更新完成-----------------");
                LoadDialog.dismiss(AccountActivity.this);
                if (e == null) {
                    if (email != null) {
                        UserModel.getInstance().logout();
                        Toast.makeText(AccountActivity.this, "更新成功,请验证后登录", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AccountActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }
                    succeed[0] = true;
                } else {
                    succeed[0] = false;
                    Toast.makeText(AccountActivity.this, "更新失败,代码：" + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return succeed[0];
    }
}
