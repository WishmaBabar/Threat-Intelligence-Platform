# Development of Security System using Microservices

A microservice-based threat intelligence platform that ingests raw threat text, extracts IP/domain IOCs, scores them, and stores results in MySQL.

## Quick Start

### 1. Prepare environment file

edit `.env` and set:

```env
THREAT_SOURCE_MODE=hybrid
ABUSEIPDB_API_KEY=your_abuseipdb_key_here
ALIENVAULT_API_KEY=your_alienvault_key_here
```

If you want mock data only:

```env
THREAT_SOURCE_MODE=mock
```

If you want real data only:

```env
THREAT_SOURCE_MODE=real
```

### 2. Start the services

```powershell
docker compose --env-file .env up --build
```

### 3. Start the dashboard

```powershell
cd analytics-dashboard
npm install
npm run dev
```

Open the dashboard at `http://localhost:3000`.

## REST APIs

- Database Service: `http://localhost:8088/api/iocs`
- Analytics Summary: `http://localhost:8088/api/analytics/summary`
- Mock Threat API: `http://localhost:8081/api/threats`
- Ranking Service: `http://localhost:8087/api/score`

## Notes

- The threat feed is intentionally simulated for reproducible lab deployment, representing AbuseIPDB and AlienVault sources.

- Contribution Statement
Wishma Babar
➢ Registration: B23F0001SE030
➢ Role: Team Lead & Developer

Responsibility: Ingestion Service, Kafka Producer Service, Mock Services, Kafka/Zookeeper configuration, Docker Compose setup, Architecture diagrams, Next.js Dashboard (charts, filters, statistics), Testing and validation
Hafra Zaheer
➢ Registration: B23F0001SE032
➢ Role: Documentation Lead

Responsibility: Processing Service, Ranking Service, Database Service, MySQL schema design, REST API endpoints, Extraction Service, Data Flow Diagram.
