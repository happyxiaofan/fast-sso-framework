package com.rhwayfun.sso.server.model;

import com.rhwayfun.sso.common.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
		try {
			String url = innerAddress + "/notice/timeout?vt=" + vt + "&tokenTimeout=" + tokenTimeout;
			String ret = httpAccess(url);

			if (StringUtil.isEmpty(ret)) {
				return null;
			} else {
				return new Date(Long.parseLong(ret));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 通知客户端用户退出
	 */
	public boolean noticeLogout(String vt) {
		try {
			String url = innerAddress + "/notice/logout?vt=" + vt;
			String ret = httpAccess(url);

			return Boolean.parseBoolean(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * @param theUrl
	 * @return
     */
	private String httpAccess(String theUrl){
		String ret = null;
		try {
			URL url = new URL(theUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			conn.connect();

			byte[] buff = new byte[is.available()];
			is.read(buff);
			ret = new String(buff, "utf-8");

			conn.disconnect();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作
	 */
	public boolean noticeShutdown() {
		try {
			String url = innerAddress + "/notice/shutdown";
			String ret = httpAccess(url);

			return Boolean.parseBoolean(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
