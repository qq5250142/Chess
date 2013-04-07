package com.chess.mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class IoSessionFactory extends BasePoolableObjectFactory {
	
	private static final Logger logger = Logger.getLogger("log");
	private InetSocketAddress remote;
	private IoConnector ioConnector;
	private IoHandler handler;
	private int connectTimeout = 1000 * 5;

	public IoSessionFactory(InetSocketAddress remote, IoHandler handler) 
	{
		this.remote = remote;
		this.setHandler(handler);
		this.initConnector(this.handler, this.connectTimeout);
	}
	
	private void initConnector(IoHandler handler, int connectTimeout)
	{
		this.ioConnector = new NioSocketConnector();

		ioConnector.getFilterChain().addLast("mdc", new MdcInjectionFilter());
		TextLineCodecFactory textCodecFactory = new TextLineCodecFactory(Charset.forName( "UTF-8" ));
		textCodecFactory.setDecoderMaxLineLength(9000);
		ioConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(textCodecFactory));
		ioConnector.getFilterChain().addLast("logger", new LoggingFilter());
		ioConnector.getSessionConfig().setUseReadOperation(true);
		ioConnector.getSessionConfig().setBothIdleTime(30);
		ioConnector.setHandler(handler);
		ioConnector.setConnectTimeoutMillis(connectTimeout);
	}

	public Object makeObject() throws Exception
	{
		//System.out.println("---------make object--------------");	
			IoSession pooled = null;
			try {

				ConnectFuture future = ioConnector.connect(remote);
				future.awaitUninterruptibly();
				if (future.isConnected()) {
					pooled = future.getSession();
				}
				else
				{
				}

			} catch (Exception e) {
				if (pooled != null) {
					try {
						pooled.close(true);
					} catch (Exception e1) {
					}
				}
			}
			return pooled;
	}

	public void activateObject(Object parm1) throws Exception 
	{
		super.activateObject(parm1);
	}

	public void passivateObject(Object parm1) throws Exception
	{
		super.passivateObject(parm1);
	}

	public void destroyObject(Object parm1) throws Exception
	{
		if (parm1 instanceof IoSession) {
			IoSession pooled = (IoSession) parm1; 
			try {
				pooled.close(true);
			} catch (Exception e) {
			}
		}
	}

	public boolean validateObject(Object parm1) 
	{
		if (parm1 instanceof IoSession) {
			IoSession pooled = (IoSession) parm1;
			return (pooled.isConnected() && !pooled.isClosing());
		} else {
			return false;
		}
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public InetSocketAddress getRemote() {
		return remote;
	}

	public void setRemote(InetSocketAddress remote) {
		this.remote = remote;
	}

	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}

	public IoHandler getHandler() {
		return handler;
	}
}
