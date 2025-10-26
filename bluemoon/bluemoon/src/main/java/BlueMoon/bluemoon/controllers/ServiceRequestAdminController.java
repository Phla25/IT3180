package BlueMoon.bluemoon.controllers;

import BlueMoon.bluemoon.models.ServiceRequest;
import BlueMoon.bluemoon.models.ServiceRequest.RequestStatus;
import BlueMoon.bluemoon.services.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/service-requests")
public class ServiceRequestAdminController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping
    public String listAllRequests(@RequestParam(required = false) String status, Model model) {
        List<ServiceRequest> requests;

        if (status != null && !status.isEmpty()) {
            requests = serviceRequestService.getRequestsByStatus(RequestStatus.valueOf(status));
        } else {
            requests = serviceRequestService.getAllRequests();
        }

        model.addAttribute("requests", requests);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", RequestStatus.values());
        return "service-request-list-admin";
    }

    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        ServiceRequest request = serviceRequestService.getRequestById(id);
        model.addAttribute("request", request);
        model.addAttribute("statuses", RequestStatus.values());
        return "service-request-detail-admin";
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
            @RequestParam RequestStatus status,
            @RequestParam(required = false) String adminNote,
            RedirectAttributes redirectAttributes) {
        try {
            serviceRequestService.updateStatus(id, status, adminNote);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi: " + e.getMessage());
        }
        return "redirect:/admin/service-requests/" + id;
    }
}