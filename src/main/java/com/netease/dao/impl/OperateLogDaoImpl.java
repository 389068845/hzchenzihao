package com.netease.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netease.dao.OperateLogDao;
import com.netease.dao.po.OperateLog;
@Service
public class OperateLogDaoImpl extends SqlSessionDaoSupport implements OperateLogDao{

	public void insert(OperateLog blog) {
		
	}

	public OperateLog selectByAccount(String userId) {
		return this.getSqlSession().selectOne("com.netease.dao.OperateLogDao.select", userId);
	}
	
	@Autowired  
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){  
          
        super.setSqlSessionFactory(sqlSessionFactory);  
    }  
}
