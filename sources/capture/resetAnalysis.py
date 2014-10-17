import dbutils

config = {
    "database": "webdiff", 
    "host": "127.0.0.1", 
    "password": "db_passw0rd", 
    "port": "3306", 
    "user": "webdiff"
  }

conn = dbutils.dbConnect(config);
dbutils.resetAnalysis(conn)