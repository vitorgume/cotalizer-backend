package com.gumeinteligenciacomercial.orcaja.application.usecase.cpf_cnpj;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator  implements ConstraintValidator<CPF, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        if (cpf == null || cpf.isEmpty()) return false;

        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++)
                soma += (cpf.charAt(i) - '0') * (10 - i);

            int digito1 = 11 - (soma % 11);
            digito1 = (digito1 >= 10) ? 0 : digito1;

            soma = 0;
            for (int i = 0; i < 10; i++)
                soma += (cpf.charAt(i) - '0') * (11 - i);

            int digito2 = 11 - (soma % 11);
            digito2 = (digito2 >= 10) ? 0 : digito2;

            return digito1 == (cpf.charAt(9) - '0') && digito2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }
}
