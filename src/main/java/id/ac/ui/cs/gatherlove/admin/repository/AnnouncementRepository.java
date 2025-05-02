package id.ac.ui.cs.gatherlove.admin.repository;

import id.ac.ui.cs.gatherlove.admin.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    List<Announcement> findAllByOrderByCreatedAtDesc();
}