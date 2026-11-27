# 第4章 バイブコーディング実践でアプリを狙いどおり作る

第4章「4.4 フックでコードレビューの確認と秘密情報検査を設定してから、MVPを実装する」と「4.7 インフラを構築して、Webサービスとしてデプロイする」で使うファイルです。
4.3節で使うエージェントスキルの記述例もあります。
`settings.after-4.7.json`には4.4と4.7で登録するフックをまとめています。

## 構成

```text
chapter-04/
└── .claude/
    ├── settings.json                  # 4.4で登録するフック
    ├── settings.after-4.7.json        # 4.7でデプロイ前の確認を足したあとの settings.json
    ├── skills/
    │   ├── ddd/SKILL.md               # 用語と決定を記録する
    │   └── tsumete/SKILL.md           # 要件を詰め、実装資料にまとめる（更新後）
    └── hooks/
        ├── pre-commit-check.mjs       # ステージ済みの追加行の秘密情報検査とコードレビューの鮮度確認
        ├── mark-code-reviewed.mjs     # /code-review と打ったときに、レビューを促すかの判定に使う制御ファイルを更新する
        └── pre-deploy-check.mjs       # 4.7。コミットしていない変更があれば、デプロイを止める
```

## 前提

Git、Node.js、Claude Code と、第4章の手順で作った作業用リポジトリ（以下 `myapp`）が必要です。
このディレクトリ自体でアプリを起動・デプロイする教材ではありません。

## 使い方

4.3節の `ddd` と更新後の `tsumete` は `.claude/skills/` にあります。先に作る短い `tsumete` は書籍の手順に沿って作り、あとでこの例と比較してください。

4.4では、`.claude/settings.json`、`.claude/hooks/pre-commit-check.mjs`、`.claude/hooks/mark-code-reviewed.mjs`を、自分の`myapp`へコピーします。
`.gitignore`に`.claude/code-reviewed`を追加するのは、書籍の手順どおりです。

4.7では、`.claude/hooks/pre-deploy-check.mjs`をコピーし、`myapp`の`.claude/settings.json`を`settings.after-4.7.json`の内容にします。
すでに別の設定がある場合は、上書きせず必要なフックだけを追加します。
コピーしたあとは、`settings.after-4.7.json`ではなく`settings.json`という名前で置いてください。

Claude Securityプラグイン本体はこのサンプルに含めていません。
書籍の手順どおり、Claude Codeの公式マーケットプレイスからインストールしてください。

どの設定も、登録する前に中身を読んでください。
フックのコマンドは、自分のユーザーアカウントと同じ権限で実行されます。

コミットはプロジェクトのルートで`git commit ...`、デプロイは`npx wrangler deploy ...`を、それぞれ単独のコマンドとして実行します。
`git add`と`git commit`、ビルドとデプロイは分けて実行してください。
スクリプトはフックから標準入力へ渡されるJSONを読み、コマンドの先頭を確認します。
`if`の絞り込みで別のコマンドに対して呼ばれても、検査や記録を行いません。
シェルの構文全体を解析するものではなく、`git -C ... commit`、`cd ... && git commit`、`npm run deploy`などの形は対象外です。

デプロイ前の確認では、Gitの表示設定にかかわらず未追跡ファイルも列挙します。
`.gitignore`で無視されたファイルは、この確認の対象には入りません。
公開するフォルダに置いてよい内容かは、書籍の手順どおり別途確認します。

このフックは演習用の補助です。レビュー結果の合否を判定するものではなく、外部で実行する操作や、すべての秘密情報・コマンド形式を検査するものでもありません。
