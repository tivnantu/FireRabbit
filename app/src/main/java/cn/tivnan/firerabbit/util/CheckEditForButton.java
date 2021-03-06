package cn.tivnan.firerabbit.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class CheckEditForButton implements TextWatcher {//用户名和密码都填写后按钮变为可点击状态

    // 声明一个监听器
    private EditTextChangeListener mListener;

    private Button button1, button2;
    private EditText[] mEditTexts;

    // 监听器的set方法
    public void setListener(EditTextChangeListener listener) {
        mListener = listener;
    }

    public CheckEditForButton(Button button1, Button button2) {
        this.button1 = button1;
        this.button2 = button2;
    }

    public void addEditText(EditText... editTexts) {
        mEditTexts = editTexts;
        for (EditText editText : mEditTexts) {
            editText.addTextChangedListener(this);
        }
    }

    private boolean allEditIsEmpty() {
        // 因为此处没有对下标进行操作,所以遍历采用增强for循环
        for (EditText editText : mEditTexts) {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                // 如果是空的,那么直接返回true
                return true;
            } else {
                // 如果输入框中有内容,那么进行循环(检查下一个输入框中是否有内容)
                // 当循环执行完毕后会return false;
                continue;
            }
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // 当输入框内容改变之前,会调用此方法
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 当输入框内容改变之时,会调用此方法

    }

    @Override
    public void afterTextChanged(Editable s) {
        // 当输入框内容改变之后,会调用此方法
        if (allEditIsEmpty()) {
            // 如果不是所有的EditText中都没有有数据 那么就将接口中的方法设置为false
            mListener.allHasContent(false);
//            button1.setEnabled(false);
//            button2.setEnabled(false);
        } else {
            // 如果所有的EditText中都有数据 那么就将接口中的方法设置为true
            mListener.allHasContent(true);
//            button1.setEnabled(true);
//            button2.setEnabled(true);
        }
    }
    
    public interface EditTextChangeListener {
        // 是否所有的EditText中都有内容
        void allHasContent(boolean isHasContent);
    }
}
