package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ComposeActivity extends AppCompatActivity {

    private EditText recipientField, subjectField, bodyField;
    private Button sendButton, aiEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        recipientField = findViewById(R.id.recipientField);
        subjectField = findViewById(R.id.subjectField);
        bodyField = findViewById(R.id.bodyField);
        sendButton = findViewById(R.id.sendButton);
        aiEditButton = findViewById(R.id.aiEditButton);

        // AI 수정 버튼 클릭 시 웹 페이지로 이동
        aiEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aiIntent = new Intent(Intent.ACTION_VIEW);
                aiIntent.setData(android.net.Uri.parse("https://chatgpt.com/g/g-6740329652cc8191a72428f552bb3e1f-protalkcall"));
                startActivity(aiIntent);
            }
        });

        // 메일 전송 버튼 클릭
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recipientField.getText() != null ? recipientField.getText().toString().trim() : "";
                String subject = subjectField.getText() != null ? subjectField.getText().toString().trim() : "";
                String body = bodyField.getText() != null ? bodyField.getText().toString().trim() : "";

                if (recipient.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    new SendEmailTask().execute(recipient, subject, body);
                }
            }
        });
    }

    // 비동기 메일 전송 작업
    private class SendEmailTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String recipient = params[0];
            String subject = params[1];
            String body = params[2];

            try {
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        String userEmail = LoginActivity.userEmail;
                        String userPassword = LoginActivity.userPassword;
                        return new PasswordAuthentication(userEmail, userPassword);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(LoginActivity.userEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                return "메일 전송 성공!";
            } catch (Exception e) {
                Log.e("EmailTask", "알 수 없는 오류", e);
                return "메일 전송 실패: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ComposeActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
