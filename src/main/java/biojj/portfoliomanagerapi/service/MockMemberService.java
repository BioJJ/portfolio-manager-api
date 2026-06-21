package biojj.portfoliomanagerapi.service;

import biojj.portfoliomanagerapi.dto.MemberRequest;
import biojj.portfoliomanagerapi.dto.MemberResponse;
import biojj.portfoliomanagerapi.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockMemberService {
    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, MemberResponse> members = new LinkedHashMap<>();

    public MemberResponse create(MemberRequest request) {
        long id = sequence.incrementAndGet();
        MemberResponse member = new MemberResponse(id, request.name(), request.attribution());
        members.put(id, member);
        return member;
    }

    public MemberResponse get(Long id) {
        MemberResponse m = members.get(id);
        if (m == null) throw new NotFoundException("Membro externo não encontrado: " + id);
        return m;
    }

    public List<MemberResponse> list() {
        return List.copyOf(members.values());
    }
}
