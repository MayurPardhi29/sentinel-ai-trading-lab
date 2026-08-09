# Sentinel AI Trading Lab ☸️📈

A cloud-native trading strategy playground built using React, Spring Boot, Docker, and Kubernetes.

Sentinel allows users to analyze stock market behavior, test trading indicators, visualize signals, and simulate strategies through a modern dashboard.

---

# Sentinel

---

# Preview

```text
User
 ↓
Frontend (React + TypeScript)
 ↓
Strategy Engine
 ↓
Market Data Service
 ↓
External Market Provider
```

---

# Features

## Market Visualization

✔ Candlestick-style market view  
✔ Volume display  
✔ EMA overlays  
✔ Multiple timeframes  

Supported:

- 1 Day
- 1 Week
- 1 Month

---

## Strategy Playground

Run indicators on historical data.

Implemented:

### EMA
- Configurable Fast EMA
- Configurable Slow EMA
- Signal confidence

Example:

```json
{
  "pattern":"ema",
  "detected":true,
  "confidence":92
}
```

### FVG (Fair Value Gap)

Example:

```json
{
 "pattern":"fvg",
 "detected":true,
 "confidence":76
}
```

---

## Sentinel Consensus

Combine multiple indicators.

Example:

```json
{
  "ema": "BUY",
  "fvg": "BUY",
  "agreement": 100,
  "recommendation": "STRONG BUY",
  "interval": "15min",
  "entryHint": "Entry: next 15-minute candle open."
}
```

`entryHint` only reads as an actual entry instruction when the recommendation is
actionable (`STRONG BUY` / `HIGH CONFIDENCE`) — otherwise it explicitly tells you
nothing was confirmed, so the dashboard never implies a trade that wasn't there.

---

## Backtesting

Simulates strategy performance with real trade mechanics — each signal enters a
position with a configurable stop-loss and take-profit, held for up to a
configurable max holding period, rather than closing every trade after a single
candle.

Metrics:

- Trades / Wins / Losses / Win Rate
- Profit Factor (gross profit ÷ gross loss, in currency — not a trade-count ratio)
- Total Return %
- Buy & Hold benchmark return, for the same period
- Max Drawdown %
- Sharpe Ratio
- Expectancy per trade
- Full trade history (entry/exit price, exit reason, P&L%)
- Equity curve, rendered as a chart alongside price action

Example:

```json
{
  "strategy": "ema",
  "trades": 80,
  "wins": 30,
  "losses": 50,
  "winRate": 37.5,
  "profitFactor": 1.17,
  "totalReturnPct": 18.11,
  "benchmarkReturnPct": 174.75,
  "maxDrawdownPct": 15.23,
  "sharpeRatio": 1.36,
  "expectancy": 22.64
}
```

Trade entry/exit points are also plotted directly on the price chart (blue =
entry, green = winning exit, red = losing exit).

---

## AI Insight

Turns a backtest + consensus result into a plain-language summary — e.g. *"This
strategy underperformed buy-and-hold by 156pp, driven mainly by early
take-profit exits during a strong uptrend."*

- Backed by Groq's free-tier inference API (Llama 3.3 70B) when `GROQ_API_KEY` is set.
- The prompt is grounded in the actual computed numbers (win rate, profit
  factor, drawdown, benchmark comparison) — the model is instructed to
  interpret those figures only, not invent price predictions.
- Falls back automatically to a deterministic, dependency-free rule-based
  summary if no API key is configured or the call fails, so the feature never
  breaks the app. The response is tagged `"source": "AI"` or
  `"source": "RULE_BASED"` so it's always clear which one produced it.

```bash
curl -X POST http://localhost:8081/api/strategy/insight/ema/AAPL \
  -H "Content-Type: application/json" \
  -d '{"interval":"1day"}'
```

---

# Tech Stack

## Frontend

- React
- TypeScript
- Axios
- Recharts
- CSS

## Backend

- Java 21
- Spring Boot
- JUnit 5
- Spring Boot Actuator
- Mockito
- Groq API (free-tier inference) for AI Insight generation

## DevOps

- Docker
- Docker Compose
- Kubernetes (Kind)
- Helm

## Testing

Unit tests implemented using:

- JUnit 5
- Mockito

Coverage includes:

- Consensus Service
- Strategy Resolver
- Chart Service
- Backtest Service

Run tests:

```bash
mvn test
```

---

# Project Structure

```text
sentinel-ai-trading-lab/

frontend/
└── sentinel-ui/

backend/

├── market-data-service/

└── strategy-engine/

k8s/

docker-compose.yml

README.md
```

---

# Environment Variables

| Variable | Service | Default | Purpose |
|---|---|---|---|
| `TWELVEDATA_API_KEY` | market-data | `demo` | API key for TwelveData |
| `TWELVEDATA_DEFAULT_OUTPUTSIZE` | market-data | `300` | Candles fetched when a caller doesn't specify |
| `TWELVEDATA_MAX_OUTPUTSIZE` | market-data | `500` | Server-side cap regardless of what's requested |
| `MARKET_DATA_CACHE_TTL_SECONDS` | market-data | `60` | TTL for the in-memory candle cache (reduces load on the rate-limited free-tier API) |
| `MARKET_DATA_URL` | strategy-engine | `http://localhost:8080` | Where to reach market-data-service |
| `GROQ_API_KEY` | strategy-engine | unset | Enables the AI Insight feature; falls back to a rule-based summary if unset |
| `GROQ_MODEL` | strategy-engine | `llama-3.3-70b-versatile` | Which Groq-hosted model to call |

---

# Run Locally

## Build Services

```bash
cd backend/market-data-service
mvn clean package

cd ../strategy-engine
mvn clean package
```

---

## Start Containers

```bash
docker compose up --build
```

Open:

```text
http://localhost:5173
```

---

# Kubernetes Deployment

Two ways to deploy to the Kind cluster below: raw manifests (`k8s/`, quick and
simple) or the Helm chart (`helm/sentinel/`, parameterized — recommended, since
it's what actually wires in `GROQ_API_KEY` and the cache/outputsize settings;
the raw manifests predate those features and don't set them).

## 1. Build and load images into Kind

```bash
kind create cluster --name sentinel
```

```bash
docker build -t sentinel-ai-trading-lab-market-data ./backend/market-data-service
docker build -t sentinel-ai-trading-lab-strategy-engine ./backend/strategy-engine
docker build -t sentinel-ai-trading-lab-frontend ./frontend/sentinel-ui

kind load docker-image sentinel-ai-trading-lab-market-data --name sentinel
kind load docker-image sentinel-ai-trading-lab-strategy-engine --name sentinel
kind load docker-image sentinel-ai-trading-lab-frontend --name sentinel
```

## 2a. Deploy with Helm (recommended)

```bash
helm install sentinel ./helm/sentinel \
  --set secrets.twelveDataApiKey=$TWELVEDATA_API_KEY \
  --set secrets.groqApiKey=$GROQ_API_KEY
```

Upgrade after a code change (rebuild + reload the image first, then):

```bash
helm upgrade sentinel ./helm/sentinel \
  --set secrets.twelveDataApiKey=$TWELVEDATA_API_KEY \
  --set secrets.groqApiKey=$GROQ_API_KEY
```

Uninstall:

```bash
helm uninstall sentinel
```

All tunables (replica counts, ports, cache TTL, model name) live in
`helm/sentinel/values.yaml` — override any of them with `--set` or a custom
values file instead of editing the chart directly.

## 2b. Deploy with raw manifests (legacy/simple path)

```bash
kubectl apply -R -f k8s/
```

Note: this path does **not** set `GROQ_API_KEY` or the market-data caching env
vars — it predates those features. The AI Insight feature will silently fall
back to its rule-based summary under this deployment path. Use Helm if you want
the full feature set.

## Verify

```bash
kubectl get pods
```

Expected:

```text
frontend

market-data

strategy-engine
```

Access:

​```bash
kubectl port-forward service/frontend 5173:5173
​```

The frontend's nginx reverse-proxies `/api/` requests to `strategy-engine`
internally (see `frontend/sentinel-ui/nginx.conf.template`), so a single
port-forward is enough — you no longer need to separately forward
`strategy-engine` for the app to work. This exists specifically because the
browser can't resolve Kubernetes-internal service names like
`strategy-engine` itself; only pods inside the cluster (like nginx here) can.

Open:

​```text
http://localhost:5173
​```

---

# API Examples

## Playground

```http
POST /api/strategy/playground/ema/AAPL
```

Body:

```json
{
 "fast":9,
 "slow":21,
 "interval":"1day"
}
```

---

## Backtest

```http
POST /api/strategy/backtest/ema/AAPL
```

---

## Consensus

```http
POST /api/strategy/consensus/AAPL
```

---

## Observability

Spring Boot Actuator is enabled.

Health Endpoints:

GET /actuator/health
GET /actuator/info

---

# Future Roadmap

- Live streaming candles
- WebSocket updates
- Replay mode
- Equity curve
- More indicators
- Alerts
- Authentication

---

# Learnings

This project explores:

- Microservices
- REST APIs
- Frontend integration
- Strategy modeling
- Containerization
- Kubernetes networking
- Service discovery

---

# Version

**Sentinel V1**
