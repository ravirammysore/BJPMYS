package in.appfocus.bjpmys.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.RealmResults;


/**
 * Created by Ravi on 22/05/2017.
 */

public class GroupAdapter extends BaseAdapter {
    private final RealmResults<Group> lstGroup;

    private final LayoutInflater layoutInflater;

    private Context context;

    public GroupAdapter(Context context, RealmResults<Group> items) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstGroup = items;
        this.context = context;
    }
    @Override
    public int getCount() {
        return lstGroup.size();
    }

    @Override
    public Object getItem(int position) {
        return lstGroup.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position + 1;
    }

    @Override
    public View getView(int position, View group_list_item, ViewGroup viewGroup) {
        if (group_list_item == null) {
            //This is how the adapter gets to know which template to use for showing an item in the collection - ravi
            group_list_item = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }
        TextView tvText1 = (TextView) group_list_item.findViewById(android.R.id.text1);
        TextView tvText2 = (TextView) group_list_item.findViewById(android.R.id.text2);


        tvText1.setText(lstGroup.get(position).getName());
        tvText2.setText(lstGroup.get(position).getNote());

        return group_list_item;
    }
}
