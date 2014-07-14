package com.ixaris.twitterlite.webapp.ws;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ixaris.twitterlite.module.bd.Message;
import com.ixaris.twitterlite.module.bd.facade.MessagesAdminFacade;

@Controller
public class TwitterController {

	   @Autowired
	   private MessagesAdminFacade facade;
		
	   @RequestMapping(value = "/tweet", method = RequestMethod.POST)
	   @ResponseBody 
	   public String addmessage(@RequestParam("Username") String username, @RequestParam("Content") List<String> content ){
		   	return facade.addMessage(username, content);
		   
	   }
	   	   
	   @RequestMapping(value = "/messages", method = RequestMethod.GET)
	   @ResponseBody 
	   public List<Message> getMessages(@RequestParam("offset") int offset, @RequestParam("limit") int limit){
		   	return facade.lookupMessages(offset, limit);		   	
	   }
	   
	   
	   @RequestMapping(value = "/messages/hashtags", method = RequestMethod.GET)
	   @ResponseBody 
	   public List<Message> getMessageByHashtag(@RequestParam("hashtag") Set<String> hashtag, @RequestParam("offset") int offset, @RequestParam("limit") int limit){
		   	return facade.lookupMessagesByHashtags(hashtag, offset, limit);	
	   }
	   
	   @RequestMapping(value = "/messages/mentions", method = RequestMethod.GET)
	   @ResponseBody 
	   public List<Message> getMessageByMention(@RequestParam("Username") String Username, @RequestParam("offset") int offset, @RequestParam("limit") int limit){
		   	return facade.lookupMessagesMentioningUser(Username, offset, limit);	
	   }
	   
	   @RequestMapping(value = "/messages/users", method = RequestMethod.GET)
	   @ResponseBody 
	   public List<Message> getMessageByUser(@RequestParam("Username") String Username, @RequestParam("offset") int offset, @RequestParam("limit") int limit){
		   	return facade.lookupMessagesByUser(Username, offset, limit);
	   }
	   
	   @RequestMapping(value = "/messages/count", method = RequestMethod.GET)
	   @ResponseBody 
	   public int getMessageByUser(@RequestParam("Username") String Username){
		   	return facade.countMessagesByuser(Username);
	   }
	   

	
}
