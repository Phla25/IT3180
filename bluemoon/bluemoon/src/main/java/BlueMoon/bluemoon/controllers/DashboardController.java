package BlueMoon.bluemoon.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
	// Controller cho cư dân 
    @GetMapping("/resident/dashboard")
    public String residentDashboard() {
        return "dashboard-resident";
    }
    
    @GetMapping("/resident/fees-resident")
    public String showFeesPageSimplified() {
        return "fees-resident"; 
    }
    
    @GetMapping("/resident/resident-profile")
    public String showResidentProfile() {
    	return "resident-profile";
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
