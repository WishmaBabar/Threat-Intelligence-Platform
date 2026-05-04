# AbuseIPDB and AlienVault Integration Guide

## Overview
The CCP Threat Intelligence System now supports real integration with AbuseIPDB and AlienVault threat intelligence APIs, while maintaining backward compatibility with mock data for testing and demonstrations.

---

## How It Works

### Threat Source Modes

The system supports **3 operational modes**:

| Mode | Description | Use Case |
|------|-------------|----------|
| **mock** | Returns simulated threat data | Development, testing, demos (default) |
| **real** | Fetches from real APIs only | Production (requires valid API keys) |
| **hybrid** | Real APIs with mock fallback | Production with fallback (resilient) |

---

## Getting Started with Real APIs

### Step 1: Register for Free API Keys

#### AbuseIPDB
- Visit: https://www.abuseipdb.com/register
- Create a free account
- Generate API key from the dashboard
- **Features**: Check IP addresses for abuse history, get reports

#### AlienVault OTX (Open Threat Exchange)
- Visit: https://otx.alienvault.com/
- Create a free account
- Generate API key from settings
- **Features**: Check domains and IPs for known threats

### Step 2: Configure the System

#### Option A: Using .env File (Recommended)

1. Copy the example configuration:
```bash
cp .env.example .env
```

2. Edit `.env` and add your API keys:
```bash
THREAT_SOURCE_MODE=hybrid
ABUSEIPDB_API_KEY=your_abuseipdb_api_key_here
ALIENVAULT_API_KEY=your_alienvault_api_key_here
```

3. Run with environment variables:
```bash
docker compose --env-file .env up --build
```

#### Option B: Using Command Line
```bash
docker compose \
  -e THREAT_SOURCE_MODE=hybrid \
  -e ABUSEIPDB_API_KEY=your_key \
  -e ALIENVAULT_API_KEY=your_key \
  up --build
```

#### Option C: Edit docker-compose.yml Directly
```yaml
mock-threat-api:
  environment:
    THREAT_SOURCE_MODE: hybrid
    ABUSEIPDB_API_KEY: your_actual_key_here
    ALIENVAULT_API_KEY: your_actual_key_here
```

### Step 3: Verify Configuration

Check the threat API status endpoint:
```bash
curl http://localhost:8081/api/status
```

Example response:
```json
{
  "service": "mock-threat-api",
  "status": "running",
  "threat_sources": "Mode: HYBRID | AbuseIPDB: CONFIGURED | AlienVault: CONFIGURED"
}
```

---

## System Architecture

### How Real APIs Are Integrated

```
┌─────────────────────────────────────────────────────────────┐
│                    Threat Intelligence Flow                  │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  1. Ingestion Service (every 30 seconds)                    │
│     ↓                                                         │
│  2. Calls Mock Threat API endpoint (/api/threats)           │
│     ↓                                                         │
│  3. Mock Threat API Service (ThreatService)                 │
│     ├─ Mode: MOCK → Returns simulated data                  │
│     ├─ Mode: REAL → Calls real APIs:                        │
│     │  ├─ AbuseIPDB API (with API key)                      │
│     │  └─ AlienVault OTX API (with API key)                 │
│     └─ Mode: HYBRID → Real APIs + Mock fallback             │
│     ↓                                                         │
│  4. Returns unified threat list with source attribution      │
│     ↓                                                         │
│  5. Extraction Service continues processing...              │
│     ↓                                                         │
│  6. IOCs extracted and enriched through pipeline             │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## API Response Structure

Both real and mock APIs return data in this format:

```java
public class Threat {
    private String id;           // Unique identifier
    private String source;       // "AbuseIPDB" or "AlienVault"
    private String raw;          // Raw threat data (IP, domain, description)
}
```

**Example API Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "source": "AbuseIPDB",
    "raw": "192.168.1.100 - Detected as malicious by AbuseIPDB"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "source": "AlienVault",
    "raw": "evil.com - Detected as malicious by AlienVault"
  }
]
```

---

## Code Example: How Real APIs Are Called

### In ThreatService.java:

```java
// When mode is "real" or "hybrid":
private List<Threat> fetchFromAbuseIPDB() {
    if (abuseIPDBKey == null || abuseIPDBKey.isEmpty()) {
        System.out.println("API key not configured, skipping");
        return new ArrayList<>();
    }
    
    // Calls: https://api.abuseipdb.com/api/v2/check
    // with suspicious IPs: 192.168.1.100, 10.0.0.5, etc.
    // Returns threats with source="AbuseIPDB"
}

private List<Threat> fetchFromAlienVault() {
    if (alienVaultKey == null || alienVaultKey.isEmpty()) {
        System.out.println("API key not configured, skipping");
        return new ArrayList<>();
    }
    
    // Calls: https://otx.alienvault.com/api/v1/...
    // with suspicious domains: evil.com, malware.xyz, etc.
    // Returns threats with source="AlienVault"
}
```

---

## Features Breakdown

### What Data is Extracted?

**AbuseIPDB Provides:**
- IP reputation scores
- Report count for each IP
- Last reported date
- Abuse types (malware, spam, phishing, etc.)

**AlienVault OTX Provides:**
- Domain reputation
- Known malicious domains
- Associated threat indicators
- Threat type classification

### Processing Pipeline After Integration

1. **Real APIs fetch** threat indicators (IPs and domains)
2. **Extraction Service** uses regex to find IPs/domains in the threat text
3. **Kafka Producer** publishes structured IOC data
4. **Processing Service** validates and scores each IOC
5. **Ranking Service** adds severity scores
6. **Database Service** stores enriched IOCs with metadata

---

## Troubleshooting

### Issue: "API key not configured, skipping"
- **Cause**: Environment variable not set or empty
- **Fix**: Ensure API keys are properly passed to the docker container

### Issue: API call fails with 401/403
- **Cause**: Invalid or expired API key
- **Fix**: Check API key validity, regenerate if needed on the service dashboard

### Issue: Too many API requests (rate limit)
- **Cause**: Exceeding free tier rate limits
- **Fix**: Consider upgrading to paid tier or implement request throttling

### Issue: Services still use mock data despite real mode enabled
- **Cause**: Invalid API keys or API service down
- **Fix**: Check logs with `docker compose logs mock-threat-api`, verify API keys, enable hybrid mode for fallback

---

## Running the System

### Default (Mock Mode - No Keys Required)
```bash
docker compose up --build
```

### With Real APIs (Requires Valid Keys)
```bash
# Create .env file with your API keys
docker compose --env-file .env up --build
```

### With Hybrid Mode (Real APIs + Mock Fallback)
```bash
docker compose \
  -e THREAT_SOURCE_MODE=hybrid \
  -e ABUSEIPDB_API_KEY=your_key \
  -e ALIENVAULT_API_KEY=your_key \
  up --build
```

---

## Demo Talking Points for Viva

1. **"The system now supports real threat intelligence sources"**
   - Explain THREAT_SOURCE_MODE configuration options

2. **"AbuseIPDB and AlienVault are integrated as per the proposal requirements"**
   - Show the ThreatService that calls both APIs
   - Explain how API keys are configured

3. **"The system gracefully handles missing API keys"**
   - Mock mode provides fallback
   - Hybrid mode ensures resilience

4. **"Real threat data flows through the same pipeline"**
   - Whether from mock or real APIs, downstream services work identically
   - Data structure remains consistent

5. **"No existing functionality is broken"**
   - Default behavior is unchanged (mock mode)
   - Real API integration is opt-in via configuration

---

## Expected Outcomes (Proposal Compliance)

✅ **Integrates external threat intelligence sources** (AbuseIPDB & AlienVault)
✅ **API keys properly configured and secured** (via environment variables)
✅ **Extracts IP addresses and domains** from threat data
✅ **Processes through distributed Kafka pipeline**
✅ **Maintains backward compatibility** with mock data
✅ **No breach of existing functionality**
✅ **Supports real-time threat processing**

---

## Next Steps

1. Register for free API keys on both services
2. Copy `.env.example` to `.env`
3. Add your API keys to `.env`
4. Run: `docker compose --env-file .env up --build`
5. Verify with: `curl http://localhost:8081/api/status`
6. Demo shows real threat data flowing through the pipeline
