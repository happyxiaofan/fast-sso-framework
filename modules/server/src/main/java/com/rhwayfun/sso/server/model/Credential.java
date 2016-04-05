package com.rhwayfun.sso.server.model;

/**
 * 对登录页面提交的内容集中存储，并提供特定获取方法的一个实体类
 * 
 * @author Administrator
 *
 */
public abstract class Credential {
	private String error; // 错误信息

	/**
	 * 获取一个参数值
	 * 
	 * @param name
	 * @return
	 */
	public abstract String getParameter(String name);

	/**
	 * 获取多值参数数组
	 * 
	 * @param name
	 * @return
	 */
	public abstract String[] getParameterValue(String name);

	/**
	 * 由PreLoginHandler通过setSessionValue()方法写入特定session属性值
	 * 
	 * @return
	 */
	public abstract Object getSettedSessionValue();

	/**
	 * 授权失败时，设置失败提示信息
	 * 
	 * @param errorMsg
	 */
	public void setErrorMsg(String errorMsg) {
		this.error = errorMsg;
	}

	/**
	 * 获取失败提示信息
	 * 
	 * @return
	 */
	public String getErrorMsg() {
		return this.error;
	}
}
