package BackEndProject.BackEndProject.service;

import BackEndProject.BackEndProject.email.EmailObject;

public interface EmailService {
	
	void sendSimpleMessage(EmailObject object);
	void sendTemplateMessage(EmailObject object) throws Exception;
	void sendMessageWithAttachment(EmailObject object, String pathToAttachment) throws Exception;

}


