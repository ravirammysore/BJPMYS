package in.appfocus.messageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import in.appfocus.messageit.models.Contact;
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

//this approach did not work (know issue with realm list (because filtering happens in a background thread)
//someone has also proposed a workaround (were he is doing things on UI thread)
//see link:
// https://github.com/realm/realm-java/issues/646
/*
public class ContactAdapter extends BaseAdapter implements Filterable {

    //RealmResults<Type> is similar to ArrayList, looks like we can use it at any place where ListView is used!

    //private final ArrayList<Contact> lstContacts;
    private final RealmList<Contact> lstContacts;

    private final LayoutInflater layoutInflater;

    private Context context;

    private RealmList<Contact> filteredList;

    private ContactFilter contactFilter;

    public ContactAdapter(Context context, RealmList<Contact> items) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstContacts = items;
        this.context = context;
        filteredList = items;

        getFilter();
    }

    @Override
    public int getCount() {
        //return lstContacts.size();
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        //return lstContacts.get(position);
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return position + 1;
        return position;
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

    @Override
    public Filter getFilter() {
        if (contactFilter == null) {
            contactFilter = new ContactFilter();
        }

        return contactFilter;
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                RealmList<Contact> tempList = new RealmList<>();

                // search content in friend list
                for (Contact contact : lstContacts) {
                    if (contact.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(contact);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = lstContacts.size();
                filterResults.values = lstContacts;
            }

            return filterResults;
        }

        */
/**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         *//*

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (RealmList<Contact>) results.values;
            notifyDataSetChanged();
        }
    }
}*/
