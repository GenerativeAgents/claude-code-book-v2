// git commit の直前に、秘密情報らしい追加行とコードレビューの鮮度を確認する
import { spawnSync } from "node:child_process";
import { existsSync, readFileSync, statSync, writeFileSync } from "node:fs";
import { join } from "node:path";

const root = process.env.CLAUDE_PROJECT_DIR ?? process.cwd();
const marker = join(root, ".claude", "code-reviewed");
const docFile = /\.md$/i;
const maxBuffer = 64 * 1024 * 1024;

function git(args) {
  return spawnSync("git", args, {
    cwd: root,
    encoding: "utf8",
    maxBuffer,
  });
}

function deny(messages) {
  const message = messages.join("\n");
  process.stdout.write(JSON.stringify({
    hookSpecificOutput: {
      hookEventName: "PreToolUse",
      permissionDecision: "deny",
      permissionDecisionReason: message,
      additionalContext: message,
    },
  }));
  process.exit(0);
}

let input;
try {
  input = JSON.parse(readFileSync(0, "utf8"));
} catch {
  deny(["フックの入力JSONを読み取れないため、操作を止めました。フックの設定を確認してください。"]);
}
const command = input?.tool_input?.command;
if (!["Bash", "PowerShell"].includes(input?.tool_name) || typeof command !== "string") {
  deny(["フックの入力から実行コマンドを取得できないため、操作を止めました。フックの設定を確認してください。"]);
}
// if の絞り込みで誤って呼ばれても、別のコマンドでは検査や記録をしない
if (!/^\s*git[ \t]+commit(?:[ \t]|$)/.test(command)) process.exit(0);

function isPlaceholder(value) {
  return /^(?:<[^>]+>|your(?:[-_ ].*)?|(?:replace|change)[-_ ]?me(?:[-_ ].*)?|example|sample|dummy|fake|redacted|todo|test(?:[-_ ]only)?|x{3,}|0{4,}|1{4,})$/i.test(value.trim());
}

function secretKind(line) {
  if (/-----BEGIN (?:RSA |DSA |EC |OPENSSH )?PRIVATE KEY-----/i.test(line)) {
    return "秘密鍵";
  }
  if (/\bAKIA[0-9A-Z]{16}\b/.test(line)) {
    return "AWSアクセスキー形式";
  }
  if (/\b(?:gh[pousr]_[A-Za-z0-9]{30,}|github_pat_[A-Za-z0-9_]{20,})\b/.test(line)) {
    return "GitHubトークン形式";
  }
  if (/\bxox[baprs]-[A-Za-z0-9-]{10,}\b/.test(line)) {
    return "Slackトークン形式";
  }
  if (/\b(?:sk|rk)_live_[A-Za-z0-9]{16,}\b/.test(line)) {
    return "Stripeライブキー形式";
  }
  if (/\bAIza[0-9A-Za-z_-]{30,}\b/.test(line)) {
    return "Google APIキー形式";
  }

  const assignment = line.match(/\b(?:api[_-]?key|access[_-]?token|client[_-]?secret|password|secret)\b\s*[:=]\s*["']([^"'\r\n]{12,})["']/i);
  if (assignment && !isPlaceholder(assignment[1])) {
    return "秘密情報の代入";
  }
  return null;
}

function scanStagedAdditions() {
  const diff = git([
    "-c", "core.quotepath=false", "diff", "--cached", "--no-ext-diff",
    "--no-color", "--no-renames", "--unified=0", "--",
  ]);
  if (diff.error || diff.status !== 0) {
    return { error: true, findings: [] };
  }

  const findings = [];
  let file = "";
  let lineNumber = 0;
  let inHunk = false;

  for (const line of diff.stdout.split(/\r?\n/)) {
    if (line.startsWith("diff --git ")) {
      file = "";
      inHunk = false;
      continue;
    }
    if (!inHunk && line.startsWith("+++ b/")) {
      file = line.slice("+++ b/".length);
      continue;
    }

    const hunk = line.match(/^@@ -\d+(?:,\d+)? \+(\d+)(?:,\d+)? @@/);
    if (hunk) {
      lineNumber = Number(hunk[1]);
      inHunk = true;
      continue;
    }
    if (!inHunk) continue;

    if (line.startsWith("+")) {
      const kind = secretKind(line.slice(1));
      if (kind) findings.push(`${file || "<staged file>"}:${lineNumber} ${kind}`);
      lineNumber += 1;
    } else if (line.startsWith(" ")) {
      lineNumber += 1;
    }
  }

  return { error: false, findings };
}

const messages = [];
const secretScan = scanStagedAdditions();
if (secretScan.error) {
  messages.push("コミット対象の追加行を検査できませんでした。安全のためコミットを止めました。git diff --cached を確認してください。");
} else if (secretScan.findings.length > 0) {
  messages.push(
    "コミット対象に秘密情報らしい値を検出しました。値は表示していません。該当行を確認し、必要なら取り除いてステージし直してください。",
    ...secretScan.findings,
  );
}

try {
  const listed = git(["ls-files", "--cached", "--others", "--exclude-standard", "-z"]);
  if (listed.error || listed.status !== 0) {
    messages.push("変更ファイルを確認できなかったため、コードレビューの確認を行えません。安全のためコミットを止めました。");
  } else {
    const files = listed.stdout.split("\0").filter((file) => file && !docFile.test(file) && existsSync(join(root, file)));
    const newest = files.reduce((value, file) => Math.max(value, statSync(join(root, file)).mtimeMs), 0);
    const reviewedAt = existsSync(marker) ? statSync(marker).mtimeMs : 0;

    if (newest > reviewedAt) {
      writeFileSync(marker, new Date().toISOString() + "\n");
      messages.push(
        "コードレビューのあとに変更したファイルがあるため、コミットをいったん止めました。/code-review を実行し、指摘を確認してから同じコミットを再試行してください。",
      );
    }
  }
} catch {
  messages.push("コードレビューの確認に必要なファイルを調べられなかったため、コミットを止めました。");
}

if (messages.length > 0) deny(messages);
