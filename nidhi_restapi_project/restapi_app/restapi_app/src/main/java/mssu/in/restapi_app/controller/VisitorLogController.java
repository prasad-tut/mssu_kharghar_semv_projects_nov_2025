package mssu.in.restapi_app.controller;

import mssu.in.restapi_app.entity.VisitorLog;
import mssu.in.restapi_app.service.VisitorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visitors")
public class VisitorLogController {
    
    @Autowired
    private VisitorLogService visitorLogService;
    
    @GetMapping
    public ResponseEntity<List<VisitorLog>> getAllVisitors() {
        return ResponseEntity.ok(visitorLogService.getAllVisitors());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VisitorLog> getVisitorById(@PathVariable Integer id) {
        return ResponseEntity.ok(visitorLogService.getVisitorById(id));
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<VisitorLog> checkInVisitor(@RequestBody VisitorLog visitorLog) {
        VisitorLog savedVisitor = visitorLogService.checkInVisitor(visitorLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVisitor);
    }
    
    @PutMapping("/checkout/{id}")
    public ResponseEntity<VisitorLog> checkOutVisitor(@PathVariable Integer id) {
        return ResponseEntity.ok(visitorLogService.checkOutVisitor(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VisitorLog> updateVisitor(@PathVariable Integer id, 
                                                     @RequestBody VisitorLog visitorLog) {
        return ResponseEntity.ok(visitorLogService.updateVisitor(id, visitorLog));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Integer id) {
        visitorLogService.deleteVisitor(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/current")
    public ResponseEntity<List<VisitorLog>> getCurrentlyCheckedInVisitors() {
        return ResponseEntity.ok(visitorLogService.getCurrentlyCheckedInVisitors());
    }
    
    @GetMapping("/host/{hostName}")
    public ResponseEntity<List<VisitorLog>> getVisitorsByHost(@PathVariable String hostName) {
        return ResponseEntity.ok(visitorLogService.getVisitorsByHost(hostName));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<VisitorLog>> searchVisitorsByName(@RequestParam String name) {
        return ResponseEntity.ok(visitorLogService.searchVisitorsByName(name));
    }
    
    @GetMapping("/daterange")
    public ResponseEntity<List<VisitorLog>> getVisitorsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(visitorLogService.getVisitorsByDateRange(start, end));
    }
}
