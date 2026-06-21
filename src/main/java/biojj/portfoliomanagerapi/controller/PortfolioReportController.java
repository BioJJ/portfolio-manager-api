package biojj.portfoliomanagerapi.controller;

import biojj.portfoliomanagerapi.dto.PortfolioReport;
import biojj.portfoliomanagerapi.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioReportController {
    private final ProjectService service;

    public PortfolioReportController(ProjectService service) {
        this.service = service;
    }

    @GetMapping("/report")
    public PortfolioReport report() {
        return service.report();
    }
}
