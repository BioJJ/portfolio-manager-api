package biojj.portfoliomanagerapi.model;

public enum ProjectStatus {
    EM_ANALISE, ANALISE_REALIZADA, ANALISE_APROVADA, INICIADO, PLANEJADO, EM_ANDAMENTO, ENCERRADO, CANCELADO;

    public boolean isFinalStatus() {
        return this == ENCERRADO || this == CANCELADO;
    }

    public boolean mayTransitionTo(ProjectStatus next) {
        return next == CANCELADO || (!isFinalStatus() && next.ordinal() == ordinal() + 1 && next != CANCELADO);
    }
}
