package com.example.bugtracker.controller;

import com.example.bugtracker.model.Bug;
import com.example.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Bug Controller Class
 * Handles all HTTP requests and routing for the Bug Tracking System
 */
@Controller
@RequestMapping("/")
public class BugController {

    @Autowired
    private BugService bugService;

    /**
     * Home page - redirect to bugs list
     */
    @GetMapping
    public String index() {
        return "redirect:/bugs";
    }

    /**
     * Display all bugs (Developer Dashboard)
     * GET /bugs
     */
    @GetMapping("/bugs")
    public String displayBugs(Model model) {
        try {
            List<Bug> allBugs = bugService.getAllBugs();
            
            // Separate active bugs (OPEN, IN_PROGRESS) and fixed bugs
            List<Bug> activeBugs = new java.util.ArrayList<>();
            List<Bug> fixedBugsList = new java.util.ArrayList<>();
            
            for (Bug bug : allBugs) {
                if ("FIXED".equals(bug.getStatus())) {
                    fixedBugsList.add(bug);
                } else {
                    activeBugs.add(bug);
                }
            }
            
            // Reverse to show latest first
            java.util.Collections.reverse(activeBugs);
            java.util.Collections.reverse(fixedBugsList);
            
            long totalBugs = bugService.getTotalBugCount();
            long openBugs = bugService.getCountByStatus("OPEN");
            long progressBugs = bugService.getCountByStatus("IN_PROGRESS");
            long fixedBugs = bugService.getCountByStatus("FIXED");

            model.addAttribute("activeBugs", activeBugs);
            model.addAttribute("fixedBugs", fixedBugsList);
            model.addAttribute("totalBugs", totalBugs);
            model.addAttribute("openBugs", openBugs);
            model.addAttribute("progressBugs", progressBugs);
            model.addAttribute("fixedBugsCount", fixedBugs);

            return "bugs";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching bugs: " + e.getMessage());
            e.printStackTrace();
            return "bugs";
        }
    }

    /**
     * Display form to raise a new bug (Tester Dashboard)
     * GET /raise-bug
     */
    @GetMapping("/raise-bug")
    public String raiseBugForm(Model model) {
        model.addAttribute("bug", new Bug());
        model.addAttribute("today", LocalDate.now());
        return "raise_bug";
    }

    /**
     * Submit a new bug report
     * POST /raise-bug
     */
    @PostMapping("/raise-bug")
    public String submitBug(@RequestParam String description,
                           @RequestParam String priority,
                           @RequestParam String severity,
                           @RequestParam String detectedOn,
                           @RequestParam(required = false) String assignedTo,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            // Validate inputs
            if (description == null || description.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Description is required!");
                return "redirect:/raise-bug";
            }
            if (priority == null || priority.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Priority is required!");
                return "redirect:/raise-bug";
            }
            if (severity == null || severity.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Severity is required!");
                return "redirect:/raise-bug";
            }
            if (detectedOn == null || detectedOn.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Detected On date is required!");
                return "redirect:/raise-bug";
            }

            // Parse date
            LocalDate detectedDate = LocalDate.parse(detectedOn);
            
            // Validate date is not in future
            if (detectedDate.isAfter(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error", "Detected date cannot be in the future!");
                return "redirect:/raise-bug";
            }

            // Create bug
            Bug bug = new Bug(description, priority, severity, detectedDate, assignedTo);
            boolean success = bugService.createBug(bug);

            if (success) {
                redirectAttributes.addFlashAttribute("success", 
                    "✓ Bug report submitted successfully! Status: OPEN");
                return "redirect:/raise-bug";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to save bug report!");
                return "redirect:/raise-bug";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/raise-bug";
        }
    }

    /**
     * Display bug details and update form
     * GET /update-bug/{id}
     */
    @GetMapping("/update-bug/{id}")
    public String updateBugForm(@PathVariable Integer id, Model model) {
        try {
            Bug bug = bugService.getBugById(id);
            if (bug == null) {
                return "redirect:/bugs";
            }
            model.addAttribute("bug", bug);
            return "update_bug";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/bugs";
        }
    }

    /**
     * Update bug status
     * POST /update-bug/{id}
     */
    @PostMapping("/update-bug/{id}")
    public String updateBugStatus(@PathVariable Integer id,
                                  @RequestParam String status,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (status == null || status.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Status is required!");
                return "redirect:/update-bug/" + id;
            }

            boolean success = bugService.updateBugStatus(id, status);

            if (success) {
                redirectAttributes.addFlashAttribute("success", 
                    "✓ Bug #" + id + " status updated to " + status + " successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update bug status!");
            }

            return "redirect:/bugs";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/update-bug/" + id;
        }
    }

    /**
     * API endpoint to get all bugs as JSON
     * GET /api/bugs
     */
    @GetMapping("/api/bugs")
    @ResponseBody
    public List<Bug> getAllBugsJson() {
        return bugService.getAllBugs();
    }

    /**
     * API endpoint to get single bug as JSON
     * GET /api/bug/{id}
     */
    @GetMapping("/api/bug/{id}")
    @ResponseBody
    public Bug getBugJsonById(@PathVariable Integer id) {
        return bugService.getBugById(id);
    }

    /**
     * API endpoint to get bugs by status
     * GET /api/bugs/status/{status}
     */
    @GetMapping("/api/bugs/status/{status}")
    @ResponseBody
    public List<Bug> getBugsByStatusJson(@PathVariable String status) {
        return bugService.getBugsByStatus(status);
    }

    /**
     * Handle 404 errors
     */
    @GetMapping("/404")
    public String notFound() {
        return "404";
    }

    /**
     * Handle 500 errors
     */
    @GetMapping("/500")
    public String serverError() {
        return "500";
    }
}
