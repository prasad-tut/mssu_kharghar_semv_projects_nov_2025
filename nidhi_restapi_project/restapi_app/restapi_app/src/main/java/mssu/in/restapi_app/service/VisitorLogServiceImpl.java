package mssu.in.restapi_app.service;

import mssu.in.restapi_app.entity.VisitorLog;
import mssu.in.restapi_app.exception.ResourceNotFoundException;
import mssu.in.restapi_app.repository.VisitorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorLogServiceImpl implements VisitorLogService {
    
    @Autowired
    private VisitorLogRepository visitorLogRepository;
    
    @Override
    public List<VisitorLog> getAllVisitors() {
        return visitorLogRepository.findAll();
    }
    
    @Override
    public VisitorLog getVisitorById(Integer id) {
        return visitorLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Visitor not found with id: " + id));
    }
    
    @Override
    public VisitorLog checkInVisitor(VisitorLog visitorLog) {
        if (visitorLog.getCheckInTime() == null) {
            visitorLog.setCheckInTime(LocalDateTime.now());
        }
        return visitorLogRepository.save(visitorLog);
    }
    
    @Override
    public VisitorLog checkOutVisitor(Integer id) {
        VisitorLog visitor = getVisitorById(id);
        if (visitor.getCheckOutTime() != null) {
            throw new IllegalStateException("Visitor already checked out");
        }
        visitor.setCheckOutTime(LocalDateTime.now());
        return visitorLogRepository.save(visitor);
    }
    
    @Override
    public VisitorLog updateVisitor(Integer id, VisitorLog visitorLog) {
        VisitorLog existingVisitor = getVisitorById(id);
        
        existingVisitor.setVisitorName(visitorLog.getVisitorName());
        existingVisitor.setContactNumber(visitorLog.getContactNumber());
        existingVisitor.setEmail(visitorLog.getEmail());
        existingVisitor.setPurpose(visitorLog.getPurpose());
        existingVisitor.setHostName(visitorLog.getHostName());
        existingVisitor.setDepartment(visitorLog.getDepartment());
        existingVisitor.setVisitorType(visitorLog.getVisitorType());
        existingVisitor.setIdProofType(visitorLog.getIdProofType());
        existingVisitor.setIdProofNumber(visitorLog.getIdProofNumber());
        existingVisitor.setRemarks(visitorLog.getRemarks());
        
        return visitorLogRepository.save(existingVisitor);
    }
    
    @Override
    public void deleteVisitor(Integer id) {
        VisitorLog visitor = getVisitorById(id);
        visitorLogRepository.delete(visitor);
    }
    
    @Override
    public List<VisitorLog> getCurrentlyCheckedInVisitors() {
        return visitorLogRepository.findByCheckOutTimeIsNull();
    }
    
    @Override
    public List<VisitorLog> getVisitorsByHost(String hostName) {
        return visitorLogRepository.findByHostName(hostName);
    }
    
    @Override
    public List<VisitorLog> getVisitorsByDateRange(LocalDateTime start, LocalDateTime end) {
        return visitorLogRepository.findByCheckInTimeBetween(start, end);
    }
    
    @Override
    public List<VisitorLog> searchVisitorsByName(String name) {
        return visitorLogRepository.findByVisitorNameContainingIgnoreCase(name);
    }
}
