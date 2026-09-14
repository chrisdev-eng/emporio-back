package com.chrisdev.eng.pdvediel.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}