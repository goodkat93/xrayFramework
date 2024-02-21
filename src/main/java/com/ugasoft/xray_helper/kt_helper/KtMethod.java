package com.ugasoft.xray_helper.kt_helper;

public class KtMethod {

    private String name;
    private String xrayKey;
    private String description;
    private String declaringClassName;
    private String methodName;

    public KtMethod(String name, String xrayKey, String description, String declaringClassName, String methodName) {
        this.name = name;
        this.xrayKey = xrayKey;
        this.description = description;
        this.declaringClassName = declaringClassName;
        this.methodName = methodName;
    }

    public String getName() {
        return name;
    }

    public String getXrayKey() {
        return xrayKey;
    }

    public String getDescription() {
        return description;
    }

    public String getDeclaringClassName() {
        return declaringClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setXrayKey(String xrayKey) {
        this.xrayKey = xrayKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeclaringClassName(String declaringClassName) {
        this.declaringClassName = declaringClassName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
