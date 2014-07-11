import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.verification.NeverWantedButInvoked;

import com.ixaris.twitterlite.module.bd.facade.MessagesAdminFacade;
import com.ixaris.twitterlite.module.impl.MessageFunctions;
import com.ixaris.twitterlite.module.impl.MessageImpl;



public class MessageFunctionsTest {
	
	@Mock 
	private EntityManager em;
	
	@Mock
	private Query query;
	
	
	private static MessageFunctions MessageFunctions;
	private MessagesAdminFacade facade;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		MessageFunctions = CommonsTestUtils.initSingletonClassWithMocks(this, MessageFunctions.class);	
	}


	
	@Test
	public void addMessageSeccess(){
		
		String Username = "Matthew";
		List<String>Content = new ArrayList<String>();
		Content.add("Testing post #test @mention");
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Hashtags.add("test");
		Mentions.add("mention");
		
		MessageImpl m = new MessageImpl(Username, Content, Timestamp, Hashtags, Mentions);
				

		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(Mentions);
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em).persist(argument.capture());
		
	    Assert.assertEquals(Username, argument.getValue().getUsername());
	    Assert.assertEquals(Content.get(0),  argument.getValue().getContent());
	    Assert.assertEquals(Hashtags, argument.getValue().getHashtags());
	    Assert.assertEquals(Mentions, argument.getValue().getMentions());
		
	}
	
	
	@Test
	public void addMessageMultipleContents(){
		
		String Username = "Matthew";
		List<String>Content = new ArrayList<String>();
		Content.add("Testing post #test @mention");
		Content.add("Second Post");
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Hashtags.add("test");
		Mentions.add("mention");
		
		MessageImpl m = new MessageImpl(Username, Content, Timestamp, Hashtags, Mentions);
				

		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(Mentions);
		
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em, Mockito.times(2)).persist(argument.capture());

		
	    List<MessageImpl> messages = argument.getAllValues();
	    Assert.assertEquals(Username, argument.getValue().getUsername());
	    for(int i = 0; i < Content.size(); i++){
	    	Assert.assertEquals(Content.get(i),  messages.get(i).getContent());
	    }
	    Assert.assertEquals(Hashtags, argument.getValue().getHashtags());
	    Assert.assertEquals(Mentions, argument.getValue().getMentions());
	    	
	}
	
	
	@Test
	public void addMessageMultipleContentsWithOneNull(){
		
		String Username = "Matthew";
		List<String>Content = new ArrayList<String>();
		Content.add("Testing post #test @mention");
		Content.add(null);
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Hashtags.add("test");
		Mentions.add("mention");
		
		MessageImpl m = new MessageImpl(Username, Content, Timestamp, Hashtags, Mentions);
				

		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(Mentions);
		
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em, Mockito.times(1)).persist(argument.capture());

		
	    List<MessageImpl> messages = argument.getAllValues();
	    Assert.assertEquals(Username, argument.getValue().getUsername());
	   
	    	Assert.assertEquals(Content.get(0),  messages.get(0).getContent());
	    
	    Assert.assertEquals(Hashtags, argument.getValue().getHashtags());
	    Assert.assertEquals(Mentions, argument.getValue().getMentions());
	    
	    
		
	}
	
	
	
	@Test
	public void addMessageWithUsernameAndContentNull(){
		String Username = null;
		List<String> Content =  null;
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		
		Hashtags.add("");
		Mentions.add("");
		

		MessageFunctions.addMessage(Username, Content);
	
	    ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em,Mockito.never()).persist(argument.capture());

	}
	

	
	@Test
	public void SpaceBetweenHashtagAndWord(){
		String Username = "Matthew";
		List<String> Content = new ArrayList<String>();
	    Content.add("Test # test");
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Hashtags.add("test");
		Mentions.add("mention");
		
		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em).persist(argument.capture());
		

	    Assert.assertEquals("test", argument.getValue().getHashtags().get(0));
	}
	
	
	@Test
	public void SpaceBetweenMentionAndWord(){
		
		String Username = "Matthew";
		List<String>Content = new ArrayList<String>();
		Content.add("Test @ mention");
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Hashtags.add("test");
		Mentions.add("mention");

		
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(Mentions);
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em).persist(argument.capture());
		

	    Assert.assertEquals("mention", argument.getValue().getMentions().get(0));
		
	}
	
	
	@Test
	public void DoubleMention(){
		
		String Username = "Matthew";
		List<String>Content = new ArrayList<String>();
		Content.add("Testing post @test @mention");
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Mentions.add("test");
		Mentions.add("mention");
		

		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(Mentions);
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em).persist(argument.capture());
		
	    Assert.assertEquals(Username, argument.getValue().getUsername());
	    Assert.assertEquals(Content.get(0),  argument.getValue().getContent());
	    Assert.assertEquals(Hashtags, argument.getValue().getHashtags());
	    Assert.assertEquals(Mentions, argument.getValue().getMentions());
		
	}
	
	@Test
	public void DoubleHashtags(){
		
		String Username = "Matthew";
		List<String>Content = new ArrayList<String>();
		Content.add("Testing post #test #second");
		long Timestamp = 10;
		List<String> Hashtags = new ArrayList<String>();
		List<String> Mentions = new ArrayList<String>();
		
		Hashtags.add("test");
		Hashtags.add("second");
		

		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(Mentions);
		
		MessageFunctions.addMessage(Username, Content);
		
		ArgumentCaptor<MessageImpl> argument = ArgumentCaptor.forClass(MessageImpl.class);
	    Mockito.verify(em).persist(argument.capture());
		
	    Assert.assertEquals(Username, argument.getValue().getUsername());
	    Assert.assertEquals(Content.get(0),  argument.getValue().getContent());
	    Assert.assertEquals(Hashtags, argument.getValue().getHashtags());
	    Assert.assertEquals(Mentions, argument.getValue().getMentions());
		
	}
	
	@Test
	public void LookupOffsetAndLimitAreNegative(){
		int limit = -10;
		int offset = -10;
		Mockito.when(em.createNamedQuery("message.all")).thenReturn(query);

		MessageFunctions.lookupMessages(offset, limit);

		ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(Integer.class);
	    Mockito.verify(query).setFirstResult(argument.capture());
	    
	    Assert.assertEquals(0, argument.getValue().intValue());

	}
	
	@Test
	public void LookupHashtagIsNull(){
		int limit = 0;
		int offset= 0;
		Set<String> hashtags = null;
		Mockito.when(em.createNamedQuery("message.byhashtag")).thenReturn(query);
		
		try{
		MessageFunctions.lookupMessagesByHashtags(hashtags, offset, limit);
		fail("Test should have thrown NPE");}
		catch(Exception expected){
				
		assertTrue(expected instanceof NullPointerException);
		}

	}
	
	
	@Test
	public void LookupUsernameIsNull(){
		int limit = 0;
		int offset= 0;
		String username = null;
		Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
		
		try{
		MessageFunctions.lookupMessagesByUser(username, offset, limit);
		fail("Test should have thrown NPE");}
		catch(Exception expected){
				
		assertTrue(expected instanceof NullPointerException);}
		}
		
	
	
		@Test
		public void LookupMentionIsNull(){
			int limit = 0;
			int offset= 0;
			String username = null;
			Mockito.when(em.createNamedQuery("message.bymention")).thenReturn(query);
			
			try{
			MessageFunctions.lookupMessagesMentioningUser(username, offset, limit);
			fail("Test should have thrown NPE");}
			catch(Exception expected){
					
			assertTrue(expected instanceof NullPointerException);
			}

	}
		
		
		
		@Test
		public void CountUsernameIsNull(){
			int limit = 0;
			int offset= 0;
			String username = null;
			Mockito.when(em.createNamedQuery("message.byUsername")).thenReturn(query);
			
			try{
			MessageFunctions.countMessagesByuser(username);
			fail("Test should have thrown NPE");}
			catch(Exception expected){
					
			assertTrue(expected instanceof NullPointerException);
			}

	}
		
		
	
		
		
	
	
	
	
	@AfterClass
	public static void tearDown(){
			
	}
}
