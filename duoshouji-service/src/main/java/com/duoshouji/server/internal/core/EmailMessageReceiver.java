package com.duoshouji.server.internal.core;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MessageProxy;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.util.MobileNumber;

public class EmailMessageReceiver implements MessageProxyFactory {

	private static final String SMTP_SERVER_NAME = "smtp.163.com";
	private static final String FROM_ADDRESS = "testduoshouji@163.com";
	private static final String TO_ADDRESS = "testduoshouji@163.com";
	private static final Authenticator DEFAULT_AUTHENTICATOR =
			new Authenticator() {
				private PasswordAuthentication auth = new PasswordAuthentication("testduoshouji", "duo2016");
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return auth;
				}
	};
	@Override
	public MessageProxy getMessageProxy(MobileNumber mobileNumber) {
		return new InnerUserMessageProxy(mobileNumber);
	}

	private void sendTextMessage(String subject, String content) {	
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_SERVER_NAME);
		props.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(props, DEFAULT_AUTHENTICATOR);
		
		try {
		    // create a message
		    MimeMessage msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress(FROM_ADDRESS));
		    InternetAddress[] address = {new InternetAddress(TO_ADDRESS)};
		    msg.setRecipients(Message.RecipientType.TO, address);
		    msg.setSubject(subject);
		    msg.setSentDate(new Date());
		    msg.setText(content);
		    
		    Transport.send(msg);
		} catch (MessagingException mex) {
		    System.out.println("\n--Exception handling in msgsendsample.java");

		    mex.printStackTrace();
		    System.out.println();
		    Exception ex = mex;
		    do {
			if (ex instanceof SendFailedException) {
			    SendFailedException sfex = (SendFailedException)ex;
			    Address[] invalid = sfex.getInvalidAddresses();
			    if (invalid != null) {
				System.out.println("    ** Invalid Addresses");
				for (int i = 0; i < invalid.length; i++) 
				    System.out.println("         " + invalid[i]);
			    }
			    Address[] validUnsent = sfex.getValidUnsentAddresses();
			    if (validUnsent != null) {
				System.out.println("    ** ValidUnsent Addresses");
				for (int i = 0; i < validUnsent.length; i++) 
				    System.out.println("         "+validUnsent[i]);
			    }
			    Address[] validSent = sfex.getValidSentAddresses();
			    if (validSent != null) {
				System.out.println("    ** ValidSent Addresses");
				for (int i = 0; i < validSent.length; i++) 
				    System.out.println("         "+validSent[i]);
			    }
			}
			System.out.println();
			if (ex instanceof MessagingException)
			    ex = ((MessagingException)ex).getNextException();
			else
			    ex = null;
		    } while (ex != null);
		}
	}
	
	private class InnerUserMessageProxy implements MessageProxy {
		private MobileNumber mobileNumber;
		
		private InnerUserMessageProxy(MobileNumber mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
		@Override
		public void sendVerificationCode(VerificationCode verificationCode) {
			sendTextMessage(mobileNumber.toString(), verificationCode.toString());
		}
		
		@Override
		public void sendInvitationMessage(MobileNumber inviterMobileNumber) {
			sendTextMessage(mobileNumber.toString(), inviterMobileNumber + " invite you donwload duoshouji app.");
		}
	}
}
