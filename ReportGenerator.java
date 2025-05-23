public class ReportGenerator {

    public String generateUserReport(User user) {
        StringBuilder report = new StringBuilder();
        report.append("User Report:\n");
        report.append("Name: ").append(user.getName()).append("\n");
        report.append("Email: ").append(user.getEmail()).append("\n");
        return report.toString();
    }
}