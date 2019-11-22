package com.armandogomez.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

public class GovernmentOfficialActivity extends AppCompatActivity {
	private GovernmentOfficial official;

	private ScrollView officialScrollView;
	private TextView officialLocText;
	private TextView officialOffice;
	private TextView officialName;
	private TextView officialParty;
	private ImageView officialPartyLogo;
	private TextView officialAddress;
	private TextView officialPhone;
	private TextView officialEmail;
	private TextView officialWebsite;
	private ImageView googlePlus;
	private ImageView facebook;
	private ImageView twitter;
	private ImageView youtube;
	private ImageView officialPhoto;

	private boolean networkStatusOnline = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_official);
		officialScrollView = findViewById(R.id.officialScrollView);
		officialLocText = findViewById(R.id.officialLocText);
		officialOffice = findViewById(R.id.officialOffice);
		officialName = findViewById(R.id.officialName);
		officialParty = findViewById(R.id.officialParty);
		officialPartyLogo = findViewById(R.id.officialPartyLogo);
		officialAddress = findViewById(R.id.officialAddress);
		officialPhone = findViewById(R.id.officialPhone);
		officialEmail = findViewById(R.id.officialEmail);
		officialWebsite = findViewById(R.id.officialWebsite);
		officialPhoto = findViewById(R.id.officialPhoto);

		googlePlus = findViewById(R.id.googlePlus);
		facebook = findViewById(R.id.facebook);
		twitter = findViewById(R.id.twitter);
		youtube = findViewById(R.id.youtube);

		Intent intent = getIntent();
		if (intent.hasExtra("INDEX")) {
			int index = intent.getIntExtra("INDEX", -1);
			if (index != -1) {
				official = AsyncOfficialsLoader.getOfficial(index);
			}
		}
		doNetworkCheck();
		if(savedInstanceState != null){
			this.officialLocText.setText(savedInstanceState.getString("LOCATION"));
		} else {
			officialLocText.setText(AsyncOfficialsLoader.getLocationText());
		}
		setOfficial();
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

	private void setOfficial() {
		officialOffice.setText(official.getOffice());
		officialName.setText(official.getName());
		String party = official.getParty();
		officialParty.setText("(" + party + ")");
		doNetworkCheck();
		String photoUrl = official.getPhotoUrl();

		if (!networkStatusOnline) {
			officialPhoto.setImageResource(R.drawable.brokenimage);
		} else if (!photoUrl.isEmpty()) {
			Log.d("OfficialActivity", "loadImage: " + photoUrl);
			Picasso picasso = new Picasso.Builder(this).build();
			picasso.setLoggingEnabled(true);
			picasso.load(photoUrl)
					.placeholder(R.drawable.placeholder)
					.error(R.drawable.missing)
					.into(officialPhoto);
		} else {
			officialPhoto.setImageResource(R.drawable.missing);
		}

		if (party.contains("Dem") || party.contains("dem")) {
			int color = ContextCompat.getColor(this, R.color.democrat);
			officialScrollView.setBackgroundColor(color);
			officialPartyLogo.setImageResource(R.drawable.dem_logo);
		} else if (party.contains("Rep") || party.contains("rep")) {
			int color = ContextCompat.getColor(this, R.color.republican);
			officialScrollView.setBackgroundColor(color);
			officialPartyLogo.setImageResource(R.drawable.rep_logo);
		} else {
			int color = ContextCompat.getColor(this, R.color.otherParty);
			officialScrollView.setBackgroundColor(color);
			officialPartyLogo.setVisibility(View.INVISIBLE);
		}


		String address = official.getAddress();
		if (!address.isEmpty()) {
			officialAddress.setText(address);
		} else {
			findViewById(R.id.addressLayout).setVisibility(View.GONE);
		}
		String phone = official.getPhone();
		if (!phone.isEmpty()) {
			officialPhone.setText(phone);
		} else {
			findViewById(R.id.phoneLayout).setVisibility(View.GONE);
		}

		String email = official.getEmail();
		if (!email.isEmpty()) {
			officialEmail.setText(email);
		} else {
			findViewById(R.id.emailLayout).setVisibility(View.GONE);
		}

		String website = official.getWebsite();
		if (!website.isEmpty()) {
			officialWebsite.setText(website);
		} else {
			findViewById(R.id.websiteLayout).setVisibility(View.GONE);
		}

		String googlePlusId = official.getGooglePlus();
		if (googlePlusId.isEmpty()) {
			googlePlus.setVisibility(View.GONE);
		}

		String facebookId = official.getFacebook();
		if (facebookId.isEmpty()) {
			facebook.setVisibility(View.GONE);
		}

		String twitterId = official.getTwitter();
		if (twitterId.isEmpty()) {
			twitter.setVisibility(View.GONE);
		}

		String youtubeId = official.getYoutube();
		if (youtubeId.isEmpty()) {
			youtube.setVisibility(View.GONE);
		}
	}

	public void photoClick(View v) {
		String photoUrl = official.getPhotoUrl();
		if (!photoUrl.isEmpty()) {
			Intent intent = new Intent(this, FullScreenImageActivity.class);
			intent.putExtra("URL", photoUrl);
			intent.putExtra("OFFICE", official.getOffice());
			intent.putExtra("NAME", official.getName());
			intent.putExtra("PARTY", official.getParty());
			startActivity(intent);
		}
	}

	public void partyClick(View v) {
		doNetworkCheck();
		if (networkStatusOnline) {
			String party = official.getParty();
			String partyUrl = "";
			if (party.contains("Dem") || party.contains("dem")) {
				partyUrl = "https://democrats.org/";
			} else if (party.contains("Rep") || party.contains("rep")) {
				partyUrl = "https://www.gop.com/";
			} else {
				return;
			}
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(partyUrl));
			startActivity(i);
		}
	}

	public void googleClick(View v) {
		doNetworkCheck();
		if (networkStatusOnline) {
			String id = official.getGooglePlus();
			Intent intent = null;
			try {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName("com.google.android.apps.plus",
						"com.google.android.apps.plus.phone.UrlGatewayActivity");
				intent.putExtra("customAppUri", id);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + id)));
			}
		}
	}

	public void facebookClick(View v) {
		doNetworkCheck();
		if (networkStatusOnline) {
			String id = official.getFacebook();
			String FACEBOOK_URL = "https://www.facebook.com/" + id;
			String urlToUse;
			PackageManager packageManager = getPackageManager();
			try {
				int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
				if (versionCode >= 3002850) { //newer versions of fb app
					urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
				} else { //older versions of fb app
					urlToUse = "fb://page/" + id;
				}
			} catch (PackageManager.NameNotFoundException e) {
				urlToUse = FACEBOOK_URL; //normal web url
			}
			Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
			facebookIntent.setData(Uri.parse(urlToUse));
			startActivity(facebookIntent);
		}
	}

	public void twitterClick(View v) {
		doNetworkCheck();
		if (networkStatusOnline) {
			Intent intent = null;
			String id = official.getTwitter();
			try {
				// get the Twitter app if possible
				getPackageManager().getPackageInfo("com.twitter.android", 0);
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + id));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			} catch (Exception e) {
				// no Twitter app, revert to browser
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + id));
			}
			startActivity(intent);
		}
	}

	public void youtubeClick(View v) {
		doNetworkCheck();
		if (networkStatusOnline) {
			String id = official.getYoutube();
			Intent intent = null;
			try {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setPackage("com.google.android.youtube");
				intent.setData(Uri.parse("https://www.youtube.com/" + id));
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://www.youtube.com/" + id)));
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("LOCATION", officialLocText.getText().toString());
	}
}
