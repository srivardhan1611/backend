package com.hostelfinder.repository;

import com.hostelfinder.entity.SavedHostel;
import com.hostelfinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedHostelRepository extends JpaRepository<SavedHostel, Long> {
    List<SavedHostel> findByStudent(User student);
    Optional<SavedHostel> findByStudentAndHostelHostelId(User student, Long hostelId);
}
