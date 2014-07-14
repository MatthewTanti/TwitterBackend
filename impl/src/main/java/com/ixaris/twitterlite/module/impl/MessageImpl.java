package com.ixaris.twitterlite.module.impl;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name ="twit_message")
@NamedQueries({
	
@NamedQuery(name = "message.byUsername", query = "SELECT m FROM MessageImpl m WHERE username =:Username"),
@NamedQuery(name = "message.all", query = "SELECT m FROM MessageImpl m ORDER BY m.timestamp DESC"),
@NamedQuery(name = "message.bymention", query = "SELECT m FROM MessageImpl m WHERE :Username in elements (m.mentions)"),
@NamedQuery(name = "message.byhashtag", query = "select m from MessageImpl m join m.hashtags h where h in :hashtag group by m.id having count(m.id) = :tagCount")
})
public class MessageImpl {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "timestamp")
	private long timestamp;
	
    @ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="twit_hashtag", joinColumns=@JoinColumn(name="messageId"))
	@Column(name="hashtag")
	private List<String> hashtags;
    
    @ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="twit_mention", joinColumns=@JoinColumn(name="messageId"))
	@Column(name="mentionedUsername")
	private List<String> mentions;
	
	

	public MessageImpl(String username, List<String> content, long timestamp, List<String> hashtags, List<String> mentions) {
		this.username = username;
		this.content = content.get(0);
		this.timestamp = timestamp;
		this.hashtags = hashtags;
		this.mentions = mentions;
	}

	public MessageImpl() {
	}
	
	public long getId() {
		return id;
	}
	
	public void seId(Integer messageId) {
		this.id = messageId;
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String string) {
		this.content = string;
	}

	@Column(name = "timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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



	public void setMentions(List<String> mention) {
		this.mentions =  mention;
	}


 
	


}
