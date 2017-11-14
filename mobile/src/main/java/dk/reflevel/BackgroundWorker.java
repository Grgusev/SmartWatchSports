package dk.reflevel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Rimon Nassory on 13-02-2017.
 */

public class BackgroundWorker extends AsyncTask<String, Void, String> {
	private Context				context			= null;
	private AlertDialog			alertDialog		= null;


	public BackgroundWorker(Context ctx) {
		context = ctx;
	}

	@Override
	protected String doInBackground(String... params) {
		String type = params[0];
		String login_url = "http://www.ar-webdesign.com/referee/login.php";
		String register_url = "http://www.ar-webdesign.com/referee/register2.php";

		if (type.equals("login")) {
			try {
				String user_name = params[1];
				String password = params[2];
				URL url = new URL(login_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				OutputStream outPutStream = httpURLConnection.getOutputStream();
				BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outPutStream, "UTF-8"));
				String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
				bufferWriter.write(post_data);
				bufferWriter.flush();
				bufferWriter.close();
				outPutStream.close();
				InputStream inPutStream = httpURLConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inPutStream, "iso-8859-1"));
				String result = "";
				String line = "";

				while ((line = bufferedReader.readLine()) != null) {
					result += line;
				}
				bufferedReader.close();
				inPutStream.close();
				httpURLConnection.disconnect();
				return result;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (type.equals("register")) {
			try {
				String username = params[1];
				String password = params[2];
				URL url = new URL(register_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				OutputStream outPutStream = httpURLConnection.getOutputStream();
				BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outPutStream, "UTF-8"));
				String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
				bufferWriter.write(post_data);
				bufferWriter.flush();
				bufferWriter.close();
				outPutStream.close();
				InputStream inPutStream = httpURLConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inPutStream, "iso-8859-1"));
				String result = "";
				String line = "";

				while ((line = bufferedReader.readLine()) != null) {
					result += line;
				}
				bufferedReader.close();
				inPutStream.close();
				httpURLConnection.disconnect();
				return result;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("Login Status");
	}

	@Override
	protected void onPostExecute(String result) {

		if (result.contains("Login Success")) {
			Intent i = new Intent(context, Register.class);
			context.startActivity(i);
		} else {
			alertDialog.setMessage(result);
			alertDialog.show();
		}


	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
}
