package com.rhwayfun.sso.server.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户端应用列表
 */
@SuppressWarnings("serial")
public class ClientSystem implements Serializable {

	private String id; // 唯一标识
	private String name; // 系统名称
	
	private String baseUrl; // 应用基路径，代表应用访问起始点 
	private String homeUri; // 应用主页面URI，baseUrl + homeUri = 主页URL
	private String innerAddress; // 系统间内部通信地址

	public String getHomeUrl() {
		return baseUrl + homeUri;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getHomeUri() {
		return homeUri;
	}

	public void setHomeUri(String homeUri) {
		this.homeUri = homeUri;
	}

	public String getInnerAddress() {
		return innerAddress;
	}

	public void setInnerAddress(String innerAddress) {
		this.innerAddress = innerAddress;
	}

	/**
	 * 与客户端系统通信，通知客户端token过期
	 * 
	 * @param tokenTimeout
	 * @return 延期的有效期
	 */
	public Date noticeTimeout(String vt, int tokenTimeout) {
		// TODO 与客户端通信处理有效期
		return null;
	}

	/**
	 * 通知客户端用户退出
	 */
	public void noticeLogout(String vt) {
		//TODO 通知logout
	}

	/**
	 * 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作
	 */
	public void noticeShutdown() {
		// TODO 通知shutdown
	}

}
