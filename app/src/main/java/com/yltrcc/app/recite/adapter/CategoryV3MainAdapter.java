package com.yltrcc.app.recite.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yltrcc.app.recite.R;
import com.yltrcc.app.recite.entity.QuestionV2ListEntity;
import com.yltrcc.app.recite.entity.QuestionV3ListEntity;

import java.util.List;

public class CategoryV3MainAdapter extends BaseAdapter {

	private Context context;
	private List<QuestionV3ListEntity> list;
	private int position = 0;
	private boolean islodingimg = false;
	Holder hold;

	public CategoryV3MainAdapter(Context context, List<QuestionV3ListEntity> list) {
		this.context = context;
		this.list = list;
	}

	public CategoryV3MainAdapter(Context context, List<QuestionV3ListEntity> list,
                                 boolean islodingimg) {
		this.context = context;
		this.list = list;
		this.islodingimg = islodingimg;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int arg0, View view, ViewGroup viewGroup) {

		if (view == null) {
			view = View.inflate(context, R.layout.item_classify_mainlist, null);
			hold = new Holder(view);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		hold.img.setVisibility(View.GONE);
		hold.txt.setText(list.get(arg0).getCategoryName());
		hold.layout.setBackgroundColor(0xFFEBEBEB);
		if (arg0 == position) {
			hold.layout.setBackgroundColor(0xFFFFFFFF);
		}
		return view;
	}

	public void setSelectItem(int position) {
		this.position = position;
	}

	public int getSelectItem() {
		return position;
	}

	private static class Holder {
		LinearLayout layout;
		ImageView img;
		TextView txt;

		public Holder(View view) {
			txt = (TextView) view.findViewById(R.id.mainitem_txt);
			img = (ImageView) view.findViewById(R.id.mainitem_img);
			layout = (LinearLayout) view.findViewById(R.id.mainitem_layout);
		}
	}
}
