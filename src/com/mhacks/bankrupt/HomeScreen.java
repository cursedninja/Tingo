package com.mhacks.bankrupt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class HomeScreen extends Activity {

	/** Called when the activity is first created. */
	ImageButton ib;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home_screen);
	    ib = (ImageButton)findViewById(R.id.imageButton1);
	    ib.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeScreen.this,MainActivity.class);
				startActivity(i);
				
			}
		});
	}

}
