package com.chess.pojo;

import java.io.Serializable;
/**
* @ClassName: UserOnlineInfo
* @Description: TODO
* @author Melon
* @date 2013-4-6 上午10:53:19
*
 */
public class UserOnlineInfo implements Serializable {
	private String userId;//用户id
	private String ip;//用户终端ip
	private int port;//用户终端端口
	private String nick;//用户昵称
	private Long dateTime;//上户上次在线更新时间
	private Integer isInGame;//用户是否在游戏中
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public Long getDateTime() {
		return dateTime;
	}
	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}
	
	public Integer getIsInGame() {
		return isInGame;
	}
	public void setIsInGame(Integer isInGame) {
		this.isInGame = isInGame;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
