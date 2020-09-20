package org.idempiere.telegram.adinterface;

import java.util.Collection;

import org.telegram.telegrambots.generics.LongPollingBot;

public interface ITelegramBotFactory
{
	/**
	 * Return all telegram bots implements this factory
	 * @return Collection objects implement ITelegramBot
	 */
	public Collection<LongPollingBot> getTelegramBots();
	
}