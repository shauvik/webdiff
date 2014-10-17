import dbutils, os

config = {
    "database": "webdiff", 
    "host": "127.0.0.1", 
    "password": "db_passw0rd", 
    "port": "3306", 
    "user": "webdiff"
  }

conn = dbutils.dbConnect(config)
dbutils.cleardb(conn)
files = os.listdir("C:/WebDiff/Screenshots")
for f in files:
    if f[:1] != ".":
        os.remove("C:/WebDiff/Screenshots/"+f)