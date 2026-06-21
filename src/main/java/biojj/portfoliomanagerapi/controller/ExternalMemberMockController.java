package biojj.portfoliomanagerapi.controller;

import biojj.portfoliomanagerapi.dto.MemberRequest;
import biojj.portfoliomanagerapi.dto.MemberResponse;
import biojj.portfoliomanagerapi.service.MockMemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/external/members")
public class ExternalMemberMockController {
    private final MockMemberService service;

    public ExternalMemberMockController(MockMemberService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest r) {
        MemberResponse m = service.create(r);
        return ResponseEntity.created(URI.create("/api/external/members/" + m.id())).body(m);
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<MemberResponse> list() {
        return service.list();
    }
}
