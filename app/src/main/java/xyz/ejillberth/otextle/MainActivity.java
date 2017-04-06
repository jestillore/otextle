package xyz.ejillberth.otextle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MessageUtil.newMessage(MessageUtil.formatNumber("639778039119"), "@help");
        MessageUtil.newMessage(MessageUtil.formatNumber("639253012595"), "@help");
    }
}
