<h1 align="center">
  <img style="vertical-align:middle" height="200"
  src="./docs/imgs/logo.png">
</h1>
<p align="center">
  <i>The world's first source available lending platform</i>
</p>


Corefin is the world's first source available lending platform. It allows you to deeply customize this software
for your company's needs. It uses the Actual/365 method for calculating interest. See information from the
[CFPB](https://www.consumerfinance.gov/rules-policy/regulations/1030/7/).


Built by the engineers who built Square's first Consumer Lending product (Afterpay's Pay Monthly).

<h4 align="center">
    <p>
        <a href="#prereqs">Prerequisites</a> |
        <a href="#getting-started">Getting Started</a> |
        <a href="#coming-soon">Coming Soon</a> |
    <p>
</h4>

# Prereqs

- Make sure you have docker installed.
- Make sure you have Java17 installed.
`brew install openjdk@17`


# Getting Started
- Boot it up

`make docker-stubs`

`make db-migrate`

`make run-server`

- Create a 0% loan
```
curl --location 'http://localhost:8080/loans' \
--header 'Content-Type: application/json' \
--data '{
    "term": 6,
    "originatedAmount": 1000.00,
    "currency": "USD",
    "targetInterestRate": 0.00,
    "effectiveInterestRate": 0.00,
    "externalReference": "orderId_12345",
    "startDate": "2024-03-03",
    "endDate": "2024-03-03",
    "timezone": "America/Los_Angeles",
    "region": "US",
    "state": "CA"
}'
```
- View installment schedule
`make db-shell`
`select loan_installment_id, num_term, principal_amount, interest_amount from loan_installment;`

# Coming soon!
- Interest bearing loan installment schedules
- Accepting on-time, early and late payments
- Paid, hosted version
- BaaS integration for 1-line loan originations
- Management portal
- Auditability and reporting features

# Roadmap
1. Completing the source available LMS software.
2. Simplify your embedded finance stack by offering underwriting and hosting services
3. Build out auditability and reporting features.

Contact us at founders@corefin.org for more information.
