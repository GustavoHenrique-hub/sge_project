package com.enterprise.validation;

public class DocumentsValidation {

    public static boolean cpfValidation(String cpf) {

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica tamanho
        if (cpf.length() != 11) return false;

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Cálculo do 1º dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }

            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;

            // Cálculo do 2º dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }

            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;

            // Verifica se os dígitos batem
            return digito1 == (cpf.charAt(9) - '0') &&
                    digito2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean rgValidation(String rg) {
        if (rg == null) return false;

        rg = rg.replaceAll("[^0-9Xx]", "");

        if (rg.length() != 9) return false;

        int soma = 0;
        int peso = 2;

        for (int i = 7; i >= 0; i--) {
            soma += (rg.charAt(i) - '0') * peso++;
        }

        int resto = soma % 11;
        int digito;

        if (resto == 10) {
            return rg.charAt(8) == 'X' || rg.charAt(8) == 'x';
        } else {
            digito = resto;
        }

        return digito == (rg.charAt(8) - '0');
    }

}
