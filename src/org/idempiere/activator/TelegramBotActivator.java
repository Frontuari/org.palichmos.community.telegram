package org.idempiere.activator;

import java.util.HashMap;
import java.util.Map;
import org.compiere.util.CLogger;
import org.idempiere.telegram.adinterface.ITelegramBotFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import org.telegram.telegrambots.generics.LongPollingBot;

public class TelegramBotActivator implements BundleActivator, ServiceTrackerCustomizer<Object, Object>
{
	private static BundleContext bundleContext;
	private final static CLogger logger = CLogger.getCLogger(TelegramBotActivator.class);
	private static Map<String, BotSession> bots = new HashMap<String, BotSession>();
	
	static BundleContext getContext() 
	{
		return bundleContext;
	}
	
	@Override
	public void start(BundleContext context) throws Exception
	{
		bundleContext = context;
		
		ServiceTracker<Object, Object> registryTracker = new ServiceTracker<Object, Object>(context, ITelegramBotFactory.class.getName(), this);
		registryTracker.open();
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) 
	{
		ITelegramBotFactory factory = (ITelegramBotFactory) bundleContext.getService(reference);

		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		
		for (LongPollingBot bot : factory.getTelegramBots())
		{
			try 
			{
				BotSession sessionBot = telegramBotsApi.registerBot(bot);
				bots.put(bot.getBotToken(), sessionBot);
			}
			catch (TelegramApiException e) 
			{
				logger.warning("Error starting Telegram bot: " + e.getMessage());
			}
		}
		
		return factory;
	}

	@Override
	public void modifiedService(ServiceReference<Object> reference, Object service) 
	{
		// do nothing
	}

	@Override
	public void removedService(ServiceReference<Object> reference, Object service) 
	{
		bundleContext.ungetService(reference);
		
		ITelegramBotFactory factory = (ITelegramBotFactory) bundleContext.getService(reference);
		
		for (LongPollingBot bot : factory.getTelegramBots())
		{
			bots.get(bot.getBotToken()).stop();
			bots.remove(bot.getBotToken());
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		bundleContext = null;
	}

}