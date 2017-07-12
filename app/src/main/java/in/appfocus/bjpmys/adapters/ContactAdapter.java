package in.appfocus.bjpmys.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import in.appfocus.bjpmys.models.Contact;
import io.realm.RealmList;

/**
 * Created by Ravi on 22/05/2017.
 */

public class ContactAdapter extends BaseAdapter {

    //RealmResults<Type> is similar to ArrayList, looks like we can use it at any place where ListView is used!

    //private final ArrayList<Contact> lstContacts;
    private final RealmList<Contact> lstContacts;

    private final LayoutInflater layoutInflater;

    private Context context;

    public ContactAdapter(Context context, RealmList<Contact> items) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstContacts = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lstContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return lstContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position + 1;
    }

    @Override
    public View getView(int position, View Contact_list_item, ViewGroup parent) {

        if (Contact_list_item == null) {
            //This is how the adapter gets to know which template to use for showing an item in the collection - ravi
            Contact_list_item = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }
        TextView tvText1 = (TextView) Contact_list_item.findViewById(android.R.id.text1);
        TextView tvText2 = (TextView) Contact_list_item.findViewById(android.R.id.text2);


        tvText1.setText(lstContacts.get(position).getName());
        tvText2.setText(lstContacts.get(position).getMobileNo());

        return Contact_list_item;
    }
}