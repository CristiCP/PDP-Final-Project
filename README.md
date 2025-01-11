# Sports Betting Data Processing System
## Overview
This project is a distributed sports betting data processing system that integrates with Superbet and Unibet APIs. It fetches live match data, processes match details, and consolidates results through worker nodes and a consumer service. The system is designed for efficient task distribution using RabbitMQ and scheduled execution with Quartz Scheduler.

## Features
### API Integration:

- Fetches live match data from Superbet and Unibet APIs.
- Processes individual match details, including event IDs, match names, and betting odds.

### Worker-Based Processing:

- Dedicated worker nodes for each platform (Superbet and Unibet).
- Workers retrieve match details and send results to a centralized consumer.

### Task Distribution:

- RabbitMQ ensures task queues for efficient processing.
- Separate queues for Superbet and Unibet tasks.

### Centralized Consumer:

- Consolidates all processed data.
- Logs match details for analysis and verification.

## Getting started

###  Clone the Repository: 

- Start by cloning the project repository from GitHub:



git clone https://github.com/your-username/sports-betting-processor.git
cd sports-betting-processor


### Install RabbitMQ: 

- Install and start RabbitMQ on your local system. For installation: https://www.rabbitmq.com/docs/download

Download RabbitMQ and install it.
Run RabbitMQ server:


rabbitmq-server


### Set Up the Environment:

- Ensure that Java 11+ and Maven are installed on your system.

#### Run the Superbet Service: Open a terminal and execute the following command to start the Superbet service:


mvn spring-boot:run "-Dspring-boot.run.profiles=superbet"


#### Run the Unibet Service: Open another terminal and execute the following command to start the Unibet service:


mvn spring-boot:run "-Dspring-boot.run.profiles=unibet"


No Database Configuration Needed: This project does not require any database configuration. The data processing and task management are entirely handled using RabbitMQ for message queues and worker communication.

###  Superbet Workers: 
- Process match data from the Superbet API and send it to the Superbet consumer.

### Unibet Workers: 
- Process match data from the Unibet API and send it to the Superbet consumer.

#### All results are consolidated and logged by the Superbet Consumer.

### Quartz Scheduler:

- Periodically triggers fetch jobs for Superbet and Unibet.

### Worker Queues:

#### RabbitMQ maintains separate task queues:
- SuperbetWorkerQueue: For Superbet workers.
- UnibetWorkerQueue: For Unibet workers.

### Workers:

#### Superbet Workers:
- Fetch match details from the Superbet API.
- Send processed results to the centralized Superbet Consumer.

#### Unibet Workers:
- Fetch match details from the Unibet API.
- Send processed results to the centralized Superbet Consumer.

### Superbet Consumer:
- Logs and consolidates processed match data from Superbet and Unibet.


### Cristan-Iosif Popan (Superbet API and Workers):

- Implement the Superbet Fetch Job for retrieving match data.
- Create the Superbet Worker class to process match IDs.
- Send processed data to the Superbet Consumer via RabbitMQ.
- ReadMe file

### Razvan Pop (Unibet API and Workers):

- Implement the Unibet Fetch Job for retrieving match data.
- Create the Unibet Worker class to process match IDs.
- Send processed data to the Superbet Consumer via RabbitMQ.

### Cristian Pop (RabbitMQ and Consumer):

- Set up RabbitMQ queues for task management.
- Develop the centralized Superbet Consumer to handle processed data from both Superbet and Unibet.
- Ensure proper logging and consolidation of match details.
- Diagram
