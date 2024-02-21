package com.ugasoft.xray_helper.imgur_helper;

import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;


public class ImgurHelper {

    public static void main(String[] args) throws IOException {
        downloadScreenshotViaAttachmentId("6574");
    }

    public static File downloadScreenshotViaAttachmentId(String attachmentId) throws IOException {
        byte[] fileData = RestAssured.given()
                .auth().preemptive().basic("xhqtester@ugasoft.com", "HappyW0rld63")
                .get("https://ugasoft.testrail.com/index.php?/attachments/get/" + attachmentId)
                .asByteArray();

        String directoryPath = System.getProperty("user.dir") + "/Screenshots/TestRail/";
        Path dirPathObj = Paths.get(directoryPath);
        Files.createDirectories(dirPathObj); // Создание папки, если она не существует

        String destinationFile = directoryPath + RandomStringUtils.randomAlphabetic(8) + ".png";
        Path filePath = Paths.get(destinationFile);

        Files.write(filePath, fileData, StandardOpenOption.CREATE);

        return new File(destinationFile);
    }

    public static String uploadScreenshot(String UUID) throws IOException {
        File scr = downloadScreenshotViaAttachmentId(UUID);
        return getImgurScreenshot(scr);
    }

    public static String getImgurScreenshot(File imageFile) {
        String link = "";
        try {
            link = RestAssured.given()
                    .header("Authorization", "Client-ID " + readProperty("client.id"))
                    .header("Content-Type", "image/jpeg")
                    .body(imageFile)
                    .expect()
                    .statusCode(200)
                    .when()
                    .post("https://api.imgur.com/3/image").jsonPath().getString("data.link");
        } catch (NoSuchMethodError ignored) {
        }
        if (link != null && !link.isEmpty()) {
            imageFile.delete();  // delete screenshot
        }

        return link;
    }

    public static String readProperty(String property) {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/PropertyFiles/imgur.properties"))) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(property);
    }
}
