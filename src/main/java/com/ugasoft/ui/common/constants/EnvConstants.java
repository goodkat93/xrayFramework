package com.ugasoft.ui.common.constants;


public class EnvConstants extends InstanceConstants {

    public static class User {
        public static final String BASIC_USER_LOGIN = getPropertyFromRoot("default.properties", "basic.user.login");
        public static final String BASIC_USER_PASSWORD = getPropertyFromRoot("default.properties", "basic.user.password");
    }

    public static class Page {
        public static final String DEFAULT_HOME_PAGE = getPropertyFromRoot("default.properties", "default.home.page");
    }

    public static class Tokens {
        public static final String JIRA_TOKEN = getPropertyFromRoot("default.properties", "jira.token");
    }
}
