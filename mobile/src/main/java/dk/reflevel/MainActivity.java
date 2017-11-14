package dk.reflevel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
	/*
	private EditText						usernameET, passwordET;
	private Button							loginBtn;

	private CheckBox						saveLoginCheckBox;
	private SharedPreferences				loginPreferences;
	private SharedPreferences.Editor		loginPrefsEditor;

	private Boolean							saveLogin;
	private String							username, password;
*/

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		loginBtn = (Button) findViewById(R.id.btnLogin);
		usernameET = (EditText) findViewById(R.id.etEmail);
		passwordET = (EditText) findViewById(R.id.etPassword);
		saveLoginCheckBox = (CheckBox) findViewById(R.id.remember_me);
		loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
		loginPrefsEditor = loginPreferences.edit();

		saveLogin = loginPreferences.getBoolean("saveLogin", false);
		if (saveLogin == true) {
			usernameET.setText(loginPreferences.getString("user_name", ""));
			passwordET.setText(loginPreferences.getString("password", ""));
			saveLoginCheckBox.setChecked(true);

			Intent i = new Intent(this, Register.class);
			this.startActivity(i);
		}
		*/
	}


	/*
	public void OnLogin(View view) {
		username = usernameET.getText().toString();
		password = passwordET.getText().toString();
		String type = "login";

		BackgroundWorker backgroundWorker = new BackgroundWorker(this);
		backgroundWorker.execute(type, username, password);

		if (type == "login") {
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(usernameET.getWindowToken(), 0);

			username = usernameET.getText().toString();
			password = passwordET.getText().toString();

			if (saveLoginCheckBox.isChecked()) {
				loginPrefsEditor.putBoolean("saveLogin", true);
				loginPrefsEditor.putString("user_name", username);
				loginPrefsEditor.putString("password", password);
				loginPrefsEditor.commit();
			} else {
				loginPrefsEditor.clear();
				loginPrefsEditor.commit();
			}
		}
	}
	*/

	/*
	public void OpenReg(View view) {
		startActivity(new Intent(this, Register.class));
	}
	*/
}
