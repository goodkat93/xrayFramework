package com.ugasoft.xray_helper.test_plan_preparer;


public class XrayTestParser {

    public static void main(String[] args) {
        String jiraTestPlan = "UG-669";
        if (args.length > 0) {
            jiraTestPlan = args[0];
        }
        System.out.println("testPlanKey is: " + jiraTestPlan);
        TestPlanPreparer.generateXML(jiraTestPlan);
    }
}
