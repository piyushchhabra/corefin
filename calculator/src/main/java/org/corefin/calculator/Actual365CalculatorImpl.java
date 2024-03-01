package org.corefin.calculator;

import org.corefin.calculator.model.Installment;
import org.corefin.calculator.model.Loan;

import java.util.ArrayList;
import java.util.List;

// Compute interest via the actuarial method,
// based on Appendix J of Regulation Z.
// https://www.consumerfinance.gov/rules-policy/regulations/1026/j/
public class Actual365CalculatorImpl {
    public List<Installment> newInstallments(Loan loan) {
        return new ArrayList<>();
    }
}
