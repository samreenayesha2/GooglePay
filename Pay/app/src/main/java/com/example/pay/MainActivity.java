package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String GPAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

    EditText amount, note, name, upiId;
    TextView msg;
    Button pay;
    Uri uri;
    String approvalRefNo;

    public static String payerName, UpiId, msgNote, sendAmount, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        upiId = findViewById(R.id.upi_id);
        amount = findViewById(R.id.amount);
        note = findViewById(R.id.transaction_note);
        msg = findViewById(R.id.status);
        pay = findViewById(R.id.pay);


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payerName = name.getText().toString();
                UpiId = upiId.getText().toString();
                msgNote = msg.getText().toString();
                sendAmount = amount.getText().toString();
                status = name.getText().toString();

                if (!payerName.equals("") && !upiId.equals("") && !msgNote.equals("") && !sendAmount.equals("")) {

                    uri = getUpiPaymentUri(payerName, UpiId, msgNote, sendAmount);
                    payWithGpay(GPAY_PACKAGE_NAME);
                }
                else{
                    Toast.makeText(MainActivity.this, "Fill all above details and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static Uri getUpiPaymentUri(String name, String upiId, String note, String amount) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
    }
//click on pay button after filling details
// hi?

    private void payWithGpay(String packageName) {
        if(isAppInstalled(this,packageName)){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent, 0);
        }
        else{
            Toast.makeText(MainActivity.this, "googlepay is not isnatlled. Pleasse install anf try again", Toast.LENGTH_SHORT).show();
        }}


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode ,data);

        if(data!= null){
            status=data.getStringExtra("Status").toLowerCase();
            approvalRefNo = data.getStringExtra("txnRef");
        }
        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(MainActivity.this, "Transaction successful. "+approvalRefNo, Toast.LENGTH_SHORT).show();
            msg.setText("Transaction successful of ₹" + sendAmount);
            msg.setTextColor(Color.GREEN);

        }

        else{
            Toast.makeText(MainActivity.this, "Transaction cancelled or failed please try again.", Toast.LENGTH_SHORT).show();
            msg.setText("Transaction Failed of ₹" + sendAmount);
            msg.setTextColor(Color.RED);
        }}

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}