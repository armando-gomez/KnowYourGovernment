package com.armandogomez.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
	TextView officialOffice;
	TextView officialNameAndParty;

	ViewHolder(View view) {
		super(view);
		officialOffice = view.findViewById(R.id.officialOffice);
		officialNameAndParty = view.findViewById(R.id.officialNameAndParty);
	}
}
