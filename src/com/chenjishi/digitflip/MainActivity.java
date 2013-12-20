package com.chenjishi.digitflip;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private DigitFlipView digitalView;
    private EditText editText1;
    private EditText editText2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        digitalView = (DigitFlipView) findViewById(R.id.digital_view);
        editText1 = (EditText) findViewById(R.id.edit1);
        editText2 = (EditText) findViewById(R.id.edit2);
    }

    public void onButtonClicked(View view) {
        String s1 = editText1.getText().toString().trim();
        String s2 = editText2.getText().toString().trim();

        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            Toast.makeText(this, "please type the number", Toast.LENGTH_SHORT).show();
            return;
        }

        digitalView.setNumber(Integer.valueOf(s1), Integer.valueOf(s2));
    }
}
