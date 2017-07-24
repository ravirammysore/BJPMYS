package in.appfocus.messageit.database;

import java.util.UUID;

import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Customer;
import in.appfocus.messageit.models.Group;
import in.appfocus.messageit.models.Settings;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Ravi on 20/05/2017.
 */

public class MyInitialTransaction implements Realm.Transaction {
    @Override
    public void execute(Realm realm) {
        /*error - realm is already in write mode!
        realm.beginTransaction();
        realm.commitTransaction();
        since this is execute method of realm API, neither begin nor commit should be put, it would be an error.*/

        Customer customer = new Customer();
        customer.setUid("626a706d7973");
        customer.setSenderId("MYSORE");
        customer.setApiPin("Fa3a6e79a945dfbeb0369647865e13a1");
        customer.setRoute("4");

        realm.copyToRealm(customer);

        Settings settings = new Settings();
        settings.setSmsGatewayUrl("http://smsalertbox.com/api/sms.php");

        realm.copyToRealm(settings);

        Group testGroup = new Group("TEST_ID","Test Group","For testing purpose");
        RealmList contactList = new RealmList();
        contactList.add(new Contact("Ravi Airtel","9663977976","Some note"));
        contactList.add(new Contact("Ravi Jio","918217818636","Some note"));
        contactList.add(new Contact("Appu Airtel","8197973528","Some note"));
        contactList.add(new Contact("Appu Jio","918317401189","Some note"));
        contactList.add(new Contact("Pramodh","9972035630","Some note"));
        contactList.add(new Contact("Pramodh Jio","918618693616","Some note"));
        testGroup.setContacts(contactList);

        realm.copyToRealm(testGroup);

        for (long i=570001;i<=570005;i++){
            Group group = new Group();
            group.setId(UUID.randomUUID().toString());
            group.setName(String.valueOf(i));
            group.setNote("This is group for area-" + i);
            group.setContacts(new RealmList<Contact>());
            for(long j= 984512000,l=1; j<=984512005;j++,l++){
                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setMobileNo(String.valueOf(j));
                contact.setName("Person-"+l);
                contact.setNote("This is some note about the person!");
                group.getContacts().add(contact);
            }

            realm.copyToRealm(group);
        }
    }
}
