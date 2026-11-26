# 第5章 AIネイティブなソフトウェア開発ライフサイクルの実践

既存の受注取消APIに取消期限を足す演習で使う教材です。
書籍の第5章に沿って、AI-DLCワークフローツールで計画、設計、構築、テストを進めます。

| パス | 内容 |
| :-- | :-- |
| [`ec-handson/`](ec-handson/) | 演習開始時点の作業用リポジトリ。Java 21とSpring Boot 4.1.1の2つのサービス（商品在庫、受注）と共通部品、Docker Composeの実行環境、DBの初期データ |
| [`answers/business-answers.md`](answers/business-answers.md) | 業務担当者の回答集。AI-DLCの質問に答えるときに参照する |

## 演習を始める

Docker Composeを利用できるDocker環境（macOSで検証）を用意します。
`ec-handson/`を、このリポジトリの外の作業用ディレクトリへコピーし、Gitリポジトリとして初期化します。

```bash
git clone https://github.com/GenerativeAgents/claude-code-book-v2.git
cp -R claude-code-book-v2/chapter-05/ec-handson ~/github/ec-handson
cd ~/github/ec-handson
git init -b main
git add .
git commit -m "演習開始時点のコード"
```

コンテナとサービスの起動は、コピーした作業用リポジトリの`README.md`と書籍の5.3節に従います。

## 回答集の扱い

回答集は作業用リポジトリへコピーしないでください。
作業用リポジトリに置くと、Claude Codeが質問する前に回答を読み、業務担当者へ確認する工程が演習から消えます。
