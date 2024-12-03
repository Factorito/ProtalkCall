package com.example.myapplication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private String userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Intent로 이메일 및 비밀번호 받기
        userEmail = getIntent().getStringExtra("email");
        userPassword = getIntent().getStringExtra("password");

        // XML 연결
        recipientField = findViewById(R.id.recipientField);
        subjectField = findViewById(R.id.subjectField);
        bodyField = findViewById(R.id.bodyField);
        sendButton = findViewById(R.id.sendButton);
        aiEditButton = findViewById(R.id.aiEditButton);

        // 보내기 버튼 클릭 리스너
        sendButton.setOnClickListener(v -> {
            String recipient = recipientField.getText().toString();
            String subject = subjectField.getText().toString();
            String body = bodyField.getText().toString();

            if (recipient.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
            } else {
                sendEmail(recipient, subject, body);
            }
        });

        // AI 수정 버튼 클릭 리스너
        aiEditButton.setOnClickListener(v -> {
            String body = bodyField.getText().toString();
            if (body.isEmpty()) {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {
                openAiEditUrl(body);
            }
        });
    }

    // SMTP 메일 전송
    private void sendEmail(String recipient, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userEmail, userPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            Toast.makeText(this, "메일이 성공적으로 전송되었습니다!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // AI 수정 URL 열기
    private void openAiEditUrl(String body) {
        String url = "https://chatgpt.com/g/g-6740329652cc8191a72428f552bb3e1f-protalkcall";
        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter("body", body)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}