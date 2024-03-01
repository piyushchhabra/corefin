-- Create new table 'loan'
CREATE TABLE loan (
  loan_id VARCHAR(36) DEFAULT (UUID()) PRIMARY KEY,
  term INT NOT NULL,
  originated_amount DECIMAL(32, 2) NOT NULL,
  currency CHAR(3) NOT NULL,
  target_interest_rate DECIMAL(13, 10) NOT NULL,
  effective_interest_rate DECIMAL(13, 10) NOT NULL,
  external_reference VARCHAR(36),
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status VARCHAR(12) NOT NULL,
  timezone VARCHAR(36) NOT NULL,
  region VARCHAR(3) NOT NULL,
  state VARCHAR(2),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create new table 'loan_installment'
CREATE TABLE loan_installment (
  loan_installment_id VARCHAR(36) DEFAULT (UUID()) PRIMARY KEY,
  loan_id VARCHAR(36) NOT NULL,
  num_term INT NOT NULL,
  principal_amount DECIMAL(32, 2) NOT NULL,
  interest_amount DECIMAL(32, 2) NOT NULL,
  due_date DATE NOT NULL,
  status VARCHAR(12) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
);

-- Create new table 'payment'
CREATE TABLE payment (
  payment_id VARCHAR(36) DEFAULT (UUID()) PRIMARY KEY,
  loan_id VARCHAR(36) NOT NULL,
  amount DECIMAL(32, 2) NOT NULL,
  payment_type VARCHAR(12) NOT NULL,
  payment_datetime TIMESTAMP,
  timezone VARCHAR(36) NOT NULL,
  is_reversed BOOL NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
);

-- Create new table 'payment_installment_mapping'
CREATE TABLE payment_installment_mapping (
  payment_installment_mapping_id VARCHAR(36) DEFAULT (UUID()) PRIMARY KEY,
  loan_installment_id VARCHAR(36) NOT NULL,
  payment_id VARCHAR(36) NOT NULL,
  principal DECIMAL(32, 2) NOT NULL,
  interest DECIMAL(32, 2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (loan_installment_id) REFERENCES loan_installment(loan_installment_id),
  FOREIGN KEY (payment_id) REFERENCES payment(payment_id)
);