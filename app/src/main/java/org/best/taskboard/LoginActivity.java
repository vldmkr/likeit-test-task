package org.best.taskboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.loginButton)
    Button mLoginButton;
    @BindView(R.id.loginUsername)
    TextView mUsernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mUsernameText.getText().toString();
                if (name.trim().isEmpty()) {
                    name = getSaltString();
                }
                startActivity(new Intent(LoginActivity.this, BoardActivity.class).putExtra("name", name));
            }
        });
    }

    protected String getSaltString() {
        final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder salt = new StringBuilder();
        final Random rnd = new Random();
        while (salt.length() < 6) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }
}
