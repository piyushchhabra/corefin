package org.corefin.calculator;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;

import java.time.LocalDate;
import java.util.List;

public interface CalculatorInterface {

    public List<Installment> newInstallments(Loan loan);

    //Loan includes loan, payment, installment
    public Loan updateInstallments(Loan loan, LocalDate calculationDate);

}
