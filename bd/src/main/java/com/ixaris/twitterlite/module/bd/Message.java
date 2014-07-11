package com.ixaris.twitterlite.module.bd;

import java.util.ArrayList;
import java.util.List;


public class Message {

	private String Username;
	private String Content;
	private long Timestamp;
	private List<String> hashtags = new ArrayList<String>();
	private List<String> mentions = new ArrayList<String>();
	
	 public Message(String username, String content, long l,
			List<String> list, List<String> list2) {
		super();
		Username = username;
		Content = content;
		Timestamp = l;
		this.hashtags = list;
		this.mentions = list2;
	}	
	
	public Message(){} 
	
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public long getDate() {
		return Timestamp;
	}
	public void setDate(long timestamp) {
		Timestamp = timestamp;
	}
	public List<String> getHashtags() {
		return hashtags;
	}
	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}
	public List<String> getMentions() {
		return mentions;
	}
	public void setMentions(List<String> mentions) {
		this.mentions = mentions;
	}
	
	

}
