<h1 align="center">
  <img style="vertical-align:middle" alt="image" width="400"
  src="./docs/imgs/logo.png">
</h1>
<p align="center">
  <i>Open-Source Lending Infrastructure</i>
</p>


[Corefin](https://corefin.com) is a highly scalable open-source loan management system that allows you to build and launch lending products. It uses the Actual/365 method for calculating interest. See information from the
[CFPB](https://www.consumerfinance.gov/rules-policy/regulations/1030/7/).

<p align="center">
   <a href="https://join.slack.com/t/corefin-community/shared_invite/zt-2emg4brbp-tXuIKP_fWLdOphBtVG~rcA"><img src="https://img.shields.io/badge/Corefin%20Slack%20Community-8A2BE2"></a>
   <a href="(https://github.com/getcorefin/corefin/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
   <a href="https://www.ycombinator.com"><img src="https://img.shields.io/badge/Backed%20by-Y%20Combinator-%23f26625"></a>
</p>


<!--
<h4 align="center">
    <p>
        <a href="#prerequisites">Prerequisites</a> |
        <a href="#getting-started">Getting Started</a> |
        <a href="#roadmap">Roadmap</a> |
    <p>
</h4>
-->


## Getting started

### Prerequisites
1. Make sure you have Docker installed on your machine.
2. Make sure you have Java17 installed on your machine.

To install Java17, run the following commands in the shell:

```
# Install Java17
brew install openjdk@17

# Check which Java versions you have installed
/usr/libexec/java_home -V

# Set your java version to Java17
export JAVA_HOME=$(/usr/libexec/java_home -v 17.0.10)
```

### Running locally
To start running Corefin, run the commands in the shell:
```
# Get the code
git clone https://github.com/getcorefin/corefin.git

# Go to the folder
cd corefin

# Create docker stubs
make docker-stubs

# Perform DB migrations
make db-migrate

# Start
make run-server
```

### Using Corefin

#### Create a loan
Here's an example of creating a 6 month term loan for $1000 at a 5% target interest rate.
```
curl --location 'http://localhost:8080/loans' \
--header 'Content-Type: application/json' \
--data '{
    "term": 6,
    "originatedAmount": 1000.00,
    "currency": "USD",
    "targetInterestRate": 0.05,
    "effectiveInterestRate": 0.05,
    "externalReference": "orderId_12345",
    "startDate": "2024-03-01",
    "endDate": "2024-09-01",
    "timezone": "America/Los_Angeles",
    "region": "US",
    "state": "CA"
}'
```

#### Make a payment
```
curl --location 'http://localhost:8080/payments/$LOAN_ID/makePayment' \
--header 'Content-Type: application/json' \
--data '{
    "amount": "171.56",
    "paymentType": "PAYMENT",
    "paymentDateTime" : "2024-03-01T12:00:00Z"
}'
```

#### View the installment schedule
```
# Connect to the DB
make db-shell

# Execute the following SQL
mysql> select loan_installment_id, num_term, principal_amount, interest_amount from loan_installment;
+--------------------------------------+----------+------------------+-----------------+
| loan_installment_id                  | num_term | principal_amount | interest_amount |
+--------------------------------------+----------+------------------+-----------------+
| 132b124a-e247-11ee-bf07-0242ac140002 |        1 |           129.62 |          424.66 |
| 132d3525-e247-11ee-bf07-0242ac140002 |        2 |           196.59 |          357.69 |
| 132e2291-e247-11ee-bf07-0242ac140002 |        3 |           268.15 |          286.13 |
| 132eab9b-e247-11ee-bf07-0242ac140002 |        4 |           405.64 |          166.70 |
| 17ec7677-e247-11ee-bf07-0242ac140002 |        1 |           129.62 |          424.66 |
| 17edbbf7-e247-11ee-bf07-0242ac140002 |        2 |           196.59 |          357.69 |
```

## Roadmap
Check out our [public roadmap](https://github.com/orgs/getcorefin/projects/1/views/4) for a more detailed view.

At a high level, we're working on the following features next:
- [ ]  Early/on-time/late payments
- [ ]  Custom installment amount payments
- [ ]  Support for other lending products such as MCAs
- [ ]  Robust reporting API

... and many more! 

## Corefin community
* Join our [Slack community](https://join.slack.com/t/corefin-community/shared_invite/zt-2emg4brbp-tXuIKP_fWLdOphBtVG~rcA) if you need help or want to chat
* Contact us at hello@corefin.com
* Follow us on [Twitter](https://twitter.com/GetCorefin) for the latest news


## Contributions and development environment
Coming soon!



