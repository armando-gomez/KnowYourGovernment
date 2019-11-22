package com.armandogomez.knowyourgovernment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity  {
	private ImageView fullScreenImage;
	private ImageView fullScreenPartyLogo;
	private TextView fullScreenOffice;
	private TextView fullScreenName;
	private TextView fullScreenParty;
	private TextView fullScreenLocText;
	private ConstraintLayout fullScreenLayout;

	private boolean networkStatusOnline = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);

		fullScreenImage = findViewById(R.id.fullScreenImage);
		fullScreenPartyLogo = findViewById(R.id.fullScreenPartyLogo);
		fullScreenLocText = findViewById(R.id.fullScreenLocText);
		fullScreenName = findViewById(R.id.fullScreenName);
		fullScreenOffice = findViewById(R.id.fullScreenOffice);
		fullScreenParty = findViewById(R.id.fullScreenParty);
		fullScreenLayout = findViewById(R.id.fullScreenLayout);

		doNetworkCheck();

		Intent intent = getIntent();
		if(intent.hasExtra("URL")) {
			String photoUrl = intent.getStringExtra("URL");
			if(!networkStatusOnline) {
				fullScreenImage.setImageResource(R.drawable.brokenimage);
			}
			else if(!photoUrl.isEmpty()) {
				Log.d("OfficialActivity", "loadImage: " + photoUrl);
				Picasso picasso = new Picasso.Builder(this).build();
				picasso.setLoggingEnabled(true);
				picasso.load(photoUrl)
						.placeholder(R.drawable.placeholder)
						.error(R.drawable.missing)
						.into(fullScreenImage);
			} else {
				fullScreenImage.setImageResource(R.drawable.missing);
			}
		}

		String office = intent.getStringExtra("OFFICE");
		String name = intent.getStringExtra("NAME");
		String party = intent.getStringExtra("PARTY");
		fullScreenOffice.setText(office);
		fullScreenName.setText(name);
		fullScreenParty.setText("(" + party + ")");

		if(party.contains("Dem") || party.contains("dem")) {
			int color = ContextCompat.getColor(this, R.color.democrat);
			fullScreenLayout.setBackgroundColor(color);
			fullScreenPartyLogo.setImageResource(R.drawable.dem_logo);
		} else if(party.contains("Rep") || party.contains("rep")) {
			int color = ContextCompat.getColor(this, R.color.republican);
			fullScreenLayout.setBackgroundColor(color);
			fullScreenPartyLogo.setImageResource(R.drawable.rep_logo);
		} else {
			int color = ContextCompat.getColor(this, R.color.otherParty);
			fullScreenLayout.setBackgroundColor(color);
			fullScreenPartyLogo.setVisibility(View.INVISIBLE);
		}

		if(savedInstanceState != null){
			fullScreenLocText.setText(savedInstanceState.getString("LOCATION"));
		} else {
			fullScreenLocText.setText(AsyncOfficialsLoader.getLocationText());
		}
	}

	private void doNetworkCheck() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			Toast.makeText(this, "Cannot Access ConnectivityManager", Toast.LENGTH_SHORT).show();
			return;
		}

		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			this.networkStatusOnline = true;
		} else {
			this.networkStatusOnline = false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("LOCATION", fullScreenLocText.getText().toString());
	}
}
