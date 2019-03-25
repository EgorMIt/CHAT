package com.example.egor.egorchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.egor.firebasechat.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static int signed = 1;
    private FirebaseListAdapter<Message> adapter; //поддержка списка сообщений

    RelativeLayout activity_main;
    Button button;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = (RelativeLayout)findViewById(R.layout.activity_main);
        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.editText);

                FirebaseDatabase.getInstance().getReference().push() //отправка в базу данных
                        .setValue(new Message(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                input.setText("");
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //проверка авторизации
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()//настройка окна авторизации
                    .build(), signed);
        } else {
            displayChat();
        }
    }

    private void displayChat() {

        @SuppressLint("ResourceType")
        ListView list = (ListView)findViewById(R.id.listView); //список сообщений
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.item,
                FirebaseDatabase.getInstance().getReference()) { //сама база данных
            @Override
            protected void populateView(View v, Message model, int position) { //список пунктов сообщения
                TextView textMessage, autor, timeMessage;

                textMessage = (TextView)v.findViewById(R.id.tvMessage);
                autor = (TextView)v.findViewById(R.id.tvUser);
                timeMessage = (TextView)v.findViewById(R.id.tvTime);

                textMessage.setText(model.getText());
                autor.setText(model.getAutor());
                timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
            }
        };

        list.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int code, int result, Intent data) { //показ окна чата
        super.onActivityResult(code, result, data);
        if (code == signed)
        {
            if (result == RESULT_OK)
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Вы вошли в приложение!", Toast.LENGTH_SHORT);
                toast.show();
                displayChat();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//выход из чата
        if (item.getItemId() == R.id.menu_signout)
        {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Вы вышли из приложения!", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();

                        }
                    });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
