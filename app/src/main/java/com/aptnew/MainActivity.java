package com.aptnew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.commen.ViewBind;
import com.modules.BindView;
import com.modules.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv_content)
    TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBind.bind(this);
    }

    @OnClick({R.id.tv_content})
    public void clickThis(View view) {
        Toast.makeText(this, "哈哈哈哈" + view.getId(), Toast.LENGTH_SHORT).show();
    }
}
