package com.u2tzjtne.aboutme.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.model.UserModel;
import com.u2tzjtne.aboutme.ui.fragment.AppFragment;
import com.u2tzjtne.aboutme.ui.fragment.BaseFragment;
import com.u2tzjtne.aboutme.ui.fragment.MomentsFragment;
import com.u2tzjtne.aboutme.ui.fragment.WebFragment;
import com.u2tzjtne.aboutme.util.Const;
import com.u2tzjtne.aboutme.util.LogUtil;
import com.u2tzjtne.aboutme.util.SPUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BaseFragment.BackHandledInterface,
        View.OnClickListener {

    //app pager
    private AppFragment appFragment;
    private Fragment currentFragment;
    //blog pager
    private WebFragment blogFragment;
    //github pager
    private WebFragment gitHubFragment;
    //moments pager
    private MomentsFragment momentsFragment;

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private FloatingActionButton fab;

    private AlertDialog dialog;

    private CheckBox checkBox0;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;


    private CircleImageView nv_header_avatar;
    private TextView nv_header_nickname;
    private TextView nv_header_email;

    private int REQUEST_CODE = 1001;

    //选择的item
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();
        //初始化主题
        initTheme();
        setContentView(R.layout.activity_main);
        LogUtil.d("测试信息");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, EditMessageActivity.class), REQUEST_CODE);
            }
        });
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {//滑动

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {//打开

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {//关闭
                //切换pager
                switchPager(selectedItem);
            }

            @Override
            public void onDrawerStateChanged(int newState) {//状态改变

            }
        });


        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        nv_header_avatar = headerLayout.findViewById(R.id.header_avatar);
        nv_header_nickname = headerLayout.findViewById(R.id.header_nickname);
        nv_header_email = headerLayout.findViewById(R.id.header_email);
        nv_header_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭drawer
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawers();
                }
                if (UserModel.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        //默认显示app界面
        fab.setVisibility(View.GONE);
        appFragment = new AppFragment();
        currentFragment = appFragment;
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, appFragment).commit();
        navigationView.setCheckedItem(R.id.nav_app);
        //设置Toolbar的标题
        toolbar.setTitle(getString(R.string.nav_title_app));
    }

    //初始化dialog
    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_theme_dialog, null);
        checkBox0 = view.findViewById(R.id.checkBox0);
        checkBox1 = view.findViewById(R.id.checkBox1);
        checkBox2 = view.findViewById(R.id.checkBox2);
        checkBox3 = view.findViewById(R.id.checkBox3);
        checkBox4 = view.findViewById(R.id.checkBox4);
        checkBox5 = view.findViewById(R.id.checkBox5);

        checkBox0.setOnClickListener(this);
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBox4.setOnClickListener(this);
        checkBox5.setOnClickListener(this);

        builder.setView(view);
        dialog = builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Const.RESULT_CODE_SEND_MESSAGE:
                momentsFragment.onRefresh();
                break;
        }
    }

    //初始化主题
    private void initTheme() {
        int themeType = SPUtil.getInt(Const.THEME_TYPE, 100);
        switch (themeType) {
            case 100:
                setTheme(R.style.AppTheme_Main);
                checkBox0.setChecked(true);
                break;
            case 101:
                setTheme(R.style.AppTheme_Main_Color1);
                checkBox1.setChecked(true);
                break;
            case 102:
                setTheme(R.style.AppTheme_Main_Color2);
                checkBox2.setChecked(true);
                break;
            case 103:
                setTheme(R.style.AppTheme_Main_Color3);
                checkBox3.setChecked(true);
                break;
            case 104:
                setTheme(R.style.AppTheme_Main_Color4);
                checkBox4.setChecked(true);
                break;
            case 105:
                setTheme(R.style.AppTheme_Main_Color5);
                checkBox5.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
//        //如果fragment未处理 交由activity处理
//        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
//            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                super.onBackPressed();
//            } else {
//                getSupportFragmentManager().popBackStack();
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //TODO 夜间模式
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //关闭drawer
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        }
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_app) {//应用
            selectedItem = Const.PAGER_APP;
        } else if (id == R.id.nav_blog) {//blog
            selectedItem = Const.PAGER_BLOG;
        } else if (id == R.id.nav_github) {//github
            selectedItem = Const.PAGER_GIT_HUB;
        } else if (id == R.id.nav_moments) {
            selectedItem = Const.PAGER_MOMENTS;
        } else if (id == R.id.nav_theme) {
            //弹出dialog
            if (dialog != null) {
                dialog.show();
            }
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_about_me) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_exit) {
            System.exit(0);
            finish();
        }
        return true;
    }


    //切换页面
    private void switchPager(int selectedItem) {
        switch (selectedItem) {
            case Const.PAGER_APP:
                toolbar.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                if (appFragment == null) {
                    appFragment = new AppFragment();
                }
                switchFragment(appFragment);
                navigationView.setCheckedItem(R.id.nav_app);
                toolbar.setTitle(getString(R.string.nav_title_app));
                break;
            case Const.PAGER_BLOG:
                toolbar.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                if (blogFragment == null) {
                    blogFragment = new WebFragment();
                }
                Bundle bundle1 = new Bundle();
                bundle1.putString(Const.WEB_VIEW_URL, Const.BLOG_URL);
                blogFragment.setArguments(bundle1);
                switchFragment(blogFragment);
                navigationView.setCheckedItem(R.id.nav_blog);
                break;
            case Const.PAGER_GIT_HUB:
                toolbar.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                if (gitHubFragment == null) {
                    gitHubFragment = new WebFragment();
                }
                Bundle bundle2 = new Bundle();
                bundle2.putString(Const.WEB_VIEW_URL, Const.GIT_HUB_URL);
                gitHubFragment.setArguments(bundle2);
                switchFragment(gitHubFragment);
                navigationView.setCheckedItem(R.id.nav_github);
                break;
            case Const.PAGER_MOMENTS:
                toolbar.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                if (momentsFragment == null) {
                    momentsFragment = new MomentsFragment();
                }
                switchFragment(momentsFragment);
                navigationView.setCheckedItem(R.id.nav_moments);
                toolbar.setTitle(getString(R.string.nav_title_moments));
                break;
        }
    }

    //正确的做法
    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(currentFragment)
                    .add(R.id.content_main, targetFragment)
                    .commit();
            System.out.println("还没添加呢");
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment)
                    .commit();
            System.out.println("添加了( ⊙o⊙ )哇");
        }
        currentFragment = targetFragment;
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        //this.mBackHandedFragment = selectedFragment;
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
                nv_header_email.setText(email);
            }
            if (nickname != null) {
                nv_header_nickname.setText(nickname);
            }
            if (avatarURL != null) {
                //TODO 加载图片
//                ImageOptions.Builder builder = new ImageOptions.Builder();
//                builder.setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP);
//                ImageOptions options = builder.build();
//                x.image().bind(nv_header_avatar, avatarURL, options);
                Glide.with(MainActivity.this).load(avatarURL).into(nv_header_avatar);
            } else {
                //TODO 默认头像
                nv_header_avatar.setImageResource(R.drawable.splash);
            }
        }
    }

    @Override
    public void onClick(View view) {
        //取消当前选中
        unSelect();
        switch (view.getId()) {
            case R.id.checkBox0:
                checkBox0.setChecked(true);
                SPUtil.putInt(Const.THEME_TYPE, 100);
                break;
            case R.id.checkBox1:
                checkBox1.setChecked(true);
                SPUtil.putInt(Const.THEME_TYPE, 101);
                break;
            case R.id.checkBox2:
                checkBox2.setChecked(true);
                SPUtil.putInt(Const.THEME_TYPE, 102);
                break;
            case R.id.checkBox3:
                checkBox3.setChecked(true);
                SPUtil.putInt(Const.THEME_TYPE, 103);
                break;
            case R.id.checkBox4:
                checkBox4.setChecked(true);
                SPUtil.putInt(Const.THEME_TYPE, 104);
                break;
            case R.id.checkBox5:
                checkBox5.setChecked(true);
                SPUtil.putInt(Const.THEME_TYPE, 105);
                break;
        }
        dialog.dismiss();
        MainActivity.this.recreate();
    }

    private void unSelect() {
        int type = SPUtil.getInt(Const.THEME_TYPE, 100);
        switch (type) {
            case 100:
                checkBox0.setChecked(false);
                break;
            case 101:
                checkBox1.setChecked(false);
                break;
            case 102:
                checkBox2.setChecked(false);
                break;
            case 103:
                checkBox3.setChecked(false);
                break;
            case 104:
                checkBox4.setChecked(false);
                break;
            case 105:
                checkBox5.setChecked(false);
                break;
        }
    }
}
