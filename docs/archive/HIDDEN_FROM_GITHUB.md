# Files hidden from GitHub

To keep the GitHub repo readable, these paths are no longer tracked by git and are listed in `.gitignore`. **They are still on disk in the working folder, unchanged.** They also stay in git history (any commit before the "clean repo for interview" commit still has them).

To show one again: remove its line from `.gitignore`, then `git add <path>` and commit.

| Path | What it is | Why hidden |
|---|---|---|
| `app_for_understanding/HealthAssistant-KMP/` | Kotlin Multiplatform mobile app (338 files) | Not backend, belongs to the app developer |
| `web-building/DEEPBLUE-FINALS/` | Kiosk website (React + TypeScript) | Not backend |
| `for_now_adaptationOFweb/` | Earlier version of the website | Duplicate of the above |
| `CallFunction/` | Node.js prototype of the voice agent | Replaced by the Python `app/call_function/` |
| `db_replication/` | Postgres logical replication SQL (publisher and subscriber) and guide | Contains a replication password and host IP. This is how the admin portal gets its data |
| `DEPLOYMENT.md`, `FREE-DEPLOYMENT.md`, `BACKEND_README.md` | Older deployment and backend notes | Superseded by the README |
| `setup-ec2.sh`, `Dockerfile.ec2-optimized` | EC2 free-tier deployment attempt | Real deployment is ECS Fargate |
| `cloudwatch-logs-policy.json` | IAM policy for CloudWatch logs | Deployment detail |
| `ecs-task-definition-ready.json` | ECS task definition filled with real values | Holds live API keys and DB password. Never commit |
| `app/vision_model/vision_client.py`, `llava_client.py`, `vision_config.py`, `vision_routes.py`, `README.md` | Old CLIP / LLaVA vision code | Replaced by Gemini (`gemini_vision.py`). Nothing imports them |
| `app/chatbot/README.md`, `app/data/rag_model/README.md`, `app/data/rag_model/validate.py` | Old module notes and a dev helper | Stale |

## Still tracked on purpose

- `web-app/` and `netlify.toml`: the project blog site deployed on Netlify. `web-app/final_arc.png` is the architecture diagram used in the README. Removing this folder would take the live blog down.
- `docs/DeepBlue_Project_Dossier.docx` and `docs/generate_dossier.py`: project dossier.

## Notes for whoever reads this later

- Local-only (never tracked): all `.env` files, `docs/*.docx` letters and reports, `docs/images/`.
- Working-tree edits to `ecs-task-definition.json` and `ecs-task-definition-ready.json` contain real secrets and were deliberately **not** committed. The committed `ecs-task-definition.json` has placeholders.
