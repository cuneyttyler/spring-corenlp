package com.gsu.knowledgebase.service;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpObject;
import com.ecwid.mailchimp.method.v2_0.lists.Email;
import com.ecwid.mailchimp.method.v2_0.lists.SubscribeMethod;
import com.gsu.knowledgebase.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("mailChimpService")
public class MailChimpService {

    public static class MergeVars extends MailChimpObject {
        /**
		 * 
		 */
		private static final long serialVersionUID = 4346843152611164129L;
		@Field
        public String EMAIL, FNAME, LNAME;

        public MergeVars() {
        }

        public MergeVars(String email, String fname, String lname) {
            this.EMAIL = email;
            this.FNAME = fname;
            this.LNAME = lname;
        }
    }

    @Value("#{propertyConfigurer['mailchimp.api_key']}")
    private String API_KEY;

    @Value("#{propertyConfigurer['mailchimp.list_id']}")
    private String LIST_ID;

    public void subscribeUser(User user) throws IOException, MailChimpException {

        // reuse the same MailChimpClient object whenever possible
        MailChimpClient mailChimpClient = new MailChimpClient();

        // Subscribe a person
        SubscribeMethod subscribeMethod = new SubscribeMethod();
        subscribeMethod.apikey = API_KEY;
        subscribeMethod.id = LIST_ID;
        subscribeMethod.email = new Email();
        subscribeMethod.email.email = user.getEmail();
        subscribeMethod.double_optin = false;
        subscribeMethod.update_existing = true;
        subscribeMethod.merge_vars = new MergeVars(user.getEmail(), user.getFirstName(), user.getLastName());

        mailChimpClient.execute(subscribeMethod);
        mailChimpClient.close();
    }
}
