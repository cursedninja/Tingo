package com.mhacks.bankrupt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class BankruptService extends Service {

	private String SENDER_ID = "73636588476";
	private GoogleCloudMessaging gcm;
	private NotificationHub hub;

	@SuppressWarnings("unchecked")
	private void registerWithNotificationHubs() {
		new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				try {
					String regid = gcm.register(SENDER_ID);

					hub.register(regid);
				} catch (Exception e) {
					return e;
				}
				return null;
			}
		}.execute(null, null, null);
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		BankruptService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return BankruptService.this;
		}
	}

	String URL = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByProduct&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=SairamBa-2a57-48a2-9982-496895d9d7f7&RESPONSE-DATA-FORMAT=json&REST-PAYLOAD&paginationInput.entriesPerPage=2&productId.@type=ReferenceID&productId=53039031";
	String result = "";
	String deviceId = "xxxxx";
	final String tag = "Your Logcat tag: ";

	class RetrieveFeedTask extends AsyncTask<String, String, String> {
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet request = new HttpGet(URL);
				request.addHeader("deviceId", deviceId);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
					result = httpclient.execute(request, handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
				httpclient.getConnectionManager().shutdown();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String feed) {
			Log.i("sai", feed);
			try {
				JSONObject jObject = new JSONObject(feed);

				JSONArray job = (JSONArray) jObject
						.get("findItemsByProductResponse");
				JSONObject jobj2 = (JSONObject) job.get(0);
				JSONArray jobj3 = (JSONArray) jObject.get("searchResult");
				// .get("searchResult");

				Log.d("sai", jobj3.getString(0));

				String aJsonString = "";// job.getString("viewItemURL");
				Log.i("sai", aJsonString);
				Toast.makeText(BankruptService.this, aJsonString,
						Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate() {
		NotificationsManager.handleNotifications(this, SENDER_ID,
				MyHandler.class);
		Log.d("sai", "Service started");

		gcm = GoogleCloudMessaging.getInstance(this);

		String connectionString = "Endpoint=sb://stockupdate-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=/M7vDgPNOoWfd1GwxPnb4Ha79pASk/yqcz/V5Yc71ek=";
		hub = new NotificationHub(
				"https://stockupdate-ns.servicebus.windows.net/stockupdate",
				connectionString, this);

		registerWithNotificationHubs();
		super.onCreate();

		new RetrieveFeedTask().execute(null, null, null);
	}

	class QueryEBayTask extends AsyncTask<String, String, String> {
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				String keywords = currentCompany.replace(" ", ",");
//				String EBayURL = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=SairamBa-2a57-48a2-9982-496895d9d7f7&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&keywords=" + keywords + "&paginationInput.entriesPerPage=2";
				String EBayURL = "http://open.api.ebay.com/shopping?callname=FindProducts&VERSION=1.0.0&appid=SairamBa-2a57-48a2-9982-496895d9d7f7&QueryKeywords="
						+ keywords
						+ "&AvailableItemsOnly=true&MaxEntries=30&version=880";
				Log.d("sai", "EbayURL=" + EBayURL);
				HttpGet request = new HttpGet(EBayURL);
				request.addHeader("deviceId", deviceId);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
					result = httpclient.execute(request, handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
				httpclient.getConnectionManager().shutdown();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String feed) {
			try {
				InputStream inputStream = new ByteArrayInputStream(
						feed.getBytes(Charset.forName("UTF-8")));

				queries = parse(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	List<EBayListItem> queries = null;
	private static final String ns = null;

	public List<EBayListItem> parse(InputStream in)
			throws XmlPullParserException, IOException {
		try {
			Log.d("sai", "Starting parsing of xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} catch (Exception e) {
			Log.d("sai", "XML parse exception");
			e.printStackTrace();
		} finally {
			in.close();
		}
		return null;
	}

	private List<EBayListItem> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<EBayListItem> entries = new ArrayList<EBayListItem>();
		boolean productbool = false, productIDbool = false, dispnamebool = false;
		Log.d("sai", "readFeed");
		EBayListItem curItem = null;
		// parser.require(XmlPullParser.START_TAG, ns, "feed");
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			// Log.d("sai", "parser = " + parser.getText() +
			// parser.getEventType());
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				// Log.d("sai", "starting tag = " + parser.getName());
				if (parser.getName().equals("Product")) {
					curItem = new EBayListItem();
					continue;
				}
				if (parser.getName().equals("ProductID")
						&& parser.getAttributeValue(0).equals("Reference")) {
					productIDbool = true;
				}
//				if(parser.getName())
				if (parser.getName().equals("Title")) {
					dispnamebool = true;
				}
			}
			if (parser.getEventType() == XmlPullParser.TEXT) {
				if (productIDbool) {
					// Log.d("sai","ProductID = " + parser.getText());
					curItem.productID = parser.getText();
					productIDbool = false;
				}
				if (dispnamebool) {
					curItem.name = parser.getText();
					dispnamebool = false;
				}
			}
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName().equals("Product")) {
					entries.add(curItem);
				}
			}
		}
		Log.d("sai", "Parsing complete");
		Log.d("sai", "Products found = " + entries.size());
		sendBroadcast(new Intent("updateui"));
		return entries;
	}

	String currentCompany = "";

	public List<EBayListItem> getEbayItemList(String currentComp) {
		currentCompany = currentComp;
		new QueryEBayTask().execute(null, null, null);
		return queries;

	}

	String productKey = "";

	class LinkEBayTask extends AsyncTask<String, String, String> {
		protected String doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				// String keywords = currentCompany.replace(" ", "%20");
				String EBayURL = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByProduct&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=SairamBa-2a57-48a2-9982-496895d9d7f7&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&paginationInput.entriesPerPage=2&productId.@type=ReferenceID&productId="
						+ productKey;
				Log.d("sai", "EbayURL=" + EBayURL);
				HttpGet request = new HttpGet(EBayURL);
				request.addHeader("deviceId", deviceId);
				ResponseHandler<String> handler = new BasicResponseHandler();
				try {
					result = httpclient.execute(request, handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
				httpclient.getConnectionManager().shutdown();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String feed) {
			try {
				InputStream inputStream = new ByteArrayInputStream(
						feed.getBytes(Charset.forName("UTF-8")));

				String link = parseLink(inputStream);
				Intent inte = new Intent("openlink");
				inte.putExtra("link", link);
				sendBroadcast(inte);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	public String parseLink(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			Log.d("sai", "Starting parsing of xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeedLink(parser);
		} catch (Exception e) {
			Log.d("sai", "XML parse exception");
			e.printStackTrace();
		} finally {
			in.close();
		}
		return null;
	}

	private String readFeedLink(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<EBayListItem> entries = new ArrayList<EBayListItem>();
		boolean linkbool = false;
		Log.d("sai", "readFeed");
		EBayListItem curItem = null;
		// parser.require(XmlPullParser.START_TAG, ns, "feed");
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			// Log.d("sai", "parser = " + parser.getText() +
			// parser.getEventType());
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				// Log.d("sai", "starting tag = " + parser.getName());
				if (parser.getName().equals("itemSearchURL")) {
					linkbool = true;
				}
			}
			if (parser.getEventType() == XmlPullParser.TEXT) {
				if (linkbool) {
					// Log.d("sai","ProductID = " + parser.getText());
					linkbool = false;
					return parser.getText();

				}
			}
			if (parser.getEventType() == XmlPullParser.END_TAG) {
				if (parser.getName().equals("Product")) {
					entries.add(curItem);
				}
			}
		}
		Log.d("sai", "Parsing complete");
		Log.d("sai", "Products found = " + entries.size());
		sendBroadcast(new Intent("updateui"));
		return "";
	}

	public void getProductLink(String prodKey) {
		productKey = prodKey;
		new LinkEBayTask().execute(null, null, null);
	}

}
