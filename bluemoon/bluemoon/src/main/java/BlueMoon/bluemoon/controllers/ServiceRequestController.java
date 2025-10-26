package BlueMoon.bluemoon.controllers;

import BlueMoon.bluemoon.models.ServiceRequest;
import BlueMoon.bluemoon.models.Resident;
import BlueMoon.bluemoon.models.Apartment;
import BlueMoon.bluemoon.services.ServiceRequestService;
import BlueMoon.bluemoon.services.ResidentService;
import BlueMoon.bluemoon.services.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/resident/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ApartmentService apartmentService;

    @GetMapping
    public String listRequests(Model model, Authentication auth) {
        Resident resident = residentService.findByUsername(auth.getName());
        List<ServiceRequest> requests = serviceRequestService.getRequestsByResident(resident);
        model.addAttribute("requests", requests);
        return "service-request-list-resident";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, Authentication auth) {
        Resident resident = residentService.findByUsername(auth.getName());
        List<Apartment> apartments = apartmentService.getApartmentsByResident(resident);

        model.addAttribute("serviceRequest", new ServiceRequest());
        model.addAttribute("apartments", apartments);
        return "service-request-add";
    }

    @PostMapping("/new")
    public String createRequest(@RequestParam("apartmentId") Long apartmentId,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("description") String description,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("📝 [Controller] Nhận request - Apartment ID: " + apartmentId);
            System.out.println("📝 [Controller] Service Type: " + serviceType);
            System.out.println("📝 [Controller] Description: " + description);

            Resident resident = residentService.findByUsername(auth.getName());
            Apartment apartment = apartmentService.findById(apartmentId);

            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setResident(resident);
            serviceRequest.setApartment(apartment);
            serviceRequest.setServiceType(serviceType);
            serviceRequest.setDescription(description);

            serviceRequestService.createRequest(serviceRequest);

            redirectAttributes.addFlashAttribute("success", "Tạo yêu cầu thành công!");
            return "redirect:/resident/service-requests";
        } catch (Exception e) {
            System.err.println("❌ [Controller] Lỗi: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/resident/service-requests/new";
        }
    }

    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model, Authentication auth) {
        ServiceRequest request = serviceRequestService.getRequestById(id);

        Resident resident = residentService.findByUsername(auth.getName());
        if (!request.getResident().getId().equals(resident.getId())) {
            return "redirect:/resident/service-requests";
        }

        model.addAttribute("request", request);
        return "service-request-detail-resident";
    }
}