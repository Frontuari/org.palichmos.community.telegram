package org.idempiere.telegram.model;

import org.compiere.util.CLogger;
import org.idempiere.telegram.adinterface.IBotHandler;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

public abstract class AbstractBotHandler implements IBotHandler
{
	protected static final CLogger log = CLogger.getCLogger(AbstractBotHandler.class);
	
	@Override
	public boolean isHideInlineButton() 
	{
		return true;
	}
	
	protected void sendMessage(Session session, String text, ReplyKeyboard keyboard) throws TelegramApiException
	{
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(session.getChatID());
		sendMessage.setText(EmojiParser.parseToUnicode(text));
		sendMessage.setReplyMarkup(keyboard);
		sendMessage.enableHtml(true);
		sendMessage.disableWebPagePreview();
		sendMessage.disableNotification();
		
		try 
		{
			session.getBotInstance().sendMessage(sendMessage);
		}
		catch (TelegramApiException e)
		{
			log.warning(e.getMessage() + " TID: " + session.getChatID());
			throw e;
		}
	}
	
	protected void sendMessageToChatID(Session session, Long chatID, String text, ReplyKeyboard keyboard) throws TelegramApiException
	{
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatID);
		sendMessage.setText(EmojiParser.parseToUnicode(text));
		sendMessage.setReplyMarkup(keyboard);
		sendMessage.enableHtml(true);
		sendMessage.disableWebPagePreview();
		
		try 
		{
			session.getBotInstance().sendMessage(sendMessage);
		}
		catch (TelegramApiException e)
		{
			log.warning(e.getMessage() + " TID: " + session.getChatID());
			throw e;
		}
	}
	
	protected void sendPhoto(Session session, String urlImage) throws TelegramApiException
	{
		SendPhoto sendPhoto = new SendPhoto();
		sendPhoto.setPhoto(urlImage);
		sendPhoto.setChatId(session.getChatID());
		sendPhoto.disableNotification();
		
		try 
		{
			session.getBotInstance().sendPhoto(sendPhoto);
		} 
		catch (TelegramApiException e) 
		{
			log.warning(e.getMessage() + " URL: " + urlImage + " TID: " + session.getChatID());
			throw e;
		}
	}
	
	/**
	 * 
	 * @param session
	 * @param message Maximum is 200 characters
	 * @param isModal
	 * @throws TelegramApiException
	 */
	protected void sendAlertCallbackQuery(Session session, String message, boolean isModal) throws TelegramApiException
	{
		if (!session.getUpdate().hasCallbackQuery())
		{
			log.warning("Callback query not found");
			return;
		}
		
		AnswerCallbackQuery answer = new AnswerCallbackQuery();
		answer.setCallbackQueryId(session.getUpdate().getCallbackQuery().getId());
		answer.setText(message);
		answer.setShowAlert(isModal);
		
		session.getBotInstance().answerCallbackQuery(answer);
	}
	
	/**
	 * 
	 * @param session
	 * @param message Maximum is 200 characters
	 * @param isModal
	 * @throws TelegramApiException
	 */
	protected void sendAlertCallbackQueryOrMessage(Session session, String message, boolean isModal) throws TelegramApiException
	{
		if (!session.getUpdate().hasCallbackQuery())
		{
			SendMessage sendMessage = new SendMessage();
			sendMessage.setChatId(session.getChatID());
			sendMessage.setText(message);
			sendMessage.enableHtml(true);
			sendMessage.disableWebPagePreview();
			
			try 
			{
				session.getBotInstance().sendMessage(sendMessage);
			}
			catch (TelegramApiException e)
			{
				log.warning(e.getMessage());
			}
			
			return;
		}
		
		AnswerCallbackQuery answer = new AnswerCallbackQuery();
		answer.setCallbackQueryId(session.getUpdate().getCallbackQuery().getId());
		answer.setText(message);
		answer.setShowAlert(isModal);
		
		try
		{
			session.getBotInstance().answerCallbackQuery(answer);
		}
		catch (Exception e)
		{
			SendMessage sendMessage = new SendMessage();
			sendMessage.setChatId(session.getChatID());
			sendMessage.setText(message);
			sendMessage.enableHtml(true);
			sendMessage.disableWebPagePreview();
			
			try 
			{
				session.getBotInstance().sendMessage(sendMessage);
			}
			catch (TelegramApiException ex)
			{
				log.warning(ex.getMessage());
			}
		}
	}
	
}