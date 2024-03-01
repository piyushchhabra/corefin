package org.corefin.calculator;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;

import java.time.LocalDate;
import java.util.List;

public class Actuarial365Calculator implements CalculatorInterface {
    Actual365CalculatorImpl actual365Calculator = new Actual365CalculatorImpl();

    @Override
    public List<Installment> newInstallments(Loan loan) {
        return this.actual365Calculator.newInstallments(loan);
    }

    @Override
    public Loan updateInstallments(Loan loan, LocalDate calculationDate) {
        return null;
    }
}