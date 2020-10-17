package id.kasandra.retail;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import id.kasandra.retail.ExpandableListAdapter.Group.Type;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private static final int ALL_LIST_VIEW = 1;
	private static final int WEEK_LIST_VIEW = 0;

	private Activity activity;
	private Context context = null;
	ArrayList<Group> groups = new ArrayList<Group>();
    private SessionManager session;
    private Context mContext;
	private DecimalFormat df;
	
	public ExpandableListAdapter(Activity a, Context context){
		activity = a;
		this.context = context;
		/*DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator('.');
		df = new DecimalFormat("#,###", otherSymbols);*/
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int type = -1;
		if(groups.size() == 2 && groupPosition == 1){
			type = ALL_LIST_VIEW;
		} else {
			type = WEEK_LIST_VIEW;
		}
		
		return type;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).transactionItems.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
		
		//get the type of the group this child belongs
		Type viewType = groups.get(groupPosition).type;
		View view = convertView;
        session = new SessionManager(context);

		//if the type is future travel, use the future travel layout
		if(viewType == Type.WEEK) {
			if(view == null){
				view = LayoutInflater.from(context).inflate(R.layout.week_item, parent, false);

				WeekTransViewHolder holder = new WeekTransViewHolder();
				holder.title = (TextView) view.findViewById(R.id.weekTransTitle);
				holder.price = (TextView) view.findViewById(R.id.weekTransPrice);
				holder.date = (TextView) view.findViewById(R.id.weekTransDate);

				view.setTag(holder);
			}

			WeekTransViewHolder holder = (WeekTransViewHolder) view.getTag();

			final TransactionItem currentItem = (TransactionItem)getChild(groupPosition, childPosition);

            //String sPrice = "Rp"+String.valueOf(df.format(currentItem.getPrice()));//currentItem.getPrice();
			holder.title.setText("#"+currentItem.getTitle());
			holder.price.setText(currentItem.getPrice());
			holder.date.setText(currentItem.getDate());
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    session.setTransNO(Integer.parseInt(currentItem.getTitle()));
                    session.setTransID(Integer.parseInt(currentItem.getID()));
					session.setTransRealNo(Integer.parseInt(currentItem.getTitle()));
                    ((TransactionActivity)activity).showDetail();
                }
            });
		} else {
			//if the type is past, use the past travel layout
			if(view == null){
				view = LayoutInflater.from(context).inflate(R.layout.all_item, parent, false);

				AllTransViewHolder holder = new AllTransViewHolder();
				holder.title = (TextView) view.findViewById(R.id.allTransTitle);
				holder.price = (TextView) view.findViewById(R.id.allTransPrice);
				holder.date = (TextView) view.findViewById(R.id.allTransDate);

				view.setTag(holder);
			}

			AllTransViewHolder holder = (AllTransViewHolder) view.getTag();

			final TransactionItem currentItem = (TransactionItem)getChild(groupPosition, childPosition);

			holder.title.setText("#"+currentItem.getTitle());
			holder.price.setText(currentItem.getPrice());
			holder.date.setText(currentItem.getDate());
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    session.setTransNO(Integer.parseInt(currentItem.getTitle()));
                    session.setTransID(Integer.parseInt(currentItem.getID()));
					session.setTransRealNo(Integer.parseInt(currentItem.getTitle()));
                    ((TransactionActivity)activity).showDetail();
                }
            });
		}
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).transactionItems.size();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return groups.size();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

		View view = convertView;
		TextView text = null;
		ImageView image = null;

		if(view == null){
			view = LayoutInflater.from(context).inflate(R.layout.expandable_list_group_view, parent, false);
		}

		text = (TextView) view.findViewById(R.id.groupHeader);
		image = (ImageView) view.findViewById(R.id.expandableIcon);

		StringBuilder title = new StringBuilder();
		if(groupPosition == 0){
			//title.append(context.getString(R.string.week));
			title.append("Transaksi Terakhir");
			//title.append("1 Apr - 30 Apr");
		} else {
			title.append(context.getString(R.string.everything));
		}
		title.append(" (");
		title.append(groups.get(groupPosition).transactionItems.size());
		title.append(")");

		text.setText(title.toString());

		/*
		 * if this is not the first group (future travel) show the arrow image
		 * and change state if necessary
		 */
		if(groupPosition != 0){
			int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
			image.setImageResource(imageResourceId);

			image.setVisibility(View.VISIBLE);
		} else {
			image.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/*
	 * setup travel plans and past trips into groups
	 */
	public void setupTrips(ArrayList<TransactionItem> pastPlans, ArrayList<TransactionItem> futurePlans){
		groups.clear();

		if(pastPlans != null){
			Group g1 = new Group();
			g1.type = Type.WEEK;
			g1.transactionItems.clear();
			g1.transactionItems = new ArrayList<TransactionItem>(futurePlans);

			groups.add(g1);
		}
		if(futurePlans != null){
			Group g2 = new Group();
			g2.type = Type.ALL;
			g2.transactionItems.clear();
			g2.transactionItems = new ArrayList<TransactionItem>(pastPlans);
			
			groups.add(g2);
		}
		
		notifyDataSetChanged();
	}

	public void setupTrips2(ArrayList<TransactionItem> pastPlans){
		groups.clear();

		if(pastPlans != null){
			Group g1 = new Group();
			g1.type = Type.WEEK;
			g1.transactionItems.clear();
			g1.transactionItems = new ArrayList<TransactionItem>(pastPlans);

			groups.add(g1);
		}

		notifyDataSetChanged();
	}
	/*
	 * Holder for the Past view type
	 */
	class AllTransViewHolder {
		TextView title;
		TextView price;
		TextView date;
	}
	
	/*
	 * Holder for the Future view type
	 */
	class WeekTransViewHolder {
		TextView title;
		TextView price;
		TextView date;
	}
	
	/*
	 * Wrapper for each group that contains the
	 * list elements and the type of travel.
	 */
	public static class Group {
		public enum Type {
			ALL, WEEK;
		};
		
		public Type type;
		ArrayList<TransactionItem> transactionItems = new ArrayList<TransactionItem>();
	}
}