package com.utapps.httppost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HttpPostActivity extends Activity {

	public static final String DEBUG_TAG = "HttpPostActivity";
	public static final String INFO_TAG = "HttpPostActivity";

	private final String mUrl = "http://surya-interview.appspot.com/list";

	private EditText mEmailEditText;
	private Button mSubmitButton;
	private TextView mConnectionStatusTextView;

	private ProgressDialog mProgressDialog;

	String mEmailString;

	HttpPostModel mHttpPostModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_http_post);

		// Set References
		mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
		mSubmitButton = (Button) findViewById(R.id.submit_button);
		mConnectionStatusTextView = (TextView) findViewById(R.id.connection_text_view);

		// Display a message and disable the button if not connected to
		// internet.
		if (!isConnected()) {
			mConnectionStatusTextView
					.setText("No Network connection available");
			mSubmitButton.setEnabled(false);
		}

		// Reference to Model Class
		mHttpPostModel = new HttpPostModel();

		// Method to enable caching
		enableCache();

		// OnClicklistener event to be called when the button is pressed to
		// download the data
		mSubmitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mEmailString = mEmailEditText.getText().toString();
				if (validateInput(mEmailString)) {
					mHttpPostModel.setEmail(mEmailString);
					new DownloadResultTask().execute(mUrl);
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.toast_display_email_message,
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		HttpResponseCache cache = HttpResponseCache.getInstalled();
		if (cache != null) {
			cache.flush();
		}
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class DownloadResultTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// Progress dialog while connection is created and data is read.
			mProgressDialog = new ProgressDialog(HttpPostActivity.this);
			mProgressDialog.setMessage("Please Wait...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			System.out.println("email: " + mEmailEditText.getText().toString());
			try {
				return postData();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(" " + e.getMessage());
				throw new RuntimeException("Unable to retrieve data");
			} catch (JSONException e) {
				throw new RuntimeException("JSONException");
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// Dismissing Progress dialog after the data is in local.
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			// Starting another activity to display the data in ListView
			Intent mIntent = new Intent(HttpPostActivity.this,
					ResponseActivity.class);
			mIntent.putExtra("KEY", result);
			startActivity(mIntent);
		}

	}

	private String postData() throws IOException, JSONException {

		InputStream inputStream = null;

		try {
			System.out.println("Entering into try");
			URL mUrl = new URL(this.mUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl
					.openConnection();
			httpURLConnection.setReadTimeout(10000); // 10000milliseconds
			httpURLConnection.setConnectTimeout(15000);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestProperty("Content-Type",
					"application/json");

			// cached response
			int maxStale = 60 * 60 * 24 * 1; // Maximum 1 days of cached data
			httpURLConnection.setUseCaches(true);
			httpURLConnection.addRequestProperty("Cache-Control",
					"only-if-cached"); // locally-cached resources
			httpURLConnection.setRequestProperty("Cache-Control", "max-age=0"); // max
																				// age
																				// for
																				// cached
																				// data
			httpURLConnection.addRequestProperty("Cache-Control", "max-stale="
					+ maxStale); // max age of state data if cannot get latest
									// data

			Log.d(DEBUG_TAG, "Opening Connection " + this.mUrl);
			httpURLConnection.setRequestProperty("charset", "UTF-8");

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					httpURLConnection.getOutputStream()));
			out.write(writeJson().toString());
			out.close();
			System.out.println("Http Response: message: "
					+ httpURLConnection.getResponseMessage() + " Code: "
					+ httpURLConnection.getResponseCode());

			// Start the query
			int response_code = httpURLConnection.getResponseCode();
			Log.d(DEBUG_TAG, "The response is: " + response_code);

			inputStream = httpURLConnection.getInputStream();

			// Convert Stream to String
			String contentAsString = JsonUtility.streamToString(inputStream);
			Log.d(DEBUG_TAG, contentAsString);

			return contentAsString;

			// Closing the InputStream as app is
			// finished using it.
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

	// Convert into Json object
	public JSONObject writeJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("emailId", mHttpPostModel.getEmail());
		return object;
	}

	// Check connectivity of the network
	public boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	// Checks if user enters an email address
	private boolean validateInput(String mEmailString) {
		if (mEmailString != "")
			return true;
		else
			return false;
	}

	// Enable Caching
	private void enableCache() {
		try {
			File httpCacheDir = new File(getApplicationContext().getCacheDir(),
					"http");
			long httpCacheSize = 1 * 1024 * 1024; // 1 MiB
			HttpResponseCache.install(httpCacheDir, httpCacheSize);
			Class.forName("android.net.http.HttpResponseCache")
					// Working with previous released then 4.0
					.getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, httpCacheSize);
		} catch (IOException e) {
			Log.i(INFO_TAG, "HTTP response cache installation failed:" + e);
		} catch (Exception httpResponseCacheNotAvailable) {
			Log.i(DEBUG_TAG, "Http Response Cache Not available: "
					+ httpResponseCacheNotAvailable);
		}
	}
}
