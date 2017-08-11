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

        customer.setUid("534d53415050");
        customer.setSenderId("SMSAPP");
        customer.setApiPin("cf0cda92d0ef2bb268f527a8d47ab782");
        customer.setRoute("4");
        customer.setAppTitle("MSGit");
        customer.setAppSubTitle("On time.Every Time.");

        realm.copyToRealm(customer);

        Settings settings = new Settings();

        /*settings.setSmsGatewayUrl("http://smsalertbox.com/api/sms.php");
        settings.setSmsBalanceUrl("http://smsalertbox.com/api/balance.php");*/

        settings.setSmsGatewayUrl("http://apps.digitali.in/api/sms.php");
        settings.setSmsBalanceUrl("http://apps.digitali.in/api/balance.php");
        settings.setSmsDeliveryReportUrl("http://apps.digitali.in/api/dlr.php");

        realm.copyToRealm(settings);

        Group testGroup1 = new Group("TEST_ID1","Test Group","For testing purpose");
        RealmList contactList = new RealmList();
        contactList.add(new Contact("Ravi Airtel","9663977976","Some note"));
        contactList.add(new Contact("Ravi Jio","918217818636","Some note"));
        contactList.add(new Contact("Appu Airtel","8197973528","Some note"));
        contactList.add(new Contact("Appu Jio","918317401189","Some note"));
        contactList.add(new Contact("Pramodh","9972035630","Some note"));
        contactList.add(new Contact("Pramodh Jio","918618693616","Some note"));
        testGroup1.setContacts(contactList);

        realm.copyToRealm(testGroup1);

        Group testGroup2 = new Group("TEST_ID2","Ravi Jio Only","For Testing");
        RealmList contactList2 = new RealmList();
        contactList2.add(new Contact("Ravi Jio","918217818636","Some note"));
        testGroup2.setContacts(contactList2);

        realm.copyToRealm(testGroup2);

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
