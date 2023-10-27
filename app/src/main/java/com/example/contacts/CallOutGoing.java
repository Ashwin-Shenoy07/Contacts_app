package com.example.contacts;//package com.example.contacts;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.view.View;
//import android.widget.Chronometer;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//public class CallOutGoing extends AppCompatActivity {
//    String contactName;
//    String contactPhone;
//    long contactId;
//    private Chronometer chronometer;
//    private boolean isTimerRunning = false;
//    private long timeWhenStopped = 0;
//    TextView contact_name , contact_number;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calling);
//
//
//
//        // Retrieve the contact details and ID from the intent
//        Intent intent = getIntent();
//        contactName = intent.getStringExtra("contact_name");
//        contactPhone = intent.getStringExtra("contact_phone");
//        contactId = intent.getLongExtra("contact_id", -1);
//
//        // Initialize the Chronometer
//        chronometer = findViewById(R.id.chronometer);
//        contact_name=findViewById(R.id.Contact_Name_View);
//        contact_number=findViewById(R.id.textView2);
//
//        contact_name.setText(contactName);
//        contact_number.setText(contactPhone);
//
//        // Add a click listener to the end call button
//        ImageView callEndButton = findViewById(R.id.callend_btn);
//        callEndButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Stop the timer and store the timer in a variable
//                chronometer.stop();
//                isTimerRunning = false;
//                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
//
//                Toast.makeText(CallOutGoing.this," call duration is :"+ timeWhenStopped ,Toast.LENGTH_SHORT).show();
//                // You can save the call duration using 'timeWhenStopped'
//                // Implement your logic here for saving the call duration.
//
//
//                new android.os.Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish(); // Finish the activity after the delay
//                    }
//                }, 2000);
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!isTimerRunning) {
//            // Start or resume the timer
//            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
//            chronometer.start();
//            isTimerRunning = true;
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (isTimerRunning) {
//            // Stop the timer and save the time when stopped
//            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
//            chronometer.stop();
//            isTimerRunning = false;
//        }
//    }
//}




import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallOutGoing extends AppCompatActivity {
    String contactName;
    String contactPhone;
    long contactId;
    private Chronometer chronometer;
    private boolean isTimerRunning = false;
    private long timeWhenStopped = 0;
    TextView contact_name, contact_number;
    private ContactsDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        // Initialize the database manager
        databaseManager = new ContactsDatabaseManager(this).open();

        // Retrieve the contact details and ID from the intent
        Intent intent = getIntent();
        contactName = intent.getStringExtra("contact_name");
        contactPhone = intent.getStringExtra("contact_phone");
        contactId = intent.getLongExtra("contact_id", -1);

        // Initialize the Chronometer
        chronometer = findViewById(R.id.chronometer);
        contact_name = findViewById(R.id.Contact_Name_View);
        contact_number = findViewById(R.id.textView2);

        contact_name.setText(contactName);
        contact_number.setText(contactPhone);

        // Add a click listener to the end call button
        ImageView callEndButton = findViewById(R.id.callend_btn);
        callEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop the timer and store the timer in a variable
                chronometer.stop();
                isTimerRunning = false;
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();

                Toast.makeText(CallOutGoing.this, "Call duration is: " + timeWhenStopped, Toast.LENGTH_SHORT).show();

                // Get the current system date and format it
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String callDate = dateFormat.format(new Date());

                // Insert the call log information into the database
                long callLogId = databaseManager.insertCallLog(contactId, contactPhone, contactName, -timeWhenStopped, callDate);

                if (callLogId != -1) {
                    // Call log inserted successfully
                    Toast.makeText(CallOutGoing.this, "Call log saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle insertion error
                    Toast.makeText(CallOutGoing.this, "Error saving the call log", Toast.LENGTH_SHORT).show();
                }

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish(); // Finish the activity after the delay
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isTimerRunning) {
            // Start or resume the timer
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chronometer.start();
            isTimerRunning = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTimerRunning) {
            // Stop the timer and save the time when stopped
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            isTimerRunning = false;
        }

        // Close the database connection
        databaseManager.close();
    }
}
