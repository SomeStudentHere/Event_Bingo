docker run --name postgres-db \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=bingo \
  -p 5432:5432 \
  -d postgres