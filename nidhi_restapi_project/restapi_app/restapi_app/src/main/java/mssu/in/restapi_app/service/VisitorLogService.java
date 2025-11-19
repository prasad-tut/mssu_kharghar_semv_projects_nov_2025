package mssu.in.restapi_app.service;

import mssu.in.restapi_app.entity.VisitorLog;
import java.time.LocalDateTime;
import java.util.List;

public interface VisitorLogService {
    
    List<VisitorLog> getAllVisitors();
    
    VisitorLog getVisitorById(Integer id);
    
    VisitorLog checkInVisitor(VisitorLog visitorLog);
    
    VisitorLog checkOutVisitor(Integer id);
    
    VisitorLog updateVisitor(Integer id, VisitorLog visitorLog);
    
    void deleteVisitor(Integer id);
    
    List<VisitorLog> getCurrentlyCheckedInVisitors();
    
    List<VisitorLog> getVisitorsByHost(String hostName);
    
    List<VisitorLog> getVisitorsByDateRange(LocalDateTime start, LocalDateTime end);
    
    List<VisitorLog> searchVisitorsByName(String name);
}
