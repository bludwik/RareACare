package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.server.BaseServiceLocator;
import com.blsoft.rareacare.server.reqservices.UserService;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(value = UserService.class, locator=BaseServiceLocator.class)
public interface UserDataRequest extends RequestContext{
	Request<UserProxy> login(String loginTxt, String passTxt);
	Request<UserProxy> getLoggedUser();
	Request<Void> logOut();
	Request<UserProxy> findUser(Integer id);
	Request<List<UserProxy>> getUsers(String patern, Integer from, Integer count);
	Request<Integer> getCount();
	
	Request<Void> persist(UserProxy p);
	Request<Void> remove(UserProxy p);
	Request<String> changePass(Integer userId, String oldPass, String newPass);
	Request<String> setNewPass(int userId, String newPass);
}
