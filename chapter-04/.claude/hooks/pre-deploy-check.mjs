// wrangler deploy の前に、コミットしていない変更が無いかを確かめる
import { spawnSync } from "node:child_process";
import { readFileSync } from "node:fs";

const root = process.env.CLAUDE_PROJECT_DIR ?? process.cwd();
let input;
try {
  input = JSON.parse(readFileSync(0, "utf8"));
} catch {
  process.stderr.write("フックの入力JSONを読み取れないため、操作を止めました。フックの設定を確認してください。\n");
  process.exit(2);
}
const command = input?.tool_input?.command;
if (!["Bash", "PowerShell"].includes(input?.tool_name) || typeof command !== "string") {
  process.stderr.write("フックの入力から実行コマンドを取得できないため、操作を止めました。フックの設定を確認してください。\n");
  process.exit(2);
}
// ビルドなど別のコマンドでは、デプロイ前の確認をしない
if (!/^\s*npx[ \t]+wrangler[ \t]+deploy(?:[ \t]|$)/.test(command)) process.exit(0);

const r = spawnSync("git", ["status", "--porcelain", "--untracked-files=all"], { cwd: root, encoding: "utf8" });
if (r.error || r.status !== 0) {
  process.stderr.write("git status を実行できなかったため、デプロイを止めました。\n");
  process.exit(2);
}
if (r.stdout.trim() !== "") {
  process.stderr.write(
    "コミットしていない変更があるため、デプロイを止めました。\n" +
    r.stdout +
    "先にコミットして、コミット前のレビューを通してください。\n",
  );
  process.exit(2);
}
