package com.u2tzjtne.aboutme.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MomentsBean;
import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.constant.Constant;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.model.UserModel;
import com.u2tzjtne.aboutme.util.UIUtil;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.xw.repo.BubbleSeekBar;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditMessageActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EmojiEditText emojiEditText;
    private ViewGroup rootView;
    private CheckBox box_emoji;
    private CheckBox box_font;
    private CheckBox box_theme;
    private RadioGroup radioGroup;
    private BubbleSeekBar bubbleSeekBar;

    private View layout_font_size;
    private View layout_theme;
    private CardView layout_content;

    private View edit_message_form;
    private ProgressBar edit_message_progress;

    private int fontSize = 18;
    private int bgColor = 0;
    private AlphaAnimation alphaAnimationShowIcon;

    private EmojiPopup emojiPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        emojiEditText = findViewById(R.id.edit_text);
        rootView = findViewById(R.id.root_view);
        box_emoji = findViewById(R.id.box_emoji);
        box_font = findViewById(R.id.box_font_size);
        box_theme = findViewById(R.id.box_theme);
        radioGroup = findViewById(R.id.radioGroup);

        layout_content = findViewById(R.id.layout_content);
        layout_font_size = findViewById(R.id.layout_font_size);
        layout_theme = findViewById(R.id.layout_theme);

        edit_message_form = findViewById(R.id.edit_message_form);
        edit_message_progress = findViewById(R.id.edit_message_progress);

        bubbleSeekBar = findViewById(R.id.bubbleSeekBar);

        alphaAnimationShowIcon = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimationShowIcon.setDuration(500);
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(emojiEditText);

        bubbleSeekBar.setOnProgressChangedListener(
                new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar,
                                          int progress, float progressFloat) {
                emojiEditText.setTextSize(progressFloat);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar,
                                              int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar,
                                             int progress, float progressFloat) {
                emojiEditText.setTextSize(progress);
                fontSize = progress;
            }
        });

        radioGroup.check(R.id.radioButton0);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButton0://默认主题
                        layout_content
                                .setCardBackgroundColor(getResources().getColor(R.color.white));
                        bgColor = 0;
                        break;
                    case R.id.radioButton1:
                        layout_content.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        bgColor = 1;
                        break;
                    case R.id.radioButton2:
                        layout_content.setCardBackgroundColor(getResources().getColor(R.color.theme1_Primary));
                        bgColor = 2;
                        break;
                    case R.id.radioButton3:
                        layout_content.setCardBackgroundColor(getResources().getColor(R.color.theme2_Primary));
                        bgColor = 3;
                        break;
                    case R.id.radioButton4:
                        layout_content.setCardBackgroundColor(getResources().getColor(R.color.theme3_Primary));
                        bgColor = 4;
                        break;
                    case R.id.radioButton5:
                        layout_content.setCardBackgroundColor(getResources().getColor(R.color.theme4_Primary));
                        bgColor = 5;
                        break;
                    case R.id.radioButton6:
                        layout_content.setCardBackgroundColor(getResources().getColor(R.color.theme5_Primary));
                        bgColor = 6;
                        break;
                }
            }
        });

        box_emoji.setOnClickListener(this);
        box_theme.setOnClickListener(this);
        box_font.setOnClickListener(this);
        box_font.setOnCheckedChangeListener(this);
        box_theme.setOnCheckedChangeListener(this);
        box_emoji.setOnCheckedChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_edit_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_send:
                edit_message_form.setVisibility(View.GONE);
                edit_message_progress.setVisibility(View.VISIBLE);
                sendJsonToServer(getJson());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getJson() {
        MyUser myUser = UserModel.getInstance().getCurrentUser();
        String message = emojiEditText.getText().toString();
        String nickname = myUser.getNickname();
        String avatarURL = myUser.getAvatarURL();
        String userID = myUser.getObjectId();
        String email = myUser.getEmail();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = sdf.format(new Date());


        MomentsBean bean = new MomentsBean();
        bean.setAvatarURL(avatarURL);
        bean.setMessage(message);
        bean.setFontSize(fontSize);
        bean.setDate(date);
        bean.setBgColor(bgColor);
        bean.setNickname(nickname);
        bean.setUserID(userID);
        bean.setEmail(email);

        return new Gson().toJson(bean);
    }

    //向服务器发送JSON数据
    public void sendJsonToServer(final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpHelper.HttpResult result = HttpHelper.post(HttpHelper.URL + "moments", new StringEntity(json, "UTF-8"));
                    if (result.getCode() == 200) {
                        UIUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (emojiPopup.isShowing()) {
                                    emojiPopup.dismiss();
                                }
                            }
                        });
                        setResult(Constant.RESULT_CODE_SEND_MESSAGE);
                        finish();
                    } else {
                        UIUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edit_message_progress.setVisibility(View.GONE);
                                edit_message_form.setVisibility(View.VISIBLE);
                                Toast.makeText(EditMessageActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.box_emoji:
                box_emoji.startAnimation(alphaAnimationShowIcon);
                break;
            case R.id.box_font_size:
                box_font.startAnimation(alphaAnimationShowIcon);
                break;
            case R.id.box_theme:
                box_theme.startAnimation(alphaAnimationShowIcon);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.box_emoji:
                emojiPopup.toggle();
                //选中
                if (b) {
                    //1.将字体和主题布局隐藏
                    layout_font_size.setVisibility(View.GONE);
                    layout_theme.setVisibility(View.GONE);
                    //2.将字体和布局图标设置为未选中
                    box_font.setChecked(false);
                    box_theme.setChecked(false);
                }
                break;
            case R.id.box_theme:
                if (b) {
                    //字体和表情布局隐藏
                    layout_font_size.setVisibility(View.GONE);
                    if (emojiPopup.isShowing()) {
                        emojiPopup.dismiss();
                    }
                    hideKeyboard(EditMessageActivity.this);
                    //主题和表情图标设置为未选中
                    box_font.setChecked(false);
                    box_emoji.setChecked(false);
                }
                //主题布局显示
                layout_theme.setVisibility(b ? View.VISIBLE : View.GONE);
                break;
            case R.id.box_font_size:
                if (b) {
                    //主题和表情布局隐藏
                    layout_theme.setVisibility(View.GONE);
                    if (emojiPopup.isShowing()) {
                        emojiPopup.dismiss();
                    }
                    hideKeyboard(EditMessageActivity.this);
                    //主题和表情图标设置为未选中
                    box_emoji.setChecked(false);
                    box_theme.setChecked(false);

                }
                //将字体布局显示
                layout_font_size.setVisibility(b ? View.VISIBLE : View.GONE);
                break;
        }
    }
}
