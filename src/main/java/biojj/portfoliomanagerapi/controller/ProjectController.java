package biojj.portfoliomanagerapi.controller;

import biojj.portfoliomanagerapi.dto.ProjectRequest;
import biojj.portfoliomanagerapi.dto.ProjectResponse;
import biojj.portfoliomanagerapi.dto.StatusUpdateRequest;
import biojj.portfoliomanagerapi.model.ProjectStatus;
import biojj.portfoliomanagerapi.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projetos")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest r) {
        ProjectResponse p = service.create(r);
        return ResponseEntity.created(URI.create("/api/projects/" + p.id())).body(p);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id) {
        return service.find(id);
    }

    @GetMapping
    public Page<ProjectResponse> list(@RequestParam(required = false) String name, @RequestParam(required = false) ProjectStatus status, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startFrom, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTo, @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, status, startFrom, startTo, pageable);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest r) {
        return service.update(id, r);
    }

    @PatchMapping("/{id}/status")
    public ProjectResponse status(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest r) {
        return service.changeStatus(id, r.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
