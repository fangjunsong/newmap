package com.example.gusor;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gusor.entity.Contentinfo;

public class MyAdapter extends BaseAdapter {

	Context context;
	List<Contentinfo> contents;

	public MyAdapter(Context context, List<Contentinfo> contents) {
		super();
		this.context = context;
		this.contents = contents;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return contents.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@SuppressWarnings("null")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		ImageView image;
		TextView tv_name;
		TextView tv_type;
		TextView tv_address;
		TextView tv_money;
		TextView tv_meter;
		if(convertView==null){
		     convertView=LayoutInflater.from(context).inflate(R.layout.list_item,null);
		     image=(ImageView) convertView.findViewById(R.id.item_image);
		     tv_name=(TextView) convertView.findViewById(R.id.item_name);
		     tv_type=(TextView) convertView.findViewById(R.id.item_type);
		     tv_address=(TextView) convertView.findViewById(R.id.item_address);
		     tv_meter=(TextView) convertView.findViewById(R.id.item_meter);
		     tv_money=(TextView) convertView.findViewById(R.id.item_money);
		     viewholder=new ViewHolder(image, tv_name, tv_type, tv_address, tv_money, tv_meter);
		     convertView.setTag(viewholder);
		}else{
			viewholder=(ViewHolder)convertView.getTag();
			 
		}
		image=viewholder.image;
		tv_name=viewholder.tv_name;
		tv_type=viewholder.tv_type;
		tv_address=viewholder.tv_address;
		tv_money=viewholder.tv_money;
		tv_meter=viewholder.tv_meter;
		
        
	       
 
		image.setBackgroundResource(contents.get(position).getImage());
		tv_name.setText(contents.get(position).getName());
		tv_type.setText(contents.get(position).getType());
		tv_address.setText(contents.get(position).getAddress());
		tv_money.setText(contents.get(position).getMoney());
		tv_meter.setText(contents.get(position).getMeter());
		return convertView;
	}
	
	public class ViewHolder{
		private ImageView image;
		private TextView tv_name;
		private TextView tv_type;
		private TextView tv_address;
		private TextView tv_money;
		private TextView tv_meter;
		public ViewHolder(ImageView image, TextView tv_name, TextView tv_type,
				TextView tv_address, TextView tv_money, TextView tv_meter) {
			super();
			this.image = image;
			this.tv_name = tv_name;
			this.tv_type = tv_type;
			this.tv_address = tv_address;
			this.tv_money = tv_money;
			this.tv_meter = tv_meter;
		}
		
	}

}
