package com.yltrcc.app.recite.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yltrcc.app.recite.R;
import com.yltrcc.app.recite.entity.QuestionEntity;

import java.util.List;

public class ClassifyMoreAdapter extends BaseAdapter {

	private Context context;
	private List<QuestionEntity> text_list;
	private int position = 0;
	Holder hold;

	public ClassifyMoreAdapter(Context context, List<QuestionEntity> text_list) {
		this.context = context;
		this.text_list = text_list;
	}

	public int getCount() {
		return text_list.size();
	}

	public Object getItem(int position) {
		return text_list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int arg0, View view, ViewGroup viewGroup) {

		if (view == null) {
			view = View.inflate(context, R.layout.item_classify_morelist, null);
			hold = new Holder(view);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		hold.txt.setText(text_list.get(arg0).getArticleTitle());
		hold.txt.setTextColor(0xFF666666);
		if (arg0 == position) {
			hold.txt.setTextColor(0xFFFF8C00);
		}
		return view;
	}

	public void setSelectItem(int position) {
		this.position = position;
	}

	private static class Holder {
		TextView txt;

		public Holder(View view) {
			txt = (TextView) view.findViewById(R.id.moreitem_txt);
		}
	}
}
