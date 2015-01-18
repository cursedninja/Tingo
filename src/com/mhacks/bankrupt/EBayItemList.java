package com.mhacks.bankrupt;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EBayItemList extends Activity {

	public final BroadcastReceiver uireceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("openlink")) {
				String link = intent.getStringExtra("link");
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(link));
				startActivity(i);
			} else {
				updateUI();
			}
		}
	};
	String currentCompany = "";
	private ListView listView;
	TextView tv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ebay_itemlist);
		tv = (TextView)findViewById(R.id.textView1);
		tv.setText("No Relevant Items Found");
		Intent i = getIntent();
		currentCompany = i.getStringExtra("name");
		setTitle("eBay - " + currentCompany);
		Log.d("sai", "Current company on EBay = " + currentCompany);
		listView = (ListView) findViewById(R.id.list);

		Intent connectDropService = new Intent(this, BankruptService.class);
		bindService(connectDropService, dropServiceConnection,
				Service.BIND_AUTO_CREATE);
		IntentFilter ifill = new IntentFilter();
		ifill.addAction("updateui");
		ifill.addAction("openlink");
		registerReceiver(uireceiver, ifill);
	}

	String[] values = null;

	public void updateUI() {
		items = bankruptService.queries;
		if(items.size()>0)
			tv.setText("Relevant results:");
		values = new String[items.size()];
		for (int i = 0; i < items.size(); i++) {
			values[i] = items.get(i).name;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				EBayItemList.this, android.R.layout.simple_list_item_1,
				android.R.id.text1, values);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;
				String productKey = items.get(position).productID;
				Log.d("sai", "Open Page for " + productKey);
				bankruptService.getProductLink(productKey);
			}
		});
		// Assign adapter to ListView
		listView.setAdapter(adapter);
	}

	BankruptService bankruptService;
	boolean isServiceConnected = false;
	List<EBayListItem> items;

	ServiceConnection dropServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder iBinder) {
			BankruptService.LocalBinder mBinder = (BankruptService.LocalBinder) iBinder;
			bankruptService = mBinder.getService();
			isServiceConnected = true;
			items = bankruptService.getEbayItemList(currentCompany);
			Log.d("sai", "items returned " + items);
			if (items != null)
				Log.d("sai", "Items count = " + items.size());
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			isServiceConnected = false;
			bankruptService = null;
		}
	};

}
