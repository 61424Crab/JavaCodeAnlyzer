# コードレビュー規則

> 適用範囲：Python、PostgreSQL に関連するソースコード、SQL、設定ファイル、テストコード、DB マイグレーション、および関連文書。  
> レビュー主体：人間の Reviewer。  
> AI の位置付け：補助分析ツールであり、最終レビュー責任を負わない。  
> 目的：レビュー基準、責任範囲、確認項目、重大度、最終判定方法を統一する。

---

## クイック利用ガイド

本規則は、次の二つの部分で構成する。

1. **クイックチェックリスト**：通常のレビューで最初に使用する。
2. **詳細規則**：問題の確認、判断根拠の確認、専門項目の確認に使用する。

推奨レビュー順序：

```text
要件・差分確認
→ ソースコードレビュー
→ 単体テスト項目レビュー
→ Python / PostgreSQL 固有確認
→ AI・自動化ツール結果の確認
→ 指摘記録
→ 人間 Reviewer による最終判定
```

> クイックチェックリストをすべて確認した場合でも、変更内容に応じて関連する詳細規則を参照すること。  
> AI、Lint、静的解析、自動テストは補助情報であり、最終判定は人間 Reviewer が行う。

---

## Reviewer クイックチェックリスト

### A. レビュー開始前

- [ ] 要件、設計、仕様を確認した。
- [ ] 変更目的を理解した。
- [ ] 変更範囲と影響範囲を確認した。
- [ ] 無関係な変更が含まれていない。
- [ ] Python、PostgreSQL、依存ライブラリのバージョンを確認した。
- [ ] テスト結果と実行環境を確認した。
- [ ] 情報不足の事項を「要確認」とした。

### B. ソースコードレビュー

#### B-1. 設計・仕様

- [ ] 設計、仕様どおりに実装されている。
- [ ] 入出力、戻り値、更新内容が妥当である。
- [ ] 公開インターフェースに未説明の変更がない。
- [ ] 業務ルールの実装漏れがない。
- [ ] 要件外の動作を追加していない。

#### B-2. 処理ロジック

- [ ] 処理順序が正しい。
- [ ] 条件分岐が網羅されている。
- [ ] 正常系、異常系の動作が正しい。
- [ ] `None`、空値、0、最大値、最小値を適切に処理している。
- [ ] 再実行しても安全である。
- [ ] 部分成功・部分失敗で不整合が発生しない。
- [ ] 必要な冪等性がある。

#### B-3. 例外・エラー・ログ

- [ ] 例外を適切に捕捉、処理している。
- [ ] 例外を握りつぶしていない。
- [ ] 失敗時に成功応答を返していない。
- [ ] 元の例外情報と stack trace を保持している。
- [ ] log level と内容が適切である。
- [ ] 必要な業務識別子を出力している。
- [ ] password、Token、個人情報、接続情報を出力していない。

#### B-4. セキュリティ

- [ ] ユーザー入力を検証している。
- [ ] SQL は parameter binding を使用している。
- [ ] command injection、path traversal、任意 file access がない。
- [ ] 認証回避、権限外 access がない。
- [ ] password、key、Token、接続情報を hardcode していない。
- [ ] error message で内部実装を公開していない。

#### B-5. 可読性・保守性

- [ ] プロジェクトの coding 規約に準拠している。
- [ ] 命名が明確である。
- [ ] 関数、class の責務が明確である。
- [ ] 長すぎる関数、深い nest、重複 code がない。
- [ ] コメントと実装が一致している。
- [ ] 無意味な abstraction、過剰設計がない。
- [ ] AI 生成の冗長 code、架空 API がない。

#### B-6. 性能・リソース

- [ ] loop 内で DB や外部 service を不要に繰り返し呼んでいない。
- [ ] 大量データを一括読込していない。
- [ ] file、connection、cursor、response を解放している。
- [ ] 必要な timeout を設定している。
- [ ] 無限 retry、無制限 cache、queue がない。
- [ ] 性能指摘にデータ量、実行計画、測定根拠がある。

### C. 単体テスト項目レビュー

#### C-1. テスト網羅性

- [ ] テストケースが設計、仕様を十分に網羅している。
- [ ] 正常系を含む。
- [ ] 異常系を含む。
- [ ] 境界値を含む。
- [ ] 主要な条件分岐を含む。
- [ ] regression test を含む。

#### C-2. テスト内容

- [ ] 入力値が妥当である。
- [ ] 前提条件が妥当である。
- [ ] 期待値が妥当である。
- [ ] assert が重要な結果を確認している。
- [ ] 「例外が発生しないこと」だけを確認していない。
- [ ] Mock が実際の interface と一致している。
- [ ] test data が分離されている。
- [ ] 実行順、現在時刻、外部環境に依存していない。

#### C-3. DB テスト

- [ ] Commit、Rollback を確認している。
- [ ] unique constraint、競合処理を確認している。
- [ ] concurrency、retry、冪等性を確認している。
- [ ] PostgreSQL 固有動作を使用して確認している。
- [ ] SQLite の成功のみで PostgreSQL 検証済みとしていない。

### D. Python 固有クイックチェック

- [ ] 対象 Python version が明確である。
- [ ] library、class、method、argument が実在する。
- [ ] type hint と実値が一致する。
- [ ] mutable default argument がない。
- [ ] `Optional`、`Decimal`、`datetime` を正しく扱っている。
- [ ] `is` と `==` を正しく使用している。
- [ ] async 関数内に blocking I/O がない。
- [ ] coroutine を正しく `await` している。
- [ ] file、connection、response を閉じている。
- [ ] untrusted data に `eval`、`exec`、`pickle` を使用していない。
- [ ] Mock target path が正しい。

### E. PostgreSQL 固有クイックチェック

- [ ] JOIN 条件が完全である。
- [ ] `NULL` の扱いが正しい。
- [ ] `UPDATE`、`DELETE` に正しい WHERE 条件がある。
- [ ] parameterized query を使用している。
- [ ] transaction boundary が正しい。
- [ ] 例外後に Rollback している。
- [ ] concurrent update、lost update を考慮している。
- [ ] PK、FK、Unique、NOT NULL、CHECK が適切である。
- [ ] data type、precision が業務要件に合う。
- [ ] index が query 条件に合う。
- [ ] 大量 OFFSET、N+1、不要列を確認した。
- [ ] migration で lock、rollback、既存データを考慮した。

### F. AI 生成コード固有クイックチェック

- [ ] 架空の library、API、argument、return field、exception がない。
- [ ] API、dependency version が一致している。
- [ ] 他 DB の SQL 方言が混在していない。
- [ ] sync API を async API として使用していない。
- [ ] sample password、placeholder、test URL が残っていない。
- [ ] test、Mock が実行可能である。
- [ ] table、index、Extension の存在を誤って仮定していない。
- [ ] 根拠なく thread-safe、transaction-safe、高性能と説明していない。

### G. 最終判定

- [ ] BLOCKER、HIGH をすべて解消した。
- [ ] MEDIUM リスクを解消、または明示的に受容した。
- [ ] すべての「要確認」に結論がある。
- [ ] AI、自動化ツールの結果を人間が確認した。
- [ ] 人間 Reviewer が最終判定した。

---

## 目次

1. [基本原則](#1-基本原則)
2. [レビュー前の確認](#2-レビュー前の確認)
3. [役割と責任](#3-役割と責任)
4. [レビュープロセス](#4-レビュープロセス)
5. [重大度](#5-重大度)
6. [指摘の書き方](#6-指摘の書き方)
7. [共通確認](#7-共通確認)
8. [セキュリティ](#8-セキュリティ)
9. [例外処理・ログ](#9-例外処理ログ)
10. [性能・リソース](#10-性能リソース)
11. [可読性・保守性](#11-可読性保守性)
12. [テスト](#12-テスト)
13. [Python 固有確認](#13-python-固有確認)
14. [PostgreSQL 固有確認](#14-postgresql-固有確認)
15. [Python・PostgreSQL 連携](#15-pythonpostgresql-連携)
16. [AI 生成コード固有確認](#16-ai-生成コード固有確認)
17. [レビュー判定](#17-レビュー判定)
18. [レビュー出力テンプレート](#18-レビュー出力テンプレート)
19. [AI 補助レビュー用プロンプト](#19-ai-補助レビュー用プロンプト)
20. [Reviewer 最終チェックリスト](#20-reviewer-最終チェックリスト)

---

# 詳細レビュー規則

## 1. 基本原則

### 1.1 人間 Reviewer が責任主体である

Reviewer は以下を実施する。

1. 要件、設計意図、変更範囲を理解する。
2. 実装が業務要件を満たすか判断する。
3. リスクが許容可能か判断する。
4. 指摘の正確性と妥当性に責任を持つ。
5. Approve、Approve with Comments、Request Changes を判断する。
6. 最終レビュー結果に責任を持つ。

AI、静的解析、テストが問題を検出しなかった場合でも、自動的にレビュー済みとはみなさない。

### 1.2 AI は補助ツールである

AI は以下に利用できる。

- 差分要約。
- 一般的なロジック不備の検出。
- 境界条件の提示。
- Python、PostgreSQL 固有問題の確認。
- 例外、ログ、セキュリティ、性能リスクの提示。
- テスト観点の作成。
- レビュー指摘の整理。

AI は以下を代行しない。

- 最終承認。
- 業務仕様の正否判断。
- リスク受容判断。
- Reviewer の署名、確認、責任。
- 情報不足時の独断的な結論。

Reviewer は AI の出力を独立して確認し、そのまま最終指摘として転記しない。

### 1.3 事実と推測を分離する

以下を区別する。

- 確認済み問題。
- 発生可能性の高いリスク。
- 追加情報が必要な事項。
- 任意の改善提案。

確認不能な事項は「要確認」とする。

### 1.4 優先順位

1. 要件不備。
2. 業務ロジック不備。
3. データ破損・不整合。
4. セキュリティ。
5. トランザクション・並行処理。
6. 実行時例外。
7. 性能・リソース。
8. 保守性・テスト。
9. 書式・命名・スタイル。

個人の好みを優先し、実質的な問題を見落としてはならない。

---

## 2. レビュー前の確認

可能な限り以下を確認する。

- 変更目的。
- 要件または受入条件。
- 設計書。
- 影響範囲。
- 差分。
- Python バージョン。
- PostgreSQL バージョン。
- 依存ライブラリとバージョン。
- テーブル、制約、インデックス。
- データ量。
- 並行処理、バッチ、定期処理、外部システム。
- 公開インターフェース変更の可否。
- DB スキーマ変更の可否。
- テスト範囲と結果。
- リリース、ロールバック方法。

情報不足時は前提を記録し、影響を明示し、関係者へ確認する。

---

## 3. 役割と責任

### 3.1 Reviewer

- 要件、設計の理解。
- 変更範囲の確認。
- コード、SQL、テストの確認。
- 明確で実行可能な指摘。
- 必須修正と提案の区別。
- 修正後の再確認。
- 最終判定。

### 3.2 開発者

- 要件、設計、変更内容の説明。
- 重要実装とリスクの説明。
- テスト結果の提示。
- Reviewer の質問への回答。
- 問題修正。
- 未対応指摘の理由説明。
- 修正による新規不具合がないことの確認。

### 3.3 AI・自動化ツール

AI、Lint、静的解析、単体テスト、セキュリティスキャンは補助情報を提供する。

結果は判断材料の一つであり、人間の判断を代替しない。

---

## 4. レビュープロセス

1. 要件、設計を読む。
2. 変更目的、範囲を確認する。
3. 差分を確認する。
4. 重大ロジック、セキュリティ、データリスクを確認する。
5. 異常系、境界条件を確認する。
6. Python 固有確認を行う。
7. PostgreSQL 固有確認を行う。
8. テストを確認する。
9. AI、ツール結果を参照する。
10. 指摘を作成する。
11. 開発者が修正する。
12. Reviewer が再確認する。
13. 最終判定する。

---

## 5. 重大度

### BLOCKER

マージ・リリース前に必ず修正する。

### HIGH

機能不具合、例外、権限問題、データ不整合、重大な性能問題につながる可能性が高い。

### MEDIUM

直ちに機能を阻害しないが、信頼性、保守性、テスト品質に影響する。

### LOW

軽微なスタイル、命名、コメント、局所改善。

### INFO

説明、任意提案、参考情報。

---

## 6. 指摘の書き方

各指摘には以下を含める。

- 重大度。
- ファイル名。
- 行番号、関数、クラス、SQL 箇所。
- 問題内容。
- 発生条件。
- 影響。
- 判断根拠。
- 修正案。
- 状態：確定 / 要確認。

```markdown
### [HIGH] 動的 SQL が文字列連結されています

- ファイル：repository/equipment.py
- 箇所：search_equipment()
- 問題：ユーザー入力を WHERE 条件へ直接連結しています。
- 発生条件：keyword に SQL として解釈可能な文字列が含まれる場合。
- 影響：SQL インジェクションが発生する可能性があります。
- 判断根拠：パラメータバインドが使用されていません。
- 修正案：値はパラメータ化し、動的列名は whitelist で制御してください。
- 状態：確定
```

曖昧な指摘だけで終えてはならない。

---

## 7. 共通確認

### 7.1 要件整合性

- 受入条件。
- 異常系。
- 要件外動作。
- デフォルト動作。
- 入出力互換性。
- 公開 API 変更。
- エラーコード、戻り値。
- 正常系のみの実装。
- 無関係なリファクタリング。

### 7.2 境界条件

- `None`、空文字、空 collection。
- 0、負数、最大値、最小値。
- 重複入力。
- 長大文字列、特殊文字、不正 encoding。
- 日付境界、timezone。
- 小数精度。
- 大量データ。
- paging 境界。
- 再実行。
- 部分成功、部分失敗。
- 外部 service timeout。

### 7.3 状態と副作用

- 状態変更順序。
- 失敗時の中間状態。
- retry による重複。
- 冪等性。
- cache、file、DB、外部 system の不整合。
- 共有状態。
- 例外時の resource 解放。

---

## 8. セキュリティ

必須確認：

- SQL injection。
- command injection。
- path traversal。
- 任意 file read/write。
- SSRF、XSS、CSRF。
- 認証回避、権限外 access。
- 機密情報漏えい。
- password、key、Token、接続情報の hardcode。
- 機密情報の log 出力。
- unsafe deserialize。
- 不安全な乱数。
- input validation。
- error message の内部情報。
- upload file。
- third-party dependency。

指摘には攻撃条件、影響、修正方針を含める。

---

## 9. 例外処理・ログ

### 9.1 例外処理

- 広すぎる例外捕捉。
- 空の `except`。
- 例外握りつぶし。
- 例外後の不正継続。
- 誤った成功応答。
- stack trace の消失。
- 不適切な例外型。
- recoverable / unrecoverable の区別。
- 通常処理を例外で制御。
- cleanup。

### 9.2 ログ

- 開始、終了、失敗。
- log level。
- 業務識別子。
- 機密情報。
- 例外重複出力。
- stack trace。
- 検索性。
- 高頻度 loop 内の大量 log。
- 性能影響。

---

## 10. 性能・リソース

- 不要な nested loop。
- 重複計算。
- loop 内 DB、外部 service access。
- 大量データ一括読込。
- 無限 retry。
- 無制限 cache、queue。
- timeout。
- file、connection、cursor、response の解放。
- paging、stream、batch。
- 長時間 lock。
- N+1 query。
- 性能判断の根拠。

実行計画、データ量、測定結果なしに断定しない。

---

## 11. 可読性・保守性

- 単一責務。
- module 境界。
- 命名。
- 重複 code。
- 長すぎる関数。
- 深い nest。
- magic number、magic string。
- コメントと実装。
- hidden side effect。
- 設定分離。
- 既存構造への準拠。
- 無意味な抽象化。
- 過剰設計。
- AI による冗長 code。

個人の好みは、規約がない限り必須修正にしない。

---

## 12. テスト

確認：

- 追加・変更ロジック。
- 正常系、異常系、境界条件。
- 権限、セキュリティ。
- commit、rollback。
- concurrency、retry、冪等性。
- Mock と実 API の一致。
- 実行順、現在時刻依存。
- test data isolation。
- 重要結果の assert。
- regression test。

必要に応じて CI、test log、実行結果を確認する。

---

# 13. Python 固有確認

## 13.1 バージョン・依存

- Python version。
- 構文、standard library API。
- third-party API の実在性。
- dependency version。
- deprecated API。
- sync / async 混在。
- 不要 dependency。
- AI が生成した架空 API。

## 13.2 型・データモデル

- type hint。
- return type。
- `Optional`。
- mutable default argument。
- container element type。
- `Any`。
- `dataclass`、Pydantic。
- serialization。
- `Decimal`、`float`、`datetime`。
- bool、int。
- Enum。

## 13.3 Python 特有ロジック

- `is` と `==`。
- shallow / deep copy。
- closure late binding。
- iteration 中の collection 変更。
- generator 再利用。
- `dict.get()`。
- truthy/falsy。
- `and`、`or`。
- float 比較。
- aware / naive datetime。
- encoding。
- current directory。
- import side effect。
- circular import。
- global state。

## 13.4 async・並行処理

- blocking I/O。
- `await`。
- 未回収 task。
- cancel、timeout。
- thread unsafe object。
- lock 粒度。
- multiprocessing。
- DB Session、connection 共有。
- retry storm。

## 13.5 リソース

- `with`。
- network response。
- DB connection、cursor。
- temp file。
- subprocess return code。
- timeout。
- streaming。
- context manager。

## 13.6 セキュリティ

- `eval`、`exec`。
- untrusted `pickle`。
- `subprocess(shell=True)`。
- unsafe YAML。
- temp file race。
- path escape。
- regex DoS。
- dynamic import。
- JWT。
- `secrets`。
- Web framework 認証、入力検証。

## 13.7 テスト

- `pytest` fixture。
- Mock target。
- 時刻、乱数、UUID。
- async test。
- temp directory。
- DB rollback。
- parameterize。
- exception type、message。
- 無意味な coverage test。

## 13.8 スタイル・ツール

既存規約を優先する。未定義時は PEP 8、PEP 257、`black`、`isort`、`ruff`、`mypy` を参考にする。

---

# 14. PostgreSQL 固有確認

## 14.1 SQL 正確性

- table、column、alias。
- JOIN 条件。
- Cartesian product。
- `NULL`。
- `NOT IN` と `NULL`。
- aggregate と `GROUP BY`。
- stable sort。
- deterministic paging。
- `LIMIT`。
- datetime range。
- implicit cast。
- `ON CONFLICT`。
- `UPDATE`、`DELETE` の WHERE。
- `RETURNING`。

## 14.2 parameter binding

- parameterized query。
- user input の直接連結禁止。
- dynamic identifier の whitelist。
- `LIKE` escape。
- identifier を通常 parameter として bind しない。
- ORM native SQL。
- 機密 parameter の log 禁止。

## 14.3 transaction

- 複数 write。
- commit、rollback。
- exception 後 state。
- partial commit。
- transaction 内外部 service。
- long lock。
- isolation level。
- lost update。
- retry、冪等性。
- savepoint。
- DDL、DML。

## 14.4 concurrency・lock

- read-then-update race。
- `SELECT ... FOR UPDATE`。
- optimistic lock。
- UPSERT。
- deadlock。
- lock order。
- long transaction と Vacuum。
- batch lock。
- Advisory Lock。
- `SKIP LOCKED`。

## 14.5 data type・精度

- 金額の `numeric`。
- timestamp、timezone。
- Python/PostgreSQL timezone。
- `varchar(n)`、`text`。
- ID type。
- JSON/JSONB。
- array。
- `numeric` precision、scale。
- empty string、`NULL`。
- status、bool、enum。
- large object。

## 14.6 constraint

- Primary Key。
- Foreign Key。
- Unique。
- NOT NULL。
- CHECK。
- Default。
- cascade。
- 論理削除と unique。
- application 側だけの一意性。
- orphan data。
- migration と既存不正データ。

## 14.7 index・性能

- WHERE、JOIN、ORDER BY。
- composite index。
- 重複 index。
- write cost。
- partial index。
- expression index。
- `LIKE '%keyword%'`。
- function、implicit cast。
- OFFSET、Keyset Pagination。
- N+1。
- 不要列。
- `EXPLAIN (ANALYZE, BUFFERS)`。

## 14.8 PostgreSQL 固有動作

- identifier case。
- double quote。
- `SERIAL`、Identity。
- Sequence 欠番。
- `now()`。
- `statement_timestamp()`。
- `clock_timestamp()`。
- MVCC。
- Vacuum。
- JSONB、GIN。
- `DISTINCT ON`。
- `ON CONFLICT`。
- `RETURNING`。
- Trigger、Function。
- RLS。
- Extension。

## 14.9 migration

- 再実行可能性。
- rollback。
- 大規模 table lock。
- NOT NULL column。
- `CREATE INDEX CONCURRENTLY`。
- backfill 分割。
- 互換期間。
- release order。
- 新旧 version 互換。
- failure recovery。
- backup。
- migration 後確認。

---

# 15. Python・PostgreSQL 連携

- Python type と DB type。
- `None` と SQL `NULL`。
- `Decimal` と `float`。
- timezone 付き `datetime`。
- UUID、JSONB、array、enum mapping。
- connection pool。
- transaction responsibility。
- exception 後 rollback。
- 大量 result。
- bulk operation。
- ORM N+1。
- ORM Cascade。
- Autocommit。
- DB exception。
- unique conflict。
- retryable error。
- SQL log。
- SQLite の成功だけで PostgreSQL 検証済みとしない。

---

# 16. AI 生成コード固有確認

1. 架空の library、class、function、argument、config。
2. API version 不一致。
3. 他 DB の SQL 方言。
4. sync API を async として使用。
5. 架空の戻り field、exception。
6. demo 環境限定。
7. placeholder、sample password、test URL。
8. コメントと実装の不一致。
9. 無意味な abstraction。
10. standard library の重複実装。
11. 実行不能な test、Mock。
12. table、index、Extension の存在前提。
13. 根拠のない安全性・性能主張。
14. deprecated API。
15. file 間の type、命名、error handling 不統一。

AI 生成コードであることを理由にレビュー基準を下げない。

---

# 17. レビュー判定

### Approve

- BLOCKER、HIGH がない。
- MEDIUM が受入を妨げない。
- 主要機能、異常系に適切な test がある。
- 重要な未解決事項がない。

### Approve with Comments

- 合流を妨げない少数問題のみ。
- LOW、INFO のみ。
- MEDIUM リスクが明示的に受容されている。

### Request Changes

- BLOCKER、HIGH がある。
- 主要要件未達。
- データ、安全、transaction リスク未解決。
- test が著しく不足。
- 重要動作を確認できない。
- 対象環境で明らかに実行不能。

最終判定は人間 Reviewer が行う。

---

# 18. レビュー出力テンプレート

```markdown
# レビュー結果

## 1. 総合判定

- Reviewer：
- レビュー日：
- 判定：Approve / Approve with Comments / Request Changes
- 最高重大度：
- 主なリスク：
- テスト確認方法：
- AI・ツール利用状況：
- 主な前提：

## 2. 必須修正事項

### [HIGH] 指摘タイトル

- ファイル：
- 箇所：
- 問題：
- 発生条件：
- 影響：
- 判断根拠：
- 修正案：
- 状態：確定 / 要確認

## 3. 修正推奨事項

## 4. Python 固有確認

## 5. PostgreSQL 固有確認

## 6. 要確認事項

## 7. 問題なしと確認した重点項目

## 8. Reviewer 最終確認

- [ ] 要件を理解した。
- [ ] 変更範囲を確認した。
- [ ] 主要ロジックを確認した。
- [ ] テストを確認した。
- [ ] AI・ツール出力を確認した。
- [ ] 最終判定を行った。
```

---

# 19. AI 補助レビュー用プロンプト

```text
「コードレビュー規則」に従い、今回の変更をレビュー補助してください。

1. 人間 Reviewer が最終責任を持ちます。
2. あなたの結論は参考情報であり、最終承認を代替しません。
3. 機能、データ、安全性、transaction、concurrency、runtime error を優先してください。
4. Python、PostgreSQL 固有確認を行ってください。
5. 架空 API、version 不一致、誤 SQL 方言、demo 環境限定実装を重点確認してください。
6. 各問題に重大度、file、箇所、発生条件、影響、根拠、修正案を付けてください。
7. 確認不能事項は「要確認」としてください。
8. test 実行結果を捏造しないでください。
9. 書式問題だけを中心にしないでください。
10. 無関係な一般論を大量に出力しないでください。
```

---

# 20. Reviewer 最終チェックリスト

- [ ] 要件、設計を確認した。
- [ ] 変更目的、範囲を確認した。
- [ ] 確定問題と要確認事項を分離した。
- [ ] 重大度を付けた。
- [ ] 主要ロジック、安全、データを優先した。
- [ ] 境界条件を確認した。
- [ ] 例外、ログ、resource 解放を確認した。
- [ ] テストを確認した。
- [ ] Python 固有確認を行った。
- [ ] PostgreSQL 固有確認を行った。
- [ ] transaction、lock、concurrency、冪等性を確認した。
- [ ] SQL parameter 化を確認した。
- [ ] type、constraint、index、migration を確認した。
- [ ] AI 生成コードを重点確認した。
- [ ] AI、自動化ツールの結論を検証した。
- [ ] 個人の好みを必須指摘にしていない。
- [ ] 各指摘に明確な根拠がある。
- [ ] 人間 Reviewer が最終判定した。
