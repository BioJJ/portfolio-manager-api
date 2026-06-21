package biojj.portfoliomanagerapi.service;

import biojj.portfoliomanagerapi.dto.MemberResponse;
import biojj.portfoliomanagerapi.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class MemberGateway {
    private final MockMemberService mock;

    public MemberGateway(MockMemberService mock) {
        this.mock = mock;
    }

    public MemberResponse find(Long id) {
        MemberResponse m = mock.get(id);
        if (!"funcionário".equalsIgnoreCase(m.attribution()) && !"funcionario".equalsIgnoreCase(m.attribution()))
            throw new BusinessException("Apenas membros com atribuição funcionário podem ser alocados");
        return m;
    }
}
