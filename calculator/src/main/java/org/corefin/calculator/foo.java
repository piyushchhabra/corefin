//        while (!queue.isEmpty()) {
//            CalculationEvent event = queue.poll();
//            if (event.calculationType.equals(CalculationType.PAYMENT)) {
//                if (!event.payment.isPresent()) {
//                   LOGGER.severe("Payment event does not have payment. This is probably a bug");
//                   continue;
//                }
//                currentPayment = event.payment().get();
//                // Enterprise feature: Custom payment amounts
//                if (!currentPayment.amount().equals(installmentAmount)) {
//                    LOGGER.severe("Payment amount does not match with the next unpaid installment. Installment" +
//                            "amount: %s. Payment Amount: %s".formatted(installmentAmount, currentPayment.amount()));
//                    throw new RuntimeException("Payment amount does not match next unpaid installment amount");
//                }
//
//                long numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, currentPayment.paymentDateTime());
//
//                BigDecimal interestAmountAccruedAndPaid = dailyRate.multiply(BigDecimal.valueOf(numDays))
//                        .multiply(runningBalance.outstandingPrincipal)
//                        .setScale(2, RoundingMode.HALF_UP);
//                BigDecimal principalAmountLeftoverAndPaid = installmentAmount.subtract(interestAmountAccruedAndPaid);
//
//                PaymentStatus paymentStatus = getPaymentStatus(currentPayment.paymentDateTime().toLocalDate(),
//                        currentInstallment.dueDate());
////                // Only add installments in dummy payment portion
////                updatedInstallments.add(new Installment(
////                        currentInstallment.installmentId(),
////                        currentInstallment.loanId(),
////                        currentInstallment.numTerm(),
////                        principalAmountLeftoverAndPaid,
////                        interestAmountAccruedAndPaid,
////                        currentInstallment.startDate(),
////                        currentInstallment.dueDate(),
////                        currentPayment.paymentDateTime().toLocalDate(),
////                        InstallmentStatus.PAID // TODO: Update to PAID, EARLY, LATE
////                ));
//                List<InstallmentMapping> installmentMappings = new ArrayList<>();
//                InstallmentMapping installmentMapping = new InstallmentMapping(
//                        currentInstallment.installmentId(),
//                        currentPayment.paymentId(),
//                        principalAmountLeftoverAndPaid,
//                        interestAmountAccruedAndPaid
//                );
//                installmentMappings.add(installmentMapping);
//                currentPayment = currentPayment.withInstallmentMappings(installmentMappings);
//                updatedPayments.add(currentPayment);
//
//                runningBalance.setOutstandingPrincipal(runningBalance.getOutstandingPrincipal().subtract(principalAmountLeftoverAndPaid));
//                runningBalance.setAccruedInterest(runningBalance.getAccruedInterest().subtract(interestAmountAccruedAndPaid));
//                runningBalance.setAccruedInterest(runningBalance.getAccruedInterest().subtract(interestAmountAccruedAndPaid));
//                runningBalance.setTargetAPR(runningBalance.targetAPR);
//                runningBalance.setCurrentCalculationDate(currentPayment.paymentDateTime().toLocalDate());
//
//                if (runningBalance.getOutstandingPrincipal().compareTo(BigDecimal.ZERO) == 0) {
//                    shouldCloseLoan = true;
//                }
//
//                // TODOs:
//                // x check payment amount
//                // x Check if the amount is the same as EMI
//                // - Calculate number of days since RunningBalance.calculationDate
//                // x Check if this is EARLY, ontime, or late
//                // x Generate a payment<> installment mapping that includes p/i breakdown
//                // x Check if this is the last payment to pay off the loan
//                // - Update InstallmentStatus to PAID, EARLY, LATE, instead of just paid
//                // - Fix installmentAmount for last installment
//                // x Update RunningBalance
//
//            }
//            // Installment
//            else {
//                currentInstallment = event.installment.get();
//                BigDecimal currentInstallmentAmount = currentInstallment.principalAmount().add(currentInstallment.interestAmount());
//                long numDays = 0;
//                // Case 1: If calculation date is within this installment
//                if (currentInstallment.startDate().isBefore(calculationDate) &&
//                        (currentInstallment.dueDate().isAfter(calculationDate)
//                                || currentInstallment.dueDate().isEqual(calculationDate))) {
//                    // Compute interest up to calculation date
//                    numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, calculationDate);
//                } // Case 2: If calculation date is after this installment, so we accrue the full installment
//                else if (currentInstallment.endDate().isBefore(calculationDate)) {
//                    numDays = ChronoUnit.DAYS.between(runningBalance.currentCalculationDate, currentInstallment.dueDate());
//                }
//
//                if (runningBalance.paidPrincipal.add(runningBalance.paidInterest).compareTo(currentInstallmentAmount) >= 0) {
//                    // We paid off the installment
//                    // Interest = Math.min(RB.interestPaid, installmentAmount)
//                    // Principal = InstallmentAmount.subtract(Interest)
//                    BigDecimal interestPaid = runningBalance.getPaidInterest().min(currentInstallmentAmount);
//                    BigDecimal principalPaid = currentInstallmentAmount.subtract(interestPaid);
//                    // This won't work for late payments, since the payment is coming in after
//                    updatedInstallments.add(new Installment(
//                            currentInstallment.installmentId(),
//                            currentInstallment.loanId(),
//                            currentInstallment.numTerm(),
//                            principalPaid,
//                            interestPaid,
//                            currentInstallment.startDate(),
//                            currentInstallment.dueDate(),
//                            runningBalance.getCurrentCalculationDate(), // TODO: use this to whether this was early or late
//                            InstallmentStatus.PAID // TODO: Update to PAID, EARLY, LATE
//                    ));
//                }
//                // Case 3 : If calculation date is before this installment, we've already processed so nothing to do
//                BigDecimal interestAmountAccrued = dailyRate.multiply(BigDecimal.valueOf(numDays))
//                        .multiply(runningBalance.outstandingPrincipal)
//                        .setScale(2, RoundingMode.HALF_UP);
//                // TODO: This is wrong, because this implies 0 interest, we need to recalculate it.
//                // TODO: We need to create and estimate the Installment amounts
//                BigDecimal principalAmountLeftOver = currentInstallmentAmount.subtract(interestAmountAccrued);
//
//                // Subtract outstandingPrincipal, as we go through the loan to mimic on-time payment
//                runningBalance.setOutstandingPrincipal(runningBalance.outstandingPrincipal.subtract(principalAmountLeftOver));
//                runningBalance.setAccruedInterest(runningBalance.getAccruedInterest().add(interestAmountAccrued));
//                runningBalance.setTargetAPR(runningBalance.targetAPR);
//                runningBalance.setCurrentCalculationDate(currentInstallment.dueDate());
//            }
//
//        }
