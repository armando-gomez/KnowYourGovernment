package com.armandogomez.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private static int MY_LOCATION_REQUEST_CODE_ID = 329;

	private Menu menu;
	private RecyclerView recyclerView;
	private List<GovernmentOfficial> officialList = new ArrayList<>();
	private GovernmentOfficialAdapter governmentOfficialAdapter;

	private LocationManager locationManager;
	private Criteria criteria;

	private String addressInput = "";
	private String locationString;
	private Address address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);

		recyclerView = findViewById(R.id.recycler);
		governmentOfficialAdapter = new GovernmentOfficialAdapter(officialList, this);

		recyclerView.setAdapter(governmentOfficialAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
			String[] blah = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
			ActivityCompat.requestPermissions(this, blah, MY_LOCATION_REQUEST_CODE_ID);
		} else {
			setLocation();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:
				openAboutActivity();
				return true;
			case R.id.menu_search:
				openSetLocation();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void openSetLocation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter a City, State, or a Zip Code:");
		final EditText input = new EditText(this);
		input.setGravity(Gravity.CENTER_HORIZONTAL);
		builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				addressInput = input.getText().toString();
				if (!addressInput.isEmpty()) {
					getOfficials(addressInput);
				}
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();
	}

	public void openAboutActivity() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int pos = recyclerView.getChildLayoutPosition(v);
		GovernmentOfficial official = officialList.get(pos);
		Intent i = new Intent(Intent.ACTION_VIEW);
		Toast.makeText(this, official.getName(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
			if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
					grantResults[0] == PERMISSION_GRANTED) {
				setLocation();
				return;
			}
		}
	}

	@SuppressLint("MissingPermission")
	private void setLocation() {
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			try {
				this.address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getOfficials(this.address.getPostalCode());

	}

	private void getOfficials(String address) {
		new AsyncOfficialsLoader(MainActivity.this).execute(address);
	}

	public void updateOfficialList(List<GovernmentOfficial> _officialList) {
		if (_officialList != null) {
			for (GovernmentOfficial g : _officialList) {
				this.officialList.add(g);
			}
		}
		updateAdapter();
	}

	private void updateAdapter() {
		TextView textView = findViewById(R.id.locText);
		textView.setText(AsyncOfficialsLoader.getLocationText());
		governmentOfficialAdapter.notifyDataSetChanged();
	}
}
