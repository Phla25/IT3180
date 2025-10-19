package BlueMoon.bluemoon.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/resident/dashboard")
    public String residentDashboard() {
        return "dashboard-resident";
    }
    @GetMapping("/staff/dashboard")
    public String staffDashboard() {
        return "dashboard-staff";
    }
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "dashboard-admin";
    }
    @GetMapping("/accountant/dashboard")
    public String accountantDashboard() {
        return "dashboard-accountant";
    }

}
