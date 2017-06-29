package in.appfocus.bjpmys.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Ravi on 28/06/2017.
 */

public class HistoryAdapter extends BaseAdapter {
    private final RealmResults<History> lstHistory;

    private final LayoutInflater layoutInflater;

    private Context context;

    public HistoryAdapter(Context context, RealmResults<History> items) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstHistory = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lstHistory.size();
    }

    @Override
    public Object getItem(int position) {
        return lstHistory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position + 1;
    }

    @Override
    public View getView(int position, View History_list_item, ViewGroup parent) {

        if (History_list_item == null) {
            //This is how the adapter gets to know which template to use for showing an item in the collection - ravi
            History_list_item = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }
        TextView tvText1 = (TextView) History_list_item.findViewById(android.R.id.text1);
        TextView tvText2 = (TextView) History_list_item.findViewById(android.R.id.text2);


        tvText1.setText(lstHistory.get(position).getMessage());
        tvText2.setText(lstHistory.get(position).getGroup()+ "     " + lstHistory.get(position).getDate());

        return History_list_item;
    }
}
