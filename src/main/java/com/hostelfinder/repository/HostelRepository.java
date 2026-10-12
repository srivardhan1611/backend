package com.hostelfinder.repository;

import com.hostelfinder.entity.Hostel;
import com.hostelfinder.entity.Status;
import com.hostelfinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HostelRepository extends JpaRepository<Hostel, Long> {
    List<Hostel> findByCollegeCollegeIdAndStatus(Long collegeId, Status status);
    long countByCollegeCollegeId(Long collegeId);
    List<Hostel> findByOwner(User owner);
    List<Hostel> findByStatus(Status status);
}
