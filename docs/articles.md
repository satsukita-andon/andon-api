/articles
=========

GET /articles
-------------

### example request

```json
{
"sort": "id"
```

### example response

```json
[{
  "id": 1,
  "title": "行灯の作り方",
  "owner": {
  },
```


GET /articles/:id
-----------------

### example response

```json
{
  "id": 1,
  "title": "行灯の作り方",
  "body": "# 行灯の作り方\n\n### 設計図の描き方...",
  "owner": {
    "id": 1,
    "login": "asobinin",
    "name": "遊び人",
    "times": 51,
    ...
  },
  "collaborators": [{
    "id": 2,
    "login": "shigotonin",
    "name": "仕事人",
    "times": 51,
    ...
  }, {
    ...
  }],
  "created_at": "...",
  "updated_at": "...",
  "tags": [{
    "id": 1,
    "name": "総合"
  }]
}
```
