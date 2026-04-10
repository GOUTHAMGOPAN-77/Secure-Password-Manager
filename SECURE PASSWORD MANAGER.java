import java.io.*;
import java.util.*;

public class Main {

    //  USER CLASS
    static class User {
        private String username;
        private String masterPassword;

        public User(String username, String masterPassword) {
            this.username = username;
            this.masterPassword = masterPassword;
        }

        public boolean authenticate(String password) {
            return this.masterPassword.equals(password);
        }
    }

    //  CREDENTIAL CLASS
    static class Credential {
        private String site;
        private String username;
        private String encryptedPassword;

        public Credential(String site, String username, String encryptedPassword) {
            this.site = site;
            this.username = username;
            this.encryptedPassword = encryptedPassword;
        }

        public String getSite() { return site; }
        public String getUsername() { return username; }
        public String getEncryptedPassword() { return encryptedPassword; }
    }

    //  ENCRYPTION CLASS
    static class EncryptionUtil {
        public static String encrypt(String password) {
            return Base64.getEncoder().encodeToString(password.getBytes());
        }

        public static String decrypt(String encrypted) {
            return new String(Base64.getDecoder().decode(encrypted));
        }
    }

    //  PASSWORD MANAGER CLASS
    static class PasswordManager {
        private ArrayList<Credential> list = new ArrayList<>();
        private final String FILE = "data.txt";

        public PasswordManager() {
            loadFromFile();
        }

        public void add(String site, String user, String pass) {
            String enc = EncryptionUtil.encrypt(pass);
            list.add(new Credential(site, user, enc));
            save(site, user, enc);
            System.out.println("✅ Added!");
        }

        public void view() {
            if (list.isEmpty()) {
                System.out.println("No data.");
                return;
            }

            for (Credential c : list) {
                System.out.println("\nSite: " + c.getSite());
                System.out.println("Username: " + c.getUsername());
                System.out.println("Password: " + EncryptionUtil.decrypt(c.getEncryptedPassword()));
            }
        }

        public void search(String site) {
            for (Credential c : list) {
                if (c.getSite().equalsIgnoreCase(site)) {
                    System.out.println("Found:");
                    System.out.println("Username: " + c.getUsername());
                    System.out.println("Password: " + EncryptionUtil.decrypt(c.getEncryptedPassword()));
                    return;
                }
            }
            System.out.println("Not found.");
        }

        public void delete(String site) {
            Iterator<Credential> it = list.iterator();
            boolean found = false;

            while (it.hasNext()) {
                if (it.next().getSite().equalsIgnoreCase(site)) {
                    it.remove();
                    found = true;
                }
            }

            if (found) {
                rewrite();
                System.out.println("Deleted.");
            } else {
                System.out.println("Not found.");
            }
        }

        private void save(String site, String user, String pass) {
            try (FileWriter fw = new FileWriter(FILE, true)) {
                fw.write(site + "," + user + "," + pass + "\n");
            } catch (Exception e) {}
        }

        private void loadFromFile() {
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length == 3) {
                        list.add(new Credential(p[0], p[1], p[2]));
                    }
                }
            } catch (Exception e) {}
        }

        private void rewrite() {
            try (FileWriter fw = new FileWriter(FILE)) {
                for (Credential c : list) {
                    fw.write(c.getSite() + "," + c.getUsername() + "," + c.getEncryptedPassword() + "\n");
                }
            } catch (Exception e) {}
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        User user = new User("admin", "1234");

        System.out.print("Enter Master Password: ");
        if (!user.authenticate(sc.nextLine())) {
            System.out.println("Access Denied!");
            return;
        }

        PasswordManager pm = new PasswordManager();

        while (true) {
            System.out.println("\n1.Add  2.View  3.Search  4.Delete  5.Exit");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    System.out.print("Site: ");
                    String s = sc.nextLine();
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();
                    pm.add(s, u, p);
                    break;

                case 2:
                    pm.view();
                    break;

                case 3:
                    System.out.print("Search site: ");
                    pm.search(sc.nextLine());
                    break;

                case 4:
                    System.out.print("Delete site: ");
                    pm.delete(sc.nextLine());
                    break;

                case 5:
                    System.exit(0);
            }
        }
    }
}