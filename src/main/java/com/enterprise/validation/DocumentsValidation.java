package com.enterprise.validation;

/**
 * Classe utilitaria com validacoes reutilizaveis de documentos e entradas de formulario.
 */
public class DocumentsValidation {

    /**
     * Valida o CPF removendo a mascara, conferindo o tamanho e recalculando os dois digitos verificadores.
     */
    public static boolean cpfValidation(String cpf) {

        // Remove qualquer pontuacao para trabalhar apenas com os digitos.
        cpf = cpf.replaceAll("[^\\d]", "");

        // CPF valido sempre precisa ter 11 digitos.
        if (cpf.length() != 11) return false;

        // Sequencias com todos os numeros iguais nao sao aceitas.
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // O primeiro digito e calculado com os 9 primeiros numeros e pesos decrescentes de 10 ate 2.
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }

            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;

            // O segundo digito reutiliza a mesma regra, agora considerando os 10 primeiros numeros.
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }

            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;

            // O CPF so e valido quando os digitos calculados batem com os digitos informados.
            return digito1 == (cpf.charAt(9) - '0') &&
                    digito2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida o RG em formato simples recalculando o digito final a partir dos oito primeiros caracteres.
     */
    public static boolean rgValidation(String rg) {
        if (rg == null) return false;

        // Mantem apenas numeros e o caractere X, que pode ser usado como verificador.
        rg = rg.replaceAll("[^0-9Xx]", "");

        if (rg.length() != 9) return false;

        int soma = 0;
        int peso = 2;

        // Multiplica os digitos da direita para a esquerda para chegar ao resto usado no verificador.
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
