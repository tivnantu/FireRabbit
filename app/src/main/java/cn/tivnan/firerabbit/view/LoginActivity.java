package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.util.CheckEditForButton;
import cn.tivnan.firerabbit.util.EditTextChangeListener;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_name, editText_password;
    private Button button_login;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_name = findViewById(R.id.user_name);
        editText_password = findViewById(R.id.user_password);
        button_login = findViewById(R.id.login);
        checkBox = findViewById(R.id.checkbox);

        //1.创建工具类对象 设置监听空间
        CheckEditForButton checkEditForButton = new CheckEditForButton(button_login);

        //2.把所有被监听的EditText设置进去
        checkEditForButton.addEditText(editText_name, editText_password);

        //3.根据接口回调的方法,分别进行操作
        checkEditForButton.setListener(new EditTextChangeListener() {
            @Override
            public void allHasContent(boolean isHasContent) {
                if (isHasContent) {
                    button_login.setTextColor(Color.parseColor("#FF148F"));
                } else {
                    button_login.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        //隐藏密码
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //如果选中，显示密码
                    editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });


    }
}