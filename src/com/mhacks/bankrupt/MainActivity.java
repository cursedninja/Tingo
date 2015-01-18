package com.mhacks.bankrupt;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class MainActivity extends Activity {

	private String SENDER_ID = "73636588476";
	private GoogleCloudMessaging gcm;
	private NotificationHub hub;

	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	private DrawerLayout mDrawerLayout;
	private ListView mNavigationDrawer;
	private ActionBarDrawerToggle mDrawerToggle;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	private ArrayList<Company> al;
	private CompanyListAdapter cl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent connectDropService = new Intent(this, BankruptService.class);
		bindService(connectDropService, dropServiceConnection,
				Service.BIND_AUTO_CREATE);

		NotificationsManager.handleNotifications(this, SENDER_ID,
				MyHandler.class);

		gcm = GoogleCloudMessaging.getInstance(this);

		String connectionString = "Endpoint=sb://stockupdate-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=/M7vDgPNOoWfd1GwxPnb4Ha79pASk/yqcz/V5Yc71ek=";
		hub = new NotificationHub(
				"https://stockupdate-ns.servicebus.windows.net/stockupdate",
				connectionString, this);

		cl = new CompanyListAdapter(this);

		ListView lv = (ListView) findViewById(R.id.company_list);
		lv.setAdapter(cl);
		getInitialList();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						cl.get(position).getDescription(), Toast.LENGTH_SHORT)
						.show();
				Intent i = new Intent(MainActivity.this,
						CompanyDetailActivity.class);
				i.putParcelableArrayListExtra("list", cl.getCompanyList());
				i.putExtra("selected", position);
				startActivity(i);
			}
		});

	}

	private void getInitialList() {
		new RetrieveFeedTask().execute(null, null, null);
	}

	String result = "";

	class RetrieveFeedTask extends AsyncTask<String, String, String> {
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet request = new HttpGet(
						"https://raw.githubusercontent.com/StockMarker/node-server/master/data/company.json");
				// request.addHeader("deviceId", deviceId);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
					result = httpclient.execute(request, handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
				httpclient.getConnectionManager().shutdown();

			} catch (Exception e) {
				// e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String feed) {

			try {
				for (int i = 0; i < 6; i++) {
					JSONArray jObject = new JSONArray(feed);
					Log.d("sai", "object created");
					String aJsonString = jObject.getString(i);
					Log.i("sai", aJsonString);
					JSONObject jobj = new JSONObject(aJsonString);
					cl.add(new Company(jobj.getString("Company").replace(" Inc", ""), jobj
							.getString("Sector"), jobj
							.getString("Sub-Industry")));
					cl.get(i).ticker = jobj.getString("Description");
//					Log.d("sai", jobj.getString("Sub-Industry"));
				}
				cl.notifyDataSetChanged();
			} catch (JSONException e) {
				Log.d("sai", "error!");
				e.printStackTrace();
			}
		}
	}

	BankruptService bankruptService;
	boolean isServiceConnected = false;

	ServiceConnection dropServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder iBinder) {
			BankruptService.LocalBinder mBinder = (BankruptService.LocalBinder) iBinder;
			bankruptService = mBinder.getService();
			isServiceConnected = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			isServiceConnected = false;
			bankruptService = null;
		}
	};

	
}
