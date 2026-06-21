package biojj.portfoliomanagerapi.config;

import biojj.portfoliomanagerapi.dto.*;
import biojj.portfoliomanagerapi.model.ProjectStatus;
import biojj.portfoliomanagerapi.repository.ProjectRepository;
import biojj.portfoliomanagerapi.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DemoDataInitializer {
    @Bean
    @ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
    CommandLineRunner seedData(ProjectRepository projects, MockMemberService members, ProjectService projectService) {
        return args -> {
            if (projects.count() > 0) return;
            for (int i = 1; i <= 10; i++) members.create(new MemberRequest("Membro " + i, "funcionário"));
            create(projectService, "Modernização Financeira", 1, "85000", 2, ProjectStatus.EM_ANALISE);
            create(projectService, "Portal do Cliente", 2, "125000", 4, ProjectStatus.ANALISE_REALIZADA);
            create(projectService, "Aplicativo Mobile", 3, "220000", 5, ProjectStatus.ANALISE_APROVADA);
            create(projectService, "Integração ERP", 4, "480000", 6, ProjectStatus.INICIADO);
            create(projectService, "Data Lake", 5, "510000", 8, ProjectStatus.PLANEJADO);
            create(projectService, "Automação de Testes", 6, "95000", 3, ProjectStatus.EM_ANDAMENTO);
            create(projectService, "Migração em Nuvem", 7, "700000", 9, ProjectStatus.ENCERRADO);
            create(projectService, "BI Comercial", 8, "180000", 5, ProjectStatus.CANCELADO);
            create(projectService, "LGPD e Privacidade", 9, "150000", 4, ProjectStatus.ENCERRADO);
            create(projectService, "Central de Atendimento", 10, "300000", 6, ProjectStatus.EM_ANALISE);
        };
    }

    private void create(ProjectService service, String name, long memberId, String budget, int durationMonths, ProjectStatus target) {
        LocalDate start = LocalDate.now().minusMonths(durationMonths + 2L);
        ProjectRequest request = new ProjectRequest(name, start, start.plusMonths(durationMonths),
                target == ProjectStatus.ENCERRADO ? start.plusMonths(durationMonths) : null,
                new BigDecimal(budget), "Projeto demonstrativo: " + name, memberId, Set.of(memberId));
        Long id = service.create(request).id();
        while (service.find(id).status() != target) {
            ProjectStatus current = service.find(id).status();
            service.changeStatus(id, target == ProjectStatus.CANCELADO ? ProjectStatus.CANCELADO : ProjectStatus.values()[current.ordinal() + 1]);
        }
    }
}
