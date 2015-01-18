package com.mhacks.bankrupt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CompanyDetailActivity extends Activity {
	private ArrayList<Company> companyList = new ArrayList<>();
	private int selected = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.company_detail_screen);

		Intent i = getIntent();
		if (i != null) {
			onNewIntent(i);
		}
	}

	private Company cur;
	private CompanyDetailListAdapter cdla;

	TextView companyName, categ, subcateg, descr;
	Button goebay;
	
	String currentCompany = "";

	@Override
	protected void onNewIntent(Intent intent) {

		
		companyList = intent.getParcelableArrayListExtra("list");
		selected = intent.getIntExtra("selected", -1);
		Log.d("sai", "company size = " + companyList.size());
		Log.d("sai", "selected = " + selected);
		setContentView(R.layout.company_detail_screen);
		companyName = (TextView)findViewById(R.id.textView1);
		categ = (TextView)findViewById(R.id.textView2);
		subcateg = (TextView)findViewById(R.id.textView3);
//		descr = (TextView)findViewById(R.id.textView4);
		
		goebay = (Button)findViewById(R.id.button1);
		
		

		// TextView tv = (TextView) findViewById(R.id.company_stuff);
		// tv.setText(companyList.get(selected).toString());

		cur = companyList.get(selected);
		//getSupportActionBar().setTitle(cur.getName());
		//companyName.setText(cur.getName());
		categ.setText("Deals in " + cur.getDate());
		subcateg.setText(cur.getDescription());
//		descr.setText(cur.ticker);
		setTitle(cur.getName());
		cdla = new CompanyDetailListAdapter(this);
		
		goebay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent in = new Intent(getBaseContext(), EBayItemList.class);
				in.putExtra("name", currentCompany);
				startActivity(in);
			}
		});

		ListView lv = (ListView) findViewById(R.id.company_stuff);
		//lv.setAdapter(cdla);
//		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// APIHelper.printJSON(APIHelper.getEndpoint("/companies"), 0);
//				if(position==2){
//					Intent in = new Intent(getBaseContext(), EBayItemList.class);
//					in.putExtra("name", currentCompany);
//					startActivity(in);
//				}
//			}
//		});
		cdla.put("Name", cur.getName());
		currentCompany = cur.getName();
		cdla.put("Sub-Category", cur.getDescription());
		cdla.put("Category", cur.getDate().toString());
		cdla.put("View Sales", "Go to eBay");
//		cdla.put("Description", cur.)
	}

}
