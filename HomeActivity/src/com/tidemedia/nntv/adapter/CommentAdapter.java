package com.tidemedia.nntv.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.CommentListResponseModel.Comment;
import com.tidemedia.nntv.util.DateUtil;

public class CommentAdapter extends BaseAdapter {
	private List<Comment> list;
	private LayoutInflater inflater;

	public CommentAdapter(Context context, List<Comment> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Comment getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comment_list_item, parent, false);
			holder = new ViewHolder();
			holder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
			holder.userName = (TextView) convertView.findViewById(R.id.userName);
			holder.commentContent = (TextView) convertView.findViewById(R.id.commentContent);
			holder.commentTime = (TextView) convertView.findViewById(R.id.commentTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Comment comment = list.get(position);
		if (comment != null) {
			holder.userName.setText(comment.username);
			holder.commentContent.setText(UnicodeToString(comment.content));
			holder.commentTime.setText(DateUtil.toTimebackString(comment.time));
		} else {
			holder.userIcon.setImageResource(R.drawable.ic_nntv_launcher);
			holder.userName.setText(null);
			holder.commentContent.setText(null);
			holder.commentTime.setText(null);
		}
		return convertView;
	}
	
	private static String UnicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");    
        }
        return str;
    }

	class ViewHolder {
		ImageView userIcon;
		TextView userName;
		TextView commentContent;
		TextView commentTime;
	}
}
