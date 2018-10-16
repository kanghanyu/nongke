package com.khy.utils;

import com.khy.entity.User;

/***
 * 保存用户信息的
 * @author kanghanyu
 *
 */
public class SessionHolder {

  private static ThreadLocal<User> userStore = new ThreadLocal<User> ();

  public static void setUser(User user) {
    userStore.set (user);
  }

  public static User currentUser() {
//    return userStore.get ();
	  User user = new User();
	  user.setUid("10032023");
	  user.setPhone("11111111111");
    return user;
  }

  public static void cleanUser() {
    userStore.remove ();
  }

}