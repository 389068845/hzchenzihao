package com.netease.dao;

import com.netease.dao.po.OperateLog;

public interface OperateLogDao
{

	public void insert(OperateLog blog);

	public OperateLog selectByAccount(String userId);
}
