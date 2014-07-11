package com.ixaris.twitterlite.module.bd.facade;

import java.util.List;
import java.util.Set;

import com.ixaris.twitterlite.module.bd.Message;

/**
 * @author matthew.tanti
 *
 */
public interface MessagesAdminFacade {
	
	
	/**
	 * @param Username
	 * @param content
	 * @return
	 */
	public String addMessage(String Username, List<String> content);
	
	/**
	 * @param hashtag
	 * @param offset
	 * @param limit
	 * @return
	 */
	public String lookupMessagesByHashtags(Set<String> hashtag, int offset, int limit );
	
	/**
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Message> lookupMessages(int offset, int limit);
	
	/**
	 * @param Username
	 * @return
	 */
	public int countMessagesByuser(String Username );
	
	/**
	 * @param Username
	 * @param offset
	 * @param limit
	 * @return
	 */
	public String lookupMessagesByUser(String Username, int offset, int limit);
	
	/**
	 * @param Username
	 * @param offset
	 * @param limit
	 * @return
	 */
	public String lookupMessagesMentioningUser(String Username, int offset, int limit);
	

}
