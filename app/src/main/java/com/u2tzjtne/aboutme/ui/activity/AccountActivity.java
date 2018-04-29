package com.u2tzjtne.aboutme.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.model.UserModel;
import com.u2tzjtne.aboutme.ui.view.LoadDialog;
import com.u2tzjtne.aboutme.util.Const;
import com.u2tzjtne.aboutme.util.FileUtil;
import com.u2tzjtne.aboutme.util.ImageUtil;
import com.u2tzjtne.aboutme.util.LogUtil;


import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.utils.FileUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AccountActivity extends AppCompatActivity {


    @BindView(R.id.account_activity_avatar)
    ImageView userAvatar;
    @BindView(R.id.item_avatar)
    LinearLayout itemAvatar;
    @BindView(R.id.account_activity_nickname)
    TextView userNickname;
    @BindView(R.id.item_nickname)
    LinearLayout itemNickname;
    @BindView(R.id.account_activity_email)
    TextView userEmail;
    @BindView(R.id.item_email)
    LinearLayout itemEmail;
    @BindView(R.id.item_update_pass)
    LinearLayout itemUpdatePass;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.item_avatar, R.id.item_nickname, R.id.item_email, R.id.item_update_pass, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.item_avatar://头像
                AccountActivityPermissionsDispatcher.showImageSelectorWithPermissionCheck(AccountActivity.this);
                break;
            case R.id.item_nickname://昵称
                Intent nicknameIntent = new Intent(AccountActivity.this, UpdateAccountInfoActivity.class);
                nicknameIntent.putExtra(Const.UPDATE_ACCOUNT_INFO_TYPE, Const.REQUEST_CODE_NICKNAME);
                startActivity(nicknameIntent);
                break;
            case R.id.item_email://邮箱
                Intent emailIntent = new Intent(AccountActivity.this, UpdateAccountInfoActivity.class);
                emailIntent.putExtra(Const.UPDATE_ACCOUNT_INFO_TYPE, Const.REQUEST_CODE_EMAIL);
                startActivity(emailIntent);
                break;
            case R.id.item_update_pass://更改密码
                Intent passwordIntent = new Intent(AccountActivity.this, UpdateAccountInfoActivity.class);
                passwordIntent.putExtra(Const.UPDATE_ACCOUNT_INFO_TYPE, Const.REQUEST_CODE_PASSWORD);
                startActivity(passwordIntent);
                break;
            case R.id.btn_logout://退出登录
                UserModel.getInstance().logout();
                finish();
                break;
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


    /**
     * 选择图片
     */
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showImageSelector() {
        MultiImageSelector.create()
                .showCamera(true)
                .single()
                .start(AccountActivity.this, Const.REQUEST_IMAGE);
    }

    /**
     * 向用户解释为什么需要调用该权限
     * <p>
     * 只有当第一次请求权限被用户拒绝，下次请求权限之前会调用
     */
    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("拍照上传需要您提供存储权限和相机权限")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                }).show();
    }

    /* *
     *  当用户拒绝权限申请
     * */
    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onPermissionDenied() {

    }

    /*
     * 当用户选中了授权窗口中的不再询问复选框后并拒绝了权限请求时需要调用的方法
     * */
    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAsk() {
        showPermissionMessage("抱歉，您没有打开相机或外部存储权限！", true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.REQUEST_IMAGE) {//选取照片后
                if (data != null) {
                    List<String> urls = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (urls.size() > 0) {
                        LogUtil.d(urls.toString());
                        ImageUtil.startPhotoZoom(AccountActivity.this, new File(urls.get(0)));
                    }
                }

            } else if (requestCode == Const.REQUEST_CODE_EDITPIC) {//裁剪后
                saveImage(data);
            }
        }
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void saveImage(Intent data) {
        String facePath = "";
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap m_bitmap = extras.getParcelable("data");
            try {
                String path = FileUtil.initPath();
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                facePath = path + "picture_" + System.currentTimeMillis() + ".jpg";
                File file2 = new File(facePath);
                if (file2.exists()) {
                    file2.delete();
                }
                // 图片保存到本地
                FileUtil.saveBitmapWithPath(m_bitmap, facePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (m_bitmap != null) {
                System.gc();
                File mTmpFile = new File(facePath);
                //上传头像
                LoadDialog.show(AccountActivity.this);
                uploadAvatar(mTmpFile);
                // 回收
                m_bitmap.recycle();
                System.gc();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUser user = UserModel.getInstance().getCurrentUser();
        if (!TextUtils.isEmpty(user.getNickname())) {
            userNickname.setText(user.getNickname());
        }
        if (!TextUtils.isEmpty(user.getEmail())) {
            userEmail.setText(user.getEmail());
        }
        if (!TextUtils.isEmpty(user.getAvatarURL())) {
            Glide.with(AccountActivity.this).load(user.getAvatarURL()).into(userAvatar);
        }
    }

    //上传头像
    private void uploadAvatar(File file) {
        if (file.exists()) {
            final BmobFile bmobFile = new BmobFile(file);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //上传成功
                        updateAvatar(bmobFile.getFileUrl());
                    } else {
                        LoadDialog.dismiss(AccountActivity.this);
                        LogUtil.d("头像上传失败:" + e.toString());
                        Toast.makeText(AccountActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //更新头像
    private void updateAvatar(final String avatarUrl) {
        if (!TextUtils.isEmpty(avatarUrl)) {
            MyUser myUser = UserModel.getInstance().getCurrentUser();
            myUser.setAvatarURL(avatarUrl);
            myUser.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    LoadDialog.dismiss(AccountActivity.this);
                    if (e != null) {
                        LogUtil.d("头像更新失败:" + e.toString());
                        Toast.makeText(AccountActivity.this, "头像更新失败", Toast.LENGTH_SHORT).show();
                    }else {
                        Glide.with(AccountActivity.this).load(avatarUrl).into(userAvatar);
                    }
                }
            });
        } else {
            LogUtil.d("获取头像url出错");
            Toast.makeText(this, "获取头像url出错", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 没打开权限
     * 跳转到app设置页面
     */
    public void showPermissionMessage(String message, final boolean isFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("去打开", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                try {
                    startActivity(localIntent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isFinish) {
                    finish();
                }
            }
        });
        alert.show();
    }
}
