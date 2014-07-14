package com.ixaris.twitterlite.module.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ixaris.twitterlite.module.bd.Message;
import com.ixaris.twitterlite.module.bd.facade.MessagesAdminFacade;

@Service
@Transactional
public class MessageFunctions implements MessagesAdminFacade {

	private List<String> hash = new ArrayList<String>();
	private List<String> mention = new ArrayList<String>();

	@PersistenceContext
	private EntityManager em;

	private boolean validated;
	private String output = "";

	public String addMessage(String username, List<String> content) {

		// ---Refresh variables--------------//
		validated = true;
		mention.clear();
		hash.clear();
		output = "";

		if (username == null || username == "") {
			validated = false;
			output = "Username cannot be left empty";
		}

		if (content == null || content.size() < 1) {
			validated = false;
			output = "Content cannot be left empty";

		} else {

			for (int k = 0; k < content.size(); k++) {

				if (content.get(k) == null || content.get(k) == "") {
					validated = false;
					output = "Content cannot be left empty";
					break;
				}

				
				if (validated) {
					// --- Remove spaces between the hashtags/metions and the
					// words---//
					for (int i = 0; i < content.get(k).length(); i++) {
						if (content.get(k).charAt(i) == '#' || content.get(k).charAt(i) == '@') {
							
							if (content.get(k).charAt(i + 1) == ' ') {
								
								content.set(k,content.get(k).substring(0, i + 1) + content.get(k).substring(i + 2,content.get(k).length()));
							}
						}

					}
					

					// ---Populate the hash List with hash tagged words in the
					// content--------------//
					Pattern hashPattern = Pattern.compile("#(\\w+|\\W+)");
					Matcher mat = hashPattern.matcher(content.get(k));
					while (mat.find()) {
						hash.add(mat.group(1));
					}
					// ---Populate the mention List with mentioned users in the
					// content--------------//
					Pattern mentionPattern = Pattern.compile("@(\\w+|\\W+)");
					mat = mentionPattern.matcher(content.get(k));
					while (mat.find()) {
						mention.add(mat.group(1));
					}

					// ---Check if mentioned users exist--------------//
					for (int j = 0; j < mention.size(); j++) {

						Query q = em.createNamedQuery("message.byUsername");
						q.setParameter("Username", mention.get(j));

						List<MessageImpl> result = q.getResultList();
						if (result.size() != 0) {
							validated = true;
						}

						else {
							output = "The user " + mention.get(j)
									+ " does not exist";
							validated = false;
							break;
						}

					}
					// ---Persist the message in the database if everything is
					// correct--------------//
					if (validated == true) {

						MessageImpl m = new MessageImpl();

						m.setTimestamp(new Date().getTime());
						m.setContent(content.get(k));
						m.setUsername(username);
						m.setHashtags(hash);
						m.setMentions(mention);

						em.persist(m);
						output = "Message has been posted";
					}
				}
			}
		}
		return output;
	}
	
	

	public Message convertToMessage(MessageImpl m) {
		Message message = new Message();

		message.setUsername(m.getUsername());
		message.setContent(m.getContent());
		message.setDate(m.getTimestamp());
		return message;

	}

	
	
	@Override
	public List<Message> lookupMessages(int offset, int limit) {

		Message m = new Message();
		List<Message> messages = new ArrayList<Message>();

		if (offset < 0) {
			offset = 0;
		}

		if (limit < 0) {
			limit = 0;
		}

		// ---Execute the query---//
		Query q = em.createNamedQuery("message.all");
		q.setMaxResults(limit);
		q.setFirstResult(offset);
		List<MessageImpl> result = q.getResultList();

		// ---Loop through the casted list and display the messages---//
		for (int i = 0; i < result.size(); i++) {
			m = convertToMessage(result.get(i));
			messages.add(m);

		}

		return messages;
	}


	@Override
	public List<Message> lookupMessagesByHashtags(Set<String> hashtag,
			int offset, int limit) {
		Message m = new Message();
		List<Message> messages = new ArrayList<Message>();

		String output = "";
		List<MessageImpl> result = new ArrayList<MessageImpl>();

		Query q = em.createNamedQuery("message.byhashtag");
		q.setParameter("hashtag", hashtag);
		q.setParameter("tagCount", (long) hashtag.size());
		result = q.getResultList();

		for (int i = 0; i < result.size(); i++) {
			m = convertToMessage(result.get(i));
			messages.add(m);
		}

		return messages;
	}

	@Override
	public int countMessagesByuser(String username) {

		int output = 0;

		Query q = em.createNamedQuery("message.byUsername", MessageImpl.class);
		q.setParameter("Username", username);
		List<MessageImpl> result = q.getResultList();

		for (int i = 0; i < result.size(); i++) {
			output = i + 1;
		}

		return output;

	}

	@Override
	public List<Message> lookupMessagesByUser(String username, int offset,
			int limit) {

		Message m = new Message();
		List<Message> messages = new ArrayList<Message>();

		Query q = em.createNamedQuery("message.byUsername", MessageImpl.class);
		q.setParameter("Username", username);
		List<MessageImpl> result = q.getResultList();

		for (int i = 0; i < result.size(); i++) {
			m = convertToMessage(result.get(i));
			messages.add(m);
		}

		return messages;

	}

	@Override
	public List<Message> lookupMessagesMentioningUser(String username,
			int offset, int limit) {
		Message m = new Message();
		List<Message> messages = new ArrayList<Message>();

		Query q = em.createNamedQuery("message.bymention", MessageImpl.class);
		q.setParameter("Username", username);
		List<MessageImpl> result = q.getResultList();

		for (int i = 0; i < result.size(); i++) {
			m = convertToMessage(result.get(i));
			messages.add(m);
		}

		return messages;
	}


}
