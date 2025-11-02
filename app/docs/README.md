# SmishingDetectionApp — Chat Assistant (Feature Branch)

This feature adds a guided *Support Chat* with:
- Quick canned replies (e.g., “is this a scam”, “report”)
- Local database Q&A (reports/detections with date filters)
- Fallback to a local LLM through a *Flask wrapper + Ollama*

---

## How to run (reviewer quick start)

### 1) Start Ollama (host machine)
> Tip: choose a model that fits your RAM. llama3.2:1b or gemma:2b are OK for low-memory machines.

```powershell
# if Ollama isn’t already running as a service
ollama serve

# pull a model (first time only)
ollama pull llama3.2:1b
# or: ollama pull gemma:2b