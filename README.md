# Development of Security System using Microservices

This project implements a distributed threat intelligence platform for ingesting, extracting, ranking, and storing Indicators of Compromise (IOCs).

## CCP Compliance Summary

- Implements a microservices architecture with event-driven Kafka communication and REST APIs.
- Uses MySQL for persistent IOC storage.
- Simulates external threat intelligence sources for AbuseIPDB and AlienVault through the mock threat API.
- Processes raw threat telemetry, extracts IP and domain IOCs, validates entries, enriches severity scores, and stores results.
- Provides REST query and analytics endpoints plus a responsive dashboard.

## Architecture

- **Ingestion Service**: fetches raw threat records from the mock threat API and publishes raw JSON to Kafka.
- **Extraction Service**: consumes raw data, extracts IPs and domains, then forwards IOC payloads to the Kafka producer service.
- **Kafka Producer Service**: publishes IOC records to the `iocs` Kafka topic.
- **Processing Service**: consumes IOCs, validates values, calls the ranking service, and publishes enriched IOCs to Kafka.
- **Ranking Service**: forwards IOC scoring requests to the external ranking API and returns severity scores.
- **Database Service**: consumes enriched IOCs, persists them in MySQL, and exposes query/analytics REST endpoints.
- **Mock Threat API**: simulates external intelligence sources and provides sample threat records.
- **Mock Ranking API**: simulates an external scoring service used by the ranking pipeline.
- **Analytics Dashboard**: responsive web UI for IOC insights and severity analytics.

## System Workflow

1. Ingestion service fetches threat data from the mock threat API.
2. Extraction service parses JSON and extracts IP/domain IOCs.
3. Kafka streams IOC payloads between services.
4. Processing service validates and filters the IOCs.
5. Ranking service sends IOC data to the external ranking API.
6. Severity scores are returned and attached to each IOC.
7. Database service stores enriched records in MySQL.
8. Analytics dashboard and REST endpoints provide query and reporting capabilities.

## Technologies

- Java Spring Boot
- Apache Kafka
- MySQL
- Docker Compose
- Next.js, TailwindCSS, and Recharts

## Running the Project

### Prerequisites

- Docker & Docker Compose
- Node.js (for the dashboard)

### Start services

```bash
docker compose up --build
```

### Start dashboard

```bash
cd analytics-dashboard
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

## REST APIs

- Database Service: `http://localhost:8088/api/iocs`
- Analytics Summary: `http://localhost:8088/api/analytics/summary`
- Mock Threat API: `http://localhost:8081/api/threats`
- Ranking Service: `http://localhost:8087/api/score`

## Notes

- The threat feed is intentionally simulated for reproducible lab deployment, representing AbuseIPDB and AlienVault sources.
