package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import id.ac.ui.cs.gatherlove.admin.model.DashboardStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminFacade adminFacade;

    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatistics> getDashboardStatistics() {
        return ResponseEntity.ok(adminFacade.getDashboardStatistics());
    }
}