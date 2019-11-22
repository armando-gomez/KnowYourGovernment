package com.armandogomez.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GovernmentOfficialAdapter extends RecyclerView.Adapter<ViewHolder> {
	private List<GovernmentOfficial> officialList;
	private MainActivity mainActivity;

	GovernmentOfficialAdapter(List<GovernmentOfficial> _officialList, MainActivity _mainActivity) {
		this.officialList = _officialList;
		this.mainActivity = _mainActivity;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.gov_official_row, parent, false);
		itemView.setOnClickListener(mainActivity);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
		GovernmentOfficial official = officialList.get(pos);
		String nameAndParty = official.getName() + " (" + official.getParty() + ")";

		holder.officialOffice.setText(official.getOffice());
		holder.officialNameAndParty.setText((nameAndParty));
	}

	@Override
	public int getItemCount() {return officialList.size();}

}
