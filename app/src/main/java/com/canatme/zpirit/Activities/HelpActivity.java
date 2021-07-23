package com.canatme.zpirit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.canatme.zpirit.Dataclasses.ComplaintDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HelpActivity extends AppCompatActivity {
    private static final String TAG = "HelpActivity";
    private ImageView ivBack;
    private Button btSubmitComplaint;
    private EditText etWriteComplaint;
    private DatabaseReference dbSendComplaint;
    private String phNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ivBack = findViewById(R.id.ivBack);
        etWriteComplaint = findViewById(R.id.etWriteComplaint);
        btSubmitComplaint = findViewById(R.id.btSubmitComplaint);
        dbSendComplaint = FirebaseDatabase.getInstance().getReference();

        phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "nophNumberfound");

        ivBack.setOnClickListener(view -> onBackPressed());



        btSubmitComplaint.setOnClickListener(view -> {
            submitComplaint();
        });
    }

    private void submitComplaint()
    {
        if(TextUtils.isEmpty(etWriteComplaint.getText().toString().trim()))
        {
            showToast("Complaint box is empty");
        }
        else
        {
            long timeSubmitted = System.currentTimeMillis();
            String textComplaint = etWriteComplaint.getText().toString().trim();
            String StrtimeSubmitted = String.valueOf(timeSubmitted);
            String strComplaintID = "com"+phNumber+timeSubmitted;
            ComplaintDto complaintDto = new ComplaintDto(strComplaintID, StrtimeSubmitted, textComplaint, false);
            dbSendComplaint.child("complaints").child(phNumber).child(strComplaintID).setValue(complaintDto);
            showToast("Complaint has been registered, our team will contact you within 48 hours");
            finish();
        }

    }

    private void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}