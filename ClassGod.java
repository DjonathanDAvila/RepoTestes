public class UserManager {

    private String[] users = new String[100];
    private String[] passwords = new String[100];
    private int count = 0;

    public void addUser(String username, String password) {
        if (username.length() > 0 && password.length() > 0) {
            if (!username.contains(" ")) {
                users[count] = username;
                passwords[count] = password;
                count++;
            } else {
                System.out.println("Username cannot contain spaces.");
            }
        } else {
            System.out.println("Username and password cannot be empty.");
        }
    }

    public boolean login(String username, String password) {
        for (int i = 0; i < 100; i++) {
            if (users[i] != null && users[i].equals(username)) {
                if (passwords[i].equals(password)) {
                    System.out.println("Login successful!"); //Login successful
                    return true;
                } else {
                    System.out.println("Wrong password.");
                    return false;
                }
            }
        }
        System.out.println("User not found.");
        return false;
    }

    public void resetAllPasswords() {
        for (int i = 0; i < 100; i++) {
            if (users[i] != null) {
                passwords[i] = "123456"; // senha padrão
                System.out.println("Password for user " + users[i] + " reset to 123456.");
            }
        }
    }

    public void printAllUsers() {
        for (int i = 0; i < 100; i++) {
            if (users[i] != null) {
                System.out.println("User: " + users[i] + " | Password: " + passwords[i]);
            }
        }
    }
}
