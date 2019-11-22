package com.armandogomez.knowyourgovernment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AboutActivity extends AppCompatActivity {
	private final String apiUrl = "https://developers.google.com/civic-information/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		try {
			Drawable d = Drawable.createFromStream(getAssets().open("about.png"), null);
			View v = findViewById(R.id.aboutLayout);
			v.setBackground(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setHyperLink();
	}

	private void setHyperLink() {
		TextView textView = (TextView) findViewById(R.id.apiText);
		Linkify.TransformFilter filter = new Linkify.TransformFilter() {
			@Override
			public String transformUrl(Matcher match, String url) {
				return apiUrl;
			}
		};
		Linkify.addLinks(textView, Pattern.compile("^Google Civic Information API"), null, null, filter);
	}
}
