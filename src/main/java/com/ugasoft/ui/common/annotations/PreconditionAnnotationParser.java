package com.ugasoft.ui.common.annotations;

import com.ugasoft.models.DefaultPreconditions;
import com.ugasoft.models.DefaultUserData;
import com.ugasoft.ui.common.constants.EnvConstants;
import com.ugasoft.ui.common.core.Log;

import java.lang.reflect.Method;

public class PreconditionAnnotationParser {

    public static DefaultUserData parseUser(Method method) {
        DefaultUserData userData = null;
        if (method.getDeclaringClass().isAnnotationPresent(BaseState.class)) {
            BaseState baseState = method.getDeclaringClass().getAnnotation(BaseState.class);
            if (baseState.user().equals(DefaultPreconditions.User.ADMIN_ADMIN_BASIC_USER)) {
                userData = new DefaultUserData();
                Log.info("Logging via default admin username and password:\n" +
                        "username: " + userData.getUserName() + "\n" +
                        "password: " + userData.getPassword());
            } else if (!baseState.user().isEmpty()) {
                userData = getLoginData(baseState.user());
                Log.info("Logging via user: " + baseState.user());
            }
        }
        if (method.isAnnotationPresent(Precondition.class)) {
            Precondition precondition = method.getAnnotation(Precondition.class);
            if (precondition.user().equals(DefaultPreconditions.User.ADMIN_ADMIN_BASIC_USER)) {
                userData = new DefaultUserData();
                Log.info("Logging via default admin username and password:\n" +
                        "user: " + userData.getUserName() + "\n" +
                        "password: " + userData.getPassword());
            } else if (!precondition.login().isEmpty()) {
                if (!precondition.password().isEmpty()) {
                    userData = new DefaultUserData(precondition.login(), precondition.password());
                    Log.info("Logging via:\n" +
                            "user: " + precondition.login() + "\n" +
                            "password: " + precondition.password());
                } else {
                    userData = new DefaultUserData(precondition.login());
                    Log.info("Logging via:\n" +
                            "user: " + precondition.login());
                }
            } else if (!precondition.user().isEmpty()) {
                userData = getLoginData(precondition.user());
                Log.info("Logging via user: " + precondition.user());
            }
        }
        return userData;
    }

    public static String parseUrl(Method method) {
        String url = null;
        if (method.getDeclaringClass().isAnnotationPresent(BaseState.class)) {
            BaseState baseState = method.getDeclaringClass().getAnnotation(BaseState.class);
            if (!baseState.page().isEmpty())
                url = getPageUrl(baseState.page());
        }
        if (method.isAnnotationPresent(Precondition.class)) {
            Precondition precondition = method.getAnnotation(Precondition.class);
            if (!precondition.page().isEmpty())
                url = getPageUrl(precondition.page());
        }
        return url;
    }


    private static DefaultUserData getLoginData(String user) {
        switch (user) {
            case DefaultPreconditions.User.ADMIN_ADMIN_BASIC_USER:
                return new DefaultUserData();
            default:
                return null;
        }
    }

    private static String getPageUrl(String page) {
        if (page.isEmpty())
            return null;
        switch (page) {
            case DefaultPreconditions.Page.UGASOFT_HOME_PAGE:
                return EnvConstants.Page.DEFAULT_HOME_PAGE;
            default:
                return null;
        }
    }
}
