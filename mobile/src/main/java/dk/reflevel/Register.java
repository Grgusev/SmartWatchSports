package dk.reflevel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class Register extends AppCompatActivity {
	private EditText usernname, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		usernname = (EditText) findViewById(R.id.et_username);
		password = (EditText) findViewById(R.id.et_password);
	}


	public void OnReg(View view) {
		String str_username = usernname.getText().toString();
		String str_password = password.getText().toString();
		String type = "register";

		BackgroundWorker backgroundWorker = new BackgroundWorker(this);
		backgroundWorker.execute(type, str_username, str_password);
	}
}
