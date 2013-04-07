package com.chess.mina;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.chess.util.ConfigProperties;

public class ServiceMinaClient{
	Logger logger = Logger.getLogger("developLogger");

	private static String minaServerIps = ConfigProperties.getConfigProperty("serviceServer.ips");
	private static Integer minaServerPort = Integer.valueOf(ConfigProperties.getConfigProperty("serviceServer.port").trim());
	
	private static String[]serverIps = minaServerIps.split(";");
	
	private static InetSocketAddress[] serverAddresses = new InetSocketAddress[serverIps.length];
	private static GenericObjectPool[] ioSessionPool = new GenericObjectPool[serverIps.length];
	
	private static ServiceMinaClient instance = new ServiceMinaClient();
	
	public static ServiceMinaClient getInstance()
	{
		return instance;
	}

	private ServiceMinaClient() {
		
		for(int i=0;i<serverAddresses.length;i++)
		{
			serverAddresses[i]= new InetSocketAddress(serverIps[i], minaServerPort);

		
			ioSessionPool[i] = new GenericObjectPool(new IoSessionFactory(serverAddresses[i], new IoHandlerAdapter() {
			@Override
			public void sessionOpened(IoSession session) throws Exception {
				logger.info("---sessionOpened---");
			}
			@Override
			public void sessionClosed(IoSession session) throws Exception {
				if (session != null && session.isConnected())
					session.close(true);
				logger.info("---sessionClosed---");
			}

			}), 60/(serverIps.length));
		
			ioSessionPool[i].setMinEvictableIdleTimeMillis(1000*60*30);
			ioSessionPool[i].setMaxIdle(30/(serverIps.length));
			ioSessionPool[i].setMaxActive(60/(serverIps.length));
			ioSessionPool[i].setTestOnReturn(true);
			ioSessionPool[i].setTestOnBorrow(true);
			ioSessionPool[i].setTestWhileIdle(true);
			ioSessionPool[i].setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
		
		}
	}

	/**
	 * @param sessionId
	 * @param user
	 * @param command
	 */
	public void setAndSend(String user, Integer command) {
		setAndSend(user,command,null);
	}

	public void setAndSend(String user, Integer command, String[] callback) {
		if (command > com.chess.pojo.Command.values().length)
			return;
		IoSession session = null;
		Random r = new Random();
		int i = r.nextInt(serverIps.length);
		
		try {
			synchronized(ioSessionPool)
			{
				session = (IoSession) ioSessionPool[i].borrowObject();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if (session != null)
		{
			synchronized (session) 
			{
				try{
					Integer seq = (Integer)session.getAttribute("seq", new Integer(1000));
					if(seq >= Integer.MAX_VALUE-100)
					{
						seq = 0;
					}
					
					session.write(command + " " + seq + " " + user);
					
					if (callback != null)
					{
						ReadFuture readFuture = session.read();
						if (readFuture.awaitUninterruptibly(Integer.valueOf(ConfigProperties.getConfigProperty("serviceServer.timeout")))) 
						{
							String resultString = (String) readFuture.getMessage();
							
							if(resultString!=null)
							{	
								String[] result = resultString.split(" ", 2);
							
								if(seq.equals(Integer.parseInt(result[0])))
								{
									callback[0] = result[1];
									session.setAttribute("seq", new Integer(seq+1));
								}
								else
								{
									callback[0] = null;
									session.close(true);
								
								}
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
					if(session!=null)
					{	
						try
						{
							session.close(true);
						}
						catch(Exception ee)
						{
							ee.printStackTrace();
						}
					}
				} finally {
					try {
						if (session != null) {
							ioSessionPool[i].returnObject(session);	
						}
					} catch (Exception eee) {
						eee.printStackTrace();
					}
				}
			}
		} 
		else 
		{
			session = null;
		}

	}	

}
