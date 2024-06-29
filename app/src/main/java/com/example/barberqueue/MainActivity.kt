package com.example.barberqueue

import android.os.Bundle
import android.widget.Button
import android.view.View;

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isLayout1Displayed = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button switchButton = new Button(this);
//        switchButton.setText("Switch Layout");
//
//        switchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isLayout1Displayed) {
//                    setContentView(R.layout.layout2);
//                } else {
//                    setContentView(R.layout.layout1);
//                }
//                isLayout1Displayed = !isLayout1Displayed;
//            }
//        });
//
//        // Menambahkan tombol ke layout
//        LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout1);
//        layout1.addView(switchButton);
    }

}