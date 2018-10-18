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
//	  User user = new User();
//	  user.setUid("10101611");
//	  user.setPhone("15501271310");
//	  return user;
    return userStore.get ();
  }

  public static void cleanUser() {
    userStore.remove ();
  }

}