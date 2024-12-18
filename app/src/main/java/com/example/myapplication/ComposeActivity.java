package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ComposeActivity extends AppCompatActivity {

    private EditText recipientField, subjectField, bodyField;
    private Button sendButton, aiEditButton, attachButton;

    private Uri selectedFileUri; // 첨부 파일의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        recipientField = findViewById(R.id.recipientField);
        subjectField = findViewById(R.id.subjectField);
        bodyField = findViewById(R.id.bodyField);
        sendButton = findViewById(R.id.sendButton);
        aiEditButton = findViewById(R.id.aiEditButton);
        attachButton = findViewById(R.id.attachButton);

        // AI 수정 버튼 클릭
        aiEditButton.setOnClickListener(v -> {
            Intent aiIntent = new Intent(Intent.ACTION_VIEW);
            aiIntent.setData(Uri.parse("https://chatgpt.com/g/g-6740329652cc8191a72428f552bb3e1f-protalkcall"));
            startActivity(aiIntent);
        });

        // 파일 첨부 버튼 클릭
        attachButton.setOnClickListener(v -> openFilePicker());

        // 메일 전송 버튼 클릭
        sendButton.setOnClickListener(v -> {
            String recipient = recipientField.getText() != null ? recipientField.getText().toString().trim() : "";
            String subject = subjectField.getText() != null ? subjectField.getText().toString().trim() : "";
            String body = bodyField.getText() != null ? bodyField.getText().toString().trim() : "";

            if (recipient.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                Toast.makeText(ComposeActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                new SendEmailTask().execute(recipient, subject, body);
            }
        });
    }

    // 파일 선택기를 여는 메서드
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // 모든 파일 유형 허용
        startActivityForResult(Intent.createChooser(intent, "파일 선택"), 1000);
    }

    // 파일 선택 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(this, "파일 선택됨: " + selectedFileUri.getPath(), Toast.LENGTH_SHORT).show();
        }
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

                // 본문 및 첨부 파일 추가
                Multipart multipart = new MimeMultipart();

                // 본문 추가
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);
                multipart.addBodyPart(textPart);

                // 첨부 파일 추가
                if (selectedFileUri != null) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    String filePath = getFilePathFromUri(selectedFileUri);
                    if (filePath != null) {
                        attachmentPart.attachFile(new File(filePath));
                        multipart.addBodyPart(attachmentPart);
                    }
                }

                message.setContent(multipart);

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
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        if (filePath == null) {
            // 파일 경로를 가져오지 못한 경우
            filePath = uri.getPath();
        }
        return filePath;
    }
}
