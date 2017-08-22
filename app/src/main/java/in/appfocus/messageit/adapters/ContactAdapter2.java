package in.appfocus.messageit.adapters;

/**
 * Created by Ravi on 22/08/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import in.appfocus.messageit.models.Contact;
import io.realm.RealmList;

/**
 * Created by Ravi on 22/05/2017.
 */

//********************* VVIP ***************************************
/*I created this adapter to support searching the contacts!
* In case this creates any issue, i can switch back to regular adapter
* How is this adapter different from normal adapter?
*
* Normal adapter does not implement Filterable interface (which has getFilter())
*
* But why did i create a new adapter, and did not modify the old one?
* Realm as of now (22/08/2017) does not support filtering in background thread
* Hence i read a post which suggests a workaround, where filtering is done on foreground*/

public class ContactAdapter2 extends BaseAdapter implements Filterable {

    private final RealmList<Contact> lstContacts;

    private final LayoutInflater layoutInflater;

    private Context context;

    private RealmList<Contact> filteredList;

    private ContactFilter contactFilter;

    public ContactAdapter2(Context context, RealmList<Contact> items) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lstContacts = items;
        this.context = context;
        filteredList = items;
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


       /* tvText1.setText(lstContacts.get(position).getName());
        tvText2.setText(lstContacts.get(position).getMobileNo());*/

        tvText1.setText(filteredList.get(position).getName());
        tvText2.setText(filteredList.get(position).getMobileNo());

        return Contact_list_item;
    }

    @Override
    public Filter getFilter() {
        if (contactFilter == null) {
            contactFilter = new ContactFilter();
        }

        return contactFilter;
    }

    //Nested class, for easy access of container class members
    private class ContactFilter extends Filter {

        //This method runs in background thread and publishes result to foreground thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            /*RealmList as of now (22/08/2017) does not support access in background thread
            * So we don't do any filtering here*/

            //as per the workaround suggested here
            //https://github.com/realm/realm-java/issues/646
            filterResults.count=1;
            return filterResults;
        }
        /* This function receives results from background thread*/
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //all this was to happen in performFiltering method, as per android framework!
            if (constraint!=null && constraint.length()>0) {
                RealmList<Contact> tempList = new RealmList<>();

                // search in contact list for matching
                for (Contact contact : lstContacts) {
                    if (contact.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        //create a temp lst of all matching contacts
                        tempList.add(contact);
                    }
                }

                //that temp list will be our list to display
                results.count = tempList.size();
                results.values = tempList;
            }
            else {
                //when no constraint is given, our original list itself will be the result
                results.count = lstContacts.size();
                results.values = lstContacts;
            }
            filteredList = (RealmList<Contact>) results.values;
            //this is standard stuff as per android listview filtering process
            notifyDataSetChanged();
        }
    }
}

