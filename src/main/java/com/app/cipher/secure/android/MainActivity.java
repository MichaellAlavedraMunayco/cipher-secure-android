package com.app.cipher.secure.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView cipherInput;
    private EditText dataInput, keyInput;
    private Button encryptButton, decryptButton;
    private TextView resultLabel;

    private String[] CIPHERS = new String[]{};

    String dataEncrypted, dataDecrypted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cipherInput = findViewById(R.id.cipher_input);
        dataInput = findViewById(R.id.data_input);
        keyInput = findViewById(R.id.key_input);
        resultLabel = findViewById(R.id.result_label);
        encryptButton = findViewById(R.id.encrypt_button);
        decryptButton = findViewById(R.id.decrypt_button);

        CIPHERS = getCiphers();

        fillCipherInput();
        fillDataInput();
        fillKeyInput();

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dataEncrypted = encrypt(dataInput.getText().toString(), keyInput.getText().toString());
                    resultLabel.setText(dataEncrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dataEncrypted = dataEncrypted == null ? dataInput.getText().toString() : dataEncrypted;
                    dataDecrypted = decrypt(dataEncrypted, keyInput.getText().toString());
                    resultLabel.setText(dataDecrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String encrypt(String data, String key) throws Exception {
        SecretKeySpec secretKey = getSecretKey(key);
        Cipher cipher = Cipher.getInstance(cipherInput.getText().toString());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] dataBytesEncrypted = cipher.doFinal(data.getBytes());
        String dataBase64Encrypted = Base64.encodeToString(dataBytesEncrypted, Base64.DEFAULT);
        return dataBase64Encrypted;
    }

    private String decrypt(String dataEncrypted, String key) throws Exception {
        SecretKeySpec secretKey = getSecretKey(key);
        Cipher cipher = Cipher.getInstance(cipherInput.getText().toString());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] dataBase64Decrypted = Base64.decode(dataEncrypted, Base64.DEFAULT);
        byte[] dataBytesDecrypted = cipher.doFinal(dataBase64Decrypted);
        String dataDecrypted = new String(dataBytesDecrypted);
        return dataDecrypted;
    }

    private SecretKeySpec getSecretKey(String key) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = messageDigest.digest(keyBytes);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, cipherInput.getText().toString());
        return secretKey;
    }

    private String[] getCiphers() {
        return getResources().getStringArray(R.array.ciphers);
    }

    private void fillCipherInput() {

        ArrayAdapter<String> cipherAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CIPHERS);

        cipherInput.setAdapter(cipherAdapter);
        cipherInput.setText(CIPHERS[0]);

    }

    private void fillDataInput() {
        dataInput.setText(getString(R.string.data_sample));
    }

    private void fillKeyInput() {
        keyInput.setText(getString(R.string.key_sample));
    }
}