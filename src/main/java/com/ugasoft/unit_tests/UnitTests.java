package com.ugasoft.unit_tests;

import com.ugasoft.annotations.XrayTest;
import org.testng.annotations.Test;

public class UnitTests {

    @Test
    @XrayTest(key = "UG-664")
    public void testUg664() {
        System.out.println("UG-664");
    }

    @Test(description = "Unit test for ug-675")
    @XrayTest(key = "UG-675")
    public void testUg675() {
        System.out.println("UG-675");
    }

    @Test
    @XrayTest(key = "UG-663")
    public void testUg663() {
        System.out.println("UG-663");
    }
}
