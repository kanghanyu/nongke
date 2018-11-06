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
//	  return new User("10212014", "15652009705");
    return userStore.get ();
  }

  public static void cleanUser() {
    userStore.remove ();
  }

}