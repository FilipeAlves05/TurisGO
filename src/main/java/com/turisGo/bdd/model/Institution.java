package com.turisGo.bdd.model;

public class Institution extends User {
    private String cnpj;

    public Institution() {
        super();
    }

    public String getCnpj() {
        return this.cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
