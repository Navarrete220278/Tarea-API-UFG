package sv.ufg.ordenaenlinea.model;

public enum Rol {
    EMPLEADO,
    CLIENTE;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
