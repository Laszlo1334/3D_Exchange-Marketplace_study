package com.education.marketplace_3D.service;

import com.education.marketplace_3D.entities.AuditLog;
import com.education.marketplace_3D.enums.AuditAction;
import com.education.marketplace_3D.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    @Transactional (propagation = Propagation.REQUIRES_NEW)
    public AuditLog logAction (AuditAction action, String description, String userEmail){
        AuditLog newAuditLog = new AuditLog(action, description, userEmail);
auditLogRepository.save(newAuditLog);
return newAuditLog;
    }

}
