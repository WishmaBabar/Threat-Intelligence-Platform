# Analytics Dashboard

This dashboard provides a responsive interface for monitoring Indicators of Compromise (IOCs) stored by the database service.

## Usage

1. Start backend services:

   ```bash

docker compose up --build

```
2.Start the dashboard frontend:
   ```bash
cd analytics-dashboard
npm install
npm run dev
```

3.Open [http://localhost:3000](http://localhost:3000).

## Features

- IOC totals and severity summary
- IP/domain breakdown
- High-severity IOC tracking
- Real-time table view of enriched IOCs
- Responsive layout for desktop and mobile screens

## Notes

- The dashboard uses icon components rather than emoji.
- It consumes data from the database service at `http://localhost:8088/api`.
