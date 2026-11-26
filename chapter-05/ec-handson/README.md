# ECサイトの実行環境

Java 21とSpring Boot 4.1.1で書かれた2つのサービスと共通部品を含むモノレポです。
このREADMEは起動のための案内です。
機能の仕様と設計はコードと動作から調べます。

## 開発環境を起動する

Docker Desktopを起動し、macOSのターミナルで次を実行します。
Docker Composeで、作業用の`app`コンテナとDBコンテナを起動します。
JDK 21、Maven 3.9.16、JDT LS 1.61.0、PostgreSQL 17を使います。
初回のイメージ作成と依存取得にはネットワーク接続が必要です。

```bash
cd ~/github/ec-handson
docker compose up -d --build --wait
docker compose exec app bash
```

`app`コンテナ内の作業ディレクトリは`/workspace`です。
macOS側の`~/github/ec-handson`と同じファイルを読み書きします。
ホストとコンテナでファイルの所有者IDが異なるため、コンテナのGitでは共有先の`/workspace`だけを`safe.directory`に登録しています。
以後、Java、Maven、Claude Code、`curl`、`psql`は、このコンテナ内で実行します。

```bash
java -version
mvn -version
```

## Claude Codeを使う

コンテナの中で、公式インストーラーに執筆基準版を指定して手動で導入します。
インストールはDockerイメージの作成時には実行しません。

```bash
curl -fsSL https://claude.ai/install.sh | env -u DISABLE_UPDATES bash -s 2.1.228
claude --version
claude
```

初回導入のコマンドだけ`DISABLE_UPDATES`を外し、インストールを実行します。
通常の起動ではComposeの環境変数`DISABLE_UPDATES=1`が引き続き適用され、自動更新を止めます。
初回はClaude Codeへサインインしてください。
`CLAUDE_CONFIG_DIR`には`/home/vscode/.claude`を指定し、設定を専用ボリュームに保存します。
MavenのキャッシュとJDT LSの作業領域にも専用ボリュームを使います。
Claude Codeのバイナリーはコンテナ内にあるため、コンテナを再作成した場合は導入コマンドを再実行します。
Javaの言語サーバーの`jdtls`はイメージに含まれています。

## アプリを起動する

コンテナ内の`/workspace`で、ビルドして商品在庫サービスを起動します。

```bash
mvn -B -q clean -DskipTests package
java -jar catalog-service/target/catalog-service.jar
```

別のmacOSターミナルを開き、同じ`app`コンテナへ入ります。

```bash
cd ~/github/ec-handson
docker compose exec app bash
```

そのコンテナ内で受注サービスを起動します。

```bash
java -jar order-service/target/order-service.jar
```

応答の確認には、さらに別のmacOSターミナルから同じコンテナへ入ります。

```bash
cd ~/github/ec-handson
docker compose exec app bash
```

`curl`はコンテナ内で実行します。
ホスト側にはサービスやDBのポートを公開していません。

```bash
curl -sS http://localhost:8081/products/P000000001
curl -sS http://localhost:8082/orders/OR00000001
```

DBはコンテナ内から`db:5432`へ接続します。
接続先、ユーザー、パスワードは`app`コンテナの環境変数`PGHOST`、`PGUSER`、`PGPASSWORD`に設定済みなので、データベース名だけを指定します。

```bash
psql -d catalog
```

`psql`は`\q`で終了します。
教材専用の初期データを作り直す場合は、両サービスを`Ctrl+C`で停止してから次を実行し、サービスを再起動します。
`catalog`と`orders`に追加したデータは削除されます。

```bash
docker/reset-db.sh
```

接続先は`DB_URL`、`DB_USER`、`DB_PASSWORD`、`CATALOG_BASE_URL`で変更できます。
検証用のDBとサービスはローカル環境で使います。

## 作業を終了して再開する

Claude Codeを終了し、起動しているJavaプロセスはそれぞれのターミナルで`Ctrl+C`を押して停止します。
コンテナ内の各シェルを`exit`で閉じ、macOS側でコンテナを停止します。

```bash
cd ~/github/ec-handson
docker compose stop
```

コンテナとボリュームを保持したまま停止するので、次回はDBの準備を待ってから再び入れます。
Javaのサービスは、必要になった時点で起動し直します。

```bash
cd ~/github/ec-handson
docker compose up -d --wait
docker compose exec app bash
```
