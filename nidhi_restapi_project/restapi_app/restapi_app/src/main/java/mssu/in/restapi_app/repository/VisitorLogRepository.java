package mssu.in.restapi_app.repository;

import mssu.in.restapi_app.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Integer> {
    
    // Find all visitors currently checked in (no checkout time)
    List<VisitorLog> findByCheckOutTimeIsNull();
    
    // Find visitors by host name
    List<VisitorLog> findByHostName(String hostName);
    
    // Find visitors by date range
    List<VisitorLog> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Find visitors by visitor name
    List<VisitorLog> findByVisitorNameContainingIgnoreCase(String visitorName);
}
